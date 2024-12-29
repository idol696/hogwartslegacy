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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

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

    @Test
    void getStudentsCount_ReturnCount_WhenStudentsExist() throws Exception {

        when(studentRepository.getStudentsCount()).thenReturn(10);

        mockMvc.perform(get("/student/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));

        verify(studentRepository, times(1)).getStudentsCount();
        verify(studentService, times(1)).getStudentsCount();
    }

    @Test
    void getStudentsCount_ReturnNotFound_WhenNoStudentsExist() throws Exception {

        when(studentRepository.getStudentsCount()).thenReturn(0);

        mockMvc.perform(get("/student/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("0"));

        verify(studentRepository, times(1)).getStudentsCount();
        verify(studentService, times(1)).getStudentsCount();
    }

    @Test
    void getStudentsAverageAge_ReturnAverage_WhenStudentsExist() throws Exception {

        when(studentRepository.getStudentsAgeAverage()).thenReturn(25);

        mockMvc.perform(get("/student/age-average")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("25"));

        verify(studentRepository, times(1)).getStudentsAgeAverage();
        verify(studentService, times(1)).getStudentsAgeAverage();
    }

    @Test
    void getStudentsAverageAge_ReturnNotFound_WhenNoStudentsExist() throws Exception {

        when(studentRepository.getStudentsAgeAverage()).thenReturn(0);

        mockMvc.perform(get("/student/age-average")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        verify(studentRepository, times(1)).getStudentsAgeAverage();
        verify(studentService, times(1)).getStudentsAgeAverage();
    }

    @Test
    void getLast5Student_ReturnList_WhenStudentsExist() throws Exception {

        Student student1 = new Student(1L, "Alice", 20);
        Student student2 = new Student(2L, "Bob", 21);

        when(studentRepository.getStudentsLast5()).thenReturn(Arrays.asList(student1, student2));

        mockMvc.perform(get("/student/last")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Bob"));

        verify(studentRepository, times(1)).getStudentsLast5();
        verify(studentService, times(1)).getStudentsLast5();
    }

    @Test
    void getLast5Student_ThrowException_WhenNoStudentsExist() throws Exception {

        when(studentRepository.getStudentsLast5()).thenReturn(List.of());

        mockMvc.perform(get("/student/last")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(studentRepository, times(1)).getStudentsLast5();
        verify(studentService, times(1)).getStudentsLast5();
    }

    @Test
    void getStudentsStartNameA_ReturnList_WhenStudentsExist() throws Exception {

        Student student1 = new Student(1L, "Alice", 20);
        Student student2 = new Student(2L, "Алиса", 21);

        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        mockMvc.perform(get("/student/students-names-a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value("ALICE"))
                .andExpect(jsonPath("$[1]").value("АЛИСА"));

        verify(studentRepository, times(1)).findAll();
        verify(studentService, times(1)).getStudentsStartNameA();
    }

    @Test
    void getStudentsStartNameA_NotFound() throws Exception {

        when(studentRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/student/students-names-a")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(studentRepository, times(1)).findAll();
        verify(studentService, times(1)).getStudentsStartNameA();
    }

    @Test
    void getStudentAgeAverageStream_ReturnInt_AverageAge() throws Exception {

        Student student1 = new Student(1L, "Alice", 20);
        Student student2 = new Student(2L, "Алиса", 30);

        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        mockMvc.perform(get("/student/average-age-stream")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(25));

        verify(studentRepository, times(1)).findAll();
        verify(studentService, times(1)).getStudentAgeAverage();
    }

    @Test
    void getStudentAgeAverageStream_ReturnInt_Zero() throws Exception {


        when(studentRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/student/average-age-stream")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0));

        verify(studentRepository, times(1)).findAll();
        verify(studentService, times(1)).getStudentAgeAverage();
    }
}


