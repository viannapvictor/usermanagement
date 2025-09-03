package br.com.infnet.edur.usermanagement.controller;

import br.com.infnet.edur.usermanagement.dto.request.UserInputDTO;
import br.com.infnet.edur.usermanagement.model.User;
import br.com.infnet.edur.usermanagement.service.UserService;
import br.com.infnet.edur.usermanagement.utils.exceptions.UserAlreadyExistsException;
import br.com.infnet.edur.usermanagement.utils.exceptions.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UserInputDTO testUserInputDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+1234567890")
                .build();

        testUserInputDTO = new UserInputDTO(
                "John",
                "Doe",
                "john.doe@example.com",
                "+1234567890"
        );
    }

    @Test
    @DisplayName("Should get all users successfully")
    void shouldGetAllUsersSuccessfully() throws Exception {
        User user1 = User.builder().id(1L).firstName("John").lastName("Doe").email("john@example.com").phoneNumber("+1111111111").build();
        User user2 = User.builder().id(2L).firstName("Jane").lastName("Smith").email("jane@example.com").phoneNumber("+2222222222").build();
        List<User> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].firstName", is("John")))
                .andExpect(jsonPath("$.data[1].id", is(2)))
                .andExpect(jsonPath("$.data[1].firstName", is("Jane")));

        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("Should get user by id successfully")
    void shouldGetUserByIdSuccessfully() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.firstName", is("John")))
                .andExpect(jsonPath("$.data.lastName", is("Doe")))
                .andExpect(jsonPath("$.data.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.data.phoneNumber", is("+1234567890")));

        verify(userService).getUserById(1L);
    }

    @Test
    @DisplayName("Should return 404 when user not found by id")
    void shouldReturn404WhenUserNotFoundById() throws Exception {
        when(userService.getUserById(999L)).thenThrow(new UserNotFoundException(999L));

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(999L);
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserInputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.code", is(201)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.firstName", is("John")))
                .andExpect(jsonPath("$.data.lastName", is("Doe")))
                .andExpect(jsonPath("$.data.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.data.phoneNumber", is("+1234567890")));

        verify(userService).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should return 400 when creating user with invalid data")
    void shouldReturn400WhenCreatingUserWithInvalidData() throws Exception {
        UserInputDTO invalidUserInputDTO = new UserInputDTO(
                "",  // Invalid: blank firstName
                "Doe",
                "john.doe@example.com",
                "+1234567890"
        );

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUserInputDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should return 400 when creating user with invalid email")
    void shouldReturn400WhenCreatingUserWithInvalidEmail() throws Exception {
        UserInputDTO invalidUserInputDTO = new UserInputDTO(
                "John",
                "Doe",
                "invalid-email",  // Invalid email format
                "+1234567890"
        );

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUserInputDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should return 400 when creating user with invalid phone number")
    void shouldReturn400WhenCreatingUserWithInvalidPhoneNumber() throws Exception {
        UserInputDTO invalidUserInputDTO = new UserInputDTO(
                "John",
                "Doe",
                "john.doe@example.com",
                "invalid-phone"  // Invalid phone format
        );

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUserInputDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should return 409 when creating user with existing email")
    void shouldReturn409WhenCreatingUserWithExistingEmail() throws Exception {
        when(userService.createUser(any(User.class)))
                .thenThrow(new UserAlreadyExistsException("email", "john.doe@example.com"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserInputDTO)))
                .andExpect(status().isConflict());

        verify(userService).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() throws Exception {
        User updatedUser = User.builder()
                .id(1L)
                .firstName("John Updated")
                .lastName("Doe Updated")
                .email("john.updated@example.com")
                .phoneNumber("+9999999999")
                .build();

        UserInputDTO updateDTO = new UserInputDTO(
                "John Updated",
                "Doe Updated",
                "john.updated@example.com",
                "+9999999999"
        );

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.firstName", is("John Updated")))
                .andExpect(jsonPath("$.data.lastName", is("Doe Updated")))
                .andExpect(jsonPath("$.data.email", is("john.updated@example.com")))
                .andExpect(jsonPath("$.data.phoneNumber", is("+9999999999")));

        verify(userService).updateUser(eq(1L), any(User.class));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent user")
    void shouldReturn404WhenUpdatingNonExistentUser() throws Exception {
        when(userService.updateUser(eq(999L), any(User.class)))
                .thenThrow(new UserNotFoundException(999L));

        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserInputDTO)))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(999L), any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.code", is(204)))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(userService).deleteUser(1L);
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent user")
    void shouldReturn404WhenDeletingNonExistentUser() throws Exception {
        doThrow(new UserNotFoundException(999L)).when(userService).deleteUser(999L);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(999L);
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void shouldReturnEmptyListWhenNoUsersExist() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(userService).getAllUsers();
    }
}