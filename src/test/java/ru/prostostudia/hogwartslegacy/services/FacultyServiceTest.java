package ru.prostostudia.hogwartslegacy.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.prostostudia.hogwartslegacy.exceptions.*;
import ru.prostostudia.hogwartslegacy.interfaces.StudentService;
import ru.prostostudia.hogwartslegacy.models.Faculty;
import ru.prostostudia.hogwartslegacy.models.Student;
import ru.prostostudia.hogwartslegacy.repository.FacultyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FacultyServiceTest {

    @Mock
    private FacultyRepository facultyRepository;
    @Mock
    private StudentService studentService;

    @InjectMocks
    private FacultyServiceImpl facultyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("add: Успешное добавление нового факультета")
    void addNewFaculty() {
        String name = "Гриффиндор";
        String color = "Красный";

          Faculty savedFaculty = new Faculty(1L, name, color); // Имитируем сохраненный объект с ID

        when(facultyRepository.save(any(Faculty.class))).thenReturn(savedFaculty);

        Faculty result = facultyService.add(name, color);

        assertNotNull(result.getId(), "ID должен быть присвоен после сохранения");
        assertEquals(name, result.getName(), "Имя факультета должно совпадать");
        assertEquals(color, result.getColor(), "Цвет факультета должен совпадать");

        verify(facultyRepository, times(1)).save(any(Faculty.class));
    }

    @Test
    @DisplayName("add(Faculty): Негативный тест дублей и вызова исключения FacultyNameSetAlreadyException")
    void addTwoDoubleRows() {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");

        when(facultyRepository.findByName("Гриффиндор")).thenReturn(Optional.of(faculty));
        assertThrows(FacultyNameSetAlreadyException.class,() -> facultyService.add(faculty));
        verify(facultyRepository, times(1)).findByName("Гриффиндор");
    }

    @ParameterizedTest
    @DisplayName("add(Faculty): Негативный тест некорректных значений Вызов исключения FacultyIllegalParameterException")
    @MethodSource("parametersNegativeNameColorForMethodTest")
    void addThrowFacultyIllegalParameterException(String message, Long id, String name, String color) {
        assertThrows(FacultyIllegalParameterException.class, () -> facultyService.add(new Faculty(id, name, color)));
    }

    @ParameterizedTest
    @DisplayName("add(String): Негативный тест некорректных значений Вызов исключения FacultyIllegalParameterException")
    @MethodSource("parametersNegativeNameColorForMethodTest")
    void addStringThrowFacultyIllegalParameterException(String message, Long id, String name, String color) {
        assertThrows(FacultyIllegalParameterException.class, () -> facultyService.add(name, color));
    }

    @Test
    @DisplayName("get(Long id): Позитивный тест получения значения записи")
    void get() {
        List<Faculty> facultyExpected = List.of(
                new Faculty(1L,"Гриффиндор", "Красный"),
                new Faculty(2L, "Слизерин", "Зеленый")
        );

        when(facultyRepository.findAll()).thenReturn(facultyExpected);
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(facultyExpected.get(0)));
        when(facultyRepository.findById(2L)).thenReturn(Optional.of(facultyExpected.get(1)));

        List<Faculty> faculties = facultyService.getAll();
        assertEquals(faculties.size(), 2, "Размер в 2 строки: провален");
        assertEquals(facultyService.get(1L).getName(), "Гриффиндор", "Корректность добавленных значений 0 Имени: провалено");
        assertEquals(facultyService.get(1L).getColor(), "Красный", "Корректность добавленных значений 0 Цвета: провалено");
        assertEquals(facultyService.get(2L).getName(), "Слизерин", "Корректность добавленных значений 1 Имени: провалено");
        assertEquals(facultyService.get(2L).getColor(), "Зеленый", "Корректность добавленных значений 1 Цвета: провалено");
    }

    @Test
    @DisplayName("get(Long id): Негативный тест получение значения, нет Id. Исключение FacultyNotFoundException")
    void getThrow() {
        assertThrows(FacultyNotFoundException.class, () -> facultyService.get(20L));
    }


    @Test
    @DisplayName("remove(Long id): Тест удаления записи - корректность значения удаленной позиции")
    void removeCorrectReturn() {
        List<Faculty> faculties = List.of(
                new Faculty(1L, "Гриффиндор", "Красный"),
                new Faculty(2L, "Слизерин", "Зеленый")
        );
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculties.get(0)));
        when(studentService.getStudentFaculty(1L)).thenReturn(null);
        facultyService.remove(1L);

        verify(facultyRepository,times(1)).deleteById(1L);

    }

    @Test
    @DisplayName("remove(Long id): Тест удаления записи - некорректный id")
    void removeThrows() {

        when(facultyRepository.findById(20L)).thenThrow(FacultyNotFoundException.class);
        assertThrows(FacultyNotFoundException.class, () -> facultyService.remove(20L));
    }

    @Test
    @DisplayName("remove(Long id): Тест удаления записи из пустой базы - некорректный id")
    void removeThrowsIsEmpty() {
        assertThrows(FacultyNotFoundException.class, () -> facultyService.remove(1L));
    }

    @Test
    @DisplayName("edit(Faculty): Тест редактирования - корректность присвоения")
    void edit() {
        Faculty facultyExcepted = new Faculty(1L, "Гриффиндор", "Красный");
        Faculty faculty = new Faculty(1L, "Слизерин2","Зеленый");
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(facultyExcepted));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        assertEquals(faculty,facultyService.edit(faculty));
        verify(facultyRepository,times(1)).save(faculty);
    }

    @Test
    @DisplayName("edit(Faculty): Тест редактирования - повторное добавление имени FacultyNameSetAlreadyException")
    void editThrow() {
        Faculty facultyExcepted = new Faculty(1L, "Слизерин", "Зеленый");
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(facultyExcepted));
        when(facultyRepository.findByName("Слизерин")).thenReturn(Optional.of(facultyExcepted));

        assertThrows(FacultyNameSetAlreadyException.class, ()-> facultyService.edit(facultyExcepted));
    }

    @ParameterizedTest
    @DisplayName("edit(Faculty): Тест ошибок - попытка присвоить пустые значения - ошибка FacultyIllegalParameterException ")
    @MethodSource("parametersNegativeNameColorForMethodTest")
    void editThrowFacultyIllegalParameterException(String message, Long id, String name, String color) {
        facultyService.add("1","2");
        Faculty faculty = new Faculty(1L, name, color);

        assertThrows(FacultyIllegalParameterException.class, () -> facultyService.edit(faculty));
    }

    @Test
    @DisplayName("find(String name): Тест редактирования - корректность поиска")
    void find() {
        Faculty faculty = new Faculty(2L, "Слизерин","Зеленый");
        when(facultyRepository.findByName("Слизерин")).thenReturn(Optional.of(faculty));

        Long id = facultyService.find("Слизерин").getId();
        assertEquals(2, id);
    }

    @Test
    @DisplayName("find(String name): Тест редактирования - ошибка поиска \"не найдено\": FacultyNotFoundException")
    void findThrows() {
        facultyService.add("Гриффиндор", "Красный");
        facultyService.add("Слизерин", "Зеленый");

        assertThrows(FacultyNotFoundException.class, ()-> facultyService.find("Слизерин2"));
    }


    @Test
    @DisplayName("getAll: Получить полный список факультетов")
    void getAll() {

        List<Faculty> facultyExpected = List.of(
                new Faculty(1L,"Гриффиндор", "Красный"),
                new Faculty(2L, "Слизерин", "Зеленый"));
        when(facultyRepository.findAll()).thenReturn(facultyExpected);
        List<Faculty> facultyActual = facultyService.getAll();

        Faculty facultyExpected1 = new Faculty(1L,"Гриффиндор", "Красный");
        Faculty facultyExpected2 = new Faculty(2L, "Слизерин", "Зеленый");
        assertEquals(facultyExpected1,facultyActual.get(0),"Не совпала строка 1");
        assertEquals(facultyExpected2,facultyActual.get(1),"Не совпала строка 2");
    }

    @Test
    @DisplayName("getStudents: Получить всех студентов в факультете")
    void getStudents() {
        Faculty faculty = new Faculty(1L,"Гриффиндор", "Красный");
        Student student = new Student(1L, "123",10);
        student.setFaculty(faculty);
        when(studentService.filterByFaculty(1L)).thenReturn(List.of(student));

        assertEquals(List.of(student),facultyService.getStudents(1L));
        verify(studentService,times(1)).filterByFaculty(1L);
    }

    @Test
    @DisplayName("filterByColor: Фильтр по цвету")
    void filterByColor() {
        List<Faculty> redColor = List.of(new Faculty(1L,"Гриффиндор", "Красный"));
        List<Faculty> greenColor = List.of(
                new Faculty(2L,"Слизерин", "Зеленый"),
                new Faculty(3L,"Олеги", "Зеленый")
        );
        when(facultyRepository.findByColorIgnoreCase("зеленый")).thenReturn(greenColor);
        verify(facultyRepository,times(1)).findByColorIgnoreCase("зеленый");
    }



    private static Stream<Arguments> parametersNegativeNameColorForMethodTest() {
        return Stream.of(
                Arguments.of("Тест исключения:Всё пустое", 1L, "", ""),
                Arguments.of("Тест исключения:Всё null", 1L, null, null),
                Arguments.of("Тест исключения:null в имени", 1L, null, "Красный"),
                Arguments.of("Тест исключения:null в цвете", 1L, "Виталий", null)
        );
    }
}
