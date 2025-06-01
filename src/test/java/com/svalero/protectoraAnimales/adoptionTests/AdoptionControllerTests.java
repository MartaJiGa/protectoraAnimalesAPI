package com.svalero.protectoraAnimales.adoptionTests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.protectoraAnimales.controller.AdoptionController;
import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.domain.User;
import com.svalero.protectoraAnimales.domain.dto.adoption.AdoptionChangePickUpInDTO;
import com.svalero.protectoraAnimales.domain.dto.adoption.AdoptionInDTO;
import com.svalero.protectoraAnimales.domain.dto.adoption.AdoptionOutDTO;
import com.svalero.protectoraAnimales.exception.ErrorResponse;
import com.svalero.protectoraAnimales.exception.runtime.ResourceNotFoundException;
import com.svalero.protectoraAnimales.service.AdoptionService;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdoptionController.class)
public class AdoptionControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdoptionService adoptionService;

    //region GET

    @Test
    public void testGetPickUpsInNextTwoWeeksOk() throws Exception {
        User user = new User(4, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", new ArrayList<>());
        Animal animal = new Animal(3, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", true, false, 214.25f,"Afable y dormilón. En invierno le gusta ponerse al lado de la estufa.", location, null);

        List<AdoptionOutDTO> adoptionList = List.of(
                new AdoptionOutDTO(1, LocalDate.of(2025,4,2), false, LocalDate.now().plusDays(3), "12:20", animal.getId(), animal.getIncorporationDate(), animal.getName(), animal.getSpecies(), animal.getLocation().getId(), animal.getLocation().getCity(), user.getId(), user.getUsername(), user.getEmail())
        );

        when(adoptionService.findPickUpsInNextTwoWeeks()).thenReturn(adoptionList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/adoptions/pickups-next-two-weeks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<AdoptionOutDTO> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("12:20", result.get(0).getPickUpTime());
        assertEquals("laMorena", result.get(0).getUserUsername());
        assertEquals("Rocky", result.get(0).getAnimalName());

        verify(adoptionService, times(1)).findPickUpsInNextTwoWeeks();
    }

    @Test
    void testGetAdoptionsValidationError() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/adoptions")
                        .param("adoptionDate", "31-12-2023"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(400, result.getStatusCode());
    }

    @Test
    public void testGetAdoptionsNotFound() throws Exception {
        long userId = 12;

        when(adoptionService.findByUserId(userId)).thenThrow(new ResourceNotFoundException("No se encontraron adopciones con ID de usuario " + userId));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/adoptions")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(404, errorResponse.getStatusCode());
        assertEquals("No se encontraron adopciones con ID de usuario " + userId, errorResponse.getMessage());

        verify(adoptionService, times(1)).findByUserId(userId);
    }

    //endregion

    //region POST

    @Test
    public void testAddAdoptionOk() throws Exception {
        long userId = 1;
        long animalId = 3;

        User user = new User(userId, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", new ArrayList<>());
        Animal animal = new Animal(animalId, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", true, false, 214.25f,"Afable y dormilón. En invierno le gusta ponerse al lado de la estufa.", location, null);

        AdoptionInDTO adoptionInDTO = new AdoptionInDTO(false, LocalDate.now().plusDays(3), "12:20");
        AdoptionOutDTO adoptionOutDTO = new AdoptionOutDTO(1, LocalDate.of(2025,4,2), false, LocalDate.now().plusDays(3), "12:20", animal.getId(), animal.getIncorporationDate(), animal.getName(), animal.getSpecies(), animal.getLocation().getId(), animal.getLocation().getCity(), user.getId(), user.getUsername(), user.getEmail());

        when(adoptionService.saveAdoption(userId, animalId, adoptionInDTO)).thenReturn(adoptionOutDTO);

        String requestBody = objectMapper.writeValueAsString(adoptionInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/adoptions/user/{userId}/animal/{animalId}", userId, animalId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.adoptionDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.takeAccessories").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pickUpDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pickUpTime").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.animalId").exists())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        AdoptionOutDTO result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(result);
        assertEquals(201, response.getResponse().getStatus());
        assertEquals("laMorena", result.getUserUsername());
        assertEquals("Rocky", result.getAnimalName());

        verify(adoptionService, times(1)).saveAdoption(userId, animalId, adoptionInDTO);
    }

    @Test
    public void testAddAdoptionValidationError() throws Exception {
        long userId = 1;
        long animalId = 3;
        AdoptionInDTO adoptionInDTO = new AdoptionInDTO(false, LocalDate.now().plusDays(3), "12:20:00");

        String requestBody = objectMapper.writeValueAsString(adoptionInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/adoptions/user/{userId}/animal/{animalId}", userId, animalId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(400, result.getStatusCode());
        assertEquals("Errores de validación", result.getMessage());
        assertEquals(true, result.getValidationErrors().containsKey("pickUpTime"));
    }

    @Test
    public void testAddAdoptionUserNotFound() throws Exception {
        long userId = 8;
        long animalId = 2;
        AdoptionInDTO adoptionInDTO = new AdoptionInDTO(false, LocalDate.now().plusDays(3), "12:20");

        when(adoptionService.saveAdoption(userId, animalId, adoptionInDTO)).thenThrow(new ResourceNotFoundException("Usuario con id " + userId + " no encontrado."));

        String requestBody = objectMapper.writeValueAsString(adoptionInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/adoptions/user/{userId}/animal/{animalId}", userId, animalId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(404, result.getStatusCode());
        assertEquals("Usuario con id 8 no encontrado.", result.getMessage());

        verify(adoptionService, times(1)).saveAdoption(userId, animalId, adoptionInDTO);
    }

    //endregion

    //region DELETE

    @Test
    public void testRemoveAdoptionOk() throws Exception {
        long adoptionId = 1;

        doNothing().when(adoptionService).removeAdoption(adoptionId);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/adoption/{adoptionId}", adoptionId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent())
                .andReturn();

        assertEquals(204, response.getResponse().getStatus());

        verify(adoptionService, times(1)).removeAdoption(adoptionId);
    }

    @Test
    public void testRemoveAdoptionValidationError() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/adoption/pruebaadopción")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(400, result.getStatusCode());
    }

    @Test
    public void testRemoveAdoptionNotFound() throws Exception {
        long adoptionId = 8;

        doThrow(new ResourceNotFoundException("Adopción con id " + adoptionId + " no encontrada.")).when(adoptionService).removeAdoption(adoptionId);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/adoption/{adoptionId}", adoptionId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(404, result.getStatusCode());
        assertEquals("Adopción con id 8 no encontrada.", result.getMessage());

        verify(adoptionService, times(1)).removeAdoption(adoptionId);
    }

    //endregion

    //region PUT

    @Test
    public void testModifyAdoptionOk() throws Exception {
        long adoptionId = 1;
        long userId = 1;
        long animalId = 3;

        User user = new User(userId, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());
        Location location = new Location(12, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", new ArrayList<>());
        Animal animal = new Animal(animalId, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 10, "Bulldog", "Mediano", true, false, 214.25f,"Afable y dormilón. En invierno le gusta ponerse al lado de la estufa.", location, null);

        AdoptionInDTO adoptionInDTO = new AdoptionInDTO(true, LocalDate.now().plusDays(3), "12:20");
        AdoptionOutDTO adoptionOutDTO = new AdoptionOutDTO(1, LocalDate.of(2025,4,2), true, LocalDate.now().plusDays(3), "12:20", animal.getId(), animal.getIncorporationDate(), animal.getName(), animal.getSpecies(), animal.getLocation().getId(), animal.getLocation().getCity(), user.getId(), user.getUsername(), user.getEmail());

        when(adoptionService.modifyAdoption(adoptionId, animalId, userId, adoptionInDTO)).thenReturn(adoptionOutDTO);

        String requestBody = objectMapper.writeValueAsString(adoptionInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/adoption/{adoptionId}/user/{userId}/animal/{animalId}", adoptionId, userId, animalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(adoptionId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.adoptionDate").value("2025-04-02"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pickUpDate").value(adoptionOutDTO.getPickUpDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pickUpTime").value("12:20"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.takeAccessories").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.animalId").value(animalId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.animalName").value("Rocky"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.animalSpecies").value("Perro"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.animalLocationId").value(12))
                .andExpect(MockMvcResultMatchers.jsonPath("$.animalLocationCity").value("Zaragoza"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userUsername").value("laMorena"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userEmail").value("lamorena@gmail.es"))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        AdoptionOutDTO result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals("lamorena@gmail.es", result.getUserEmail());
        assertEquals("Perro", result.getAnimalSpecies());
        assertEquals(true, result.isTakeAccessories());

        verify(adoptionService, times(1)).modifyAdoption(adoptionId, animalId, userId, adoptionInDTO);
    }

    @Test
    public void testModifyAdoptionValidationError() throws Exception {
        long adoptionId = 1;
        long userId = 1;
        long animalId = 3;

        AdoptionInDTO adoptionInDTO = new AdoptionInDTO(false, null, "12:20");

        String requestBody = objectMapper.writeValueAsString(adoptionInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/adoption/{adoptionId}/user/{userId}/animal/{animalId}", adoptionId, userId, animalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(400, result.getStatusCode());
        assertEquals("Errores de validación", result.getMessage());
        assertEquals(true, result.getValidationErrors().containsKey("pickUpDate"));
    }

    @Test
    public void testModifyAdoptionNotFound() throws Exception {
        long adoptionId = 8;
        long userId = 1;
        long animalId = 3;

        AdoptionInDTO adoptionInDTO = new AdoptionInDTO(true, LocalDate.now().plusDays(3), "12:20");

        when(adoptionService.modifyAdoption(adoptionId, animalId, userId, adoptionInDTO)).thenThrow(new ResourceNotFoundException("Adopción con id " + adoptionId + " no encontrada."));

        String requestBody = objectMapper.writeValueAsString(adoptionInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/adoption/{adoptionId}/user/{userId}/animal/{animalId}", adoptionId, userId, animalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(404, result.getStatusCode());
        assertEquals("Adopción con id 8 no encontrada.", result.getMessage());

        verify(adoptionService, times(1)).modifyAdoption(adoptionId, animalId, userId, adoptionInDTO);
    }

    //endregion

    //region PATCH

    @Test
    public void testChangePickUpDataOk() throws Exception {
        long adoptionId = 1;

        AdoptionChangePickUpInDTO adoptionChangePickUpInDTO = new AdoptionChangePickUpInDTO(LocalDate.now().plusDays(5), "15:30");
        AdoptionOutDTO adoptionOutDTO = new AdoptionOutDTO(1, LocalDate.of(2025, 4, 2), false, adoptionChangePickUpInDTO.getPickUpDate(), adoptionChangePickUpInDTO.getPickUpTime(), 3L, LocalDate.of(2021, 7, 10), "Rocky", "Perro", 12, "Zaragoza", 1L, "laMorena", "lamorena@gmail.es");

        when(adoptionService.changePickUpData(adoptionId, adoptionChangePickUpInDTO)).thenReturn(adoptionOutDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/adoption/{adoptionId}/pickUp", adoptionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoptionChangePickUpInDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(adoptionId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pickUpDate").value(adoptionChangePickUpInDTO.getPickUpDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pickUpTime").value(adoptionChangePickUpInDTO.getPickUpTime()))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        AdoptionOutDTO result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(adoptionChangePickUpInDTO.getPickUpDate(), result.getPickUpDate());
        assertEquals(adoptionChangePickUpInDTO.getPickUpTime(), result.getPickUpTime());

        verify(adoptionService, times(1)).changePickUpData(adoptionId, adoptionChangePickUpInDTO);
    }

    @Test
    public void testChangePickUpDataBadRequest() throws Exception {
        long adoptionId = 1L;

        AdoptionChangePickUpInDTO adoptionChangePickUpInDTO = new AdoptionChangePickUpInDTO(LocalDate.of(2025, 1, 3), "15:30");

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/adoption/{adoptionId}/pickUp", adoptionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoptionChangePickUpInDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(400, result.getStatusCode());
        assertEquals("Errores de validación", result.getMessage());

        verify(adoptionService, times(0)).changePickUpData(anyLong(), any());
    }

    @Test
    public void testChangePickUpDataNotFound() throws Exception {
        long adoptionId = 8;

        AdoptionChangePickUpInDTO adoptionChangePickUpInDTO = new AdoptionChangePickUpInDTO(LocalDate.now().plusDays(5), "15:30");

        when(adoptionService.changePickUpData(adoptionId, adoptionChangePickUpInDTO)).thenThrow(new ResourceNotFoundException("Adopción con id " + adoptionId + " no encontrada."));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/adoption/{adoptionId}/pickUp", adoptionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoptionChangePickUpInDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(404, result.getStatusCode());
        assertEquals("Adopción con id 8 no encontrada.", result.getMessage());

        verify(adoptionService, times(1)).changePickUpData(adoptionId, adoptionChangePickUpInDTO);
    }

    //endregion
}