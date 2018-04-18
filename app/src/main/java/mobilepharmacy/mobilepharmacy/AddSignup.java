package mobilepharmacy.mobilepharmacy;

/**
 * Created by Kaushek on 25/02/2018.
 */

public class AddSignup {
    private String Name;
    private String Address;

    public AddSignup(){}
    public AddSignup(String name,String address){
        this.Name=name;
        this.Address=address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
