package mobilepharmacy.mobilepharmacy;

import java.io.Serializable;

/**
 * Created by Kaushek on 14/04/2018.
 */

public class AddDeliveryMan implements Serializable{

    private String empName;
    private String empJbRole;
    private String empUname;
    private String empPass;

    AddDeliveryMan(){}

    public AddDeliveryMan(String eName, String ERole, String EuName, String Epass)
    {
        this.empName = eName;
        this.empJbRole = ERole;
        this.empUname = EuName;
        this.empPass = Epass;
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
