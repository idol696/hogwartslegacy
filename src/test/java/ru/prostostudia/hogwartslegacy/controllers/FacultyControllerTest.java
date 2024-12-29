package ru.prostostudia.hogwartslegacy.controllers;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import ru.prostostudia.hogwartslegacy.services.FacultyServiceImpl;
import ru.prostostudia.hogwartslegacy.services.StudentServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
class FacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private FacultyRepository facultyRepository;

    @SpyBean
    private FacultyServiceImpl facultyService;

    @SpyBean
    private StudentServiceImpl studentService;

    @InjectMocks
    private FacultyController facultyController;

    @Test
    void testGetFacultyById() throws Exception {
        Faculty gryffindor = new Faculty(1L, "Gryffindor", "Red");

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(gryffindor));

        mockMvc.perform(get("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));
    }

    @Test
    void testGetFacultyById_NotFound() throws Exception {
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/faculty/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddFaculty() throws Exception {
        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", "Gryffindor");
        facultyObject.put("color", "Red");

        Faculty savedFaculty = new Faculty(1L, "Gryffindor", "Red");
        when(facultyRepository.save(any(Faculty.class))).thenReturn(savedFaculty);

        mockMvc.perform(post("/faculty/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(facultyObject.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1L));
    }

    @Test
    void testEditFaculty() throws Exception {
        JSONObject facultyObject = new JSONObject();
        facultyObject.put("id", 1);
        facultyObject.put("name", "Gryffindor Updated");
        facultyObject.put("color", "Gold");

        Faculty updatedFaculty = new Faculty(1L, "Gryffindor Updated", "Gold");
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(updatedFaculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(updatedFaculty);

        mockMvc.perform(put("/faculty/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(facultyObject.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor Updated"))
                .andExpect(jsonPath("$.color").value("Gold"));
    }

    @Test
    void testEditFaculty_NotFound() throws Exception {
        JSONObject facultyObject = new JSONObject();
        facultyObject.put("id", 1);
        facultyObject.put("name", "Gryffindor Updated");
        facultyObject.put("color", "Gold");

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/faculty/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(facultyObject.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRemoveFaculty() throws Exception {
        Faculty deleteFaculty = new Faculty(1L, "Gryffindor", "Red");
        doNothing().when(facultyRepository).deleteById(anyLong());
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(deleteFaculty));

        mockMvc.perform(delete("/faculty/remove/1"))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(facultyRepository).deleteById(captor.capture());
        assertEquals(1L, captor.getValue());
    }

    @Test
    void testRemoveFaculty_NotFound() throws Exception {
        doNothing().when(facultyRepository).deleteById(anyLong());
        when(facultyRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/faculty/remove/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllFaculties() throws Exception {
        Faculty gryffindor = new Faculty(1L, "Gryffindor", "Red");
        Faculty slytherin = new Faculty(2L, "Slytherin", "Green");

        when(facultyRepository.findAll()).thenReturn(List.of(gryffindor, slytherin));

        mockMvc.perform(get("/faculty")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[1].name").value("Slytherin"));
        verify(facultyRepository, times(1)).findAll();
    }

    @Test
    void testGetAllFaculties_EmptyValid() throws Exception {

        mockMvc.perform(get("/faculty")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void testFilterStudentsByFaculty_Success() throws Exception {
        long facultyId = 1L;
        Student student1 = new Student(1L, "Harry Potter", 17);
        Student student2 = new Student(2L, "Hermione Granger", 18);
        List<Student> students = List.of(student1, student2);

        when(studentRepository.findByFacultyId(facultyId)).thenReturn(students);

        mockMvc.perform(get("/faculty/students/" + facultyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Harry Potter"))
                .andExpect(jsonPath("$[1].name").value("Hermione Granger"));

        verify(studentRepository, times(1)).findByFacultyId(facultyId);
    }

    @Test
    void testFilterStudentsByFaculty_NotFound() throws Exception {

        long facultyId = 999L;

        when(studentRepository.findByFacultyId(facultyId)).thenReturn(List.of());

        mockMvc.perform(get("/faculty/students/" + facultyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(studentRepository, times(1)).findByFacultyId(facultyId);
    }

    @Test
    void getLongestFacultyName() throws Exception {
        Faculty gryffindor = new Faculty(1L, "Gryffindor", "Red");
        Faculty slytherin = new Faculty(2L, "Slytherin Cat Dog Love", "Green");

        when(facultyRepository.findAll()).thenReturn(List.of(gryffindor, slytherin));

        mockMvc.perform(get("/faculty/longest-faculty-name")
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Slytherin Cat Dog Love"));
        verify(facultyRepository, times(1)).findAll();
    }
}
