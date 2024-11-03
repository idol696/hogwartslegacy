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
import ru.prostostudia.hogwartslegacy.models.Faculty;
import ru.prostostudia.hogwartslegacy.models.Student;
import ru.prostostudia.hogwartslegacy.repository.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentServiceTest  {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("add(Student): Позитивный тест добавления и корректности значения записей")
    void addTwoRows() {
        List<Student> studentsExpected = List.of(new Student(1L, "Олег", 18),new Student(2L, "Олеговна", 22));
        Student student = new Student(1L, "Олег", 18);
        when(studentRepository.findAll()).thenReturn(studentsExpected);

        List<Student> studentsActual = studentService.getAll();
        assertEquals(studentsActual.size(), 2, "Размер в 2 строки: провален");
        assertEquals(student, studentsActual.get(0), "Корректность добавленных значений: провалено");
    }

    @ParameterizedTest
    @DisplayName("add(Student): Негативный тест некорректных значений Вызов исключения StudentIllegalParameterException")
    @MethodSource("parametersNegativeNameAgeForMethodTest")
    void addThrowStudentIllegalParameterException(String message, Long id, String name, Integer age) {
        assertThrows(StudentIllegalParameterException.class, () -> studentService.add(new Student(id, name, age)));
    }

    @Test
    @DisplayName("add(String): Позитивный тест добавления и корректности значения записей")
    void addStringTwoRows() {
        List<Student> studentsExpected = List.of(new Student(1L, "Олег", 18),new Student(2L, "Олеговна", 22));
        when(studentRepository.findAll()).thenReturn(studentsExpected);

        List<Student> studentsActual = studentService.getAll();
        assertEquals(studentsActual.size(), 2, "Размер в 2 строки: провален");
        assertEquals(studentsActual.get(0).getName(), "Олег", "Корректность добавленных значений Имени: провалено");
        assertEquals(studentsActual.get(0).getAge(), 18, "Корректность добавленных значений Возраста: провалено");
    }

    @ParameterizedTest
    @DisplayName("add(String): Негативный тест некорректных значений Вызов исключения StudentIllegalParameterException")
    @MethodSource("parametersNegativeNameAgeForMethodTest")
    void addStringThrowStudentIllegalParameterException(String message, Long id, String name, Integer age) {
        assertThrows(StudentIllegalParameterException.class, () -> studentService.add(name, age));
    }

    @Test
    @DisplayName("get(Long id): Позитивный тест получения значения записи")
    void get() {
        List<Student> studentsExpected = List.of(new Student(1L, "Олег", 18),new Student(2L, "Олеговна", 22));
        when(studentRepository.findAll()).thenReturn(studentsExpected);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(studentsExpected.get(0)));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(studentsExpected.get(1)));

        List<Student> studentsActual = studentService.getAll();
        assertEquals(studentsActual.size(), 2, "Размер в 2 строки: провален");
        assertEquals(studentService.get(1L).getName(), "Олег", "Корректность добавленных значений 0 Имени: провалено");
        assertEquals(studentService.get(1L).getAge(), 18, "Корректность добавленных значений 0 Возраста: провалено");
        assertEquals(studentService.get(2L).getName(), "Олеговна", "Корректность добавленных значений 1 Имени: провалено");
        assertEquals(studentService.get(2L).getAge(), 22, "Корректность добавленных значений 1 Возраста: провалено");
    }

    @Test
    @DisplayName("get(Long id): Негативный тест получение значения, нет Id. Исключение StudentNotFoundException")
    void getThrow() {
        assertThrows(StudentNotFoundException.class, () -> studentService.get(20L));
    }

    @Test
    @DisplayName("remove(Long id): Тест удаления записи")
    void removeOneRecord() {
        List<Student> studentsExpected = List.of(new Student(1L, "Олег", 18),new Student(2L, "Олеговна", 22));
        when(studentRepository.findAll()).thenReturn(studentsExpected);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(studentsExpected.get(0)));

        studentService.remove(1L);
        verify(studentRepository,times(1)).deleteById(1L);
        verify(studentRepository,times(1)).findById(1L);
    }

    @Test
    @DisplayName("remove(Long id): Тест удаления записи - некорректный id")
    void removeThrows() {

        assertThrows(StudentNotFoundException.class, () -> studentService.remove(20L));
    }

    @Test
    @DisplayName("edit(Student): Тест редактирования - корректность присвоения")
    void edit() {
        Student studentExpected = new Student(1L, "Олег", 18);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(studentExpected));
        Student studentActual = new Student(1L, "Олеговна",22);
        when(studentRepository.save(argThat(f -> f.getId() == 1L))).thenReturn(studentActual);
        studentService.edit(studentActual);
        verify(studentRepository,times(1)).save(argThat(f -> f.getId() == 1L));
        assertEquals(studentExpected,studentService.get(1L));
    }

    @ParameterizedTest
    @DisplayName("edit(Student): Тест ошибок - попытка присвоить пустые значения - ошибка StudentIllegalParameterException ")
    @MethodSource("parametersNegativeNameAgeForMethodTest")
    void editThrowStudentIllegalParameterException(String message, Long id, String name, Integer age) {
        studentService.add("1",2);
        Student student = new Student(1L, name, age);

        assertThrows(StudentIllegalParameterException.class, () -> studentService.edit(student));
    }

    @Test
    @DisplayName("filterByName(String name): Тест редактирования - корректность фильтра по имени")
    void filterByName() {
        List<Student> studentsExpected = List.of(new Student(1L, "Олег", 18),new Student(2L, "Олеговна", 22));
        when(studentRepository.findAll()).thenReturn(studentsExpected);
        Long id = studentService.filterByName("Олеговна").get(0).getId();
        assertEquals(2, id);
    }

    @Test
    @DisplayName("filterByAge(Integer age): Тест редактирования - корректность фильтра по возрасту")
    void filterByAge() {
        List<Student> studentsExpected = List.of(new Student(1L, "Олег", 18),new Student(2L, "Олеговна", 22));
        when(studentRepository.findAll()).thenReturn(studentsExpected);

        Long id = studentService.filterByAge(22).get(0).getId();
        assertEquals(2, id);
    }

    @Test
    @DisplayName("filterByAge(Integer minAge maxAge): Тест редактирования - корректность фильтра по диапазону возраста")
    void filterBetweenByAge() {
        List<Student> studentsExpected = List.of(
                new Student(1L, "Олег", 18),
                new Student(2L, "Олеговна", 22),
                new Student(3L, "Олеговна", 23));
        when(studentRepository.findByAgeBetween(18,22)).thenReturn(studentsExpected);

        Long id = studentService.filterBetweenByAge(18,22).get(1).getId();
        assertEquals(2, id);
    }

    @Test
    @DisplayName("getAll: Получить полный список студентов")
    void getAll() {
        List<Student> studentsExpected = List.of(new Student(1L, "Олег", 18),new Student(2L, "Олеговна", 22));
        when(studentRepository.findAll()).thenReturn(studentsExpected);
        List<Student> studentActual = studentService.getAll();

        Student studentExpected1 = new Student(1L,"Олег", 18);
        Student studentExpected2 = new Student(2L, "Олеговна", 22);
        assertEquals(studentExpected1,studentActual.get(0),"Не совпала строка 1");
        assertEquals(studentExpected2,studentActual.get(1),"Не совпала строка 2");
    }

    @Test
    @DisplayName("getFaculty : Получить факультет студента")
    void getFaculty() {
        List<Student> students = List.of(
                new Student(1L, "Олег", 18));
        Faculty faculty = new Faculty(1L,"123","123");
        students.get(0).setFaculty(faculty);

        assertEquals(faculty, students.get(0).getFaculty());
    }

    private static Stream<Arguments> parametersNegativeNameAgeForMethodTest() {
        return Stream.of(
                Arguments.of("Тест исключения:Всё пустое", 1L, "", -1),
                Arguments.of("Тест исключения:Всё null", 1L, null, null),
                Arguments.of("Тест исключения:null в имени", 1L, null, 18),
                Arguments.of("Тест исключения:null в возрасте", 1L, "Олег", null),
                Arguments.of("Тест исключения:0 в возрасте", 1L, "Олег", 0)
        );
    }
}
