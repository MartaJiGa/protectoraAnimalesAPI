package com.svalero.protectoraAnimales.donationTests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.protectoraAnimales.controller.DonationController;
import com.svalero.protectoraAnimales.domain.Donation;
import com.svalero.protectoraAnimales.domain.User;
import com.svalero.protectoraAnimales.domain.dto.donation.DonationInDTO;
import com.svalero.protectoraAnimales.domain.dto.donation.DonationOutDTO;
import com.svalero.protectoraAnimales.domain.dto.donation.DonationSplitPaymentInDTO;
import com.svalero.protectoraAnimales.exception.ErrorResponse;
import com.svalero.protectoraAnimales.exception.runtime.ResourceNotFoundException;
import com.svalero.protectoraAnimales.service.DonationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DonationController.class)
public class DonationControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DonationService donationService;

    //region GET

    @Test
    public void testGetDonationOk() throws Exception {
        long donationId = 1;
        User user = new User(1, "juan32", "Juan", "Pérez", LocalDate.of(1990, 1, 1), "juan@gmail.com", List.of(), List.of());
        Donation donation = new Donation(donationId, LocalDate.of(2024, 5, 1), 100, "Tarjeta", false, 0, user);

        when(donationService.findById(donationId)).thenReturn(donation);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/donation/{donationId}", donationId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Donation result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(100, result.getQuantity());
        assertEquals("Tarjeta", result.getPaymentType());
        assertEquals(1, result.getId());

        verify(donationService, times(1)).findById(donationId);
    }

    @Test
    public void testGetDonationValidationError() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/donation/sf"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(400, result.getStatusCode());
    }

    @Test
    public void testGetDonationNotFound() throws Exception {
        long donationId = 8;

        when(donationService.findById(donationId)).thenThrow(new ResourceNotFoundException("Donación con id " + donationId + " no encontrada."));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/donation/{donationId}", donationId))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(404, errorResponse.getStatusCode());
        assertEquals("Donación con id 8 no encontrada.", errorResponse.getMessage());

        verify(donationService, times(1)).findById(donationId);
    }

    //endregion

    //region POST

    @Test
    public void testAddDonationOk() throws Exception {
        User user = new User(1, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        DonationInDTO donationInDTO = new DonationInDTO(100, "Tarjeta", false, 0);
        DonationOutDTO donationOutDTO = new DonationOutDTO(1, LocalDate.now(), 100, "Tarjeta", false, 0, user.getId(), user.getUsername(), user.getEmail());

        when(donationService.saveDonation(user.getId(), donationInDTO)).thenReturn(donationOutDTO);

        String requestBody = objectMapper.writeValueAsString(donationInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/donations/user/{userId}", user.getId())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.donationDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.paymentType").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.splitPayment").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.splitPaymentQuantity").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userUsername").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userEmail").exists())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        DonationOutDTO result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(result);
        assertEquals(201, response.getResponse().getStatus());
        assertEquals("laMorena", result.getUserUsername());
        assertEquals("Tarjeta", result.getPaymentType());
        assertEquals(100, result.getQuantity());

        verify(donationService, times(1)).saveDonation(user.getId(), donationInDTO);
    }

    @Test
    public void testAddDonationValidationError() throws Exception {
        DonationInDTO donationInDTO = new DonationInDTO(-3, "Tarjeta", false, 0);

        String requestBody = objectMapper.writeValueAsString(donationInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/donations/user/{userId}", 1)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(400, result.getStatusCode());
        assertEquals("Errores de validación", result.getMessage());
        assertEquals(true, result.getValidationErrors().containsKey("quantity"));
    }

    @Test
    public void testAddDonationUserNotFound() throws Exception {
        long userId = 8;
        DonationInDTO donationInDTO = new DonationInDTO(100, "Tarjeta", false, 0);

        when(donationService.saveDonation(userId, donationInDTO)).thenThrow(new ResourceNotFoundException("Usuario con id " + userId + " no encontrado."));

        String requestBody = objectMapper.writeValueAsString(donationInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/donations/user/{userId}", userId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(404, result.getStatusCode());
        assertEquals("Usuario con id 8 no encontrado.", result.getMessage());

        verify(donationService, times(1)).saveDonation(userId, donationInDTO);
    }

    //endregion

    //region DELETE

    @Test
    public void testRemoveDonationOk() throws Exception {
        long donationId = 1;

        doNothing().when(donationService).removeDonation(donationId);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/donation/{donationId}", donationId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent())
                .andReturn();

        assertEquals(204, response.getResponse().getStatus());

        verify(donationService, times(1)).removeDonation(donationId);
    }

    @Test
    public void testRemoveDonationValidationError() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/donation/donación")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(400, result.getStatusCode());
    }

    @Test
    public void testRemoveDonationNotFound() throws Exception {
        long donationId = 8;

        doThrow(new ResourceNotFoundException("Donación con id " + donationId + " no encontrada.")).when(donationService).removeDonation(donationId);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/donation/{donationId}", donationId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(404, result.getStatusCode());
        assertEquals("Donación con id 8 no encontrada.", result.getMessage());

        verify(donationService, times(1)).removeDonation(donationId);
    }

    //endregion

    //region PUT

    @Test
    public void testModifyDonationOk() throws Exception {
        long donationId = 1;
        long userId = 1;

        DonationInDTO donationInDTO = new DonationInDTO(120, "Transferencia", true, 2);
        DonationOutDTO donationOutDTO = new DonationOutDTO(donationId, LocalDate.now(), 120, "Transferencia", true, 2, userId, "laMorena", "lamorena@gmail.es");

        when(donationService.modifyDonation(donationId, userId, donationInDTO)).thenReturn(donationOutDTO);

        String requestBody = objectMapper.writeValueAsString(donationInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/donation/{donationId}/user/{userId}", donationId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(donationId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.donationDate").value(String.valueOf(donationOutDTO.getDonationDate())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(120))
                .andExpect(MockMvcResultMatchers.jsonPath("$.paymentType").value("Transferencia"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.splitPayment").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.splitPaymentQuantity").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userUsername").value("laMorena"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userEmail").value("lamorena@gmail.es"))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        DonationOutDTO result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(120, result.getQuantity());
        assertEquals("laMorena", result.getUserUsername());
        assertEquals(true, result.isSplitPayment());

        verify(donationService, times(1)).modifyDonation(donationId, userId, donationInDTO);
    }

    @Test
    public void testModifyDonationValidationError() throws Exception {
        long donationId = 1;
        long userId = 1;
        DonationInDTO donationInDTO = new DonationInDTO(120, "Bizum", true, 2);

        String requestBody = objectMapper.writeValueAsString(donationInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/donation/{donationId}/user/{userId}", donationId, userId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(400, result.getStatusCode());
        assertEquals("Errores de validación", result.getMessage());
        assertEquals(true, result.getValidationErrors().containsKey("paymentType"));
    }

    @Test
    public void testModifyDonationNotFound() throws Exception {
        long donationId = 8;
        long userId = 10;
        DonationInDTO donationInDTO = new DonationInDTO(120, "Transferencia", false, 0);

        when(donationService.modifyDonation(donationId, userId, donationInDTO)).thenThrow(new ResourceNotFoundException("Donación con id " + donationId + " no encontrada."));

        String requestBody = objectMapper.writeValueAsString(donationInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/donation/{donationId}/user/{userId}", donationId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(404, result.getStatusCode());
        assertEquals("Donación con id 8 no encontrada.", result.getMessage());

        verify(donationService, times(1)).modifyDonation(donationId, userId, donationInDTO);
    }

    //endregion

    //region PATCH

    @Test
    public void testSplitPaymentOk() throws Exception {
        long donationId = 1;
        DonationSplitPaymentInDTO donationSplitPaymentInDTO = new DonationSplitPaymentInDTO(true, 3);
        DonationOutDTO donationOutDTO = new DonationOutDTO(donationId, LocalDate.now(), 100, "Tarjeta", true, 3, 1, "laMorena", "lamorena@gmail.es");

        when(donationService.splitPayment(donationId, donationSplitPaymentInDTO)).thenReturn(donationOutDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/donation/{donationId}/splitPayment", donationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donationSplitPaymentInDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(donationId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.splitPayment").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.splitPaymentQuantity").value(3))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        DonationOutDTO result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(3, result.getSplitPaymentQuantity());

        verify(donationService, times(1)).splitPayment(donationId, donationSplitPaymentInDTO);
    }

    @Test
    public void testSplitPaymentBadRequest() throws Exception {
        long donationId = 1;
        DonationSplitPaymentInDTO donationSplitPaymentInDTO = new DonationSplitPaymentInDTO(true, 20);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/donation/{donationId}/splitPayment", donationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donationSplitPaymentInDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(400, result.getStatusCode());
        assertEquals("Errores de validación", result.getMessage());
        assertEquals(true, result.getValidationErrors().containsKey("splitPaymentQuantity"));

        verify(donationService, times(0)).splitPayment(donationId, donationSplitPaymentInDTO);
    }

    @Test
    public void testSplitPaymentNotFound() throws Exception {
        long donationId = 8;
        DonationSplitPaymentInDTO donationSplitPaymentInDTO = new DonationSplitPaymentInDTO(true, 2);

        when(donationService.splitPayment(donationId, donationSplitPaymentInDTO)).thenThrow(new ResourceNotFoundException("Donación con id " + donationId + " no encontrada."));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/donation/{donationId}/splitPayment", donationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donationSplitPaymentInDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(404, result.getStatusCode());
        assertEquals("Donación con id 8 no encontrada.", result.getMessage());

        verify(donationService, times(1)).splitPayment(donationId, donationSplitPaymentInDTO);
    }

    //endregion
}
