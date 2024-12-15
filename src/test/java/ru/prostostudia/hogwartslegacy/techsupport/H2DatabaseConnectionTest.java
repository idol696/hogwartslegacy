package ru.prostostudia.hogwartslegacy.techsupport;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class H2DatabaseConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testDatabaseConnection() throws Exception {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            String url = connection.getMetaData().getURL();
            String username = connection.getMetaData().getUserName();
            System.out.println("Connected to database: " + url);
            System.out.println("Database username: " + username);
            assertEquals("jdbc:h2:mem:testdb", url,"Не инициализирована тестовая база");
        }
    }
}

