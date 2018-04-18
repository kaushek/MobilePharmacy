package mobilepharmacy.mobilepharmacy;

/**
 * Created by Kaushek on 18/04/2018.
 */

public class UserRole {
//    private String usrId;
    private String jobRole;

    UserRole(){}

    public UserRole( String role)
    {

        this.jobRole = role;
    }


    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public String getJobRole() {
        return jobRole;
    }
}
