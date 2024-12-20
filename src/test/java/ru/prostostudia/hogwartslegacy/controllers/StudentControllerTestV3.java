package ru.prostostudia.hogwartslegacy.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import ru.prostostudia.hogwartslegacy.models.Faculty;
import ru.prostostudia.hogwartslegacy.models.Student;
import ru.prostostudia.hogwartslegacy.repository.FacultyRepository;
import ru.prostostudia.hogwartslegacy.repository.StudentRepository;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StudentControllerTestV3 {

    final Student harry = new Student(1L, "Harry Potter", 16);
    final Student draco = new Student(2L, "Draco Malfoy", 18);
    final Faculty gryffindor = new Faculty(1L, "Gryffindor", "Red");
    final Faculty slytherin = new Faculty(2L, "Slytherin", "Green");

    @Autowired
    DataSource dataSource;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

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
    void testGetAllStudents() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity("/student", Student[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть OK");
        Student[] studentsArray = response.getBody();
        assertNotNull(studentsArray, "Массив студентов не должен быть null");
        List<Student> students = Arrays.asList(studentsArray);
        List<Student> expectedStudents = List.of(harry, draco);
        assertFalse(students.isEmpty(), "Список студентов не должен быть пустым");
        assertEquals(expectedStudents, students, "Список студентов не совпадает");
    }

    @Test
    void testGetStudentById() {
        Student student = restTemplate.getForObject("/student/get/1", Student.class);
        assertEquals(harry, student, "Студент с ID 1 не совпадает с ожиданиями");
    }


    @Test
    void testGetStudentById_404_NotFound() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/student/get/999",
                HttpMethod.GET,
                null,
                Void.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Студент не должен быть найден");
    }

    @Test
    void testGetStudentFaculty() {
        ResponseEntity<Faculty> response = restTemplate.getForEntity("/student/faculty/1", Faculty.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть OK");
        Faculty faculty = response.getBody();
        assertNotNull(faculty, "Факультет не должен быть null");
        assertEquals(gryffindor, faculty, "Факультет студента не совпадает с ожиданиями");
    }

    @Test
    void testAddStudent() {
        Student newStudent = new Student(null, "Hermione Granger", 17);
        ResponseEntity<String> response = restTemplate.postForEntity("/student/add", newStudent, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "Добавление студента должно вернуть ID");
        Long studentId = Long.valueOf(response.getBody());
        assertEquals(3L, studentId, "ID студентов не совпадают");
        clearDatabaseH2();
        setUp();
    }

    @Test
    void testEditStudent() {
        Student updatedStudent = new Student(1L, "Harry Potter Update", 17);
        HttpEntity<Student> request = new HttpEntity<>(updatedStudent);
        ResponseEntity<Student> response = restTemplate.exchange(
                "/student/edit",
                HttpMethod.PUT,
                request,
                Student.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedStudent, response.getBody(), "Обновление студента не произошло");
        clearDatabaseH2();
        setUp();
    }

    @Test
    void testRemoveStudent() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/student/remove/1",
                HttpMethod.GET,
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Удаление должно завершиться успешно");
        boolean studentExists = studentRepository.findById(1L).isPresent();
        assertFalse(studentExists, "Студент с ID 1 должен быть удалён");
        clearDatabaseH2();
        setUp();
    }

    @Test
    void testRemoveStudent_404_NotFound() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/student/remove/888",
                HttpMethod.GET,
                null,
                Void.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Студент должен быть не найден");
    }

    @Test
    void testGetStudentsByAge() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity("/student/age/16", Student[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть OK");
        assertNotNull(response.getBody(), "Ответ не должен быть null");
        List<Student> students = Arrays.asList(response.getBody());
        List<Student> expectedStudents = List.of(harry);
        assertEquals(expectedStudents, students, "Список студентов по возрасту не совпадает");
    }

    @Test
    void testGetStudentsByAge_EmptyValid() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity("/student/age/10", Student[].class);
        assertNotNull(response.getBody(), "Ответ не должен быть null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть OK");
        List<Student> students = Arrays.asList(response.getBody());
        assertTrue(students.isEmpty(), "Список студентов должен быть пустым");
    }

    @Test
    void testGetStudentsByAgeRange() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity("/student/age/17/18", Student[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть OK");
        assertNotNull(response.getBody(), "Ответ не должен быть null");
        List<Student> students = Arrays.asList(response.getBody());
        List<Student> expectedStudents = List.of(draco);
        assertEquals(expectedStudents, students, "Список студентов по диапазону возраста не совпадает");
    }

    @Test
    void testGetStudentsByAgeRange_EmptyValid() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity("/student/age/19/20", Student[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть OK");
        assertNotNull(response.getBody(), "Ответ не должен быть null");
        List<Student> students = Arrays.asList(response.getBody());
        List<Student> expectedStudents = List.of();
        assertTrue(students.isEmpty(), "Список студентов должен быть пустым");
    }


}

