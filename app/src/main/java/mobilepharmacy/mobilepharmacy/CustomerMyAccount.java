package mobilepharmacy.mobilepharmacy;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerMyAccount extends Fragment {
//    private EditText firstName;
//    private EditText lastName;
//    private EditText number;
//    private EditText cusAddress;
//    private EditText cusArea;
//    private EditText cusCity;
//    private Button saveToDb;
//
//    private String fname;
//    private String lname;
//    private int num;
//    private String add;
//    private String Ccity;
//    private String Carea;
//
//
//    private FirebaseDatabase firebaseDatabase;
//    private DatabaseReference databaseReference;
//
//    public CustomerMyAccount() {
//        // Required empty public constructor
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_my_account, container, false);
//        firebaseDatabase = firebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference();
//
//        firstName = (EditText) view.findViewById(R.id.firstName);
//        lastName = (EditText)view.findViewById(R.id.lastName);
//        number = (EditText)view.findViewById(R.id.phone);
//        cusAddress = (EditText)view.findViewById(R.id.address);
//        cusArea = (EditText)view.findViewById(R.id.area);
//        cusCity = (EditText)view.findViewById(R.id.city);
//
//        saveToDb = (Button)view.findViewById(R.id.save);
//
//        saveToDb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fname = firstName.getText().toString();
//                lname = lastName.getText().toString();
//                num = Integer.parseInt(number.getText().toString());
//                add = cusAddress.getText().toString();
//                Ccity = cusCity.getText().toString();
//                Carea = cusArea.getText().toString();
//
//                addtoDatabase(fname, lname,num, add, Ccity, Carea);
//
//            }
//        });
        return view;
    }

//    private void addtoDatabase(String Fn, String Ln, int Nm, String Add, String CC, String CA)
//    {
//     //   AddCustomer addCus = new AddCustomer(Fn, Ln, Nm, Add, CC, CA);
//    //    databaseReference.child("Customers").push().setValue(addCus);
//    }

}
