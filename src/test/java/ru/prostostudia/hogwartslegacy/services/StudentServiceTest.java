package ru.prostostudia.hogwartslegacy.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.prostostudia.hogwartslegacy.exceptions.*;
import ru.prostostudia.hogwartslegacy.interfaces.StudentService;
import ru.prostostudia.hogwartslegacy.models.Student;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class StudentServiceTest  {
    
    StudentService studentService = new StudentServiceImpl();

    @Test
    @DisplayName("add(Student): Позитивный тест добавления и корректности значения записей")
    void addTwoRows() {
        Student student = new Student(1L, "Олег", 18);
        studentService.add(student);
        studentService.add(new Student(2L, "Олеговна", 22));

        List<Student> faculties = studentService.getAll();
        assertEquals(faculties.size(), 2, "Размер в 2 строки: провален");
        assertEquals(student, faculties.get(0), "Корректность добавленных значений: провалено");
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
        studentService.add("Олег", 18);
        studentService.add("Олеговна", 22);

        List<Student> faculties = studentService.getAll();
        assertEquals(faculties.size(), 2, "Размер в 2 строки: провален");
        assertEquals(faculties.get(0).getName(), "Олег", "Корректность добавленных значений Имени: провалено");
        assertEquals(faculties.get(0).getAge(), 18, "Корректность добавленных значений Возраста: провалено");
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
        studentService.add("Олег", 18);
        studentService.add("Олеговна", 22);

        List<Student> students = studentService.getAll();
        assertEquals(students.size(), 2, "Размер в 2 строки: провален");
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
        studentService.add("Олег", 18);

        studentService.remove(1L);
        assertTrue(studentService.getAll().isEmpty());
    }

    @Test
    @DisplayName("remove(Long id): Тест удаления записи - корректность значения удаленной позиции")
    void removeCorrectReturn() {
        studentService.add("Олег", 18);
        studentService.add("Олеговна", 22);

        studentService.remove(1L);

        assertEquals(1, studentService.getAll().size());
    }

    @Test
    @DisplayName("remove(Long id): Тест удаления записи - некорректный id")
    void removeThrows() {
        studentService.add("Олег", 18);
        studentService.add("Олеговна", 22);

        assertThrows(StudentNotFoundException.class, () -> studentService.remove(20L));
    }

    @Test
    @DisplayName("remove(Long id): Тест удаления записи из пустой базы - некорректный id")
    void removeThrowsIsEmpty() {
        assertThrows(StudentNotFoundException.class, () -> studentService.remove(1L));
    }

    @Test
    @DisplayName("edit(Student): Тест редактирования - корректность присвоения")
    void edit() {
        studentService.add("Олег", 18);
        Student student = new Student(1L, "Олеговна",22);
        studentService.edit(student);
        assertEquals(student,studentService.get(1L));
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
        studentService.add("Олег", 18);
        studentService.add("Олеговна", 22);

        Long id = studentService.filterByName("Олеговна").get(0).getId();
        assertEquals(2, id);
    }

    @Test
    @DisplayName("filterByAge(Integer age): Тест редактирования - корректность фильтра по возрасту")
    void filterByAge() {
        studentService.add("Олег", 18);
        studentService.add("Олеговна", 22);

        Long id = studentService.filterByAge(22).get(0).getId();
        assertEquals(2, id);
    }

    @Test
    @DisplayName("filterByAge(Integer minAge maxAge): Тест редактирования - корректность фильтра по диапазону возраста")
    void filterBetweenByAge() {
        studentService.add("Олег", 18);
        studentService.add("Олеговна", 22);
        studentService.add("Олеговна", 23);

        Long id = studentService.filterBetweenByAge(18,22).get(1).getId();
        assertEquals(2, id);
    }

    @Test
    @DisplayName("getAll: Получить полный список студентов")
    void getAll() {
        studentService.add("Олег", 18);
        studentService.add("Олеговна", 22);
        List<Student> studentActual = studentService.getAll();

        Student studentExpected1 = new Student(1L,"Олег", 18);
        Student studentExpected2 = new Student(2L, "Олеговна", 22);
        assertEquals(studentExpected1,studentActual.get(0),"Не совпала строка 1");
        assertEquals(studentExpected2,studentActual.get(1),"Не совпала строка 2");
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
