package com.cst438.domain;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class Enrollment {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="enrollment_id")
    private int enrollment_id;
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
	
	// TODO complete this class
    // add additional attribute for grade
    // create relationship between enrollment and user entities
    // create relationship between enrollment and section entities
    // add getter/setter methods


    public void setGrade(String grade) {
        this.grade = grade;
    }
    public String getGrade() {
        return grade;
    }
    public void setEnrollmentId(int enrollmentId) {
        this.enrollment_id = enrollmentId;
    }
    public int getEnrollmentId() {
        return enrollment_id;
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
