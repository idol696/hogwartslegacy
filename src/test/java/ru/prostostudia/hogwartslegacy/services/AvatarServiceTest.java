package ru.prostostudia.hogwartslegacy.services;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.prostostudia.hogwartslegacy.models.Avatar;
import ru.prostostudia.hogwartslegacy.repository.AvatarRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AvatarServiceTest {

    private final AvatarRepository avatarRepository = mock(AvatarRepository.class);
    private final AvatarServiceImpl avatarService = new AvatarServiceImpl(avatarRepository, null);

    @Test
    void getAllAvatars_ShouldReturnAvatars_WhenPageHasData() {

        Pageable pageable = PageRequest.of(0, 2);
        List<Avatar> avatars = Arrays.asList(
                new Avatar("path1", 100L, "image/png", new byte[]{1}, null),
                new Avatar("path2", 200L, "image/jpeg", new byte[]{2}, null)
        );
        Page<Avatar> page = new PageImpl<>(avatars, pageable, avatars.size());
        when(avatarRepository.findAll(pageable)).thenReturn(page);

        List<Avatar> result = avatarService.getAllAvatars(0, 2);

        assertEquals(2, result.size());
        assertEquals("path1", result.get(0).getFilePath());
        assertEquals("path2", result.get(1).getFilePath());
        verify(avatarRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllAvatars_ShouldReturnEmptyList_WhenNoData() {

        Pageable pageable = PageRequest.of(0, 2);
        Page<Avatar> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(avatarRepository.findAll(pageable)).thenReturn(page);

        List<Avatar> result = avatarService.getAllAvatars(0, 2);

        assertEquals(0, result.size());
        verify(avatarRepository, times(1)).findAll(pageable);
    }
}
