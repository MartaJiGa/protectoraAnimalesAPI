package com.svalero.protectoraAnimales.userTests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.protectoraAnimales.controller.UserController;
import com.svalero.protectoraAnimales.domain.User;
import com.svalero.protectoraAnimales.domain.dto.user.UserChangeEmailInDTO;
import com.svalero.protectoraAnimales.domain.dto.user.UserInDTO;
import com.svalero.protectoraAnimales.exception.ErrorResponse;
import com.svalero.protectoraAnimales.exception.runtime.ResourceNotFoundException;
import com.svalero.protectoraAnimales.service.UserService;
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

@WebMvcTest(UserController.class)
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    //region GET

    @Test
    public void testGetUserOk() throws Exception {
        long userId = 1;
        User mockUser = new User(userId, "Lu327", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "lu327@gmail.com", List.of(), List.of());

        when(userService.findById(userId)).thenReturn(mockUser);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        User result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals("lu327@gmail.com", result.getEmail());
        assertEquals(1, result.getId());

        verify(userService, times(1)).findById(userId);
    }

    @Test
    public void testGetUserValidationError() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/user/lu327"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(400, result.getStatusCode());
    }

    @Test
    public void testGetUserNotFound() throws Exception {
        long userId = 32;

        when(userService.findById(userId)).thenThrow(new ResourceNotFoundException("Usuario con id " + userId + " no encontrado."));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/user/{userId}", userId))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getStatusCode());
        assertEquals("Usuario con id 32 no encontrado.", errorResponse.getMessage());

        verify(userService, times(1)).findById(userId);
    }

    //endregion

    //region POST

    @Test
    public void testAddUserOk() throws Exception {
        long userId = 1;
        UserInDTO userInDTO = new UserInDTO("Lu327", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "lu327@gmail.com");
        User mockUser = new User(userId, "Lu327", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "lu327@gmail.com", List.of(), List.of());

        when(userService.saveUser(userInDTO)).thenReturn(mockUser);

        String requestBody = objectMapper.writeValueAsString(userInDTO);
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/users", userInDTO)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateOfBirth").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").exists())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        User result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(201, response.getResponse().getStatus());
        assertEquals("Lucía", result.getName());
        assertEquals("Lu327", result.getUsername());

        verify(userService, times(1)).saveUser(userInDTO);
    }

    @Test
    public void testAddUserValidationError() throws Exception {
        UserInDTO userInDTO = new UserInDTO("Lu327", "Lucía", "Gómez", LocalDate.of(2035, 2, 12), "");

        String requestBody = objectMapper.writeValueAsString(userInDTO);
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/users", userInDTO)
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
        assertEquals(true, result.getValidationErrors().containsKey("email"));
        assertEquals(true, result.getValidationErrors().containsKey("dateOfBirth"));
    }

    @Test
    public void testAddUserNotFound() throws Exception {
        long userId = 1;
        UserInDTO userInDTO = new UserInDTO("Lu327", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "lu327@gmail.com");

        when(userService.saveUser(userInDTO)).thenThrow(new ResourceNotFoundException("Usuario con id " + userId + " no encontrado."));

        String requestBody = objectMapper.writeValueAsString(userInDTO);
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/users", userInDTO)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(404, result.getStatusCode());
        assertEquals("Usuario con id 1 no encontrado.", result.getMessage());

        verify(userService, times(1)).saveUser(userInDTO);
    }

    //endregion

    //region DELETE

    @Test
    public void testRemoveUserOk() throws Exception {
        long userId = 1;

        doNothing().when(userService).removeUser(userId);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/user/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent())
                .andReturn();

        assertEquals(204, response.getResponse().getStatus());

        verify(userService, times(1)).removeUser(userId);
    }

    @Test
    public void testDeleteUserValidationError() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/user/lu327"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(400, result.getStatusCode());
    }

    @Test
    public void testRemoveUserNotFound() throws Exception {
        long userId = 8;

        doThrow(new ResourceNotFoundException("Usuario con id " + userId + " no encontrado."))
                .when(userService).removeUser(userId);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/user/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(404, result.getStatusCode());
        assertEquals("Usuario con id 8 no encontrado.", result.getMessage());

        verify(userService, times(1)).removeUser(userId);
    }

    //endregion

    //region PUT

    @Test
    public void testModifyUserOk() throws Exception {
        long userId = 1;
        UserInDTO userInDTO = new UserInDTO("Lu327", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "lu327@gmail.com");
        User mockUser = new User(userId, "Lu327", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "lu327@gmail.com", List.of(), List.of());

        when(userService.modifyUser(userInDTO, userId)).thenReturn(mockUser);

        String requestBody = objectMapper.writeValueAsString(userInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/user/{userId}", userId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Lu327"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Lucía"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("Gómez"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateOfBirth").value(String.valueOf(LocalDate.of(1985, 2, 12))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("lu327@gmail.com"))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        User result = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(LocalDate.of(1985, 2, 12), result.getDateOfBirth());
        assertEquals(1, result.getId());

        verify(userService, times(1)).modifyUser(userInDTO, userId);
    }

    @Test
    public void testModifyUserValidationError() throws Exception {
        long userId = 1;
        UserInDTO userInDTO = new UserInDTO("Lu327", "Lu", "Gómez", LocalDate.of(1985, 2, 12), "lu327@gmail.com");

        String requestBody = objectMapper.writeValueAsString(userInDTO);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/user/{userId}", userId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(json, ErrorResponse.class);

        assertEquals(400, result.getStatusCode());
        assertEquals("Errores de validación", result.getMessage());
        assertEquals(true, result.getValidationErrors().containsKey("name"));
    }

    @Test
    public void testModifyUserNotFound() throws Exception {
        long userId = 24;
        UserInDTO userInDTO = new UserInDTO("Lu327", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "lu327@gmail.com");

        when(userService.modifyUser(userInDTO, userId))
                .thenThrow(new ResourceNotFoundException("Usuario con id " + userId + " no encontrado."));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInDTO))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        User result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(result);
        assertEquals(404, response.getResponse().getStatus());

        verify(userService, times(1)).modifyUser(userInDTO, userId);
    }

    //endregion

    //region PATCH

    @Test
    public void testChangeUserEmailOk() throws Exception {
        long userId = 1;
        UserChangeEmailInDTO userChangeEmailInDTO = new UserChangeEmailInDTO("luciaGomez@gmail.com");
        User mockUser = new User(userId, "Lu327", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "luciaGomez@gmail.com", List.of(), List.of());

        when(userService.changeUserEmail(userId, userChangeEmailInDTO)).thenReturn(mockUser);

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/user/{userId}/email", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userChangeEmailInDTO))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("luciaGomez@gmail.com"))
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        User result = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(result);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals("luciaGomez@gmail.com", result.getEmail());

        verify(userService, times(1)).changeUserEmail(userId, userChangeEmailInDTO);
    }

    @Test
    public void testChangeUserEmailBadRequest() throws Exception {
        long userId = 1;
        UserChangeEmailInDTO userChangeEmailInDTO = new UserChangeEmailInDTO("luciaGomez#gmail.com");

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/user/{userId}/email", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userChangeEmailInDTO))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(jsonResponse, ErrorResponse.class);

        assertNotNull(result);
        assertEquals(400, result.getStatusCode());
        assertEquals("Errores de validación", result.getMessage());
        assertEquals(true, result.getValidationErrors().containsKey("email"));

        verify(userService, times(0)).changeUserEmail(userId, userChangeEmailInDTO);
    }

    @Test
    public void testChangeUserEmailNotFound() throws Exception {
        long userId = 2;
        UserChangeEmailInDTO userChangeEmailInDTO = new UserChangeEmailInDTO("luciaGomez@gmail.com");

        when(userService.changeUserEmail(userId, userChangeEmailInDTO))
                .thenThrow(new ResourceNotFoundException("Usuario con id " + userId + " no encontrado."));

        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.patch("/user/{userId}/email", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userChangeEmailInDTO))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResponse = response.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse result = objectMapper.readValue(jsonResponse, ErrorResponse.class);

        assertNotNull(result);
        assertEquals(404, result.getStatusCode());
        assertEquals("Usuario con id 2 no encontrado.", result.getMessage());
    }

    //endregion
}
