package model;

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
    String userID;


    public AddCustomer()
    {

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public AddCustomer(String fname, String lname, String email, String pass, String confPass, int num, Double lat, Double lng, String userID) {

        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.pass = pass;
        this.confPass = confPass;
        this.num = num;
        this.lat = lat;
        this.lng = lng;
        this.userID = userID;
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
