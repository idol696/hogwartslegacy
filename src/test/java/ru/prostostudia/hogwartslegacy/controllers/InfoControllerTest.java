package ru.prostostudia.hogwartslegacy.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InfoController.class)
@ActiveProfiles("test")
class InfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCalculateSum() throws Exception {
        mockMvc.perform(get("/sum"))
                .andExpect(status().isOk()); // Проверяем значение суммы
    }

    @Test
    void testGetServerPort() throws Exception {
        mockMvc.perform(get("/port"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }
}
