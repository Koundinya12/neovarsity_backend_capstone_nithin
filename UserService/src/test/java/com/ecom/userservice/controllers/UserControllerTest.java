package com.ecom.userservice.controllers;

import com.ecom.userservice.controllers.UserController;
import com.ecom.userservice.dtos.UserResponseDto;
import com.ecom.userservice.exceptions.UsernameNotFoundException;
import com.ecom.userservice.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RestControllerAdvice
    static class TestExceptionHandler {
        @ExceptionHandler(UsernameNotFoundException.class)
        public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @BeforeEach
    void setup() {
        UserController controller = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new TestExceptionHandler())
                .build();
    }

    @Test
    void testGetUserDetails_ReturnsDto_WhenIdExists() throws Exception {
        String id = "123";
        UserResponseDto dto = new UserResponseDto(id, "John Doe", "john@example.com");
        when(userService.getUserDetails(id)).thenReturn(dto);

        mockMvc.perform(get("/users/{id}", id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userService, times(1)).getUserDetails(id);
    }

    @Test
    void testRegisterUser_ReturnsSavedDto_WhenValidPayload() throws Exception {
        UserResponseDto requestDto = new UserResponseDto(null, "Alice", "alice@example.com");
        UserResponseDto savedDto = new UserResponseDto("abc", "Alice", "alice@example.com");
        when(userService.saveUser(any(UserResponseDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("abc")))
                .andExpect(jsonPath("$.name", is("Alice")))
                .andExpect(jsonPath("$.email", is("alice@example.com")));

        ArgumentCaptor<UserResponseDto> captor = ArgumentCaptor.forClass(UserResponseDto.class);
        verify(userService, times(1)).saveUser(captor.capture());
        UserResponseDto passed = captor.getValue();
        assertEquals(requestDto.getName(), passed.getName());
        assertEquals(requestDto.getEmail(), passed.getEmail());
    }

    @Test
    void testGetUserDetails_DelegatesToServiceWithCorrectId() throws Exception {
        String id = "abc-123_XY";
        UserResponseDto dto = new UserResponseDto(id, "Jane", "jane@example.com");
        when(userService.getUserDetails(id)).thenReturn(dto);

        mockMvc.perform(get("/users/{id}", id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userService, times(1)).getUserDetails(captor.capture());
        assertEquals(id, captor.getValue());
    }

    @Test
    void testGetUserDetails_PropagatesUsernameNotFoundException_WhenUserMissing() throws Exception {
        String id = "missing-id";
        when(userService.getUserDetails(id)).thenThrow(new UsernameNotFoundException("not found"));

        mockMvc.perform(get("/users/{id}", id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("not found"));
    }

    @Test
    void testRegisterUser_ReturnsBadRequest_WhenRequestBodyInvalid() throws Exception {
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
}
