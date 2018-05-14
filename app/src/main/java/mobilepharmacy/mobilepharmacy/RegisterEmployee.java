package mobilepharmacy.mobilepharmacy;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import model.AddEmployees;
import model.UserRole;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterEmployee extends Fragment implements Serializable{
    private EditText empName;
    private Spinner empJobRole;
    private EditText empUserName;
    private EditText empPassword;
    private Button empRegister;

    private String NameEmp;
    private String JobRoleEmp;
    private String UsernameEmp;
    private String PasswordEmp;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseAuth firebaseAuth;

    private List<AddEmployees> DmanList = new ArrayList<>();

    private static final String EMAIL_REGEX = "^(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    Boolean b;

    public RegisterEmployee() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_emp, container, false);
        empName = (EditText)view.findViewById(R.id.empNameTxt);
        empJobRole = (Spinner)view.findViewById(R.id.spinnerJobRole);
        empUserName = (EditText)view.findViewById(R.id.empUserNameTxt);
        empPassword = (EditText)view.findViewById(R.id.empPasswordTxt);
        empRegister = (Button)view.findViewById(R.id.empRegisterBtn);

        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        empRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NameEmp = empName.getText().toString();
                JobRoleEmp = empJobRole.getSelectedItem().toString();
                UsernameEmp = empUserName.getText().toString();
                PasswordEmp = empPassword.getText().toString();

                b = (empUserName.getText().toString()).matches(EMAIL_REGEX);
                if (NameEmp != null || JobRoleEmp != null || UsernameEmp != null || PasswordEmp != null) {


                    try {
                        firebaseAuth.createUserWithEmailAndPassword(UsernameEmp, PasswordEmp).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();
                                    String userId = userInfo.getUid();
                                    addUserRoletoDB(userId, JobRoleEmp);
                                    AddToDB(NameEmp, JobRoleEmp, UsernameEmp, PasswordEmp, userId);
                                    ClearFields();
                                    Toast.makeText(getActivity(), "Registration Successful.", Toast.LENGTH_SHORT).show();
                                } else {

                                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                    Toast.makeText(getActivity(), "Failed to register: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    } catch (Exception Ex) {
                        Toast.makeText(getActivity(), "Please fill the empty fields.", Toast.LENGTH_SHORT).show();
                    }
                }

                else
                {
                    Toast.makeText(getActivity(), "Please provide some values", Toast.LENGTH_SHORT).show();
                }


            }
        });
        return  view;
    }

    private void AddToDB(String Ename, String JbRole, String uName, String uPass, String uid )
    {
        AddEmployees addDeliveryMan = new AddEmployees(Ename, JbRole, uName, uPass, uid);
        databaseReference.child("Employees").child(uid).setValue(addDeliveryMan);
    }

    private void addUserRoletoDB( String userId, String jbrole)
    {
        UserRole userRole = new UserRole(jbrole);
        databaseReference.child("UserRole").child(userId).setValue(userRole);
    }


    public void ClearFields()
    {
        empName.setText("");
//        empJobRole;
        empUserName.setText("");
        empPassword.setText("");

    }

}
