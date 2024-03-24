package com.cst438.controller;

import com.cst438.domain.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
    }

    @Test
    public void addEnrollmentFailsPastAddDeadline() throws Exception {

        //check if student is already enrolled in the section
        //if so drop section

        //continue with test

        MockHttpServletResponse response;

        // section No for class with Add Deadline already passed
        int secNo = 1;  //add Deadline for class SectionNo 8 is 8/30/2023
        final int studentId = 3;    //only id=3 is a student at startup

        // issue a http POST request to SpringTestServer
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/enrollments/sections/{sectionNo}", secNo)
                                .param("studentId", String.valueOf(studentId))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        //confirm http bad response
        // response should be 400, BAD_REQUEST
        assertEquals(400, response.getStatus());

        //confirm error message
        // check the expected error message
        String message = response.getErrorMessage();
        assertEquals("today is after the Add Deadline", message);
    } //end of addEnrollmentFailsPastAddDeadline

}//end of class



