package com.cst438.controller;

import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.dto.EnrollmentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.cst438.test.utils.TestUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/*
 * unit test for instructor enters final class grades for all enrolled students
 */

@AutoConfigureMockMvc
@SpringBootTest
public class InstructorControllerAddGradesTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Test
    public void addGrades() throws Exception {

        MockHttpServletResponse response;

        //get existing grades for the enrollments being changed and save them
        Enrollment e1Before = enrollmentRepository.findById(1).orElse(null);
        assertNotNull(e1Before);
        String e1GradeBefore = e1Before.getGrade();
        Enrollment e2Before = enrollmentRepository.findById(2).orElse(null);
        assertNotNull(e2Before);
        String e2GradeBefore = e2Before.getGrade();
        Enrollment e3Before = enrollmentRepository.findById(3).orElse(null);
        assertNotNull(e3Before);
        String e3GradeBefore = e3Before.getGrade();

        // create EnrollmentDTO list with changed grades.
        List<EnrollmentDTO> glist = new ArrayList<>();
        //Add Enrollment DTO objects to list
        glist.add(new EnrollmentDTO(1, "C", 3, "thomas edison", "tedison@csumb.edu", "cst338",
                "Software Design", 1, 1, "052", "100",
                "M W 10:00-11:50", 4, 2023, "Fall"));
        glist.add(new EnrollmentDTO(2, "F", 3, "thomas edison", "tedison@csumb.edu", "cst363",
                "Introduction to Database", 1, 8, "052", "104",
                "M W 10:00-11:50", 4, 2024, "Spring"));
        glist.add(new EnrollmentDTO(3, "A", 3, "thomas edison", "tedison@csumb.edu", "cst438",
                "Software Engineering", 1, 10, "052", "222",
                "T Th 12:00-1:50", 4, 2024, "Spring"));

        //perform test enters final grades for all enrolled students
        // issue a http PUT request to SpringTestServer
        // specify MediaType for request data
        // convert section to String data and set as request content
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/enrollments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(glist)))
                .andReturn()
                .getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        //confirm that the grades were changed
        Enrollment e1After = enrollmentRepository.findById(1).orElse(null);
        assertNotNull(e1After);
        assertEquals("C", e1After.getGrade());

        Enrollment e2After = enrollmentRepository.findById(2).orElse(null);
        assertNotNull(e2After);
        assertEquals("F", e2After.getGrade());

        Enrollment e3After = enrollmentRepository.findById(3).orElse(null);
        assertNotNull(e3After);
        assertEquals("A", e3After.getGrade());

        //Cleanup
        //restore original data
        //Create new DTOArrayList with original grades
        List<EnrollmentDTO> glistCleanup = new ArrayList<>();
        //Add Enrollment DTO objects to list
        glistCleanup.add(new EnrollmentDTO(1, e1GradeBefore, 3, "thomas edison", "tedison@csumb.edu", "cst338",
                "Software Design", 1, 1, "052", "100",
                "M W 10:00-11:50", 4, 2023, "Fall"));
        glistCleanup.add(new EnrollmentDTO(2, e2GradeBefore, 3, "thomas edison", "tedison@csumb.edu", "cst363",
                "Introduction to Database", 1, 8, "052", "104",
                "M W 10:00-11:50", 4, 2024, "Spring"));
        glistCleanup.add(new EnrollmentDTO(3, e3GradeBefore, 3, "thomas edison", "tedison@csumb.edu", "cst438",
                "Software Engineering", 1, 10, "052", "222",
                "T Th 12:00-1:50", 4, 2024, "Spring"));

        // issue a http PUT request to SpringTestServer
        // specify MediaType for request data
        // convert section to String data and set as request content and pass glistCleanup ArrayList
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/enrollments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(glistCleanup)))
                .andReturn()
                .getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        //confirm that the grades were changed back to their original values
        Enrollment e1Restored = enrollmentRepository.findById(1).orElse(null);
        assertNotNull(e1Restored);
        assertEquals(e1GradeBefore, e1Restored.getGrade());

        Enrollment e2Restored = enrollmentRepository.findById(2).orElse(null);
        assertNotNull(e2Restored);
        assertEquals(e2GradeBefore, e2Restored.getGrade());

        Enrollment e3Restored = enrollmentRepository.findById(3).orElse(null);
        assertNotNull(e3Restored);
        assertEquals(e3GradeBefore, e3Restored.getGrade());

    }
}
