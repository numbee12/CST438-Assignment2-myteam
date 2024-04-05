package com.cst438.domain;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class Enrollment {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="enrollment_id")
    private int enrollmentId;
    // @OneToMany(mappedBy = "enrollment")
    // List<String> grade;
    @Column(name="grade")
    private String grade;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User student;
    @ManyToOne
    @JoinColumn(name="section_no", nullable=false)
    private Section section;

    public void setGrade(String grade) {
        this.grade = grade;
    }
    public String getGrade() {
        return grade;
    }
    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }
    public int getEnrollmentId() {
        return enrollmentId;
    }
    public void setStudent(User student) {
        this.student = student;
    }
    public User getStudent() {
        return student;
    }
    public void setSection(Section section) {
        this.section = section;
    }
    public Section getSection() {
        return section;
    }
}
