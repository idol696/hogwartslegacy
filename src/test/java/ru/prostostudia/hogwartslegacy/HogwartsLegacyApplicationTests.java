package ru.prostostudia.hogwartslegacy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.prostostudia.hogwartslegacy.controllers.FacultyController;
import ru.prostostudia.hogwartslegacy.controllers.StudentController;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class HogwartsLegacyApplicationTests {

    @Autowired
    private StudentController studentController;

    @Autowired
    private FacultyController facultyController;

    @Test
    void contextLoads() {
        assertNotNull(studentController, "StudentController должен быть загружен");
        assertNotNull(facultyController, "FacultyController должен быть загружен");
    }

}
