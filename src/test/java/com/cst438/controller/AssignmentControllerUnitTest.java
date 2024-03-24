package com.cst438.controller;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentRepository;
import com.cst438.dto.AssignmentDTO;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.dto.GradeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
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
    //instructor adds a new assignment with invalid section number
    @Test
    public void addAssignmentFailBadSectionNo() throws Exception {
        //mock Response
        MockHttpServletResponse response;

        //Section No 1-11 are in the database
        AssignmentDTO assignmentDTO = new AssignmentDTO(
            1,
            "Bad Section Test",
            "2024-05-16",
            "cst438",
            1,
            -1

        );
        //POST request
        response = mvc.perform(
                MockMvcRequestBuilders
                    .post("/assignments")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(assignmentDTO)))
            .andReturn()
            .getResponse();
        //Response should be 404, NOT_FOUND
        assertEquals(404, response.getStatus());
        //Check expected error message returned
        String message = response.getErrorMessage();
        assertEquals("section not found. secNo: "+assignmentDTO.secNo(), message);
    }

    //instructor grades an assignment and enters scores for all enrolled students and uploads the scores
    @Test
    public void gradeAsgmntEnterScoresForAll() throws Exception{
        //Mock Response
        MockHttpServletResponse response;

        List<EnrollmentDTO> enrollmentDTOList = new ArrayList<>();
        enrollmentDTOList.add(new EnrollmentDTO(
            1,
            "B",
            3,
            "thomas edison",
            "tedison@csumb.edu",
            "cst338",
            "Software Design",
            1,
            1,
            "052",
            "100",
            "M W 10:00-11:50",
            4,
            2023,
            "Fall"
        ));
        //PUT request
        response = mvc.perform(
                MockMvcRequestBuilders
                    .put("/enrollments")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(enrollmentDTOList)))
            .andReturn()
            .getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());
        assertNotEquals(null, enrollmentDTOList.get(0).grade());

    }

    //instructor attempts to grade an assignment but the assignment id is invalid
    @Test
    public void updateGradesFailBadAssignmenttId() throws Exception{
        //Mock Response
        MockHttpServletResponse response;

        //Grade ID 9 does not exist
        List<GradeDTO> gradeDTOList = new ArrayList<>();
        gradeDTOList.add(new GradeDTO(
            9,
            "thomas edison",
            "tedison@csumb.edu",
            "db homework 1",
            "cst363",
            1,
            90
        ));
        //PUT request
        response = mvc.perform(
                MockMvcRequestBuilders
                    .put("/grades")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(gradeDTOList)))
            .andReturn()
            .getResponse();
        //Response should be 404, NOT_FOUND
        assertEquals(404, response.getStatus());
        //Check expected error message returned
        String message = response.getErrorMessage();
        assertEquals("dto not found "+gradeDTOList.get(0).courseId(), message);
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
