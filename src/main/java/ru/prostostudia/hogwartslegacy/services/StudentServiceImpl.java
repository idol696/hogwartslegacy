package ru.prostostudia.hogwartslegacy.services;

import org.springframework.stereotype.Service;
import ru.prostostudia.hogwartslegacy.exceptions.StudentIllegalParameterException;
import ru.prostostudia.hogwartslegacy.exceptions.StudentNotFoundException;
import ru.prostostudia.hogwartslegacy.interfaces.StudentService;
import ru.prostostudia.hogwartslegacy.models.Student;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
    private final Map<Long, Student> students = new HashMap<>();
    private long counter = 0;

    @Override
    public Student add(String name, Integer age) {
        validStudent(name, age);
        Student student = new Student(++counter, name, age);
        students.put(student.getId(),student);
        return student;
    }

    @Override
    public Long add(Student student) {
        return add(student.getName(), student.getAge()).getId();
    }

    @Override
    public Student get(Long id) {
        if (students.containsKey(id)) {
            return students.get(id);
        }
        throw new StudentNotFoundException();
    }

    @Override
    public void remove(Long id) {
        if (!students.containsKey(id)) {
            throw new StudentNotFoundException();
        }
        students.remove(id);
    }

    @Override
    public Student edit(Student student) {
        Long id = student.getId();
        validStudent(student.getName(), student.getAge());
        if (!students.containsKey(id)) {
            throw new StudentNotFoundException();
        }
        students.put(id,student);
        return student;
    }

    @Override
    public List<Student> filterBetweenByAge(Integer ageMin, Integer ageMax) {
        return students.values().stream()
                .filter(student -> student.getAge() >= ageMin && student.getAge() <= ageMax )
                .toList();
    }

    @Override
    public List<Student> filterByAge(Integer age) {
        return students.values().stream()
                .filter(student -> Objects.equals(student.getAge(), age ))
                .toList();
    }

    @Override
    public List<Student> filterByName(String name) {
        return students.values().stream()
                .filter(student -> Objects.equals(student.getName(), name))
                .toList();
    }

    @Override
    public List<Student> getAll() {
        return students.values().stream().toList();
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