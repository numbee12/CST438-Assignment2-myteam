
package com.cst438.controller;

import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Section;
import com.cst438.domain.Enrollment;
import com.cst438.domain.SectionRepository;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.dto.SectionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.cst438.test.utils.TestUtils.asJsonString;
import static com.cst438.test.utils.TestUtils.fromJsonString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.*;

//student enrolls into a section
@AutoConfigureMockMvc
@SpringBootTest
public class StudentEnrollIntoSectionTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    EnrollmentRepository enrollmentRepo;

    @Test
    public void studentEnroll() throws Exception {
        MockHttpServletResponse response;

        EnrollmentDTO eDTO = new EnrollmentDTO(
                1,
                "A",
                3,
                "thomas edison",
                "tedison@csumb.edu",
                "cst338",
                "Software Engineering",
                1,
                5,
                "052",
                "222",
                "T Th 12:00-1:50",
                4,
                2024,
                "Spring"

        );
       // String studentEnrollJSON = asJsonString(enrollmentDTO);
//        Enrollment enrollment = new Enrollment();
//        enrollmentRepo.save(enrollment);

        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/7?studentId=3")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andReturn()
                        .getResponse();

        assertEquals(200, response.getStatus());

        EnrollmentDTO result = fromJsonString(response.getContentAsString(), EnrollmentDTO.class);

        // primary key should have a non zero value from the database
        assertNotEquals(0, result.enrollmentId());
        // check other fields of the DTO for expected values

      // assertEquals("A", result.grade());
       assertEquals(3, result.studentId());
       assertEquals("thomas edison", result.name());
       assertEquals("tedison@csumb.edu", result.email());
       assertEquals("cst338", result.courseId());
       assertEquals("Software Design", result.title());
//       assertNotEquals(1, result.sectionId();
//       assertNotEquals(5, result.sectionNo();
//       assertNotEquals(5, result.building();
//       assertNotEquals(5, result.room();
//       assertNotEquals(5, result.times();
//       assertNotEquals(5, result.credits();
//       assertNotEquals(5, result.year();
//       assertNotEquals(5, result.semester();



        Enrollment e = enrollmentRepo.findById(result.enrollmentId()).orElse(null);
        assertNotNull(e);


        // clean up after test. issue http DELETE request for section
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .delete("/enrollments/"+result.enrollmentId()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        // Check database for delete
        e = enrollmentRepo.findById(result.enrollmentId()).orElse(null);
        assertNull(e);


    }
    @Test
    public void studentEnrollSectionFail( ) throws Exception {
        MockHttpServletResponse response;
        // enrollment id does not exist
        EnrollmentDTO enroll = new EnrollmentDTO(
                1, // do I need it?
                "A",
                3,
                "thomas edison",
                "tedison@csumb.edu",
                "cst438",
                "Software Engineering",
                1,
                10,
                "052",
                "222",
                "T Th 12:00-1:50",
                4,
                2024,
                "Spring"
        );
        // issue the POST request
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/10?studentId=3")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // response should be 400, BAD_REQUEST
        assertEquals(400, response.getStatus());

        // check the expected error message
        String message = response.getErrorMessage();
        assertEquals("this student is already enrolled in this section", message);
    }


        private static String asJsonString(final Object obj) {
            try {
                return new ObjectMapper().writeValueAsString(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static <T> T  fromJsonString(String str, Class<T> valueType ) {
            try {
                return new ObjectMapper().readValue(str, valueType);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }



//
//            int enrollmentId,
//            String grade,  // final grade. May be null until instructor enters final grades.
//            int studentId,
//            String name,
//            String email,
//            String courseId,
//
//            String title,
//            int sectionId,
//            int sectionNo,
//            String building,
//            String room,
//            String times,
//            int credits,
//            int year,
//            String semester //


    }


