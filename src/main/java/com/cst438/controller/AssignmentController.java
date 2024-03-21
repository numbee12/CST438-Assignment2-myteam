package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.AssignmentDTO;
import com.cst438.dto.AssignmentStudentDTO;
import com.cst438.dto.GradeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AssignmentController {

    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;


    // instructor lists assignments for a section.  Assignments ordered by due date.
    // logged in user must be the instructor for the section
    // TEST URL http://localhost:8080/sections/8/assignments
    @GetMapping("/sections/{secNo}/assignments")
    public List<AssignmentDTO> getAssignments(@PathVariable("secNo") int secNo) {

        List<Assignment> assignments = assignmentRepository.findBySectionNoOrderByDueDate(secNo);
        //we are not checking for empty/ null assignments

        List<AssignmentDTO> assignmentDTOList = new ArrayList<>();

        for (Assignment a : assignments) {
            assignmentDTOList.add(new AssignmentDTO(
                a.getAssignmentId(),
                a.getTitle(),
                a.getDueDate(),
                a.getSection().getCourse().getCourseId(),
                a.getSection().getSecId(),
                a.getSection().getSectionNo()));
        }
        return assignmentDTOList;
    }

    // add assignment
    // user must be instructor of the section
    // return AssignmentDTO with assignmentID generated by database
    //TEST URL http://localhost:8080/assignments
    //TEST BODY {"title":"Assignment Post Test","dueDate":"2024-05-01","secId":1,"secNo":8}
    @PostMapping("/assignments")
    public AssignmentDTO createAssignment(
        @RequestBody AssignmentDTO assignmentDTO) {

        Section section = sectionRepository.findById(assignmentDTO.secNo()).orElse(null);

        if (section == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "section not found. secNo: "+assignmentDTO.secNo());
        }

        Date startDate = new Date(section.getTerm().getStartDate().getTime());
        Date endDate = new Date(section.getTerm().getEndDate().getTime());
        Date dueDate;

        try {
            SimpleDateFormat df =new SimpleDateFormat("yyyy-MM-dd");
            dueDate = df.parse(assignmentDTO.dueDate());
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "assignment due date must be in yyyy-MM-dd format");
        }
        if (dueDate.before(startDate) || dueDate.after(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "assignment due date must be within section term");
        }

        Assignment a = new Assignment();
        a.setTitle(assignmentDTO.title());
        a.setDueDate(assignmentDTO.dueDate());
        a.setSection(section);
        assignmentRepository.save(a);

        return new AssignmentDTO(
            a.getAssignmentId(),
            a.getTitle(),
            a.getDueDate(),
            a.getSection().getCourse().getCourseId(),
            a.getSection().getSecId(),
            a.getSection().getSectionNo()
        );
    }

        // update assignment for a section.  Only title and dueDate may be changed.
        // must be instructor of the section
        // return updated AssignmentDTO
        //TEST URL http://localhost:8080/assignments
        //TEST BODY {"id": 2,"title": "db homework 1 Update","dueDate": "2024-04-04","courseId": "cst363","secId": 1,"secNo": 8}
        @PutMapping("/assignments")
        public AssignmentDTO updateAssignment (@RequestBody AssignmentDTO dto){
            Assignment a = assignmentRepository.findById(dto.id()).orElse(null);
            if(a == null) {
                throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "Assignment not found "+ dto.id());
            } else {
                Section section = sectionRepository.findById(dto.secNo()).orElse(null);

                if (section == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "section not found. secNo: "+dto.secNo());
                }

                Date startDate = new Date(section.getTerm().getStartDate().getTime());
                Date endDate = new Date(section.getTerm().getEndDate().getTime());
                Date dueDate;

                try {
                    SimpleDateFormat df =new SimpleDateFormat("yyyy-MM-dd");
                    dueDate = df.parse(dto.dueDate());
                } catch (ParseException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "assignment due date must be in yyyy-MM-dd format");
                }
                if (dueDate.before(startDate) || dueDate.after(endDate)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "assignment due date must be within section term");
                }

                a.setTitle(dto.title());
                a.setDueDate(dto.dueDate());
                assignmentRepository.save(a);
                return new AssignmentDTO(
                    a.getAssignmentId(),
                    a.getTitle(),
                    a.getDueDate(),
                    a.getSection().getCourse().getCourseId(),
                    a.getSection().getSecId(),
                    a.getSection().getSectionNo()
                );
            }
        }

        // delete assignment for a section
        // logged in user must be instructor of the section
        @DeleteMapping("/assignments/{assignmentId}")
        public void deleteAssignment ( @PathVariable("assignmentId") int assignmentId){
            Assignment a = assignmentRepository.findById(assignmentId).orElse(null);

            List<Grade> grade = gradeRepository.findByAssignmentId(assignmentId);

            if (a != null) {

                for (Grade g : grade) {
                    if (g.getScore() != null){
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot delete an assignment if a grade has been recorded");
                    }
                    /**gradeRepository.delete(g);***/
                }
                assignmentRepository.delete(a);
            }
        }

        // instructor gets grades for assignment ordered by student name
        // user must be instructor for the section
        @GetMapping("/assignments/{assignmentId}/grades")
        public List<GradeDTO> getAssignmentGrades ( @PathVariable("assignmentId") int assignmentId){

//        int sectionNo = assignmentRepository.findSectionNoByAssignmentId(assignmentId);
            Assignment a = assignmentRepository.findById(assignmentId).orElse(null);
            if(a==null){
                throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "not found");
            }

            List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(a.getSection().getSectionNo());
            List<GradeDTO> assignmentGrades = new ArrayList<>();

            for (Enrollment enrollment : enrollments) {
                Grade grade = gradeRepository.findByEnrollmentIdAndAssignmentId(enrollment.getEnrollmentId(), assignmentId);

                if (grade == null) {
                   grade = new Grade();
                   grade.setEnrollment(enrollment);
                   grade.setAssignment(a);
                   grade.setScore(null);
                   gradeRepository.save(grade);
                   grade = gradeRepository.findByEnrollmentIdAndAssignmentId(enrollment.getEnrollmentId(), assignmentId);
                }
                    GradeDTO gradeDTO = new GradeDTO(
                        grade.getGradeId(),
                        enrollment.getStudent().getName(),
                        enrollment.getStudent().getEmail(),
                        grade.getAssignment().getTitle(),
                        grade.getAssignment().getSection().getCourse().getCourseId(),
                        grade.getAssignment().getSection().getSecId(),
                        grade.getScore());
                    assignmentGrades.add(gradeDTO);
            }
            return assignmentGrades;
        }

        // instructor uploads grades for assignment
        // user must be instructor for the section
        //TEST URL [{"gradeId":1,"studentName":"thomas edison", "studentEmail":"tedison@csumb.edu","assignmentTitle":"db homework 1","courseId":"cst363","sectionId":1,"score":88}]

        @PutMapping("/grades")
        public void updateGrades (@RequestBody List <GradeDTO> dlist) {
            for (GradeDTO dto : dlist) {
                Grade g = gradeRepository.findById(dto.gradeId()).orElse(null);
                if (g == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "dto not found " + dto.courseId());
                } else {
                    g.setScore(dto.score());
                    gradeRepository.save(g);
                }
            }
        }

        // SEE MASTER BRANCH - This was supposed to return an AssignmentStudentDTO, not an Assignment DTO.
        // student lists their assignments/grades for an enrollment ordered by due date
        // student must be enrolled in the section
        //TEST URL http://localhost:8080/assignments?studentId=3&year=2024&semester=Spring
        @GetMapping("/assignments")
        public List<AssignmentStudentDTO> getStudentAssignments (
                @RequestParam("studentId") int studentId,
                @RequestParam("year") int year,
                @RequestParam("semester") String semester){

            List<Assignment> assignments = assignmentRepository.findByStudentIdAndYearAndSemesterOrderByDueDate(studentId,year,semester);

            List<AssignmentStudentDTO> assignmentStudentDTO = new ArrayList<>();
            for(Assignment a: assignments){
                Enrollment enrollment =enrollmentRepository.findEnrollmentBySectionNoAndStudentId(a.getSection().getSectionNo(), studentId);
                Grade grade = gradeRepository.findByEnrollmentIdAndAssignmentId(enrollment.getEnrollmentId(),a.getAssignmentId());
                Integer score = null;
                if(grade!= null){
                    score = grade.getScore();
                }

                assignmentStudentDTO.add(new AssignmentStudentDTO(
                        a.getAssignmentId(),
                        a.getTitle(),
                        a.getDueDate(),
                        a.getSection().getCourse().getCourseId(),
                        a.getSection().getSecId(),
                        score));
            }
            return assignmentStudentDTO;
        }
}