package ru.prostostudia.hogwartslegacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.prostostudia.hogwartslegacy.interfaces.FacultyService;
import ru.prostostudia.hogwartslegacy.models.Faculty;
import ru.prostostudia.hogwartslegacy.models.Student;
import ru.prostostudia.hogwartslegacy.repository.FacultyRepository;
import ru.prostostudia.hogwartslegacy.repository.StudentRepository;

import java.util.Arrays;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class HogwartsLegacyApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private FacultyRepository facultyRepository;

	@Autowired
	private StudentRepository studentRepository;

	@BeforeEach
	void setUp() {
		// Заполнение факультетов
		Faculty gryffindor = new Faculty();
		gryffindor.setName("Gryffindor");
		gryffindor.setColor("Red");

		Faculty slytherin = new Faculty();
		slytherin.setName("Slytherin");
		slytherin.setColor("Green");

		facultyRepository.saveAll(List.of(gryffindor, slytherin));

		// Заполнение студентов
		Student harry = new Student();
		harry.setName("Harry Potter");
		harry.setAge(17);
		harry.setFaculty(gryffindor);

		Student draco = new Student();
		draco.setName("Draco Malfoy");
		draco.setAge(18);
		draco.setFaculty(slytherin);

		studentRepository.saveAll(List.of(harry, draco));
	}

	@Test
	void testGetAllFaculties() {

		Faculty[] facultiesArray = restTemplate.getForObject("/faculty", Faculty[].class);

		assertNotNull(facultiesArray, "Массив факультетов не должен быть null");
		List<Faculty> faculties = Arrays.asList(facultiesArray);
		System.out.println("Faculties: " + faculties);

		assertFalse(faculties.isEmpty(), "Список факультетов не должен быть пустым");
	}

	@Test
	void testGetFacultyById() {
		ResponseEntity<Faculty> response = restTemplate.getForEntity("/faculties/1", Faculty.class);
		//assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		//assertThat(response.getBody()).isNotNull();
	}

	@Test
	@DisplayName("testAddFaculty - Добавляем факультет через контроллер /faculty/add")
	void testAddFaculty() {
		Faculty faculty = new Faculty();
		faculty.setName("Gryffindor");
		faculty.setColor("Red");
		ResponseEntity<String> response = restTemplate.postForEntity("/faculty/add", faculty, String.class);
		assertEquals(response.getStatusCode(),HttpStatus.OK);
		assertNotNull(response.getBody());
		Long facultyId = Long.valueOf(response.getBody());
		assertEquals(3L,facultyId);
	}

	@Test
	void testEditFaculty() {
		Faculty faculty = new Faculty();
		faculty.setName("Gryffindor Updated");
		restTemplate.put("/faculties/edit", faculty);
	}

	@Test
	void testRemoveFaculty() {
		restTemplate.delete("/faculties/remove/1");
	}

	@Test
	void testGetFacultiesByColor() {
		ResponseEntity<Faculty[]> response = restTemplate.getForEntity("/faculties/color/blue", Faculty[].class);
		//assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		//assertThat(response.getBody()).isNotNull();
	}

	@Test
	void testGetStudentsByFacultyId() {
		ResponseEntity<Student[]> response = restTemplate.getForEntity("/faculties/students/1", Student[].class);
		//assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		//assertThat(response.getBody()).isNotNull();
	}
}
