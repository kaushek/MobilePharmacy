package model;

/**
 * Created by Kaushek on 22/04/2018.
 */

public class CustomerRole {

    private String role;

    CustomerRole()
    {}

    public CustomerRole(String cusrole)
    {
        this.role = cusrole;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
