package ru.prostostudia.hogwartslegacy.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.prostostudia.hogwartslegacy.exceptions.*;
import ru.prostostudia.hogwartslegacy.interfaces.FacultyService;
import ru.prostostudia.hogwartslegacy.interfaces.StudentService;
import ru.prostostudia.hogwartslegacy.models.Faculty;
import ru.prostostudia.hogwartslegacy.models.Student;
import ru.prostostudia.hogwartslegacy.repository.FacultyRepository;

import java.util.*;

@Service
public class FacultyServiceImpl implements FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyServiceImpl.class);

    private final FacultyRepository facultyRepository;
    private final StudentService studentService;

    public FacultyServiceImpl(FacultyRepository facultyRepository, StudentService studentService) {
        this.facultyRepository = facultyRepository;
        this.studentService = studentService;
    }

    @Override
    public Faculty add(String name, String color) {
        logger.info("Attempting to add a faculty with name: {} and color: {}", name, color);
        verifyParameter(name, color);
        try {
            find(name);
        } catch (FacultyNotFoundException e) {
            Faculty faculty = new Faculty(null, name, color);
            Faculty savedFaculty = facultyRepository.save(faculty);
            logger.debug("Faculty added successfully: {}", savedFaculty);
            return savedFaculty;
        }
        logger.error("Faculty with name {} already exists", name);
        throw new FacultyNameSetAlreadyException();
    }

    @Override
    public Long add(Faculty faculty) {
        logger.info("Adding faculty: {}", faculty);
        Long id = add(faculty.getName(), faculty.getColor()).getId();
        logger.debug("Faculty added with ID: {}", id);
        return id;
    }

    @Override
    public Faculty get(Long id) {
        logger.info("Fetching faculty by ID: {}", id);
        return facultyRepository.findById(id).orElseThrow(() -> {
            logger.error("Faculty with ID {} not found", id);
            return new FacultyNotFoundException();
        });
    }

    @Override
    public void remove(Long id) {
        logger.info("Attempting to remove faculty with ID: {}", id);
        facultyRepository.findById(id).orElseThrow(() -> {
            logger.error("Faculty with ID {} not found for removal", id);
            return new FacultyNotFoundException();
        });
        if (!studentService.filterByFaculty(id).isEmpty()) {
            logger.error("Faculty with ID {} contains students and cannot be removed", id);
            throw new FacultyContainStudentException();
        }
        facultyRepository.deleteById(id);
        logger.debug("Faculty with ID {} successfully removed", id);
    }

    @Override
    public Faculty edit(Faculty faculty) {
        logger.info("Attempting to edit faculty: {}", faculty);
        Long id = faculty.getId();
        verifyParameter(faculty.getName(), faculty.getColor());
        facultyRepository.findById(id).orElseThrow(() -> {
            logger.error("Faculty with ID {} not found for editing", id);
            return new FacultyNotFoundException();
        });
        if (facultyRepository.findByName(faculty.getName()).isPresent()) {
            logger.error("Faculty with name {} already exists", faculty.getName());
            throw new FacultyNameSetAlreadyException();
        }
        Faculty updatedFaculty = facultyRepository.save(faculty);
        logger.debug("Faculty updated successfully: {}", updatedFaculty);
        return updatedFaculty;
    }

    @Override
    public Faculty find(String name) {
        logger.info("Finding faculty by name: {}", name);
        return facultyRepository.findByName(name).orElseThrow(() -> {
            logger.error("Faculty with name {} not found", name);
            return new FacultyNotFoundException();
        });
    }

    @Override
    public List<Faculty> getAll() {
        logger.info("Fetching all faculties");
        List<Faculty> faculties = facultyRepository.findAll();
        if (faculties.isEmpty()) {
            logger.warn("No faculties found in the database.");
        }
        logger.debug("Total faculties found: {}", faculties.size());
        return faculties;
    }

    @Override
    public List<Faculty> filterByColor(String color) {
        logger.info("Filtering faculties by color: {}", color);
        List<Faculty> faculties = facultyRepository.findByColorIgnoreCase(color);
        if (faculties.isEmpty()) {
            logger.warn("No faculties found with color: {}", color);
        }
        logger.debug("Faculties found with color {}: {}", color, faculties);
        return faculties;
    }

    @Override
    public List<Student> getStudents(Long id) {
        logger.info("Fetching students for faculty ID: {}", id);
        List<Student> students = studentService.filterByFaculty(id);
        if (students.isEmpty()) {
            logger.warn("No students found for faculty ID: {}", id);
        }
        if (students.isEmpty()) {
            logger.error("No students found for faculty ID: {}", id);
            throw new StudentNotFoundException();
        }
        logger.debug("Students found for faculty ID {}: {}", id, students);
        return students;
    }

    private void verifyParameter(String name, String color) {
        logger.debug("Validating parameters - name: {}, color: {}", name, color);
        if (name == null || name.isBlank()) {
            logger.error("Validation failed: Name is invalid");
            throw new FacultyIllegalParameterException("Name");
        } else if (color == null || color.isBlank()) {
            logger.error("Validation failed: Color is invalid");
            throw new FacultyIllegalParameterException("Color");
        }
    }

    @Override
    public String getLongestFacultyName() {
        logger.info("Method getLongestFacultyName invoked to find the faculty with the longest name.");

        List<Faculty> faculties = getAll();
        if (faculties.isEmpty()) {
            logger.warn("The faculty list is empty. Returning an empty string.");
            return "";
        }

        logger.debug("Fetched faculty list: {}", faculties);

        String longestName = faculties.stream()
                .map(Faculty::getName)
                .max((name1, name2) -> Integer.compare(name1.length(), name2.length()))
                .orElse("");

        logger.info("Longest faculty name found: {}", longestName);
        return longestName;
    }
}
