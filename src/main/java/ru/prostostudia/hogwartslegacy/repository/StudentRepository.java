package ru.prostostudia.hogwartslegacy.repository;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.prostostudia.hogwartslegacy.models.Student;

import java.util.Optional;

@Repository
@NonNullApi
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByName(String name);
    Optional<Student> findByAge(Integer age);
}
