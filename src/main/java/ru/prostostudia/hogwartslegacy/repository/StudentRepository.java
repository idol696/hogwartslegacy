package ru.prostostudia.hogwartslegacy.repository;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.prostostudia.hogwartslegacy.models.Student;

import java.util.List;
import java.util.Optional;

@Repository
@NonNullApi
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByAgeBetween(Integer ageMin, Integer ageMax);
    List<Student> findByFacultyId(Long id);

    @Query(value = "SELECT COUNT(*) FROM student", nativeQuery = true)
    int getStudentsCount();

    @Query(value = "SELECT ROUND(AVG(age)) FROM student", nativeQuery = true)
    int getStudentsAgeAverage();

    @Query(value = "SELECT * FROM student ORDER BY id DESC LIMIT 5", nativeQuery = true)
    List<Student> getStudentsLast5();
}
