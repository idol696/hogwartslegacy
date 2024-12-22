package ru.prostostudia.hogwartslegacy.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.prostostudia.hogwartslegacy.exceptions.AvatarNotFoundException;
import ru.prostostudia.hogwartslegacy.models.Avatar;
import ru.prostostudia.hogwartslegacy.services.AvatarServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/avatar")
@Tag(name = "Аватарки", description = "Наборы аватаров")
public class AvatarController {

    private final AvatarServiceImpl avatarService;

    public AvatarController(AvatarServiceImpl avatarService) {
        this.avatarService = avatarService;
    }

    /**
     * @param studentId Id -студента,к которому будет прикреплен аватар
     * @return возвращает Id нового аватара, привязанному к Студенту
     */

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загружает аватар",
            description = "Привязывает аватар 1-к-1 му,и создает егокопию как в БД, таки на локальном устройстве",
            responses = {@ApiResponse(responseCode = "200", description = "Аватар создан"),
                    @ApiResponse(responseCode = "400", description = "Неправильный параметр")})
    public Long uploadAvatar(@RequestParam("studentId") long studentId, @RequestBody MultipartFile file) {
        return avatarService.uploadAvatar(studentId, file);
    }

    /**
     * @param studentId Id -студента, аватар которого мы получаем из базы данных
     * @return возвращает массив данных аватара
     */

    @GetMapping(value = "/get/from-db")
    public ResponseEntity<byte[]> getAvatarFromDb(@RequestParam("studentId") long studentId) {

        try {
            Avatar avatar = avatarService.getAvatarFromDb(studentId);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                    .body(avatar.getData());
        } catch (AvatarNotFoundException ex) {
            System.err.println("Error: " + ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    /**
     * @param studentId Id -студента, аватар которого мы получаем из локального хранилища
     * @return возвращает массив данных аватара
     */

    @GetMapping(value = "/get/from-local")
    public ResponseEntity<byte[]> getAvatarFromLocal(@RequestParam("studentId") long studentId) {

        try {
            Pair<MediaType, byte[]> files = avatarService.getAvatarFromLocal(studentId);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(files.getFirst())
                    .body(files.getSecond());
        } catch (AvatarNotFoundException ex) {
            System.err.println("Error: " + ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    /**
     * @param page Номер страницы (начиная с 0)
     * @param size Размер страницы (количество записей на странице)
     * @return Список аватаров с указанной страницы
     */
    @GetMapping("/all")
    @Operation(summary = "Получить все аватары с пагинацией",
            description = "Позволяет получить аватары с указанием номера страницы и размера страницы",
            responses = @ApiResponse(responseCode = "200", description = "Список аватаров успешно получен"))
    public ResponseEntity<List<Avatar>> getAllAvatarsWithPagination(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        List<Avatar> avatars = avatarService.getAllAvatars(page, size);
        if (avatars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(avatars);
    }

}
