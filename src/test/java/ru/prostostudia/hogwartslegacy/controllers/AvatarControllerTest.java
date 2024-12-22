package ru.prostostudia.hogwartslegacy.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.prostostudia.hogwartslegacy.models.Avatar;
import ru.prostostudia.hogwartslegacy.services.AvatarServiceImpl;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvatarController.class)
class AvatarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AvatarServiceImpl avatarService;

    @Test
    void getAllAvatarsWithPagination_ShouldReturnAvatars_WhenPageHasData() throws Exception {

        when(avatarService.getAllAvatars(0, 2)).thenReturn(Arrays.asList(
                new Avatar("path1", 100L, "image/png", new byte[]{1}, null),
                new Avatar("path2", 200L, "image/jpeg", new byte[]{2}, null)
        ));

        mockMvc.perform(get("/avatar/all")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].filePath").value("path1"))
                .andExpect(jsonPath("$[1].filePath").value("path2"));
    }

    @Test
    void getAllAvatarsWithPagination_ShouldReturnEmptyList_WhenNoData() throws Exception {

        when(avatarService.getAllAvatars(0, 2)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/avatar/all")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
