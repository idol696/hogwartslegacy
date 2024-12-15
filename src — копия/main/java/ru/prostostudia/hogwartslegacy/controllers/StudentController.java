package ru.prostostudia.hogwartslegacy.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
     * @param id идентификатор студента
     * @return возвращает id студента, если найден, иначе 404
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
     * @param id идентификатор студента
     * @return возвращает Факультет, если найден, иначе 404
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
     * @param student набор параметров студента для добавления
     * @return возвращает установленный id
     */
    @PostMapping("/add")
    @Operation(summary = "Добавляет студента",
            description = "Добавляет студента и устанавливает id",
            responses = @ApiResponse(responseCode = "200", description = "Студент добавлен"))
    public Long createStudent(@RequestBody Student student) {
        return studentService.add(student);
    }

    /**
     * @param id      идентификатор студента
     * @param student студент для обновления
     * @return возвращает нового студента, если не найден по id, то ошибка 404
     * Если ошибка в параметрах (неправильный возраст, или имя) то ошибка 400
     */
    @PutMapping("/edit/{id}")
    @Operation(summary = "Редактирует студента",
            description = "Редактирует студента по его id",
            responses = {@ApiResponse(responseCode = "404", description = "Студент не найден"),
                    @ApiResponse(responseCode = "400", description = "Неправильные параметры"),
                    @ApiResponse(responseCode = "200", description = "Студент изменен")})
    public Student editStudent(@PathVariable("id") long id,
                               @RequestBody Student student) {
        return studentService.edit(student);
    }

    /**
     * @param id идентификатор студента
     *
     */
    @GetMapping("/remove/{id}")
    @Operation(summary = "Удаляет студента",
            responses = {@ApiResponse(responseCode = "400", description = "Студент не найден по id"),
                    @ApiResponse(responseCode = "200", description = "Студент удален")})

    public void deleteStudent(@PathVariable("id") long id) {
        studentService.remove(id);
    }

    /**
     * @return возвращает список студентов
     */
    @GetMapping()
    @Operation(summary = "Список всех студентов",
            responses = @ApiResponse(responseCode = "200", description = "Отображение всех студентов"))
    public List<Student> getAllStudent() {
        return studentService.getAll();
    }

    /**
     * @param ageMin параметр начального возраст студента
     * @param ageMax параметр конечного возраста студента
     *               оба этих параметра формируют диапазон
     * @return возвращает коллекцию по указанному возрасту, если ничего не найдено,
     * то будет 404
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
     * @param age параметр возраст студента
     * @return возвращает коллекцию по указанному возрасту, если ничего не найдено,
     * то будет 404
     */

    @GetMapping("/age/{age}")
    @Operation(summary = "Поиск по студентам с указанным возрастом",
            description = "Поиск по студентам указанного возраста",
            responses = {@ApiResponse(responseCode = "200", description = "Список сформирован"),
                    @ApiResponse(responseCode = "404", description = "Нет студентов указанного возраста")})
    public List<Student> getStudentsBetweenByAge(@PathVariable("age") int age) {
        return studentService.filterByAge(age);
    }
}
