package ru.prostostudia.hogwartslegacy.interfaces;


import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import ru.prostostudia.hogwartslegacy.models.Avatar;

import java.util.List;

public interface AvatarService
{
    Long uploadAvatar(long studentId, MultipartFile file);
    Avatar getAvatarFromDb(long studentId);
    Pair<MediaType,byte[]> getAvatarFromLocal(long studentId);

    List<Avatar> getAllAvatars(int page, int size);
}
