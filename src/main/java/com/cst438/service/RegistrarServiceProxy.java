package com.cst438.service;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.dto.CourseDTO;
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

    public void updateEnrollment(Enrollment e) {
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

    // TODO: Add Code To Receiver Helper Methods
    
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
    private void deleteCourse(String s) {
        courseRepository.deleteById(s);
    }
    private void addSection(String s) {
    }
    private void updateSection(String s) {
    }
    private void deleteSection(String s) {
    }
    private void addUser(String s) {
    }
    private void updateUser(String s) {
    }
    private void deleteUser(String s) {
    }
    private void addEnrollment(String s) {
    }
    private void deleteEnrollment(String s) {
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