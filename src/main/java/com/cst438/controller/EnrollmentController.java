package com.cst438.controller;


import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.User;
import com.cst438.dto.EnrollmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class EnrollmentController {

    @Autowired
    EnrollmentRepository enrollmentRepository;

    // instructor downloads student enrollments for a section, ordered by student name
    // user must be instructor for the section
    @GetMapping("/sections/{sectionNo}/enrollments")
    public List<EnrollmentDTO> getEnrollments(
            @PathVariable("sectionNo") int sectionNo ) {

        List<Enrollment> enrollmentList = enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(sectionNo);
        List<EnrollmentDTO> dto_list = new ArrayList<EnrollmentDTO>();
        for (Enrollment enrollment : enrollmentList) {

            // FIXME:
            // Below is the user validation code:
            // Keeping it commented out for now until we figure out how to test it.

            // User user = enrollment.getStudent();
            // if (!user.getType().equals("INSTRUCTOR")) {
            //     throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "user is not an instructor");
            // } else if (!user.getEmail().equals(enrollment.getSection().getInstructorEmail())) {
            //     throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "user is not the section instructor");
            // }

            dto_list.add(new EnrollmentDTO(
                enrollment.getEnrollmentId(),
                enrollment.getGrade(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getName(),
                enrollment.getStudent().getEmail(),
                enrollment.getSection().getCourse().getCourseId(),
                enrollment.getSection().getCourse().getTitle(),
                enrollment.getSection().getSecId(),
                enrollment.getSection().getSectionNo(),
                enrollment.getSection().getBuilding(),
                enrollment.getSection().getRoom(),
                enrollment.getSection().getTimes(),
                enrollment.getSection().getCourse().getCredits(),
                enrollment.getSection().getTerm().getYear(),
                enrollment.getSection().getTerm().getSemester()
            ));
        }
        return dto_list;
    }

    // instructor uploads enrollments with the final grades for the section
    // user must be instructor for the section
    @PutMapping("/enrollments")
    public void updateEnrollmentGrade(@RequestBody List<EnrollmentDTO> dlist) {
    
        for (EnrollmentDTO eDTO : dlist) {
            Enrollment e = enrollmentRepository
                            .findById(eDTO.enrollmentId())
                            .orElse(null);

            if (e == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found " + eDTO.enrollmentId());
            }
            // FIXME:
            // Below is the user validation code:
            // Keeping it commented out for now until we figure out how to test it.

            // User user = e.getStudent();
            // if (!user.getType().equals("INSTRUCTOR")) {
            //     throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "user is not an instructor");
            // } else if (!user.getEmail().equals(e.getSection().getInstructorEmail())) {
            //     throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "user is not the section instructor");
            // }

            e.setGrade(eDTO.grade());
            enrollmentRepository.save(e);
        }
    }

}
