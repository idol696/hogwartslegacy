package ru.prostostudia.hogwartslegacy.services;

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

    private final FacultyRepository facultyRepository;
    private final StudentService studentService;

    public FacultyServiceImpl(FacultyRepository facultyRepository, StudentService studentService) {
        this.facultyRepository = facultyRepository;
        this.studentService = studentService;
    }

    @Override
    public Faculty add(String name, String color) {
        verifyParameter(name, color);
        try {
            find(name);
        } catch (FacultyNotFoundException e) {
            Faculty faculty = new Faculty(null, name, color);
            return facultyRepository.save(faculty);
        }
        throw new FacultyNameSetAlreadyException();
    }

    @Override
    public Long add(Faculty faculty) {
        return add(faculty.getName(), faculty.getColor()).getId();
    }

    @Override
    public Faculty get(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(FacultyNotFoundException::new);
    }

    @Override
    public void remove(Long id) {
        facultyRepository.findById(id)
                .orElseThrow(FacultyNotFoundException::new);
        if(!studentService.filterByFaculty(id).isEmpty()) {
            throw new FacultyContainStudentException();
        }
        facultyRepository.deleteById(id);
    }

    @Override
    public Faculty edit(Faculty faculty) {
        Long id = faculty.getId();
        verifyParameter(faculty.getName(), faculty.getColor());
        facultyRepository.findById(id).orElseThrow(FacultyNotFoundException::new);
        if(facultyRepository.findByName(faculty.getName()).isPresent()) {
            throw new FacultyNameSetAlreadyException();
        }
        return facultyRepository.save(faculty);
    }

    @Override
    public Faculty find(String name) {
        return facultyRepository.findByName(name)
                .orElseThrow(FacultyNotFoundException::new);
    }

    @Override
    public List<Faculty> getAll() {
        List<Faculty> faculties = facultyRepository.findAll();
        return facultyRepository.findAll();
    }

    @Override
    public List<Faculty> filterByColor(String color) {
        return  facultyRepository.findByColorIgnoreCase(color);
    }

    @Override
    public List<Student> getStudents(Long id) {
        List<Student> students =  studentService.filterByFaculty(id);
        if (students.isEmpty()) {
            throw new StudentNotFoundException();
        }
        return students;
    }

    private void verifyParameter(String name, String color) {
        if (name == null || name.isBlank()) {
            throw new FacultyIllegalParameterException("Name");
        } else if (color == null || color.isBlank()) {
            throw new FacultyIllegalParameterException("Color");
        }
    }
}