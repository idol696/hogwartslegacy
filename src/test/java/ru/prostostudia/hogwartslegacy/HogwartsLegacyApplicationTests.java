package ru.prostostudia.hogwartslegacy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class HogwartsLegacyApplicationTests {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "Контекст приложения не должен быть null");
        Object studentController = applicationContext.getBean("studentController");
        assertNotNull(studentController, "StudentController должен быть загружен в контекст");
        Object facultyController = applicationContext.getBean("facultyController");
        assertNotNull(facultyController, "FacultyController должен быть загружен в контекст");
    }

}
