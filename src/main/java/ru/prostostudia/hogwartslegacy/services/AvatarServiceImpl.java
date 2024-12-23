package ru.prostostudia.hogwartslegacy.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.prostostudia.hogwartslegacy.exceptions.*;
import ru.prostostudia.hogwartslegacy.interfaces.AvatarService;
import ru.prostostudia.hogwartslegacy.models.Avatar;
import ru.prostostudia.hogwartslegacy.models.Student;
import ru.prostostudia.hogwartslegacy.repository.AvatarRepository;
import ru.prostostudia.hogwartslegacy.repository.StudentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class AvatarServiceImpl implements AvatarService {
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${image.path}")
    private Path imagePath;

    public AvatarServiceImpl(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    @Override
    public Long uploadAvatar(long studentId, MultipartFile file) {
        Student student = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);
        if (file.getSize() > 1024 * 1024) {
            throw new FileToLargeException();
        }
        Path path = saveAvatarFile(studentId, file);
        long avatarId;
        try {
            Avatar avatar = new Avatar(path.toString(), file.getSize(), file.getContentType(), file.getBytes(), student);
            avatarId = avatarRepository.save(avatar).getId();
        } catch (IOException e) {
            throw new FileUploadErrorException();
        }
        return avatarId;
    }

    @Transactional(readOnly = true)
    @Override
    public Avatar getAvatarFromDb(long studentId) {

        return avatarRepository.findByStudentId(studentId).orElseThrow(AvatarNotFoundException::new);
    }

    @Transactional(readOnly = true)
    @Override
    public Pair<MediaType, byte[]> getAvatarFromLocal(long studentId) {
        Avatar avatar = avatarRepository.findByStudentId(studentId).orElseThrow(AvatarNotFoundException::new);
        byte[] data;
        try {
            data = Files.readAllBytes(Path.of(avatar.getFilePath()));
        } catch (IOException e) {
            throw new FileDownloadErrorException();
        }

        return Pair.of(MediaType.parseMediaType(avatar.getMediaType()), data);
    }

    private void createDirectoryIfNotExist() {
        if (Files.notExists(imagePath)) {
            try {
                Files.createDirectory(imagePath);
            } catch (IOException e) {
                throw new DirectoryCreateException();
            }
        }
    }

    private Path saveAvatarFile(Long studentId, MultipartFile file) {

        if (file.isEmpty()) {
            throw new FileUploadErrorException();
        }
        createDirectoryIfNotExist();

        Path path = Path.of(imagePath.toString(), studentId.toString() + "_" + file.getOriginalFilename());
        try {
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            throw new FileUploadErrorException();
        }
        return path;
    }

    @Transactional(readOnly = true)
    public List<Avatar> getAllAvatars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return avatarRepository.findAll(pageable).getContent();
    }
}
