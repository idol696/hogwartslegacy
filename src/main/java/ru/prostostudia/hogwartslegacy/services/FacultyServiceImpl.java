package ru.prostostudia.hogwartslegacy.services;

import org.springframework.stereotype.Service;
import ru.prostostudia.hogwartslegacy.exceptions.FacultyIllegalParameterException;
import ru.prostostudia.hogwartslegacy.exceptions.FacultyNameSetAlreadyException;
import ru.prostostudia.hogwartslegacy.exceptions.FacultyNotFoundException;
import ru.prostostudia.hogwartslegacy.interfaces.FacultyService;
import ru.prostostudia.hogwartslegacy.models.Faculty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FacultyServiceImpl implements FacultyService {
    private final Map<Long, Faculty> faculties = new HashMap<>();
    private long counter = 0;

    @Override
    public Faculty add(String name, String color) {
        verifyParameter(name, color);
        try {
            find(name);
        } catch (FacultyNotFoundException e) {
            Faculty faculty = new Faculty(++counter, name, color);
            faculties.put(faculty.getId(), faculty);
            return faculty;
        }
        throw new FacultyNameSetAlreadyException();
    }

    @Override
    public Long add(Faculty faculty) {
        return add(faculty.getName(), faculty.getColor()).getId();
    }

    @Override
    public Faculty get(Long id) {
        if (faculties.containsKey(id)) {
            return faculties.get(id);
        }
        throw new FacultyNotFoundException();
    }

    @Override
    public void remove(Long id) {
        if (!faculties.containsKey(id)) {
            throw new FacultyNotFoundException();
        }
        faculties.remove(id);
    }

    @Override
    public Faculty edit(Faculty faculty) {
        Long id = faculty.getId();
        verifyParameter(faculty.getName(), faculty.getColor());
        if (!faculties.containsKey(id)) {
            throw new FacultyNotFoundException();
        }
        try {
            find(faculty.getName());
        } catch (FacultyNotFoundException e) {
            faculties.put(id, faculty);
            return faculty;
        }
        throw new FacultyNameSetAlreadyException();
    }

    @Override
    public Faculty find(String name) {
        List<Faculty> listOfFaculty = faculties.values().stream()
                .filter(faculty -> faculty.getName().equals(name)).toList();
        if (listOfFaculty.isEmpty()) {
            throw new FacultyNotFoundException();
        }
        return listOfFaculty.get(0);
    }

    @Override
    public List<Faculty> getAll() {
        return faculties.values().stream().toList();
    }

    @Override
    public List<Faculty> filterByColor(String color) {
        return faculties.values().stream()
                .filter(faculty -> faculty.getColor().equals(color))
                .collect(Collectors.toList());
    }

    private void verifyParameter(String name, String color) {
        if (name == null || name.isBlank()) {
            throw new FacultyIllegalParameterException("Name");
        } else if (color == null || color.isBlank()) {
            throw new FacultyIllegalParameterException("Color");
        }
    }
}