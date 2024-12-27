package ru.prostostudia.hogwartslegacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.prostostudia.hogwartslegacy.services.FacultyServiceImpl;

@SpringBootApplication
public class HogwartsLegacyApplication {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    private static final Logger logger = LoggerFactory.getLogger(FacultyServiceImpl.class);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(HogwartsLegacyApplication.class, args);
        String activeProfile = context.getEnvironment().getProperty("spring.profiles.active", "default");
        logger.info("Active Profile: " + activeProfile);
    }

}
