package ru.prostostudia.hogwartslegacy.controllers;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prostostudia.hogwartslegacy.services.FacultyServiceImpl;
import ru.prostostudia.hogwartslegacy.services.StudentServiceImpl;
import ru.prostostudia.hogwartslegacy.services.TechSupportService;

@RestController
@RequestMapping("/init")
public class TechSupportController {
    private final TechSupportService techSupportService;

    public TechSupportController(TechSupportService techSupportService) {
        this.techSupportService = techSupportService;
    }
    @GetMapping
    public String initQuestion() {
        techSupportService.demoFill();
        return "Заполнено успешно!";
    }
}
