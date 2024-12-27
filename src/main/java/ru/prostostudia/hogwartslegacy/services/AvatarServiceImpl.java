package ru.prostostudia.hogwartslegacy.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(AvatarServiceImpl.class);

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
        logger.info("Uploading avatar for student ID: {}", studentId);
        Student student = studentRepository.findById(studentId).orElseThrow(() -> {
            logger.error("Student with ID {} not found for avatar upload", studentId);
            return new StudentNotFoundException();
        });

        if (file.getSize() > 1024 * 1024) {
            logger.warn("File size exceeds the limit for student ID: {}", studentId);
            throw new FileToLargeException();
        }

        Path path = saveAvatarFile(studentId, file);
        long avatarId;
        try {
            Avatar avatar = new Avatar(path.toString(), file.getSize(), file.getContentType(), file.getBytes(), student);
            avatarId = avatarRepository.save(avatar).getId();
        } catch (IOException e) {
            logger.error("Error occurred while saving avatar for student ID: {}", studentId, e);
            throw new FileUploadErrorException();
        }
        logger.info("Avatar successfully uploaded for student ID: {} with avatar ID: {}", studentId, avatarId);
        return avatarId;
    }

    @Transactional(readOnly = true)
    @Override
    public Avatar getAvatarFromDb(long studentId) {
        logger.info("Fetching avatar from database for student ID: {}", studentId);
        return avatarRepository.findByStudentId(studentId).orElseThrow(() -> {
            logger.warn("Avatar not found in database for student ID: {}", studentId);
            return new AvatarNotFoundException();
        });
    }

    @Transactional(readOnly = true)
    @Override
    public Pair<MediaType, byte[]> getAvatarFromLocal(long studentId) {
        logger.info("Fetching avatar from local storage for student ID: {}", studentId);
        Avatar avatar = avatarRepository.findByStudentId(studentId).orElseThrow(() -> {
            logger.warn("Avatar not found in local storage for student ID: {}", studentId);
            return new AvatarNotFoundException();
        });

        byte[] data;
        try {
            data = Files.readAllBytes(Path.of(avatar.getFilePath()));
        } catch (IOException e) {
            logger.error("Error occurred while reading avatar file for student ID: {}", studentId, e);
            throw new FileDownloadErrorException();
        }

        logger.info("Successfully fetched avatar from local storage for student ID: {}", studentId);
        return Pair.of(MediaType.parseMediaType(avatar.getMediaType()), data);
    }

    private void createDirectoryIfNotExist() {
        logger.debug("Checking if image directory exists");
        if (Files.notExists(imagePath)) {
            try {
                logger.info("Creating image directory at path: {}", imagePath);
                Files.createDirectory(imagePath);
            } catch (IOException e) {
                logger.error("Error occurred while creating image directory at path: {}", imagePath, e);
                throw new DirectoryCreateException();
            }
        }
    }

    private Path saveAvatarFile(Long studentId, MultipartFile file) {
        if (file.isEmpty()) {
            logger.warn("File is empty for student ID: {}", studentId);
            throw new FileUploadErrorException();
        }
        createDirectoryIfNotExist();

        Path path = Path.of(imagePath.toString(), studentId.toString() + "_" + file.getOriginalFilename());
        try {
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            logger.error("Error occurred while writing file for student ID: {}", studentId, e);
            throw new FileUploadErrorException();
        }
        return path;
    }

    @Transactional(readOnly = true)
    public List<Avatar> getAllAvatars(int page, int size) {
        logger.info("Fetching all avatars with pagination - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        List<Avatar> avatars = avatarRepository.findAll(pageable).getContent();
        if (avatars.isEmpty()) {
            logger.warn("No avatars found for the requested page: {} and size: {}", page, size);
        }
        logger.debug("Avatars fetched: {}", avatars);
        return avatars;
    }
}
