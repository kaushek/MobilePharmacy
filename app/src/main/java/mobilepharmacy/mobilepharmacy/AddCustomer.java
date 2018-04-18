package mobilepharmacy.mobilepharmacy;

import java.io.Serializable;

/**
 * Created by Kaushek on 14/03/2018.
 */

public class AddCustomer implements Serializable {

    private String fname;
    private String lname;
    private String email;
    private String pass;
    private String confPass;
    private int num;
    private String add;
    private String ccity;
    private String carea;

    AddCustomer()
    {

    }

    public AddCustomer(String FName, String LName, String EMail, String PWD, String CONFPWD, int NUM, String ADD, String CCity, String CArea)
    {
        this.fname = FName;
        this.lname = LName;
        this.email = EMail;
        this.pass = PWD;
        this.confPass = CONFPWD;
        this.num = NUM;
        this.add = ADD;
        this.ccity = CCity;
        this.carea = CArea;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getConfPass() {
        return confPass;
    }

    public void setConfPass(String confPass) {
        this.confPass = confPass;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public String getCcity() {
        return ccity;
    }

    public void setCcity(String ccity) {
        ccity = ccity;
    }

    public String getCarea() {
        return carea;
    }

    public void setCarea(String carea) {
        this.carea = carea;
    }
}
