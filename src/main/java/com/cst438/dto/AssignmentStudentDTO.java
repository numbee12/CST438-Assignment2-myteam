package com.cst438.dto;

/*
 * Data Transfer Object for assignment data including student's grade
 */
public record AssignmentStudentDTO(
        int assignmentId,

        String title,
        String dueDate,
        String courseId,
        int sectionId,
        Integer score
) {
}
