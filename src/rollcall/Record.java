/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rollcall;

/**
 *
 * @author Han
 */
public class Record {
    private String course;
    private String classroom;
    private String optime;
    private String closetime;
    private String status;
    private String coursecode;
    
    public String getCourse() {
        return course;
    }
    
    public void setCourse(String course) {
        this.course = course;
    }
    
    public String getClassroom() {
        return classroom;
    }
    
    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }
    
    public String getOptime() {
        return optime;
    }
    
    public void setOptime(String optime) {
        this.optime = optime;
    }
    
    public String getClosetime() {
        return closetime;
    }
    
    public void setClosetime(String closetime) {
        this.closetime = closetime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCourseCode() {
        return coursecode;
    }
    
    public void setCourseCode(String coursecode) {
        this.coursecode = coursecode;
    }
    
    public Record(String course, String classroom, String optime, String closetime, String status, String coursecode) {
        this.course = course;
        this.classroom = classroom;
        this.optime = optime;
        this.closetime = closetime;
        this.status = status;
        this.coursecode = coursecode;
    }
}

