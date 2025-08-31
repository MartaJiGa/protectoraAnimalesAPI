package com.svalero.protectoraAnimales.locationTests;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.protectoraAnimales.controller.LocationController;
import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.domain.dto.location.LocationInDTO;
import com.svalero.protectoraAnimales.exception.ErrorResponse;
import com.svalero.protectoraAnimales.exception.runtime.NoChangeException;
import com.svalero.protectoraAnimales.exception.runtime.ResourceNotFoundException;
import com.svalero.protectoraAnimales.service.LocationService;
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
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationController.class)
public class LocationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LocationService locationService;

    //region GET

    @Test
    public void testGetLocationOk() throws Exception {
        long locationId = 1;
        Location mockLocation = new Location(locationId, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d, new ArrayList<>());

        when(locationService.findById(locationId)).thenReturn(mockLocation);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/location/{locationId}", locationId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Location result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals("Zaragoza", result.getCity());
        assertEquals("Centro de adopción de animales en Zaragoza.", result.getDescription());

        verify(locationService, times(1)).findById(locationId);
    }

    @Test
    public void testGetAnimalsInLocationOk() throws Exception {
        long locationId = 1;
        Location mockLocation = new Location(locationId, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d, new ArrayList<>());
        List<Animal> mockAnimalList = List.of(
                new Animal(1, LocalDate.of(2023, 1, 10), "Luna", "Perro", 3, "Labrador", "Grande", true, false, 150.0f, "Muy amigable", null, mockLocation, null),
                new Animal(2, LocalDate.now(), "Milo", "Gato", 2, "Siamés", "Pequeño", false, true, 100.0f, "Cariñoso y tranquilo", null, mockLocation, null),
                new Animal(3, LocalDate.of(2024, 3, 5), "Kira", "Conejo", 1, "Enano", "Pequeño", true, false, 50.0f, "Le encanta saltar", null, mockLocation, null)
        );

        when(locationService.findAnimalsByLocationId(locationId)).thenReturn(mockAnimalList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/location/{locationId}/animals", locationId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<Animal> result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals("Milo", result.get(1).getName());
        assertEquals("Grande", result.get(0).getSize());
        assertEquals("Conejo", result.get(2).getSpecies());

        verify(locationService, times(1)).findAnimalsByLocationId(locationId);
    }

    @Test
    public void testGetLocationValidationError() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/location/za"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(400, result.getStatusCode());
    }

    @Test
    public void testGetLocationNotFound() throws Exception {
        long locationId = 3;

        when(locationService.findById(locationId)).thenThrow(new ResourceNotFoundException("Ubicación con id " + locationId + " no encontrada."));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/location/{locationId}", locationId))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getStatusCode());
        assertEquals("Ubicación con id 3 no encontrada.", errorResponse.getMessage());

        verify(locationService, times(1)).findById(locationId);
    }
    
    //endregion

    //region POST

    @Test
    public void testAddLocationOk() throws Exception {
        long locationId = 1;
        LocationInDTO locationInDTO = new LocationInDTO(true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d);
        Location mockLocation = new Location(locationId, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d, new ArrayList<>());

        when(locationService.saveLocation(locationInDTO)).thenReturn(mockLocation);

        String requestBody = objectMapper.writeValueAsString(locationInDTO);
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/locations", locationInDTO)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mainSite").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Location result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(201, response.getResponse().getStatus());
        assertEquals("50003", result.getZipCode());
        assertEquals(true, result.isMainSite());

        verify(locationService, times(1)).saveLocation(locationInDTO);
    }

    @Test
    public void testAddLocationValidationError() throws Exception {
        LocationInDTO locationInDTO = new LocationInDTO(true, "Calle del Gato 23", "", "Zar8agoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d);

        String requestBody = objectMapper.writeValueAsString(locationInDTO);
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/locations", locationInDTO)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(400, result.getStatusCode());
        assertEquals("Errores de validación", result.getMessage());
        assertEquals(true, result.getValidationErrors().containsKey("city"));
        assertEquals(true, result.getValidationErrors().containsKey("zipCode"));
    }

    @Test
    public void testAddLocationNotFound() throws Exception {
        long locationId = 1;
        LocationInDTO locationInDTO = new LocationInDTO(true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d);

        when(locationService.saveLocation(locationInDTO)).thenThrow(new ResourceNotFoundException("Ubicación con id " + locationId + " no encontrada."));

        String requestBody = objectMapper.writeValueAsString(locationInDTO);
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/locations", locationInDTO)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(404, result.getStatusCode());
        assertEquals("Ubicación con id " + locationId + " no encontrada.", result.getMessage());

        verify(locationService, times(1)).saveLocation(locationInDTO);
    }

    //endregion

    //region DELETE

    @Test
    public void testRemoveLocationOk() throws Exception {
        long locationId = 1;

        doNothing().when(locationService).removeLocation(locationId);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/location/{locationId}", locationId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent())
                .andReturn();

        assertEquals(204, response.getResponse().getStatus());

        verify(locationService, times(1)).removeLocation(locationId);
    }

    @Test
    public void testDeleteLocationValidationError() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/location/zarag"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(400, result.getStatusCode());
    }

    @Test
    public void testRemoveLocationNotFound() throws Exception {
        long locationId = 3;

        doThrow(new ResourceNotFoundException("Ubicación con id " + locationId + " no encontrada."))
                .when(locationService).removeLocation(locationId);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/location/{locationId}", locationId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(404, result.getStatusCode());
        assertEquals("Ubicación con id 3 no encontrada.", result.getMessage());

        verify(locationService, times(1)).removeLocation(locationId);
    }

    //endregion

    //region PUT

    @Test
    public void testModifyLocationOk() throws Exception {
        long locationId = 1;
        LocationInDTO locationInDTO = new LocationInDTO(true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d);
        Location mockLocation = new Location(locationId, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d, new ArrayList<>());

        when(locationService.modifyLocation(locationInDTO, locationId)).thenReturn(mockLocation);

        String requestBody = objectMapper.writeValueAsString(locationInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/location/{locationId}", locationId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(locationId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.mainSite").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("Calle del Gato 23"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("50003"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("Zaragoza"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Centro de adopción de animales en Zaragoza."))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Location result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals("Zaragoza", result.getCity());
        assertEquals("Calle del Gato 23", result.getAddress());

        verify(locationService, times(1)).modifyLocation(locationInDTO, locationId);
    }

    @Test
    public void testModifyLocationValidationError() throws Exception {
        long locationId = 1;
        LocationInDTO locationInDTO = new LocationInDTO(true, "Calle del Gato 23", "60027", "", "Centro de adopción de animales en Zaragoza.", 15d, 16d);

        String requestBody = objectMapper.writeValueAsString(locationInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/location/{locationId}", locationId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(400, result.getStatusCode());
        assertEquals("Errores de validación", result.getMessage());
        assertEquals(true, result.getValidationErrors().containsKey("zipCode"));
        assertEquals(true, result.getValidationErrors().containsKey("city"));
    }

    @Test
    public void testModifyLocationNotFound() throws Exception {
        long locationId = 3;
        LocationInDTO locationInDTO = new LocationInDTO(true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d);

        when(locationService.modifyLocation(locationInDTO, locationId))
                .thenThrow(new ResourceNotFoundException("Ubicación con id " + locationId + " no encontrada."));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/location/{locationId}", locationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationInDTO))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Location result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(result);
        assertEquals(404, response.getResponse().getStatus());

        verify(locationService, times(1)).modifyLocation(locationInDTO, locationId);
    }

    //endregion

    //region PATCH

    @Test
    public void testChangeMainSiteOk() throws Exception {
        long locationId = 1;
        Location mockLocation = new Location(locationId, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", 15d, 16d, new ArrayList<>());

        when(locationService.changeMainSite(locationId)).thenReturn(mockLocation);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/location/{locationId}/mainSite", locationId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.mainSite").value(true))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Location result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(true, result.isMainSite());

        verify(locationService, times(1)).changeMainSite(locationId);
    }

    @Test
    public void testChangeMainSiteBadRequest() throws Exception {
        long locationId = 1;

        when(locationService.changeMainSite(locationId))
                .thenThrow(new NoChangeException("Esta ubicación ya es la principal, por lo que no necesita cambiar."));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/location/{locationId}/mainSite", locationId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(jsonResponse, ErrorResponse.class);

        assertNotNull(result);
        assertEquals(400, result.getStatusCode());
        assertEquals("Esta ubicación ya es la principal, por lo que no necesita cambiar.", result.getMessage());

        verify(locationService, times(1)).changeMainSite(locationId);
    }

    @Test
    public void testChangeMainSiteNotFound() throws Exception {
        long locationId = 3;

        when(locationService.changeMainSite(locationId))
                .thenThrow(new ResourceNotFoundException("Ubicación con id " + locationId + " no encontrada."));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/location/{locationId}/mainSite", locationId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(jsonResponse, ErrorResponse.class);

        assertNotNull(result);
        assertEquals(404, result.getStatusCode());
        assertEquals("Ubicación con id " + locationId + " no encontrada.", result.getMessage());
    }

    //endregion
}
