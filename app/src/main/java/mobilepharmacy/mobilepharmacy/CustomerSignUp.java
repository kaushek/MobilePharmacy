package mobilepharmacy.mobilepharmacy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerSignUp extends AppCompatActivity {

    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText password;
    private EditText confirmpassword;
    private EditText mobile;
    private EditText address;
    private EditText city;
    private EditText area;
    private Button signUp;

    private String fnme;
    private String lnme;
    private String eml;
    private String pwd;
    private String confpwd;
    private int num;
    private String addrs;
    private String cty;
    private String ara;


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sign_up);

        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        signUp =(Button)findViewById(R.id.signUpCustomer);
        firstname=(EditText)findViewById(R.id.fnameTxt);
        lastname= (EditText)findViewById(R.id.lnameTxt);
        email = (EditText)findViewById(R.id.emailTxt);
        password = (EditText)findViewById(R.id.passwordTxt);
        confirmpassword = (EditText)findViewById(R.id.confirmPasswordTxt);
        mobile = (EditText)findViewById(R.id.mobileTxt);
        address = (EditText)findViewById(R.id.AddressTxt);
        city = (EditText)findViewById(R.id.cityTxt);
        area = (EditText)findViewById(R.id.areaTxt);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = ProgressDialog.show(CustomerSignUp.this, "Please wait..", "Your Profile is being created", true);
                fnme = firstname.getText().toString();
                lnme = lastname.getText().toString();
                eml = email.getText().toString();
                pwd = password.getText().toString();
                confpwd = confirmpassword.getText().toString();
                num = Integer.parseInt(mobile.getText().toString());
                addrs = address.getText().toString();
                cty = city.getText().toString();
                ara = area.getText().toString();

                addtoDatabase(fnme,lnme,eml,pwd, confpwd, num, addrs,cty, ara);
                firebaseAuth.createUserWithEmailAndPassword(eml, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful())
                        {
                            Toast.makeText(CustomerSignUp.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CustomerSignUp.this, Login.class);
                            startActivity(intent);
                        }
                        else {
//                            log.e("ERROR: CustomerSignUp: ", task.getException().toString());
                            Toast.makeText(CustomerSignUp.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void addtoDatabase(String fname, String lname, String email, String password, String conPassword, int number, String address, String city, String area)
    {
        AddCustomer addCustomer = new AddCustomer(fname, lname, email, password, conPassword, number, address, city, area);
        databaseReference.child("Customers").push().setValue(addCustomer);

    }
}
