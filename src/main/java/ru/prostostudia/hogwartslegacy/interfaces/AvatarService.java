package ru.prostostudia.hogwartslegacy.interfaces;


import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import ru.prostostudia.hogwartslegacy.models.Avatar;

public interface AvatarService
{
    Long uploadAvatar(long studentId, MultipartFile file);
    Avatar getAvatarFromDb(long studentId);
    Pair<MediaType,byte[]> getAvatarFromLocal(long studentId);
}
