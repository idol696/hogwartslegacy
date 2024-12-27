package ru.prostostudia.hogwartslegacy.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    @Value("${server.port}")
    private String serverPort;

    /**
     * Эндпоинт для получения текущего порта приложения.
     *
     * @return Текущий порт, на котором запущено приложение.
     */

    @GetMapping("/port")
    @Operation(
            summary = "Получить текущий порт приложения",
            description = "Возвращает значение порта, на котором запущено приложение.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно. Возвращается порт приложения."),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера.")
            }
    )
    public String getServerPort() {
        return "Application is running on port: " + serverPort;
    }
}
