package mobilepharmacy.mobilepharmacy;


import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class PharmacistMyAccount extends Fragment {

    AddDeliveryMan Employees;
    private EditText EmName;

    FirebaseDatabase database;
    DatabaseReference reference;

    public PharmacistMyAccount() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pharmacist_my_account, container, false);

        

//        database = FirebaseDatabase.getInstance();
//        reference = database.getReference("Employees");
//
//        Employees = (AddDeliveryMan) getActivity().getIntent().getSerializableExtra("PharmacistDetails");
//
//        EmName = (EditText)view.findViewById(R.id.pName);
//        EmName.setText(Employees.getEmpUname());


//        registerEmployee.get
//        view.getIntent().getSerializableExtra("MyClass");
        return view;
    }

}
