package ru.prostostudia.hogwartslegacy.services;

import org.springframework.stereotype.Service;
import ru.prostostudia.hogwartslegacy.interfaces.FacultyService;
import ru.prostostudia.hogwartslegacy.interfaces.StudentService;
import ru.prostostudia.hogwartslegacy.models.Faculty;
import ru.prostostudia.hogwartslegacy.models.Student;

import java.util.List;
import java.util.Random;

@Service
public class TechSupportService {

    private final StudentService studentService;
    private final FacultyService facultyService;


    public TechSupportService(StudentService students, FacultyService faculty) {

        this.studentService = students;
        this.facultyService = faculty;
    }

    private final Random random = new Random();

    public void createRandomFaculties() {
        List<Faculty> faculties = facultyService.getAll();

        if (faculties.isEmpty()) {
            System.out.println("Нет факультетов");
            return;
        }

        List<Student> students = studentService.getAll();
        for (Student student : students) {
            if (student.getFaculty() == null) {
                Faculty randomFaculty = faculties.get(random.nextInt(faculties.size()));
                student.setFaculty(randomFaculty);
                studentService.edit(student);
            }
        }
    }


    public void demoFill() {
     /*   studentService.add("Иван", 18);
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
        facultyService.add("Мегатрон Рулит","Красный"); */
        createRandomFaculties();
    }


}
