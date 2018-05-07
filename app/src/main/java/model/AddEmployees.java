package model;

import java.io.Serializable;

/**
 * Created by Kaushek on 14/04/2018.
 */

public class AddEmployees implements Serializable{

    private String empName;
    private String empJbRole;
    private String empUname;
    private String empPass;
    private String empId;

    public AddEmployees(){}

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public AddEmployees(String empName, String empJbRole, String empUname, String empPass, String empId) {

        this.empName = empName;
        this.empJbRole = empJbRole;
        this.empUname = empUname;
        this.empPass = empPass;
        this.empId = empId;
    }


    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpJbRole() {
        return empJbRole;
    }

    public void setEmpJbRole(String empJbRole) {
        this.empJbRole = empJbRole;
    }

    public String getEmpUname() {
        return empUname;
    }

    public void setEmpUname(String empUname) {
        this.empUname = empUname;
    }

    public String getEmpPass() {
        return empPass;
    }

    public void setEmpPass(String empPass) {
        this.empPass = empPass;
    }
}
