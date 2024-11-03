package ru.prostostudia.hogwartslegacy.services;

import org.springframework.stereotype.Service;
import ru.prostostudia.hogwartslegacy.exceptions.StudentIllegalParameterException;
import ru.prostostudia.hogwartslegacy.exceptions.StudentNotFoundException;
import ru.prostostudia.hogwartslegacy.interfaces.StudentService;
import ru.prostostudia.hogwartslegacy.models.Student;
import ru.prostostudia.hogwartslegacy.repository.StudentRepository;

import java.util.List;
import java.util.Objects;


@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student add(String name, Integer age) {
        validStudent(name, age);
        Student student = new Student(null, name, age);
        studentRepository.save(student);
        return student;
    }

    @Override
    public Long add(Student student) {
        return add(student.getName(), student.getAge()).getId();
    }

    @Override
    public Student get(Long id) {
        return studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
    }

    @Override
    public void remove(Long id) {
        if (studentRepository.findById(id).isEmpty()) {
            throw new StudentNotFoundException();
        }
        studentRepository.deleteById(id);
    }

    @Override
    public Student edit(Student student) {
        Long id = student.getId();
        validStudent(student.getName(), student.getAge());
        if (studentRepository.findById(id).isEmpty()) {
            throw new StudentNotFoundException();
        }
        return studentRepository.save(student);
    }

    @Override
    public List<Student> filterBetweenByAge(Integer ageMin, Integer ageMax) {
        return studentRepository.findAll().stream()
                .filter(student -> student.getAge() >= ageMin && student.getAge() <= ageMax )
                .toList();
    }

    @Override
    public List<Student> filterByAge(Integer age) {
        return studentRepository.findAll().stream()
                .filter(student -> Objects.equals(student.getAge(), age ))
                .toList();
    }

    @Override
    public List<Student> filterByName(String name) {
        return studentRepository.findAll().stream()
                .filter(student -> Objects.equals(student.getName(), name))
                .toList();
    }

    @Override
    public List<Student> getAll() {
        return studentRepository.findAll().stream().toList();
    }

    private void validStudent(String name, Integer age) {
        if(name == null || name.isBlank()) {
            throw new StudentIllegalParameterException("Name");
        }
        if (age == null || age <= 0) {
            throw new StudentIllegalParameterException("Age");
        }
    }
}