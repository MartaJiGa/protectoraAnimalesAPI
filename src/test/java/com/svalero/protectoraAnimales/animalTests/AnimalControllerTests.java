package com.svalero.protectoraAnimales.animalTests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.protectoraAnimales.controller.AnimalController;
import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.domain.dto.animal.AnimalInDTO;
import com.svalero.protectoraAnimales.domain.dto.animal.AnimalOutDTO;
import com.svalero.protectoraAnimales.exception.ErrorResponse;
import com.svalero.protectoraAnimales.exception.runtime.ResourceNotFoundException;
import com.svalero.protectoraAnimales.service.AnimalService;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnimalController.class)
public class AnimalControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnimalService animalService;

    //region GET

    @Test
    public void testGetAnimalOk() throws Exception {
        long animalId = 2;

        Animal mockAnimal = new Animal(2, LocalDate.of(2022, 6, 15), "Toby", "Perro", 5, "Beagle", "Mediano", false, true, 120.0f, "Muy activo y juguetón", new Location(), List.of());

        when(animalService.findById(animalId)).thenReturn(mockAnimal);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/animal/{animalId}", animalId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        Animal result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals("Toby", result.getName());
        assertEquals(120.0f, result.getPrice());
        assertEquals(LocalDate.of(2022, 6, 15), result.getIncorporationDate());

        verify(animalService, times(1)).findById(animalId);
    }

    @Test
    public void testGetAnimalNotFound() throws Exception {
        long animalId = 1;

        when(animalService.findById(animalId)).thenThrow(new ResourceNotFoundException("Animal con id " + animalId + " no encontrado."));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/animal/{animalId}", animalId))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getStatusCode());
        assertEquals("Animal con id 1 no encontrado.", errorResponse.getMessage());

        verify(animalService, times(1)).findById(animalId);
    }

    @Test
    public void testGetAllAnimalsOk() throws Exception {
        List<AnimalOutDTO> mockAnimalOutDTOList = List.of(
                new AnimalOutDTO(1, LocalDate.of(2023, 1, 10), "Luna", "Perro", 3, "Labrador", "Grande", true, false, 150.0f, "Muy amigable", 0, null),
                new AnimalOutDTO(2, LocalDate.now(), "Milo", "Gato", 2, "Siamés", "Pequeño", false, true, 100.0f, "Cariñoso y tranquilo", 0L, null),
                new AnimalOutDTO(3, LocalDate.of(2024, 3, 5), "Kira", "Conejo", 1, "Enano", "Pequeño", true, false, 50.0f, "Le encanta saltar", 0, null)
        );

        when(animalService.getAnimals()).thenReturn(mockAnimalOutDTOList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/animals")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<AnimalOutDTO> result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(3, result.size());
        assertEquals("Luna", result.get(0).getName());
        assertEquals("Siamés", result.get(1).getBreed());

        verify(animalService, times(1)).getAnimals();
    }

    @Test
    public void testGetAnimalsBySpeciesOk() throws Exception {
        String species = "Perro";

        List<AnimalOutDTO> mockAnimalOutDTOList = List.of(
                new AnimalOutDTO(1, LocalDate.of(2023, 1, 10), "Luna", "Perro", 3, "Labrador", "Grande", true, false, 150.0f, "Muy amigable", 0, null),
                new AnimalOutDTO(2, LocalDate.of(2022, 6, 15), "Toby", "Perro", 5, "Beagle", "Mediano", false, true, 120.0f, "Muy activo y juguetón", 0, null)
        );

        when(animalService.findBySpecies(species)).thenReturn(mockAnimalOutDTOList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/animals")
                        .queryParam("species", species)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<AnimalOutDTO> result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(2, result.size());
        assertEquals("Perro", result.get(0).getSpecies());
        assertEquals("Beagle", result.get(1).getBreed());

        verify(animalService, times(1)).findBySpecies(species);
    }

    @Test
    public void testGetAnimalsByAgeOk() throws Exception {
        int age = 5;

        List<AnimalOutDTO> mockAnimalOutDTOList = List.of(
                new AnimalOutDTO(3, LocalDate.of(2021, 3, 5), "Max", "Gato", 5, "Siamés", "Pequeño", true, false, 100.0f, "Tranquilo", 0, null)
        );

        when(animalService.findByAge(age)).thenReturn(mockAnimalOutDTOList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/animals")
                        .queryParam("age", String.valueOf(age))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<AnimalOutDTO> result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getAge());

        verify(animalService, times(1)).findByAge(age);
    }

    @Test
    public void testGetAnimalsBySizeOk() throws Exception {
        String size = "Grande";

        List<AnimalOutDTO> mockAnimalOutDTOList = List.of(
                new AnimalOutDTO(4, LocalDate.of(2022, 7, 20), "Rocky", "Perro", 2, "Bulldog", "Grande", false, true, 130.0f, "Fuerte y leal", 0, null),
                new AnimalOutDTO(5, LocalDate.of(2023, 4, 10), "Maximiliano", "Gato", 4, "Maine Coon", "Grande", true, false, 160.0f, "Tranquilo y muy grande", 0, null),
                new AnimalOutDTO(6, LocalDate.of(2023, 9, 1), "Lola", "Hurón", 1, "Hurón gigante", "Grande", false, false, 90.0f, "Muy juguetona y curiosa", 0, null),
                new AnimalOutDTO(7, LocalDate.of(2024, 2, 14), "Pelusa", "Conejo", 3, "Gigante de Flandes", "Grande", true, true, 110.0f, "Le encanta que la acaricien", 0, null)
        );

        when(animalService.findBySize(size)).thenReturn(mockAnimalOutDTOList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/animals")
                        .queryParam("size", size)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<AnimalOutDTO> result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(4, result.size());
        assertEquals("Grande", result.get(3).getSize());

        verify(animalService, times(1)).findBySize(size);
    }

    @Test
    public void testGetAnimalsBySpeciesAndAgeOk() throws Exception {
        String species = "Perro";
        int age = 3;

        List<AnimalOutDTO> mockAnimalOutDTOList = List.of(
                new AnimalOutDTO(1, LocalDate.of(2023, 1, 10), "Luna", "Perro", 3, "Labrador", "Grande", true, false, 150.0f, "Muy amigable", 0, null),
                new AnimalOutDTO(2, LocalDate.of(2022, 6, 15), "Toby", "Perro", 3, "Beagle", "Mediano", false, true, 120.0f, "Muy activo y juguetón", 0, null),
                new AnimalOutDTO(4, LocalDate.of(2022, 7, 20), "Rocky", "Perro", 3, "Bulldog", "Grande", false, true, 130.0f, "Fuerte y leal", 0, null)
        );

        when(animalService.findBySpeciesAndAge(species, age)).thenReturn(mockAnimalOutDTOList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/animals")
                        .queryParam("species", species)
                        .queryParam("age", String.valueOf(age))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<AnimalOutDTO> result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(3, result.size());
        assertEquals("Perro", result.get(1).getSpecies());
        assertEquals(3, result.get(2).getAge());

        verify(animalService, times(1)).findBySpeciesAndAge(species, age);
    }

    @Test
    public void testGetAnimalsBySpeciesAndSizeOk() throws Exception {
        String species = "Perro";
        String size = "Grande";

        List<AnimalOutDTO> mockAnimalOutDTOList = List.of(
                new AnimalOutDTO(1, LocalDate.of(2023, 1, 10), "Luna", "Perro", 1, "Labrador", "Grande", true, false, 150.0f, "Muy amigable", 0, null),
                new AnimalOutDTO(2, LocalDate.of(2022, 6, 15), "Toby", "Perro", 4, "Beagle", "Grande", false, true, 120.0f, "Muy activo y juguetón", 0, null),
                new AnimalOutDTO(4, LocalDate.of(2022, 7, 20), "Rocky", "Perro", 6, "Bulldog", "Grande", false, true, 130.0f, "Fuerte y leal", 0, null)
        );

        when(animalService.findBySpeciesAndSize(species, size)).thenReturn(mockAnimalOutDTOList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/animals")
                        .queryParam("species", species)
                        .queryParam("size", size)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<AnimalOutDTO> result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(3, result.size());
        assertEquals("Perro", result.get(1).getSpecies());
        assertEquals("Grande", result.get(0).getSize());

        verify(animalService, times(1)).findBySpeciesAndSize(species, size);
    }

    @Test
    public void testGetAnimalsByAgeAndSizeOk() throws Exception {
        int age = 1;
        String size = "Grande";

        List<AnimalOutDTO> mockAnimalOutDTOList = List.of(
                new AnimalOutDTO(2, LocalDate.of(2023, 9, 1), "Lola", "Hurón", 1, "Hurón gigante", "Grande", true, false, 150.0f, "Muy amigable", 0, null),
                new AnimalOutDTO(4, LocalDate.of(2022, 7, 20), "Rocky", "Perro", 1, "Bulldog", "Grande", false, true, 130.0f, "Fuerte y leal", 0, null)
        );

        when(animalService.findByAgeAndSize(age, size)).thenReturn(mockAnimalOutDTOList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/animals")
                        .queryParam("age", String.valueOf(age))
                        .queryParam("size", size)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<AnimalOutDTO> result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(2, result.size());
        assertEquals(1, result.get(1).getAge());
        assertEquals("Grande", result.get(0).getSize());

        verify(animalService, times(1)).findByAgeAndSize(age, size);
    }

    @Test
    public void testGetAnimalsBySpeciesAgeAndSizeOk() throws Exception {
        String species = "Hurón";
        int age = 1;
        String size = "Grande";

        List<AnimalOutDTO> mockAnimalOutDTOList = List.of(
                new AnimalOutDTO(2, LocalDate.of(2023, 9, 1), "Lola", "Hurón", 1, "Hurón gigante", "Grande", true, false, 150.0f, "Muy amigable", 0, null)
        );

        when(animalService.findBySpeciesAndAgeAndSize(species, age, size)).thenReturn(mockAnimalOutDTOList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/animals")
                        .queryParam("species", species)
                        .queryParam("age", String.valueOf(age))
                        .queryParam("size", size)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<AnimalOutDTO> result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(1, result.size());
        assertEquals("Hurón", result.get(0).getSpecies());
        assertEquals(1, result.get(0).getAge());
        assertEquals("Grande", result.get(0).getSize());

        verify(animalService, times(1)).findBySpeciesAndAgeAndSize(species, age, size);
    }

    @Test
    public void testGetUnadoptedAnimalsByLocationOk() throws Exception {
        long locationId = 1;

        List<AnimalOutDTO> mockAnimalOutDTOList = List.of(
                new AnimalOutDTO(1, LocalDate.of(2023, 1, 10), "Luna", "Perro", 1, "Labrador", "Grande", true, false, 150.0f, "Muy amigable", 1, "Zaragoza"),
                new AnimalOutDTO(2, LocalDate.of(2022, 6, 15), "Toby", "Perro", 4, "Beagle", "Grande", false, true, 120.0f, "Muy activo y juguetón", 1, "Zaragoza"),
                new AnimalOutDTO(4, LocalDate.of(2022, 7, 20), "Rocky", "Perro", 6, "Bulldog", "Grande", false, true, 130.0f, "Fuerte y leal", 1, "Zaragoza")
        );

        when(animalService.findUnadoptedAnimalsByLocation(locationId)).thenReturn(mockAnimalOutDTOList);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/animals/{locationId}", locationId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<AnimalOutDTO> result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(3, result.size());
        assertEquals("Toby", result.get(1).getName());
        assertEquals(120.0f, result.get(1).getPrice());
        assertEquals(LocalDate.of(2022, 6, 15), result.get(1).getIncorporationDate());

        verify(animalService, times(1)).findUnadoptedAnimalsByLocation(locationId);
    }

    //endregion

    //region POST

    @Test
    public void testAddAnimalOk() throws Exception {
        long locationId = 1;
        AnimalInDTO animalInDTO = new AnimalInDTO("Bigotes","Gato",2,"Persa","Pequeño",true,false,120.0f,"Es alegre y le gusta dormir en el sofá");
        AnimalOutDTO animalOutDTO = new AnimalOutDTO(1, LocalDate.of(2023, 1, 10), "Bigotes", "Gato", 2, "Persa","Pequeño",true,false,120.0f,"Es alegre y le gusta dormir en el sofá", 1, "Zaragoza");

        when(animalService.saveAnimal(locationId, animalInDTO)).thenReturn(animalOutDTO);

        String requestBody = objectMapper.writeValueAsString(animalInDTO);
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/location/{locationId}/animals", locationId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.incorporationDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.species").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.breed").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.neutered").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.adopted").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.locationId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.locationCity").exists())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        AnimalOutDTO result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(201, response.getResponse().getStatus());
        assertEquals("Gato", result.getSpecies());
        assertEquals("Bigotes", result.getName());

        verify(animalService, times(1)).saveAnimal(locationId, animalInDTO);
    }

    @Test
    public void testAddAnimalValidationError() throws Exception {
        long locationId = 1;
        AnimalInDTO animalInDTO = new AnimalInDTO("Tiz0n","Caballo",2,"Frisón","Enano",true,false,314.8f,"Muy sociable.");

        String requestBody = objectMapper.writeValueAsString(animalInDTO);
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/location/{locationId}/animals", locationId)
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
        assertEquals(true, result.getValidationErrors().containsKey("name"));
        assertEquals(true, result.getValidationErrors().containsKey("species"));
        assertEquals(true, result.getValidationErrors().containsKey("size"));
    }

    @Test
    public void testAddAnimalLocationNotFound() throws Exception {
        long locationId = 1;
        AnimalInDTO animalInDTO = new AnimalInDTO("Bigotes","Gato",2,"Persa","Pequeño",true,false,120.0f,"Es alegre y le gusta dormir en el sofá");

        when(animalService.saveAnimal(locationId, animalInDTO)).thenThrow(new ResourceNotFoundException("Ubicación con id " + locationId + " no encontrada."));

        String requestBody = objectMapper.writeValueAsString(animalInDTO);
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/location/{locationId}/animals", locationId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(404, result.getStatusCode());
        assertEquals("Ubicación con id 1 no encontrada.", result.getMessage());

        verify(animalService, times(1)).saveAnimal(locationId, animalInDTO);
    }

    //endregion

    //region DELETE

    @Test
    public void testRemoveAnimalOk() throws Exception {
        long animalId = 1;

        doNothing().when(animalService).removeAnimal(animalId);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/animal/{animalId}", animalId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent())
                .andReturn();

        assertEquals(204, response.getResponse().getStatus());

        verify(animalService, times(1)).removeAnimal(animalId);
    }

    @Test
    public void testRemoveAnimalNotFound() throws Exception {
        long animalId = 102;

        doThrow(new ResourceNotFoundException("Animal con id " + animalId + " no encontrado."))
                .when(animalService).removeAnimal(animalId);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/animal/{animalId}", animalId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(404, result.getStatusCode());
        assertEquals("Animal con id 102 no encontrado.", result.getMessage());

        verify(animalService, times(1)).removeAnimal(animalId);
    }

    //endregion

    //region PUT

    @Test
    public void testModifyAnimalOk() throws Exception {
        long animalId = 1;

        AnimalInDTO animalInDTO = new AnimalInDTO("Bigotes","Gato",3,"Persa","Pequeño",true,false,120.0f,"Es alegre y le gusta dormir en el sofá. Le gusta mirar a los pájaros pasar al otro lado de la ventana.");
        AnimalOutDTO animalOutDTO = new AnimalOutDTO(1, LocalDate.of(2023, 1, 10), "Bigotes", "Gato", 3, "Persa","Pequeño",true,false,120.0f,"Es alegre y le gusta dormir en el sofá. Le gusta mirar a los pájaros pasar al otro lado de la ventana.", 1, "Zaragoza");

        when(animalService.modifyAnimal(animalInDTO, animalId)).thenReturn(animalOutDTO);

        String requestBody = objectMapper.writeValueAsString(animalInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/animal/{animalId}", animalId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(animalId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.incorporationDate").value(String.valueOf(LocalDate.of(2023, 1, 10))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Bigotes"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.species").value("Gato"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breed").value("Persa"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value("Pequeño"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.neutered").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.adopted").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(120.0f))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Es alegre y le gusta dormir en el sofá. Le gusta mirar a los pájaros pasar al otro lado de la ventana."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.locationId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.locationCity").value("Zaragoza"))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        AnimalOutDTO result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals("Persa", result.getBreed());
        assertEquals("Zaragoza", result.getLocationCity());

        verify(animalService, times(1)).modifyAnimal(animalInDTO, animalId);
    }

    @Test
    public void testModifyAnimalValidationError() throws Exception {
        long animalId = 1;

        AnimalInDTO animalInDTO = new AnimalInDTO("Tizón", "Gato", -3, "Persa", "Pequeño", true, false, 100.0f, "Es tímida al principio, pero muy cariñosa cuando entra en confianza.");

        String requestBody = objectMapper.writeValueAsString(animalInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/animal/{animalId}", animalId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(400, result.getStatusCode());
        assertEquals("Errores de validación", result.getMessage());
        assertEquals(true, result.getValidationErrors().containsKey("age"));
    }

    @Test
    public void testModifyAnimalLocationOk() throws Exception {
        long locationId = 7;
        long animalId = 1;

        AnimalOutDTO animalOutDTO = new AnimalOutDTO(1, LocalDate.of(2023, 1, 10), "Bigotes", "Gato", 3, "Persa","Pequeño",true,false,120.0f,"Es alegre y le gusta dormir en el sofá. Le gusta mirar a los pájaros pasar al otro lado de la ventana.", locationId, "Barcelona");

        when(animalService.modifyAnimalLocation(animalId, locationId)).thenReturn(animalOutDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/location/{locationId}/animal/{animalId}", locationId, animalId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(animalId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.locationId").value(7))
                .andExpect(MockMvcResultMatchers.jsonPath("$.locationCity").value("Barcelona"))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        AnimalOutDTO result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals("Barcelona", result.getLocationCity());
        assertEquals(7, result.getLocationId());

        verify(animalService, times(1)).modifyAnimalLocation(animalId, locationId);
    }

    @Test
    public void testModifyAnimalLocationNotFound() throws Exception {
        long animalId = 1;
        long locationId = 999;

        when(animalService.modifyAnimalLocation(animalId, locationId))
                .thenThrow(new ResourceNotFoundException("Ubicación con id " + locationId + " no encontrada."));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/location/{locationId}/animal/{animalId}", locationId, animalId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        AnimalOutDTO result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(result);
        assertEquals(404, response.getResponse().getStatus());

        verify(animalService, times(1)).modifyAnimalLocation(animalId, locationId);
    }

    //endregion

    //region PATCH

    @Test
    public void testReturnAnimalOk() throws Exception {
        long animalId = 1;

        AnimalOutDTO animalOutDTO = new AnimalOutDTO(1, LocalDate.of(2023, 1, 10), "Bigotes", "Gato", 3, "Persa","Pequeño",true,false,120.0f,"Es alegre y le gusta dormir en el sofá. Le gusta mirar a los pájaros pasar al otro lado de la ventana.", 1, "Zaragoza");

        when(animalService.returnAnimal(animalId)).thenReturn(animalOutDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/animal/{animalId}/return", animalId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(animalId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.adopted").value(false))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        AnimalOutDTO result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(false, result.isAdopted());
        assertEquals("Bigotes", result.getName());

        verify(animalService, times(1)).returnAnimal(animalId);
    }

    //endregion
}
