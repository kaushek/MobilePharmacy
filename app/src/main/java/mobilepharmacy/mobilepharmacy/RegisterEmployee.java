package mobilepharmacy.mobilepharmacy;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterEmployee extends Fragment {
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

    private List<AddDeliveryMan> DmanList = new ArrayList<>();

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

                AddToDB(NameEmp, JobRoleEmp, UsernameEmp, PasswordEmp);
                firebaseAuth.createUserWithEmailAndPassword(UsernameEmp, PasswordEmp).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressDialog.dismiss();
                        if(task.isSuccessful())
                        {
//                            Toast.makeText(CustomerSignUp.this, "Registration successful", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(CustomerSignUp.this, Login.class);
//                            startActivity(intent);
                            UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = userInfo.getUid();
                            addUserRoletoDB(userId, JobRoleEmp);
                        }
                        else {
//                            log.e("ERROR: CustomerSignUp: ", task.getException().toString());
                            Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_SHORT).show();

                            FirebaseAuthException e = (FirebaseAuthException )task.getException();
                            Toast.makeText(getActivity(), "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
//                try {
//                    firebaseAuth.createUserWithEmailAndPassword(UsernameEmp, PasswordEmp)
//                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
////                               UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmailAsync(email).get();
//                                    if (task.isSuccessful()) {
//                                        UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();
//                                        Toast.makeText(getActivity(), userInfo.getUid(), Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
//                                    }
//
//                                     UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();
//                                String userId = userInfo.getUid();
//                                addUserRoletoDB(userId, JobRoleEmp);
//
////                                Log.d("check user info", "onComplete: " + userInfo.getEmail());
//                                    Intent intent = new Intent(getActivity(), Login.class);
////                                intent.putExtra("DeliveryManDetails", userInfo.getEmail());
//                                }
////                            OnFailureListener
//                            });
//                }
//                catch (Exception ex)
//                {
//                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
//                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
//                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
//                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
//                }

            }
        });
        return  view;
    }

    private void AddToDB(String Ename, String JbRole, String uName, String uPass )
    {
        AddDeliveryMan addDeliveryMan = new AddDeliveryMan(Ename, JbRole, uName, uPass);
        databaseReference.child("Employees").push().setValue(addDeliveryMan);
    }

    private void addUserRoletoDB( String userId, String jbrole)
    {
        UserRole userRole = new UserRole(jbrole);
        databaseReference.child("UserRole").child(userId).setValue(userRole);
    }

}
