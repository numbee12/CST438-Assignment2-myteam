package com.cst438.domain;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class Enrollment {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="enrollment_id")
    private int enrollment_id;
    @OneToMany(mappedBy = "enrollment")
    List<String> grade;
    @ManyToOne
    @JoinColumn(name="user_id")
    private int user_id;
    @ManyToOne
    @JoinColumn(name="section_no")
    private int section_no;
	
	// TODO complete this class
    // add additional attribute for grade
    // create relationship between enrollment and user entities
    // create relationship between enrollment and section entities
    // add getter/setter methods


    public void setGrade(String grade) {
        this.grade.add(grade);
    }
    public List<String> getGrade() {
        return grade;
    }
    public void setEnrollmentId(int enrollmentId) {
        this.enrollment_id = enrollmentId;
    }
    public int getEnrollmentId() {
        return enrollment_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public int getUser_id() {
        return user_id;
    }
    public void setSection_no(int section_no) {
        this.section_no = section_no;
    }
    public int getSection_no() {
        return section_no;
    }
}
