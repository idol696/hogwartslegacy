package ru.prostostudia.hogwartslegacy.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.prostostudia.hogwartslegacy.exceptions.*;
import ru.prostostudia.hogwartslegacy.interfaces.FacultyService;
import ru.prostostudia.hogwartslegacy.models.Faculty;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class FacultyServiceTest {

    FacultyService facultyService = new FacultyServiceImpl();

    @Test
    @DisplayName("add(Faculty): Позитивный тест добавления и корректности значения записей")
    void addTwoRows() {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");
        facultyService.add(faculty);
        facultyService.add(new Faculty(2L, "Слизерин", "Зеленый"));

        List<Faculty> faculties = facultyService.getAll();
        assertEquals(faculties.size(), 2, "Размер в 2 строки: провален");
        assertEquals(faculty, faculties.get(0), "Корректность добавленных значений: провалено");
    }

    @Test
    @DisplayName("add(Faculty): Негативный тест дублей и вызова исключения FacultyNameSetAlreadyException")
    void addTwoDoubleRows() {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");
        facultyService.add(faculty);

        assertThrows(FacultyNameSetAlreadyException.class,
                () -> facultyService.add(new Faculty(2L, "Гриффиндор", "Красный")));
    }

    @ParameterizedTest
    @DisplayName("add(Faculty): Негативный тест некорректных значений Вызов исключения FacultyIllegalParameterException")
    @MethodSource("parametersNegativeNameColorForMethodTest")
    void addThrowFacultyIllegalParameterException(String message, Long id, String name, String color) {
        assertThrows(FacultyIllegalParameterException.class, () -> facultyService.add(new Faculty(id, name, color)));
    }

    @Test
    @DisplayName("add(String): Позитивный тест добавления и корректности значения записей")
    void addStringTwoRows() {
        facultyService.add("Гриффиндор", "Красный");
        facultyService.add("Слизерин", "Зеленый");

        List<Faculty> faculties = facultyService.getAll();
        assertEquals(faculties.size(), 2, "Размер в 2 строки: провален");
        assertEquals(faculties.get(0).getName(), "Гриффиндор", "Корректность добавленных значений Имени: провалено");
        assertEquals(faculties.get(0).getColor(), "Красный", "Корректность добавленных значений Цвета: провалено");
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
        facultyService.add("Гриффиндор", "Красный");
        facultyService.add("Слизерин", "Зеленый");

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
    @DisplayName("remove(Long id): Тест удаления записи")
    void removeOneRecord() {
        facultyService.add("Гриффиндор", "Красный");

        facultyService.remove(1L);
        assertTrue(facultyService.getAll().isEmpty());
    }

    @Test
    @DisplayName("remove(Long id): Тест удаления записи - корректность значения удаленной позиции")
    void removeCorrectReturn() {
        facultyService.add("Гриффиндор", "Красный");
        facultyService.add("Слизерин", "Зеленый");

        facultyService.remove(1L);

        assertEquals(1, facultyService.getAll().size());
    }

    @Test
    @DisplayName("remove(Long id): Тест удаления записи - некорректный id")
    void removeThrows() {
        facultyService.add("Гриффиндор", "Красный");
        facultyService.add("Слизерин", "Зеленый");

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
        facultyService.add("Гриффиндор", "Красный");
        Faculty faculty = new Faculty(1L, "Слизерин","Зеленый");
        facultyService.edit(faculty);
        assertEquals(faculty,facultyService.get(1L));
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
        facultyService.add("Гриффиндор", "Красный");
        facultyService.add("Слизерин", "Зеленый");

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
        facultyService.add("Гриффиндор", "Красный");
        facultyService.add("Слизерин", "Зеленый");
        List<Faculty> facultyActual = facultyService.getAll();

        Faculty facultyExpected1 = new Faculty(1L,"Гриффиндор", "Красный");
        Faculty facultyExpected2 = new Faculty(2L, "Слизерин", "Зеленый");
        assertEquals(facultyExpected1,facultyActual.get(0),"Не совпала строка 1");
        assertEquals(facultyExpected2,facultyActual.get(1),"Не совпала строка 2");
    }

    @Test
    @DisplayName("getAll: Получить весь список факультетов, если он пуст")
    void getAllEmpty() {
        assertTrue(facultyService.getAll().isEmpty());
    }

    @Test
    @DisplayName("filterByColor: Фильтр по цвету")
    void filterByColor() {
        facultyService.add("Гриффиндор", "Красный");
        facultyService.add("Слизерин", "Зеленый");
        facultyService.add("Олеги", "Зеленый");
        List<Faculty> facultyActual = facultyService.filterByColor("Зеленый");

        Faculty facultyExpected1 = new Faculty(2L,"Слизерин", "Зеленый");
        Faculty facultyExpected2 = new Faculty(3L, "Олеги", "Зеленый");
        assertEquals(facultyExpected1,facultyActual.get(0),"Не совпала строка 1");
        assertEquals(facultyExpected2,facultyActual.get(1),"Не совпала строка 2");
    }

    @Test
    @DisplayName("filterByColor: Получить весь список факультетов, если он пуст")
    void filterByColorEmpty() {
        assertTrue(facultyService.filterByColor("Зеленый").isEmpty());
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
