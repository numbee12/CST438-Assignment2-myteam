package com.cst438.controller;

import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest

// public class StudentControllerUnitTestFails contains two invalid StudentController Enrollment Test Scenarios
// student attempts to enroll in a section but section number is invalid
// student attempts to enroll but the assignment id is invalid

public class StudentControllerUnitTestsFails {

    @Autowired
    MockMvc mvc;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    /*
     * unit test for student attempts to enroll in a section but the section number is invalid
    */

    @Test
    public void addEnrollmentFailsBadSecNo() throws Exception {

        MockHttpServletResponse response;

        // invalid section No for testing
        int secNo = -1;    //-1 should never be a valid Section No
        final int studentId = 3;    //only id=3 is a student at startup

        //student enrolls in section
        // issue a http POST request to SpringTestServer

        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/{sectionNo}", secNo)
                                .param("studentId", String.valueOf(studentId))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // response should be 400, BAD_REQUEST
        assertEquals(400, response.getStatus());

        //confirm error message
        // check the expected error message
        String message = response.getErrorMessage();
        assertEquals("section No is not for a valid section", message);

        //Cleanup
        //remove enrollment if somehow it succeeded
        Enrollment e = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(secNo, studentId);
        //if somehow enrollment was successful
        if(nonNull(e)) {

            //issue http DELETE request for enrollmentId
            response = mvc.perform(
                            MockMvcRequestBuilders
                                    .delete("/sections/"+e.getEnrollmentId()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, response.getStatus());
            e = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(secNo, studentId);

        }
        //confirm enrollment does not exist
        assertNull(e);
    }

    /*
     * unit test for student attempts to enroll ina section put it is past the add deadline
     */

    @Test
    public void addEnrollmentFailsPastAddDeadline() throws Exception {

        //continue with test

        MockHttpServletResponse response;

        // section No for class with Add Deadline already passed
        int secNo = 2;  //add Deadline for class SectionNo 2 is 8/30/2023
        final int studentId = 3;    //only id=3 is a student at startup

        //confirm enrollment does not already exist
        Enrollment e = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(secNo,studentId);
        assertNull(e);

        // issue a http POST request to SpringTestServer
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/{sectionNo}", secNo)
                                .param("studentId", String.valueOf(studentId))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        //confirm http bad response of 400, BAD_REQUEST
        assertEquals(400, response.getStatus());

        //confirm error message
        String message = response.getErrorMessage();
        assertEquals("today is after the Add Deadline", message);

        //Cleanup
        //remove enrollment if somehow it succeeded
        e = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(secNo, studentId);
        //if somehow enrollment was successful
        if(nonNull(e)) {

            //issue http DELETE request for enrollmentId
            response = mvc.perform(
                            MockMvcRequestBuilders
                                    .delete("/sections/"+e.getEnrollmentId()))
                    .andReturn()
                    .getResponse();

            assertEquals(200, response.getStatus());
            e = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(secNo, studentId);

        }
        //confirm enrollment does not exist
        assertNull(e);
    }
}



