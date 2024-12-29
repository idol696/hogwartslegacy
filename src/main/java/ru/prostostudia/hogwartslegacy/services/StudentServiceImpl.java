package ru.prostostudia.hogwartslegacy.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.prostostudia.hogwartslegacy.exceptions.StudentIllegalParameterException;
import ru.prostostudia.hogwartslegacy.exceptions.StudentNotFoundException;
import ru.prostostudia.hogwartslegacy.interfaces.StudentService;
import ru.prostostudia.hogwartslegacy.models.Faculty;
import ru.prostostudia.hogwartslegacy.models.Student;
import ru.prostostudia.hogwartslegacy.repository.StudentRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class StudentServiceImpl implements StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student add(String name, Integer age) {
        logger.info("Attempting to add a student with name: {} and age: {}", name, age);
        validStudent(name, age);
        Student student = new Student(null, name, age);
        Student savedStudent = studentRepository.save(student);
        logger.debug("Student saved: {}", savedStudent);
        return savedStudent;
    }

    @Override
    public Long add(Student student) {
        logger.info("Attempting to add a student: {}", student);
        Long id = add(student.getName(), student.getAge()).getId();
        logger.debug("Student added with ID: {}", id);
        return id;
    }

    @Override
    public Student get(Long id) {
        logger.info("Fetching student by ID: {}", id);
        return studentRepository.findById(id).orElseThrow(() -> {
            logger.error("Student with ID {} not found", id);
            return new StudentNotFoundException();
        });
    }

    @Override
    public Faculty getStudentFaculty(Long id) {
        logger.info("Fetching faculty for student ID: {}", id);
        Student student = studentRepository.findById(id).orElseThrow(() -> {
            logger.error("Student with ID {} not found for faculty retrieval", id);
            return new StudentNotFoundException();
        });
        logger.debug("Faculty retrieved: {}", student.getFaculty());
        return student.getFaculty();
    }

    @Override
    public void remove(Long id) {
        logger.info("Attempting to remove student with ID: {}", id);
        if (studentRepository.findById(id).isEmpty()) {
            logger.error("Student with ID {} not found for removal", id);
            throw new StudentNotFoundException();
        }
        studentRepository.deleteById(id);
        logger.debug("Student with ID {} successfully removed", id);
    }

    @Override
    public Student edit(Student student) {
        logger.info("Attempting to edit student: {}", student);
        validStudent(student.getName(), student.getAge());
        if (studentRepository.findById(student.getId()).isEmpty()) {
            logger.error("Student with ID {} not found for editing", student.getId());
            throw new StudentNotFoundException();
        }
        Student updatedStudent = studentRepository.save(student);
        logger.debug("Student updated: {}", updatedStudent);
        return updatedStudent;
    }

    @Override
    public List<Student> filterBetweenByAge(Integer ageMin, Integer ageMax) {
        logger.info("Filtering students between ages {} and {}", ageMin, ageMax);
        List<Student> students = studentRepository.findByAgeBetween(ageMin, ageMax);
        logger.debug("Students found: {}", students);
        return students;
    }

    @Override
    public List<Student> filterByFaculty(Long id) {
        logger.info("Filtering students by faculty ID: {}", id);
        List<Student> students = studentRepository.findByFacultyId(id);
        logger.debug("Students found: {}", students);
        return students;
    }

    @Override
    public List<Student> filterByAge(Integer age) {
        logger.info("Filtering students by age: {}", age);
        List<Student> students = studentRepository.findAll().stream()
                .filter(student -> Objects.equals(student.getAge(), age))
                .toList();
        logger.debug("Students found: {}", students);
        return students;
    }

    @Override
    public List<Student> filterByName(String name) {
        logger.info("Filtering students by name: {}", name);
        List<Student> students = studentRepository.findAll().stream()
                .filter(student -> Objects.equals(student.getName(), name))
                .toList();
        logger.debug("Students found: {}", students);
        return students;
    }

    @Override
    public List<Student> getAll() {
        logger.info("Fetching all students");
        List<Student> students = studentRepository.findAll();
        logger.debug("All students retrieved: {}", students);
        return students;
    }

    private void validStudent(String name, Integer age) {
        logger.debug("Validating student with name: {} and age: {}", name, age);
        if (name == null || name.isBlank()) {
            logger.error("Validation failed for student: Name is invalid");
            throw new StudentIllegalParameterException("Name");
        }
        if (age == null || age <= 0) {
            logger.error("Validation failed for student: Age is invalid");
            throw new StudentIllegalParameterException("Age");
        }
    }

    @Override
    public int getStudentsCount() {
        logger.info("Fetching total count of students");
        int count = studentRepository.getStudentsCount();
        logger.debug("Total students count: {}", count);
        return count;
    }

    @Override
    public int getStudentsAgeAverage() {
        logger.info("Fetching average age of students");
        int avgAge = studentRepository.getStudentsAgeAverage();
        logger.debug("Average age calculated: {}", avgAge);
        return avgAge;
    }

    @Override
    public List<Student> getStudentsLast5() {
        logger.info("Fetching last 5 students");
        List<Student> students = studentRepository.getStudentsLast5();
        if (students.isEmpty()) {
            logger.error("No students found for the last 5 query");
            throw new StudentNotFoundException();
        }
        students.sort(Comparator.comparingLong(Student::getId));
        logger.debug("Last 5 students sorted: {}", students);
        return students;
    }

    @Override
    public List<String> getStudentsStartNameA() {
        logger.info("Method getStudentsStartNameA invoked to fetch students whose names start with 'A' or 'А(Cyrillic)'.");

        List<Student> students = getAll();
        if (students.isEmpty()) {
            logger.error("The student list is empty. Throwing StudentNotFoundException.");
            throw new StudentNotFoundException();
        }

        logger.debug("Fetched student list: {}", students);

        List<String> result = students.stream()
                .map(Student::getName)
                .filter(name -> name.startsWith("А") || name.startsWith("A")) // Support for Cyrillic and Latin 'A'
                .map(String::toUpperCase)
                .sorted()
                .toList();

        logger.info("Processing result: {}", result);
        return result;
    }

    @Override
    public int getStudentAgeAverageStream() {
        logger.info("Method getStudentAgeAverageStream invoked to calculate the average age of students.");

        double averageAge = studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0);

        if (averageAge == 0) {
            logger.warn("Average age is 0. It is likely that there are no students in the database.");
        }

        logger.debug("Calculated average age of students: {}", averageAge);
        return (int) averageAge;
    }
}
