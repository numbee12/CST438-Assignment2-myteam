package com.cst438.domain;

import jakarta.persistence.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Entity
public class Assignment {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="assignment_id")
    private int assignmentId;

    // add additional attributes for title, dueDate
    private String title;
    private Date dueDate;

    // add relationship between assignment and section entities
    @ManyToOne
    @JoinColumn(name="section_no", nullable=false)
    private Section section;

    // add getter and setter methods
    public int getAssignmentId(){return assignmentId;}
    public void setAssignmentId(int assignmentId){this.assignmentId= assignmentId;}
    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}
    public String getDueDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(dueDate);}
    public void setDueDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
           dueDate = new Date(dateFormat.parse(dateString).getTime());
        } catch (ParseException e) {
            System.err.println("Error parsing dueDate: " + e.getMessage());
        }
    }
    public Section getSection(){return section;}
    public void setSection(Section section){this.section= section;}
}
