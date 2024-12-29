package ru.prostostudia.hogwartslegacy.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.prostostudia.hogwartslegacy.models.Faculty;
import ru.prostostudia.hogwartslegacy.models.Student;
import ru.prostostudia.hogwartslegacy.services.StudentServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/student")
@Tag(name = "Студенты", description = "Студенческий учет")
public class StudentController {

    private final StudentServiceImpl studentService;

    public StudentController(StudentServiceImpl studentService) {
        this.studentService = studentService;
    }

    /**
     * Ищет студента по его идентификатору.
     *
     * @param id идентификатор студента
     * @return возвращает найденного студента, иначе ошибка 404
     */
    @GetMapping("/get/{id}")
    @Operation(summary = "Ищет студента",
            description = "Ищет студента по его id",
            responses = {@ApiResponse(responseCode = "404", description = "Студент не найден"),
                    @ApiResponse(responseCode = "200", description = "Студент найден")})
    public Student getFindStudent(@PathVariable long id) {
        return studentService.get(id);
    }

    /**
     * Получает факультет студента по идентификатору студента.
     *
     * @param id идентификатор студента
     * @return возвращает факультет студента, иначе ошибка 404
     */
    @GetMapping("/faculty/{id}")
    @Operation(summary = "Факультет студента",
            description = "Выводит информацию о факультете студента по id студента",
            responses = {@ApiResponse(responseCode = "404", description = "Студент не найден"),
                    @ApiResponse(responseCode = "200", description = "Студент найден")})
    public Faculty getFacultyOfStudent(@PathVariable long id) {
        return studentService.getStudentFaculty(id);
    }

    /**
     * Добавляет нового студента.
     *
     * @param student набор параметров студента для добавления
     * @return возвращает идентификатор добавленного студента
     */
    @PostMapping("/add")
    @Operation(summary = "Добавляет студента",
            description = "Добавляет студента и устанавливает id",
            responses = @ApiResponse(responseCode = "200", description = "Студент добавлен"))
    public Long createStudent(@RequestBody Student student) {
        return studentService.add(student);
    }

    /**
     * Обновляет информацию о студенте.
     *
     * @param student студент для обновления
     * @return возвращает обновленного студента, иначе ошибка 404 или 400 при неправильных параметрах
     */
    @PutMapping("/edit")
    @Operation(summary = "Редактирует студента",
            description = "Редактирует студента по его id",
            responses = {@ApiResponse(responseCode = "404", description = "Студент не найден"),
                    @ApiResponse(responseCode = "400", description = "Неправильные параметры"),
                    @ApiResponse(responseCode = "200", description = "Студент изменен")})
    public Student editStudent(@RequestBody Student student) {
        return studentService.edit(student);
    }

    /**
     * Удаляет студента по его идентификатору.
     *
     * @param id идентификатор студента
     */
    @GetMapping("/remove/{id}")
    @Operation(summary = "Удаляет студента",
            responses = {@ApiResponse(responseCode = "400", description = "Студент не найден по id"),
                    @ApiResponse(responseCode = "200", description = "Студент удален")})
    public void deleteStudent(@PathVariable("id") long id) {
        studentService.remove(id);
    }

    /**
     * Получает список всех студентов.
     *
     * @return возвращает список всех студентов
     */
    @GetMapping()
    @Operation(summary = "Список всех студентов",
            responses = @ApiResponse(responseCode = "200", description = "Отображение всех студентов"))
    public List<Student> getAllStudent() {
        return studentService.getAll();
    }

    /**
     * Получает студентов в диапазоне возраста.
     *
     * @param ageMin минимальный возраст
     * @param ageMax максимальный возраст
     * @return возвращает список студентов указанного возраста, иначе ошибка 404
     */
    @GetMapping("/age/{ageMin}/{ageMax}")
    @Operation(summary = "Поиск по студентам с диапазоном возраста",
            description = "Поиск по студентам указанного возраста с указанием минимального и максимального возраста",
            responses = {@ApiResponse(responseCode = "200", description = "Список сформирован"),
                    @ApiResponse(responseCode = "404", description = "Нет студентов указанного возраста")})
    public List<Student> getStudentsBetweenByAge(@PathVariable("ageMin") int ageMin,
                                                 @PathVariable("ageMax") int ageMax) {
        return studentService.filterBetweenByAge(ageMin, ageMax);
    }

    /**
     * Получает студентов указанного возраста.
     *
     * @param age возраст студента
     * @return возвращает список студентов указанного возраста, иначе ошибка 404
     */
    @GetMapping("/age/{age}")
    @Operation(summary = "Поиск по студентам с указанным возрастом",
            description = "Поиск по студентам указанного возраста",
            responses = {@ApiResponse(responseCode = "200", description = "Список сформирован"),
                    @ApiResponse(responseCode = "404", description = "Нет студентов указанного возраста")})
    public List<Student> getStudentsBetweenByAge(@PathVariable("age") int age) {
        return studentService.filterByAge(age);
    }

    /**
     * Получает количество студентов.
     *
     * @return возвращает количество студентов в школе, иначе ошибка 404
     */
    @GetMapping("/count")
    @Operation(summary = "Количество студентов",
            description = "Возвращает количество студентов",
            responses = {@ApiResponse(responseCode = "200", description = "Список сформирован"),
                    @ApiResponse(responseCode = "404", description = "Нет студентов в школе")})
    public ResponseEntity<Integer> getStudentsCount() {
        int count = studentService.getStudentsCount();
        if (count > 0) {
            return ResponseEntity.ok(count);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
        }
    }

    /**
     * Получает средний возраст студентов.
     *
     * @return возвращает средний возраст студентов, иначе ошибка 404
     */
    @GetMapping("/age-average")
    @Operation(summary = "Средний возраст студентов",
            description = "Возвращает средний возраст студентов",
            responses = {@ApiResponse(responseCode = "200", description = "Список сформирован"),
                    @ApiResponse(responseCode = "404", description = "Нет студентов в школе")})
    public int getStudentsAverageAge() {
        return studentService.getStudentsAgeAverage();
    }

    /**
     * Получает список последних пяти добавленных студентов.
     *
     * @return возвращает список пяти студентов
     */
    @GetMapping("/last")
    @Operation(summary = "Список недавно добавленных пяти студентов",
            responses = @ApiResponse(responseCode = "200", description = "Отображение пяти студентов"))
    public List<Student> getLast5Student() {
        return studentService.getStudentsLast5();
    }

    /**
     * Получает имена студентов, начинающихся с буквы "А", в верхнем регистре.
     *
     * @return возвращает список имен студентов, отсортированных в алфавитном порядке
     */
    @GetMapping("/students-names-a")
    @Operation(summary = "Имена студентов на букву 'А'",
            description = "Возвращает список имен студентов, начинающихся с буквы 'А', в верхнем регистре, отсортированный в алфавитном порядке",
            responses = {@ApiResponse(responseCode = "200", description = "Список имен студентов сформирован"),
                    @ApiResponse(responseCode = "404", description = "Нет данных о студентах")})
    public List<String> getStudentsNamesStartingWithA() {
        return studentService.getStudentsStartNameA();
    }

    /**
     * Возвращает средний возраст всех студентов.
     *
     * @return средний возраст студентов, иначе 0
     */
    @GetMapping("/average-age-stream")
    @Operation(summary = "Средний возраст студентов",
            description = "Возвращает средний возраст всех студентов",
            responses = @ApiResponse(responseCode = "200", description = "Средний возраст вычислен"))
    public int getAverageAge() {
        return studentService.getStudentAgeAverageStream();
    }
}
