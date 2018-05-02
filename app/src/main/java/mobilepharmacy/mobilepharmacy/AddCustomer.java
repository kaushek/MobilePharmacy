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
    Double lat;
    Double lng;


    AddCustomer()
    {

    }

    public AddCustomer(String FName, String LName, String EMail, String PWD, String CONFPWD, int NUM, Double latitude, Double longtitude)
    {
        this.fname = FName;
        this.lname = LName;
        this.email = EMail;
        this.pass = PWD;
        this.confPass = CONFPWD;
        this.num = NUM;
        this.lat = latitude;
        this.lng = longtitude;

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

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

}
