package ru.prostostudia.hogwartslegacy.interfaces;

import ru.prostostudia.hogwartslegacy.models.Faculty;
import ru.prostostudia.hogwartslegacy.models.Student;

import java.util.List;

public interface FacultyService {
    Faculty add(String name, String color);
    Long add(Faculty faculty);
    Faculty get(Long id);
    void remove(Long id);
    Faculty edit(Faculty faculty);
    Faculty find(String name);
    List<Faculty> filterByColor(String color);
    List<Faculty> getAll();
    List<Student> getStudents(Long id);
}