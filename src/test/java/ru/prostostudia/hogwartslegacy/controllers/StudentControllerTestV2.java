package ru.prostostudia.hogwartslegacy.controllers;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.prostostudia.hogwartslegacy.models.Faculty;
import ru.prostostudia.hogwartslegacy.models.Student;
import ru.prostostudia.hogwartslegacy.repository.FacultyRepository;
import ru.prostostudia.hogwartslegacy.repository.StudentRepository;
import ru.prostostudia.hogwartslegacy.services.StudentServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTestV2 {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private FacultyRepository facultyRepository;

    @SpyBean
    private StudentServiceImpl studentService;

    @InjectMocks
    private StudentController studentController;

    @Test
    void testGetStudentById() throws Exception {
        Student harry = new Student(1L, "Harry Potter", 17);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(harry));

        mockMvc.perform(get("/student/get/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Harry Potter"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void testGetStudentById_NotFound() throws Exception {
        when(studentRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/student/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddStudent() throws Exception {
        JSONObject userObject = new JSONObject();
        userObject.put("name", "Валентина Петриченко");
        userObject.put("age", 16);
        Student savedStudent = new Student(3L, userObject.getString("name"), userObject.getInt("age"));

        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        mockMvc.perform(post("/student/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userObject.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(3));
    }

    @Test
    void testEditStudent() throws Exception {
        JSONObject userObject = new JSONObject();
        userObject.put("id", 1L);
        userObject.put("name", "Валентина Петриченко");
        userObject.put("age", 16);
        Student updatedStudent = new Student(userObject.getLong("id"), userObject.getString("name"), userObject.getInt("age"));

        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(updatedStudent));

        mockMvc.perform(put("/student/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userObject.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Валентина Петриченко"))
                .andExpect(jsonPath("$.age").value(16));
    }

    @Test
    void testEditStudent_NotFound() throws Exception {
        JSONObject userObject = new JSONObject();
        userObject.put("id", 1L);
        userObject.put("name", "Валентина Петриченко");
        userObject.put("age", 16);
        Student updatedStudent = new Student(2L, userObject.getString("name"), userObject.getInt("age"));

        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);
        when(studentRepository.findById(2L)).thenReturn(Optional.of(updatedStudent));

        mockMvc.perform(put("/student/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userObject.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveStudent() throws Exception {
        JSONObject userObject = new JSONObject();
        userObject.put("id", 1L);
        userObject.put("name", "Валентина Петриченко");
        userObject.put("age", 16);
        Student updatedStudent = new Student(
                userObject.getLong("id"),
                userObject.getString("name"),
                userObject.getInt("age")
        );
        doNothing().when(studentRepository).deleteById(anyLong());
        when(studentRepository.findById(1L)).thenReturn(Optional.of(updatedStudent));
        mockMvc.perform(get("/student/remove/1"))
                .andExpect(status().isOk());
        verify(studentRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void testRemoveStudent_NotFound() throws Exception {
        doNothing().when(studentRepository).deleteById(anyLong());

        mockMvc.perform(get("/students/remove/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetStudentsByAge() throws Exception {
        List<Student> students = List.of(new Student(1L, "Harry Potter", 17));

        when(studentRepository.findAll()).thenReturn(students);

        mockMvc.perform(get("/student/age/17"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Harry Potter"));
        verify(studentRepository, times(1)).findAll();
    }
    @Test
    void testGetStudentsByAge_NotFound_Valid() throws Exception {

        when(studentRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/student/age/17"))
                .andExpect(status().isOk());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void testGetStudentsByAgeRange() throws Exception {
        List<Student> students = List.of(
                new Student(1L, "Harry Potter", 17),
                new Student(2L, "Draco Malfoy", 18)
        );

        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(students);

        mockMvc.perform(get("/student/age/15/20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Harry Potter"))
                .andExpect(jsonPath("$[1].name").value("Draco Malfoy"));
        verify(studentRepository, times(1)).findByAgeBetween(anyInt(), anyInt());
    }

    @Test
    void testGetStudentsByAgeRange_NotFound_Valid() throws Exception {
        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/student/age/15/16"))
                .andExpect(status().isOk());
        verify(studentRepository, times(1)).findByAgeBetween(anyInt(), anyInt());
    }

    @Test
    void testGetStudentFaculty() throws Exception {
        Faculty gryffindor = new Faculty(1L, "Gryffindor", "Red");
        Student harry = new Student(1L, "Harry Potter", 17);
        harry.setFaculty(gryffindor);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(harry));

        mockMvc.perform(get("/student/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));
        verify(studentRepository, times(1)).findById(anyLong());
    }
}

