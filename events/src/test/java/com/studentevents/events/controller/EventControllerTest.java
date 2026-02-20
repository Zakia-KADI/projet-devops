package com.studentevents.events.controller;

import com.studentevents.events.model.Event;
import com.studentevents.events.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService service;

    @Test
    void getEvents_shouldReturnEventsView() throws Exception {
        // given
        Event e = new Event();
        e.setTitle("Conf");
        e.setDescription("ABC");
        e.setDateTime(LocalDateTime.now().plusDays(1));
        e.setLocation("Amphi");
        e.setMaxParticipants(20);

        when(service.listEvents()).thenReturn(List.of(e));
        when(service.nbInscrits(anyString())).thenReturn(0L);
        when(service.placesRestantes(anyString())).thenReturn(20L);

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("events"));
    }

    @Test
    void postEvents_withoutSession_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(post("/events")
                        .param("title", "Conf")
                        .param("description", "test")
                        .param("dateTime", "2026-02-20T10:00")
                        .param("location", "Amphi")
                        .param("maxParticipants", "20"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(service, never()).createEvent(any(), any(), any(), any(), anyInt(), any());
    }

    @Test
    void postEvents_withSession_shouldCreateAndRedirect() throws Exception {
        mockMvc.perform(post("/events")
                        .sessionAttr("userEmail", "test@mail.com")
                        .param("title", "Conf")
                        .param("description", "test")
                        .param("dateTime", "2026-02-20T10:00")
                        .param("location", "Amphi")
                        .param("maxParticipants", "20"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));

        verify(service).createEvent(
                eq("Conf"),
                eq("test"),
                any(LocalDateTime.class),
                eq("Amphi"),
                eq(20),
                eq("test@mail.com")
        );
    }
}
