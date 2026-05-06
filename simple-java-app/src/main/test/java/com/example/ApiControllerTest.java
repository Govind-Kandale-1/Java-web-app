package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiController.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        alice = new User("Alice Smith", "alice@example.com", "Engineer");
        alice.setId("1");
        bob = new User("Bob Jones", "bob@example.com");
        bob.setId("2");
    }

    // ── Health ───────────────────────────────────────────────────────────────

    @Test
    void health_returns200() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Application is running!"));
    }

    // ── GET /api/users ───────────────────────────────────────────────────────

    @Test
    void getUsers_returnsList() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of(alice, bob));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alice Smith"))
                .andExpect(jsonPath("$[1].name").value("Bob Jones"));
    }

    @Test
    void getUsers_emptyList_returns200() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ── GET /api/users/{id} ──────────────────────────────────────────────────

    @Test
    void getUser_found_returns200() throws Exception {
        when(userRepository.findById("1")).thenReturn(Optional.of(alice));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Smith"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void getUser_notFound_returns404() throws Exception {
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    // ── POST /api/users ──────────────────────────────────────────────────────

    @Test
    void createUser_success_returns201() throws Exception {
        User newUser = new User("Carol White", "carol@example.com", "Designer");
        when(userRepository.existsByEmail("carol@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Carol White"))
                .andExpect(jsonPath("$.email").value("carol@example.com"));
    }

    @Test
    void createUser_duplicateEmail_returns409() throws Exception {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alice)))
                .andExpect(status().isConflict());

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_blankName_returns400() throws Exception {
        User invalid = new User("", "valid@example.com");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_invalidEmail_returns400() throws Exception {
        User invalid = new User("Valid Name", "not-an-email");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // ── PUT /api/users/{id} ──────────────────────────────────────────────────

    @Test
    void updateUser_success_returns200() throws Exception {
        User updated = new User("Alice Updated", "alice@example.com", "Senior Engineer");
        updated.setId("1");
        when(userRepository.findById("1")).thenReturn(Optional.of(alice));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Updated"));
    }

    @Test
    void updateUser_notFound_returns404() throws Exception {
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alice)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/users/{id} ───────────────────────────────────────────────

    @Test
    void deleteUser_success_returns204() throws Exception {
        when(userRepository.existsById("1")).thenReturn(true);
        doNothing().when(userRepository).deleteById("1");

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userRepository).deleteById("1");
    }

    @Test
    void deleteUser_notFound_returns404() throws Exception {
        when(userRepository.existsById("999")).thenReturn(false);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userRepository, never()).deleteById(any());
    }

    // ── GET /api/users/search ────────────────────────────────────────────────

    @Test
    void searchUsers_returnsMatches() throws Exception {
        when(userRepository.findByNameOrEmailContaining("alice")).thenReturn(List.of(alice));

        mockMvc.perform(get("/api/users/search").param("query", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("alice@example.com"));
    }

    @Test
    void searchUsers_noMatches_returnsEmptyList() throws Exception {
        when(userRepository.findByNameOrEmailContaining("xyz")).thenReturn(List.of());

        mockMvc.perform(get("/api/users/search").param("query", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
