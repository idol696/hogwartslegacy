package ru.prostostudia.hogwartslegacy.interfaces;

import ru.prostostudia.hogwartslegacy.models.Student;

import java.util.List;

public interface StudentService {
    Student add(String name, Integer age);
    Long add(Student student);
    Student get(Long id);
    void remove(Long id);
    Student edit(Student student);
    List<Student> filterByName(String name);
    List<Student> filterByAge(Integer age);
    List<Student> filterBetweenByAge(Integer ageMin, Integer ageMax);
    List<Student> getAll();
}