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
import java.util.Base64;
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

    @Test
    void getAllAvatarsAsHtml_ShouldReturnHtmlWithImages_WhenAvatarsExist() throws Exception {

        byte[] avatarData1 = new byte[]{1, 2, 3};
        byte[] avatarData2 = new byte[]{4, 5, 6};
        String mediaType1 = "image/png";
        String mediaType2 = "image/jpeg";

        Avatar avatar1 = new Avatar("path1", 100L, mediaType1, avatarData1, null);
        Avatar avatar2 = new Avatar("path2", 200L, mediaType2, avatarData2, null);

        when(avatarService.getAllAvatars(0, 2)).thenReturn(Arrays.asList(avatar1, avatar2));

        mockMvc.perform(get("/avatar/all/html")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "<img src='data:image/png;base64," + Base64.getEncoder().encodeToString(avatarData1) + "'")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "<img src='data:image/jpeg;base64," + Base64.getEncoder().encodeToString(avatarData2) + "'")));
    }

    @Test
    void getAllAvatarsAsHtml_ShouldReturnNoContent_WhenNoAvatarsExist() throws Exception {

        when(avatarService.getAllAvatars(0, 2)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/avatar/all/html")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isNoContent());
    }
}
