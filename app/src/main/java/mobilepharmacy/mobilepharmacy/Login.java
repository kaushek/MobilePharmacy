package mobilepharmacy.mobilepharmacy;
;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import model.AddCustomer;
import model.AddEmployees;
import model.UserRole;

public class Login extends AppCompatActivity {

    private Button btnLogin;
    private EditText username;
    private EditText password;
    private TextView textView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser users;
    FirebaseDatabase database;
    private String userRole;
    DatabaseReference reference;

private static String userJobRole;

    AddEmployees Employees;
    AddCustomer customer;
    final AddEmployees deliveryMan = new AddEmployees();
    private List<RegisterEmployee> Employee = new ArrayList<>();

    private static final String EMAIL_REGEX = "^(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    private Boolean b;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        retreiveSharedVariableValues(Login.this);
        Log.d("Login", "before if condition: " + userJobRole);
        if(userJobRole!=null){
            Log.d("TAG","Customer : "+userJobRole);
            if(userJobRole.equals("Customer")){
                Intent intent =new Intent(this,CustomerMainGUI.class);
                startActivity(intent);
            }else{
                Log.d("Login", "Pharmacist: " + userJobRole);
                if(userJobRole.equals("Pharmacist")){
                    Intent intent = new Intent(this,PharmacistMainGUI.class);
                    startActivity(intent);
                }
                else{
                    Log.d("Login", "Delivery Man: " + userJobRole);
                    if(userJobRole.equals("Delivery Man")) {
                        Intent intent = new Intent(this, MapsActivity.class);
                        startActivity(intent);
                    }
                }
            }

        }
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        reference = database.getReference();

        textView = (TextView) findViewById(R.id.TxtView);
        String text = "Not Registered? Click Here..";
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Login.this,CustomerSignUp.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.RED);
            }
        };
        spannableString.setSpan(clickableSpan,16,28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        btnLogin = (Button)findViewById(R.id.login);
        username = (EditText)findViewById(R.id.userName);
        password = (EditText)findViewById(R.id.pwdTxt);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("check user info", "onComplete: " + "Test");

                b = (username.getText().toString()).matches(EMAIL_REGEX);

                try {
                    if (username.getText() != null && password.getText() != null) {
                         progressDialog = ProgressDialog.show(Login.this, "Please wait..", "You are signing in", true);

                        firebaseAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            users = firebaseAuth.getCurrentUser();

                                            Log.d("Login", "onComplete: CurrentUser: " + users.getEmail());
                                            getUserDetails();

                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                            builder.setTitle("Login Failed");
                                            builder.setMessage("Username or Password is incorrect.");
                                            builder.setPositiveButton("OK", null);
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    }

                                });


                        loginFunction(username.getText().toString(), password.getText().toString());

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    public static void getSharedPreference(String id,String fname,String lname,String pass,int num,String lat,String longtitude,String email,String cPass,String uRole,Context context){
        SharedPreferences preferences  = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("USERID",id);
        editor.putString("USERFIRSTNAME", fname);
        editor.putString("USERLASTNAME", lname);
        editor.putString("USERPASSWORD", pass);
        editor.putInt("USERPHONENUMBER", num);
        editor.putString("USERLAT", lat);
        editor.putString("USERLONGITUDE", longtitude);
        editor.putString("USEREMAIL", email);
        editor.putString("USERCONFIRMPASSWORD", cPass);
        editor.putString("USERJOBROLE", uRole);
        editor.commit();
    }


    public static void retreiveSharedVariableValues(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        userJobRole = prefs.getString("USERJOBROLE",null);
        if(userJobRole!=null){
            Log.d("TAG","Shared Preference UserID :"+userJobRole);

        }
    }

    private void setDataToSharedPreference(){
        Log.d("Login", "setDataToSharedPreference: UserRole: " + userRole);
        if(userRole.equals("Customer")){
//            Log.d("Login", "setDataToSharedPreference: UserRole: " + userRole);
            reference.child("Customers").child(users.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    AddCustomer customer = dataSnapshot.getValue(AddCustomer.class);
                  String lat =  customer.getLat().toString();
                    String lng=  customer.getLng().toString();
                getSharedPreference( customer.getUserID(),customer.getFname(),customer.getLname(), customer.getPass(),
                        customer.getNum(),lat,lng, customer.getEmail(),  customer.getConfPass(),"Customer",Login.this );
                    }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            reference.child("Employees").child(users.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    AddEmployees employee = dataSnapshot.getValue(AddEmployees.class);
                    Log.d("TAG","EmployeeID"+employee.getEmpId());
                    getSharedPreference( employee.getEmpId(),employee.getEmpName(),null, employee.getEmpPass(),
                          0,null,null,employee.getEmpUname(),  null,employee.getEmpJbRole(),Login.this );
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    private void getUserDetails(){
        reference.child("UserRole").child(users.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserRole user = dataSnapshot.getValue(UserRole.class);
                Log.d("Check",user.getJobRole());
                userRole = user.getJobRole();
                setDataToSharedPreference();
                Log.d("Login", "onDataChange: CurrentUserRole: " + userRole);
//                Toast.makeText(Login.this, "Job Role :"+user.getJobRole(), Toast.LENGTH_SHORT).show();
                if(user.getJobRole().equals("Delivery Man")){
                    Intent intent = new Intent(Login.this,MapsActivity.class);
                    startActivity(intent);
//                    setDataToSharedPreference();
                }else if (user.getJobRole().equals("Pharmacist")){
                    Intent intent = new Intent(Login.this, PharmacistMainGUI.class);
                    startActivity(intent);
                }
                else if (user.getJobRole().equals("Customer")){
                    Intent intent = new Intent(Login.this,CustomerMainGUI.class);
                    startActivity(intent);
                }



            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }



    private void loginFunction(String uName, String uPass)
    {
        if ((uName.equals("c") ) && (uPass.equals("c")))
        {
            Intent activitylogin = new Intent(Login.this, CustomerMainGUI.class);
            startActivity(activitylogin);
        }
        else if ((uName.equals("p") ) && (uPass.equals("p")))
        {
            Intent activityPharmacist = new Intent(Login.this, PharmacistMainGUI.class);
            startActivity(activityPharmacist);
        }
        else if ((uName.equals("d")) && (uPass.equals("d")))
        {
            Intent activitymaps = new Intent(Login.this, MapsActivity.class);
            startActivity(activitymaps);
        }

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}

