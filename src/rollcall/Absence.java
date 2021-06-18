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
public class Absence {
    private int count;
    private String stu_id;
    private String stu_name;
    private String department;
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public String getStu_id() {
        return stu_id;
    }
    
    public void setStu_id(String stu_id) {
        this.stu_id = stu_id;
    }
    
    public String getStu_name() {
        return stu_name;
    }
    
    public void setStu_name(String stu_name) {
        this.stu_name = stu_name;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }

    public Absence(int count, String stu_id, String stu_name, String department) {
        this.count = count;
        this.stu_id = stu_id;
        this.stu_name = stu_name;
        this.department = department;
    }
}

