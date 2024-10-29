package ru.prostostudia.hogwartslegacy.services;

import org.springframework.stereotype.Service;
import ru.prostostudia.hogwartslegacy.interfaces.FacultyService;
import ru.prostostudia.hogwartslegacy.interfaces.StudentService;

@Service
public class TechSupportService {

    private final StudentService studentService;
    private final FacultyService facultyService;


    public TechSupportService(StudentService students, FacultyService faculty) {

        this.studentService = students;
        this.facultyService = faculty;
    }

    public void demoFill() {
        studentService.add("Иван", 18);
        studentService.add("Анна", 19);
        studentService.add("Сергей", 20);
        studentService.add("Мария", 21);
        studentService.add("Дмитрий", 22);
        studentService.add("Ольга", 23);
        studentService.add("Алексей", 24);
        studentService.add("Елена", 25);
        studentService.add("Николай", 26);
        studentService.add("Юлия", 27);

        facultyService.add("Забубенные","Синий");
        facultyService.add("Футанари","Оранжевый");
        facultyService.add("Хрюшечки","Розовый");
        facultyService.add("Мегатрон Рулит","Красный");
    }


}
