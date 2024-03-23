package com.cst438.controller;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentRepository;
import com.cst438.dto.AssignmentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;

/*
 * example of unit test to add a section to an existing course
 */

@AutoConfigureMockMvc
@SpringBootTest
public class AssignmentControllerUnitTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    AssignmentRepository assignRepo;

    // instructor adds new assignment successfully
    @Test
    public void addAssignment() throws Exception {

        MockHttpServletResponse response;

        // create DTO with data for new assignment.
        // the primary key, id, is set to 0. it will be
        // set by the database when the assignment is inserted.
        AssignmentDTO asgnmtDTO = new AssignmentDTO(
                0,
                "Shiny New Assignment",
                "2024-03-23",
                "cst438",
                1,
                10
        );

        // issue a http POST request to SpringTestServer
        // specify MediaType for request and response data
        // convert section to String data and set as request content
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/assignments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(asgnmtDTO)))
                        .andReturn()
                        .getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // return data converted from String to DTO
        AssignmentDTO result = fromJsonString(response.getContentAsString(), AssignmentDTO.class);

        // primary key should have a non zero value from the database
        assertNotEquals(0, result.id());
        // check other fields of the DTO for expected values
        assertEquals("Shiny New Assignment", result.title());
        assertEquals("2024-03-23", result.dueDate());
        assertEquals("cst438", result.courseId());
        assertEquals(1, result.secId());
        assertEquals(10, result.secNo());

        // check the database
        Assignment a = assignRepo.findById(result.id()).orElse(null);
        assertNotNull(a);
        assertEquals("Shiny New Assignment", a.getTitle());
        assertEquals("2024-03-23", a.getDueDate());
        assertEquals("cst438", a.getSection().getCourse().getCourseId());
        assertEquals(1, a.getSection().getSecId());
        assertEquals(10, a.getSection().getSectionNo());

        // clean up after test. issue http DELETE request for assignment
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .delete("/assignments/"+result.id()))
                .andReturn()
                .getResponse();

        assertEquals(200, response.getStatus());

        // check database for delete
        a = assignRepo.findById(result.id()).orElse(null);
        assertNull(a);  // section should not be found after delete
    }

    // instructor adds a new assignment with a due date past the end date of the class
    @Test
    public void addAssignmentFailsBadDate( ) throws Exception {

        MockHttpServletResponse response;

        // term ends 2024-05-17, so due date 2024-05-18 should fail
        AssignmentDTO asgnmtDTO = new AssignmentDTO(
            0,
            "Shiny New Assignment",
            "2024-05-18",
            "cst438",
            1,
            10
        );

        // issue the POST request
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .post("/assignments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(asgnmtDTO)))
                .andReturn()
                .getResponse();

        // response should be 400, BAD_REQUEST
        assertEquals(400, response.getStatus());

        // check the expected error message
        String message = response.getErrorMessage();
        assertEquals("assignment due date must be within section term", message);

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
}
