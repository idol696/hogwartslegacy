package ru.prostostudia.hogwartslegacy.controllers.old_version;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.prostostudia.hogwartslegacy.models.Faculty;
import ru.prostostudia.hogwartslegacy.models.Student;
import ru.prostostudia.hogwartslegacy.repository.FacultyRepository;
import ru.prostostudia.hogwartslegacy.repository.StudentRepository;

import java.util.Arrays;
import java.util.List;

// для подключения сторонних SQL скриптов в H2
import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

// Используем JUNIT Assertions
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FacultyControllerTest {
    final Faculty gryffindor = new Faculty(1L, "Gryffindor", "Red");
    final Faculty slytherin = new Faculty(2L, "Slytherin", "Green");
    final Student harry = new Student(1L, "Harry Potter", 17);
    final Student draco = new Student(2L, "Draco Malfoy", 18);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    DataSource dataSource;

    @BeforeEach
    void setUp() {
        facultyRepository.saveAll(List.of(gryffindor, slytherin));
        harry.setFaculty(gryffindor);
        draco.setFaculty(slytherin);
        studentRepository.saveAll(List.of(harry, draco));
    }

    void runSQL(String sqlString) {
        ResourceDatabasePopulator popular = new ResourceDatabasePopulator(
                new ClassPathResource(sqlString)
        );
        popular.execute(dataSource);
    }

    void clearDatabaseH2() {
        runSQL("h2-clear-database.sql");
    }

    void deleteHarryFromH2() {
        runSQL("h2-delete-harry-potter.sql");
    }

    @Test
    void testGetAllFaculties() {
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity("/faculty", Faculty[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть OK");
        Faculty[] facultiesArray = response.getBody();
        assertNotNull(facultiesArray, "Массив факультетов не должен быть null");
        List<Faculty> faculties = Arrays.asList(facultiesArray);
        List<Faculty> expectedFaculties = List.of(gryffindor, slytherin);
        assertFalse(faculties.isEmpty(), "Список факультетов не должен быть пустым");
        assertEquals(expectedFaculties, faculties, "Список факультетов не совпадает");
    }

    @Test
    void testGetAllFaculties_EmptyValid() {
        clearDatabaseH2();
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity("/faculty", Faculty[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть OK");
        Faculty[] facultiesArray = response.getBody();
        assertNotNull(facultiesArray, "Массив факультетов не должен быть null");
        List<Faculty> faculties = Arrays.asList(facultiesArray);
        assertTrue(faculties.isEmpty(), "Список факультетов должен быть пустым");
        setUp();
    }

    @Test
    void testGetFacultyById() {
        Faculty faculty = restTemplate.getForObject("/faculty/1", Faculty.class);
        assertEquals(gryffindor, faculty, "Факультеты не совпадают");
    }

    @Test
    void testGetFacultyById_404_NotFound() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/faculty/999",
                HttpMethod.GET,
                null,
                Void.class
        );
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND,"Факультет не должен быть найден");
    }

    @Test
    void testAddFaculty() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/faculty/add", new Faculty(null, "Dolby", "Red"), String.class
        );
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        Long facultyId = Long.valueOf(response.getBody());
        assertEquals(3L, facultyId, "Некорректное добавление факультета");
        clearDatabaseH2();
        setUp();
    }

    @Test
    void testAddFaculty_400_AlreadyExists() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/faculty/add", new Faculty(null, "Gryffindor","None"), String.class
        );
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST,"Имя факультета на должно повторятся");
        clearDatabaseH2();
        setUp();
    }

    @Test
    void testEditFaculty() {

        Faculty expectedFaculty = new Faculty(1L, "Gryffindor Update", "Green");
        HttpEntity<Faculty> request = new HttpEntity<>(expectedFaculty);
        ResponseEntity<Faculty> response = restTemplate.exchange(
                "/faculty/edit",
                HttpMethod.PUT,
                request,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Faculty updatedFaculty = response.getBody();
        assertEquals(expectedFaculty, updatedFaculty, "Изменений факультета не произошло");
    }

    @Test
    void testEditFaculty_404_NotFound() {

        Faculty expectedFaculty = new Faculty(999L, "Gryffindor Update", "Green");
        HttpEntity<Faculty> request = new HttpEntity<>(expectedFaculty);
        ResponseEntity<Faculty> response = restTemplate.exchange(
                "/faculty/edit",
                HttpMethod.PUT,
                request,
                Faculty.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testRemoveFaculty() {
        deleteHarryFromH2();
        ResponseEntity<Void> response = restTemplate.exchange(
                "/faculty/remove/1",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Удаление должно завершиться успешно");
        boolean facultyExists = facultyRepository.findById(1L).isPresent();
        assertFalse(facultyExists, "Факультет с ID 1 должен быть удалён");
        clearDatabaseH2();
        setUp();
    }

    @Test
    void testRemoveFaculty_409_Conflict() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/faculty/remove/1",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "Должно быть 409 - есть студент на факультете");
    }

    @Test
    void testRemoveFaculty_404_NotFound() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/faculty/remove/999",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Должно быть 404 - факультет не найден");
    }

    @Test
    void testGetFacultiesByColor() {
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity("/faculty/color/red", Faculty[].class);
        assertEquals(HttpStatus.OK,response.getStatusCode(),"Статус должен быть OK 200");
        assertNotNull(response.getBody(),"Возвращаемое значение не должно быть Null");
        Faculty[] facultiesArray = response.getBody();
        List<Faculty> faculties = Arrays.asList(facultiesArray);
        List<Faculty> expectedFaculties = List.of(gryffindor);
        assertEquals(expectedFaculties, faculties, "Список факультетов c колором Red не совпадает");
    }

    @Test
    void testGetFacultiesByColor_EmptyValid() {
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity("/faculty/color/none", Faculty[].class);
        assertEquals(HttpStatus.OK,response.getStatusCode(),"Статус должен быть OK 400");
        assertNotNull(response.getBody(),"Возвращаемое значение не должно быть Null");
        Faculty[] facultiesArray = response.getBody();
        assertTrue(Arrays.asList(facultiesArray).isEmpty());
    }

    @Test
    void testGetStudentsByFacultyId() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity("/faculty/students/1", Student[].class);
        assertEquals(HttpStatus.OK,response.getStatusCode(),"Статус должен быть OK 400");
        assertNotNull(response.getBody(),"Возвращаемое значение не должно быть Null");
        Student[] studentsArray = response.getBody();
        List<Student> exceptedStudents = List.of(harry);
        List<Student> students = Arrays.asList(studentsArray);
        assertFalse(students.isEmpty(),"Список студентов не должен быть пустым");
        assertEquals(exceptedStudents,students,"Студенты не совпадают");
    }

    @Test
    void testGetStudentsByFacultyId_404_NotFound() {
        deleteHarryFromH2();
        ResponseEntity<String> response = restTemplate.getForEntity("/faculty/students/1", String.class);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode(),"Статус должен быть NOT_FOUND 404");
        assertNotNull(response.getBody(),"Возвращаемое значение не должно быть Null");
        clearDatabaseH2();
        setUp();
    }
}
