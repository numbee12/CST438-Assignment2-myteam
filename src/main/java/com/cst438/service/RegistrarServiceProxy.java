package com.cst438.service;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Section;
import com.cst438.domain.SectionRepository;
import com.cst438.domain.Term;
import com.cst438.domain.TermRepository;
import com.cst438.domain.User;
import com.cst438.domain.UserRepository;
import com.cst438.dto.CourseDTO;
import com.cst438.dto.EnrollmentDTO;
import com.cst438.dto.SectionDTO;
import com.cst438.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class RegistrarServiceProxy {

    Queue registrarServiceQueue = new Queue("registrar_service", true);

    @Bean
    public Queue createQueue() {
        return new Queue("gradebook_service", true);
    }

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    CourseRepository courseRepository;
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    TermRepository termRepository;

    public void updateEnrollment(EnrollmentDTO e) {
        String msg = "updateEnrollment " + asJsonString(e);
        sendMessage(msg);
    }

    @RabbitListener(queues = "gradebook_service")
    public void receiveFromRegistrar(String message)  {
        // received message from registrar service
        try {
            System.out.println("Received from registrar service: " + message);
            String[] parts = message.split(" ", 2);
            String instr = parts[0];
            String dtoStr = parts[1];
            switch (instr) {
                case "addCourse":
                {
                    System.out.println("Add course: " + dtoStr);
                    addCourse(dtoStr);   
                    break;
                }
                case "updateCourse":
                {
                    System.out.println("Update course: " + dtoStr);
                    updateCourse(dtoStr);
                    break;
                }
                case "deleteCourse":
                {
                    System.out.println("Delete course: " + dtoStr);
                    deleteCourse(dtoStr);
                    break;
                }
                case "addSection":
                {
                    System.out.println("Add section: " + dtoStr);
                    addSection(dtoStr);
                    break;
                }
                case "updateSection":
                {
                    System.out.println("Update section: " + dtoStr);
                    updateSection(dtoStr);
                    break;
                }
                case "deleteSection":
                {
                    System.out.println("Delete section: " + dtoStr);
                    deleteSection(dtoStr);
                    break;
                }
                case "addUser":
                {
                    System.out.println("Add user: " + dtoStr);
                    addUser(dtoStr);
                    break;
                }
                case "updateUser":
                {
                    System.out.println("Update user: " + dtoStr);
                    updateUser(dtoStr);
                    break;
                }
                case "deleteUser":
                {
                    System.out.println("Delete user: " + dtoStr);
                    deleteUser(dtoStr);
                    break;
                }
                case "addEnrollment":
                {
                    System.out.println("Add enrollment: " + dtoStr);
                    addEnrollment(dtoStr);
                    break;
                }
                case "deleteEnrollment":
                {
                    System.out.println("Delete enrollment: " + dtoStr);
                    deleteEnrollment(dtoStr);
                    break;
                }
                default:
                {
                    System.out.println("Malformed instruction: " + instr);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error receiving from registrar service: " + e.getMessage());
        }
    }
    
    private void addCourse(String s) {
        CourseDTO dto = fromJsonString(s, CourseDTO.class);
        Course c = new Course();
        c.setCredits(dto.credits());
        c.setTitle(dto.title());
        c.setCourseId(dto.courseId());
        courseRepository.save(c);
    }
    private void updateCourse(String s) throws Exception {
        CourseDTO dto = fromJsonString(s, CourseDTO.class);
        Course c = courseRepository.findById(dto.courseId()).orElse(null);
        if (c == null) {
            throw new Exception("Course not found: " + dto.courseId());
        }
        c.setTitle(dto.title());
        c.setCredits(dto.credits());
		courseRepository.save(c);
    }
    private void deleteCourse(String s) throws Exception {
        if (s != null && courseRepository.existsById(s)) {
            courseRepository.deleteById(s);
        } else {
            throw new Exception("Course not found: " + s);
        }
    }
    private void addSection(String s) throws Exception {
        SectionDTO sectionDTO = fromJsonString(s, SectionDTO.class);

        Course course = courseRepository.findById(sectionDTO.courseId()).orElse(null);
        if (course == null ){
            throw new Exception("Course not found: " + sectionDTO.courseId());
        }

        Section section = new Section();
        section.setSectionNo(sectionDTO.secNo());
        section.setCourse(course);

        Term term = termRepository.findByYearAndSemester(sectionDTO.year(), sectionDTO.semester());
        if (term == null) {
            throw new Exception("year, semester invalid");
        }
        section.setTerm(term);
        section.setSecId(sectionDTO.secId());
        section.setBuilding(sectionDTO.building());
        section.setRoom(sectionDTO.room());
        section.setTimes(sectionDTO.times());

        User instructor = null;
        if (sectionDTO.instructorEmail()==null || sectionDTO.instructorEmail().equals("")) {
            section.setInstructor_email("");
        } else {
            instructor = userRepository.findByEmail(sectionDTO.instructorEmail());
            if (instructor == null || !instructor.getType().equals("INSTRUCTOR")) {
                throw new Exception("email not found or not an instructor " + sectionDTO.instructorEmail());
            }
            section.setInstructor_email(sectionDTO.instructorEmail());
        }
        sectionRepository.save(section);
    }
    private void updateSection(String s) throws Exception {
        SectionDTO sectionDTO = fromJsonString(s, SectionDTO.class);
        Section section = sectionRepository.findById(sectionDTO.secNo()).orElse(null);

        if (s==null) {
            throw new Exception("section not found "+sectionDTO.secNo());
        }
        section.setSecId(sectionDTO.secId());
        section.setBuilding(sectionDTO.building());
        section.setRoom(sectionDTO.room());
        section.setTimes(sectionDTO.times());

        User instructor = null;
        if (sectionDTO.instructorEmail()==null || sectionDTO.instructorEmail().equals("")) {
            section.setInstructor_email("");
        } else {
            instructor = userRepository.findByEmail(sectionDTO.instructorEmail());
            if (instructor == null || !instructor.getType().equals("INSTRUCTOR")) {
                throw new Exception("email not found or not an instructor " + sectionDTO.instructorEmail());
            }
            section.setInstructor_email(sectionDTO.instructorEmail());
        }
        sectionRepository.save(section);
    }
    private void deleteSection(String s) throws Exception {
        try {
            Integer id = Integer.parseInt(s);
            sectionRepository.deleteById(id);
        } catch (NumberFormatException e) {
            throw new Exception("Section contains assignments or enrollments: " + s);
        }

    }
    private void addUser(String s) throws Exception {
        UserDTO dto = fromJsonString(s, UserDTO.class);
        User u = new User();
        u.setId(dto.id());
        u.setName(dto.name());
        u.setEmail(dto.email());
        // FIXME: since db expects password is not null, set password to empty string for now
        // I don't believe we need password on our end & UserDTO does not contain password
        // May need to change this in the future
        u.setPassword("");

        u.setType(dto.type());
        if (!dto.type().equals("STUDENT") &&
            !dto.type().equals("INSTRUCTOR") &&
            !dto.type().equals("ADMIN")) {
            // invalid type
            throw new Exception("invalid user type");
        }
        userRepository.save(u);
    }
    private void updateUser(String s) throws Exception {
        UserDTO dto = fromJsonString(s, UserDTO.class);
        User u = userRepository.findById(dto.id()).orElse(null);
        if (u==null) {
            throw new Exception("user id not found");
        }
        u.setName(dto.name());
        u.setEmail(dto.email());
        u.setType(dto.type());
        if (!dto.type().equals("STUDENT") &&
            !dto.type().equals("INSTRUCTOR") &&
            !dto.type().equals("ADMIN")) {
            // invalid type
            throw new Exception("invalid user type");
        }
        userRepository.save(u);
    }
    private void deleteUser(String s) throws Exception {
            try {
                Integer id = Integer.parseInt(s);
                userRepository.deleteById(id);
            } catch (NumberFormatException e) {
                throw new Exception("User not found: " + s);
            }
    }
    private void addEnrollment(String s) throws Exception {
        EnrollmentDTO dto = fromJsonString(s, EnrollmentDTO.class);
        User student = userRepository.findById(dto.studentId()).orElse(null);
        if (student == null) {
            throw new Exception("Student not found: " + dto.studentId());
        }
        Section section = sectionRepository.findById(dto.sectionNo()).orElse(null);
        if (section == null) {
            throw new Exception("Section not found: " + dto.sectionNo());
        }
        Enrollment e = new Enrollment();
        e.setEnrollmentId(dto.enrollmentId());
        e.setGrade(dto.grade());
        e.setStudent(student);
        e.setSection(section);
        enrollmentRepository.save(e);
    }

    private void deleteEnrollment(String s) throws Exception {
        try {
            Integer id = Integer.parseInt(s);
            enrollmentRepository.deleteById(id);
        } catch (NumberFormatException e) {
            throw new Exception("Enrollment not found: " + s);
        }
    }

    private void sendMessage(String s) {
        rabbitTemplate.convertAndSend(registrarServiceQueue.getName(), s);
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