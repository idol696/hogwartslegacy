package ru.prostostudia.hogwartslegacy.repository;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.prostostudia.hogwartslegacy.models.Faculty;

import java.util.Optional;

@Repository
@NonNullApi
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Optional<Faculty> findByName(String name);
    Optional<Faculty> findByColor(String color);
}
