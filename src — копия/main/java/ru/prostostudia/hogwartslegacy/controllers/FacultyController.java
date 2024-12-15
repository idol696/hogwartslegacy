package ru.prostostudia.hogwartslegacy.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import ru.prostostudia.hogwartslegacy.interfaces.FacultyService;
import ru.prostostudia.hogwartslegacy.models.Faculty;
import ru.prostostudia.hogwartslegacy.models.Student;


import java.util.List;

@RestController
@RequestMapping("/faculty")
@Tag(name = "Факультеты", description = "Факультеты студентов")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    /**
     * @return список всех факультетов
     */

    @GetMapping()
    @Operation(summary = "Список факультетов",
            description = "Возвращает список всех факультетов",
            responses = @ApiResponse(responseCode = "200", description = "Факультеты получены"))
    public List<Faculty> getAllFaculty() {
        List<Faculty> faculties = facultyService.getAll();
        System.out.println("Faculties from controller: " + faculties);
        return facultyService.getAll();
    }


    /**
     * @param id идентификатор факультета
     * @return возвращает факультет, если найден, в случае ошибки возвращает код 404
     */

    @GetMapping("/{id}")
    @Operation(summary = "Получает факультет",
            description = "Получает факультет по id",
            responses = {@ApiResponse(responseCode = "404", description = "Неправильный id"),
                    @ApiResponse(responseCode = "200", description = "Значение получено")})
    public Faculty getFaculty(@PathVariable long id) {
        return facultyService.get(id);
    }


    /**
     * @param faculty факультет для добавления
     * @return возвращает созданный факультет, в случае ошибки возвращает код 400
     * Ошибкой является повторное добавление name(с соблюдением регистра) или
     * пустое name, а также null
     */

    @PostMapping("/add")
    @Operation(summary = "Создает факультет",
            description = "Создает новый факультет",
            responses = {@ApiResponse(responseCode = "200", description = "Факультет создан"),
                    @ApiResponse(responseCode = "400", description = "Неправильный запрос или повторное добавление")})
    public Long addFaculty(@RequestBody Faculty faculty) {
        return facultyService.add(faculty);
    }

    /**
     * @param faculty факультет для обновления
     *                id по которому ищется факультет должен быть указан в id Faculty сущности факультета
     * @return новый факультет, в случае неправильного id возвращает 404, повторного значения имени 400
     */

    @PutMapping("/edit")
    @Operation(summary = "Изменяет факультет",
            description = "Изменяет факультет",
            responses = {@ApiResponse(responseCode = "400", description = "Неправильный запрос"),
                    @ApiResponse(responseCode = "404", description = "Факультет не найден"),
                    @ApiResponse(responseCode = "200", description = "Факультет изменен")})
    public Faculty editFaculty(@RequestBody Faculty faculty) {
        return facultyService.edit(faculty);
    }

    /**
     * @param id факультета для удаления
     *           404, если факультет с заданным id не был найден
     */

    @DeleteMapping("/remove/{id}")
    @Operation(summary = "Удаляет факультет",
            description = "Удаляет факультет по id",
            responses = {@ApiResponse(responseCode = "404", description = "Факультет для удаления не найден"),
                    @ApiResponse(responseCode = "409", description = "Невозможно удалить, есть студенты"),
                    @ApiResponse(responseCode = "200", description = "Факультет удален")})
    public void deleteFaculty(@PathVariable("id") long id) {
        facultyService.remove(id);
    }

    /**
     * @param color цвет, по которому будет поиск для фильтрации. String
     * @return Метод filterByColor возвращает список Faculty (тип List<Faculty>), которые имеют заданный цвет (color),
     * при отсутствии указанных цветов выдает ошибку 404, цвета чувствительны к регистру
     */

    @GetMapping("/color/{color}")
    @Operation(summary = "Ищет по цвету",
            description = "Ищет факультет по цвету, возвращает список",
            responses = {@ApiResponse(responseCode = "404", description = "Факультет не найден"),
                    @ApiResponse(responseCode = "200", description = "Факультет найден")})
    public List<Faculty> filterColor(@PathVariable("color") String color) {
        return facultyService.filterByColor(color);
    }

    /**
     * @param id факультета для отображения всех студентов факультета
     * @return Метод getStudents возвращает список Students (тип List<Students>), которые принадлежат факультету,
     * при отсутствии указанных цветов выдает ошибку 404
     */
    @GetMapping("/students/{id}")
    @Operation(summary = "Список студентов факультета",
            description = "Ищет студентов по id факультета",
            responses = {@ApiResponse(responseCode = "404", description = "Студенты факультета не найден"),
                    @ApiResponse(responseCode = "200", description = "Студенты факультета")})
    public List<Student> filterStudentsByFaculty(@PathVariable("id") long id) {
        return facultyService.getStudents(id);
    }
}