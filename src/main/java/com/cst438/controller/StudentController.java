package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.dto.GradeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.ToDoubleBiFunction;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    TermRepository termRepository;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    AssignmentRepository assignmentRepository;




   // student gets transcript showing list of all enrollments
   // studentId will be temporary until Login security is implemented
   //example URL  /transcript?studentId=19803
   @GetMapping("/transcripts")
   public List<EnrollmentDTO> getTranscript(@RequestParam("studentId") int studentId) {
       List<EnrollmentDTO> transcript = new ArrayList<EnrollmentDTO>();
       List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByStudentIdOrderByTermId(studentId);
       for (Enrollment e : enrollments) {

            // FIXME: is this the right way to see if user is a student?
            
           if (!e.getStudent().getType().equals("STUDENT")) {
               throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user is not a student");
           } else if (studentId != e.getStudent().getId()) {
               throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "student id does not match");
           }
           transcript.add(
                new EnrollmentDTO(
                    e.getEnrollmentId(),
                    e.getGrade(),
                    e.getStudent().getId(),  //we are calling get id here when it is a passed param
                    e.getStudent().getName(),
                    e.getStudent().getEmail(),
                    e.getSection().getCourse().getCourseId(),
                    e.getSection().getCourse().getTitle(),
                    e.getSection().getSecId(),
                    e.getSection().getSectionNo(),
                    e.getSection().getBuilding(),
                    e.getSection().getRoom(),
                    e.getSection().getTimes(),
                    e.getSection().getCourse().getCredits(),
                    e.getSection().getTerm().getYear(),
                    e.getSection().getTerm().getSemester()
                )
            );
       }
       return transcript;
   }

    // student gets a list of their enrollments for the given year, semester
    // user must be student
    // studentId will be temporary until Login security is implemented
   @GetMapping("/enrollments")
   public List<EnrollmentDTO> getSchedule(
           @RequestParam("year") int year,
           @RequestParam("semester") String semester,
           @RequestParam("studentId") int studentId) {

       List<EnrollmentDTO> schedule = new ArrayList<>();
       List<Enrollment> enrollments = enrollmentRepository.findByYearAndSemesterOrderByCourseId(year, semester, studentId);
       //we are not checking for empty enrollments/ enrollments not found
       for (Enrollment e : enrollments) {

           if (!e.getStudent().getType().equals("STUDENT")) {
               throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user is not a student");
           } else if (studentId != e.getStudent().getId()) {
               throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "student id does not match");
           }
           schedule.add(
                   new EnrollmentDTO(
                           e.getEnrollmentId(),
                           e.getGrade(),
                           e.getStudent().getId(),
                           e.getStudent().getName(),
                           e.getStudent().getEmail(),
                           e.getSection().getCourse().getCourseId(),
                           e.getSection().getCourse().getTitle(),
                           e.getSection().getSecId(),
                           e.getSection().getSectionNo(),
                           e.getSection().getBuilding(),
                           e.getSection().getRoom(),
                           e.getSection().getTimes(),
                           e.getSection().getCourse().getCredits(),
                           e.getSection().getTerm().getYear(),
                           e.getSection().getTerm().getSemester()
                   )
           );
       }
       return schedule;
   }

    // student adds enrollment into a section
    // user must be student
    // return EnrollmentDTO with enrollmentId generated by database
    @PostMapping("/enrollments/sections/{sectionNo}")
    public EnrollmentDTO addCourse(
		    @PathVariable int sectionNo,
            @RequestParam("studentId") int studentId ) {

        //check that user exists
        User u = userRepository.findById(studentId).orElse(null);
        if (u == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not found");
        }

        Section s = sectionRepository.findById(sectionNo).orElse(null);
        if (s == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "section No is not for a valid section");
        }

        // check that today is between addDate and addDead line for the section
        Term t = termRepository.findById(s.getTerm().getTermId()).orElse(null);

        if (t == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "term does not exist");
        }

        LocalDate addDateLD = t.getAddDate().toLocalDate();
        LocalDate addDeadLineLD = t.getAddDeadline().toLocalDate();
        if(LocalDate.now().compareTo(addDateLD)<0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "today is not after the add Date");
        }
        if ( LocalDate.now().compareTo(addDeadLineLD)>0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "today is after the Add Deadline");
        }
        // check that student is not already enrolled into this section
        Enrollment existing = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(sectionNo, studentId);
        if(existing != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "this student is already enrolled in this section");
        }

        // create a new enrollment entity and save.  The enrollment grade will
        // be NULL until instructor enters final grades for the course.
        Enrollment e = new Enrollment();
        e.setGrade(null);
        e.setStudent(u);
        e.setSection(s);

        enrollmentRepository.save(e);

        return new EnrollmentDTO(
                e.getEnrollmentId(),
                e.getGrade(),
                e.getStudent().getId(),
                e.getStudent().getName(),
                e.getStudent().getEmail(),
                e.getSection().getCourse().getCourseId(),
                e.getSection().getCourse().getTitle(),
                e.getSection().getSecId(),
                e.getSection().getSectionNo(),
                e.getSection().getBuilding(),
                e.getSection().getRoom(),
                e.getSection().getTimes(),
                e.getSection().getCourse().getCredits(),
                e.getSection().getTerm().getYear(),
                e.getSection().getTerm().getSemester()
        );
    }

    // student drops a course
    // user must be student

    //this will be the logged in user? Is this for future use after login / pwd is implemented?

   @DeleteMapping("/enrollments/{enrollmentId}")
   public void dropCourse(@PathVariable("enrollmentId") int enrollmentId) {

       //retrieve enrollment by Id
       Enrollment e = enrollmentRepository.findById(enrollmentId).orElse(null);
       if(e == null){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid enrollment id");
       }

       //retrieve section by section number in enrollment
       Section s = sectionRepository.findById(e.getSection().getSectionNo()).orElse(null);
       if (s == null) {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sectionNo not a valid section number");
       }

       //retrieve term in section
       Term t = termRepository.findById(s.getTerm().getTermId()).orElse(null);
       if (t == null) {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid term");
       }

       //retrieve user by getting student ID if user is a student, set to null is not student
       User u = userRepository.findById(e.getStudent().getId()).orElse(null);

       if(u == null){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid user id");
       }

       //check that grade is null
       Grade g = gradeRepository.findByEnrollmentId(e.getEnrollmentId());
       if (g != null) {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot delete an enrollment for which a grade has already been given");
       }

       // check that today is not after the dropDeadline for section
       LocalDate dropDeadlineLD = t.getDropDeadline().toLocalDate();

       if(LocalDate.now().compareTo(dropDeadlineLD)>0) {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "today is after the drop deadline");
       }
        enrollmentRepository.delete(e);
   }

}