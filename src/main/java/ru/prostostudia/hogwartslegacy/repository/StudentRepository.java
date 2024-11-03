package ru.prostostudia.hogwartslegacy.repository;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.prostostudia.hogwartslegacy.models.Student;

import java.util.List;

@Repository
@NonNullApi
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByAgeBetween(Integer ageMin, Integer ageMax);
    List<Student> findByFacultyId(Long id);
}
