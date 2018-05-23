package mobilepharmacy.mobilepharmacy;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import model.AddEmployees;


/**
 * A simple {@link Fragment} subclass.
 */
public class PharmacistMyAccount extends Fragment {

    private EditText name;
    private EditText email;
    private EditText password;
    private Button edit;
    private Button save;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private static String empname;
    private static String empJbRole;
    private static String empUname;
    private static String empPass;
    private static String userID;

    public PharmacistMyAccount() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pharmacist_my_account, container, false);

        name = (EditText)view.findViewById(R.id.PharAccNameEtxt);
        email = (EditText)view.findViewById(R.id.PharAccEmailEtxt);
        password = (EditText)view.findViewById(R.id.PharAccPassEtxt);
        edit = (Button)view.findViewById(R.id.PharAccEditBtn);
        save = (Button)view.findViewById(R.id.PharAccSaveBtn);

        retreiveSharedVariableValues(getActivity());
        name.setText(empname);
        email.setText(empUname);
        password.setText(empPass);

        name.setEnabled(false);
        email.setEnabled(false);
        password.setEnabled(false);
        save.setEnabled(false);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setEnabled(true);
                email.setEnabled(true);
                password.setEnabled(true);
                save.setEnabled(true);

                if (isValid())
                {
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AddEmployees employees = new AddEmployees(name.getText().toString(), empJbRole, email.getText().toString(), password.getText().toString(), userID);
                            UpdateDatabase(employees);
                            name.setEnabled(false);
                            email.setEnabled(false);
                            password.setEnabled(false);
                            save.setEnabled(false);
                            getSharedPreference(userID, name.getText().toString(), email.getText().toString(), password.getText().toString(),getActivity());
                            Toast.makeText(getActivity(), "Updated the changes successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });

        return view;
    }

    private void UpdateDatabase(Object AddEmployees)
    {
        if (AddEmployees!=null)
        {
            databaseReference.child("Employees").child(userID).setValue(AddEmployees);
        }
    }

    private Boolean isValid()
    {
        if(name.getText()==null && name.getText().length()<0){
            return false;
        }
        if(email.getText()==null && email.getText().length()<0){
            return  false;
        }
        if(password.getText()==null && password.getText().length()<0){
            return  false;
        }


        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static void getSharedPreference(String id,String name, String email, String pass,Context context){
        SharedPreferences preferences  = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("USERID",id);
        editor.putString("USERFIRSTNAME", name);
        editor.putString("USEREMAIL", email);
        editor.putString("USERPASSWORD", pass);

        editor.commit();
    }

    public static void retreiveSharedVariableValues(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        userID = prefs.getString("USERID",null);

        empUname = prefs.getString("USEREMAIL",null);
        empPass = prefs.getString("USERPASSWORD",null);
        empname = prefs.getString("USERFIRSTNAME",null);

        if(userID!=null){
            Log.d("TAG","Shared Preference UserID :"+userID);
            Log.d("TAG","Shared Preference UserID :"+empUname);
            Log.d("TAG","Shared Preference UserID :"+empPass);
            Log.d("TAG","Shared Preference UserID :"+empname);
        }
    }
}
