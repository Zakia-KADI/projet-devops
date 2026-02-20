package com.studentevents.events.controller;

import com.studentevents.events.model.User;
import com.studentevents.events.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepo;

    @Test
    void login_shouldRedirectIfWrongPassword() throws Exception {

        when(userRepo.findByEmail("a@mail.com"))
                .thenReturn(Optional.of(new User("a@mail.com", "goodpass", "Alice")));

        mockMvc.perform(post("/login")
                        .param("email", "a@mail.com")
                        .param("password", "badpass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void login_shouldRedirectToEventsIfCorrect() throws Exception {

        when(userRepo.findByEmail("a@mail.com"))
                .thenReturn(Optional.of(new User("a@mail.com", "pass", "Alice")));

        mockMvc.perform(post("/login")
                        .param("email", "a@mail.com")
                        .param("password", "pass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));
    }

    @Test
    void signup_shouldSaveUserIfNotExists() throws Exception {

        when(userRepo.existsByEmail("new@mail.com")).thenReturn(false);

        mockMvc.perform(post("/signup")
                        .param("email", "new@mail.com")
                        .param("password", "1234")
                        .param("name", "Test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userRepo).save(any(User.class));
    }

    @Test
    void logout_shouldRedirectHome() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userEmail", "a@mail.com");

        mockMvc.perform(post("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
