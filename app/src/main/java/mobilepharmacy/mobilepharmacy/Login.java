package mobilepharmacy.mobilepharmacy;
;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

//    private Button btnSignup;
    private Button btnLogin;
    private EditText username;
    private EditText password;
    private TextView textView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser users;
    FirebaseDatabase database;
    DatabaseReference reference;
//    private Button check;

    AddDeliveryMan addDeliveryMan;
    AddCustomer customer;
    final AddDeliveryMan deliveryMan = new AddDeliveryMan();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

//        btnSignup = (Button) findViewById(R.id.signUp);
        btnLogin = (Button)findViewById(R.id.login);
        username = (EditText)findViewById(R.id.userName);
        password = (EditText)findViewById(R.id.pwdTxt);

//        check = (Button)findViewById(R.id.btncheck);
//        check.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Login.this, SignUpDetails.class);
//                startActivity(intent);
//            }
//        });



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = ProgressDialog.show(Login.this, "Please wait..", "You are signing in", true);

                Log.d("check user info", "onComplete: " + "Test");

                firebaseAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful())
                                {
                                    users = firebaseAuth.getCurrentUser();
                                    getUserDetails();
                                }
                                else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                    builder.setTitle("Login Failed");
                                    builder.setMessage("Username or Password is incorrect.");

                                    // add a button
                                    builder.setPositiveButton("OK", null);

                                    // create and show the alert dialog
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }

                        });
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                progressDialog.dismiss();
//
//                                if (task.isSuccessful())
//                                {
//                                    users = firebaseAuth.getCurrentUser();
//                                    getUserDetails();
//
////                                    reference.addValueEventListener(new ValueEventListener() {
//////                                    reference.orderByChild("userId").equalTo(users.getUid())
//////                                            .addValueEventListener(new ValueEventListener() {
////                                        @Override
////                                        public void onDataChange(DataSnapshot dataSnapshot) {
////                                            for(DataSnapshot ds: dataSnapshot.getChildren() )
////                                            {
////                                                UserRole user = new UserRole();
////                                                user = ds.getValue(UserRole.class);
////                                                Toast.makeText(Login.this, user.getJobRole(), Toast.LENGTH_SHORT).show();
////
////                                            }
////
////
////                                        }
////
////                                        @Override
////                                        public void onCancelled(DatabaseError databaseError) {
////
////                                        }
////                                    });
////                                    FirebaseUser user = firebaseAuth.getCurrentUser();
////                                    //System.out.print(user);
////
////                                    Log.d("check user info", "onComplete: " + user);
////                                    Toast.makeText(Login.this, user.getEmail(), Toast.LENGTH_SHORT).show();
////                                    Toast.makeText(Login.this, user.getUid(), Toast.LENGTH_SHORT).show();
////                                    Toast.makeText(Login.this, user.toString(), Toast.LENGTH_SHORT).show();
////                                    String role = deliveryMan.getEmpJbRole();
////                                    addDeliveryMan = (AddDeliveryMan) getIntent().getSerializableExtra("DeliveryManDetails");
////                                    String role = addDeliveryMan.getEmpJbRole();
////                                    if ( role == "Delevery Man")
////                                    {
////                                        Intent activityPharmacist = new Intent(Login.this, PharmacistMainGUI.class);
////                                        startActivity(activityPharmacist);
////                                    }
////                                    else {
////                                        Toast.makeText(Login.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
////                                        Intent intent = new Intent(Login.this, CustomerMainGUI.class);
////                                        startActivity(intent);
////                                    }
//                                }
//                                else {
//                                 //   Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });


                loginFunction(username.getText().toString(), password.getText().toString());

            }
        });


//        btnSignup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Login.this,SignUpDetails.class);
//                startActivity(intent);
//
//            }
//        });


    }

    private void getUserDetails(){
        reference.child("UserRole").child(users.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserRole user = dataSnapshot.getValue(UserRole.class);
                Log.d("Check",user.getJobRole());
//                Toast.makeText(Login.this, "Job Role :"+user.getJobRole(), Toast.LENGTH_SHORT).show();
                if(user.getJobRole().equals("Delivery Man")){
                    Intent intent = new Intent(Login.this,MapsActivity.class);
                    startActivity(intent);
                }else if (user.getJobRole().equals("Pharmacist")){
                    Intent intent = new Intent(Login.this,PharmacistMainGUI.class);
                    startActivity(intent);
                }
                else if (user.getJobRole().equals("Customer")){
//                    String s = getIntent().getExtras().getString("CustomerRole");
                    Intent intent = new Intent(Login.this,CustomerMainGUI.class);
                    startActivity(intent);
//                    Intent intent2 = new Intent(Login.this,SendMessageGUI.class);
//                    intent2.putExtra("CustomerDetails", customer);
                }



            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        reference.child("UserRole").child(users.getUid()).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                UserRole user = dataSnapshot.getValue(UserRole.class);
//                Log.d("Check",user.getJobRole());
////                Toast.makeText(Login.this, "Job Role :"+user.getJobRole(), Toast.LENGTH_SHORT).show();
//                if(user.getJobRole().equals("Delivery Man")){
//                    Intent intent = new Intent(Login.this,MapsActivity.class);
//                    startActivity(intent);
//                }else if (user.getJobRole().equals("Pharmacist")){
//                    Intent intent = new Intent(Login.this,PharmacistMainGUI.class);
//                    startActivity(intent);
//                }
//                else if (user.getJobRole().equals("Customer")){
////                    String s = getIntent().getExtras().getString("CustomerRole");
//                    Intent intent = new Intent(Login.this,CustomerMainGUI.class);
//                    startActivity(intent);
//                }
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });



//        customer = new AddCustomer();
//        reference.child("Customers").child(users.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


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
//        else if (deliveryMan.getEmpJbRole() == "Delevery Man")
//        {
//            firebaseAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful())
//                            {
////                                Toast.makeText(Login.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
////                                Intent activitymaps = new Intent(Login.this, MapsActivity.class);
////                                startActivity(activitymaps);
//
//                                //String role = deliveryMan.getEmpJbRole();
//                                addDeliveryMan = (AddDeliveryMan) getIntent().getSerializableExtra("DeliveryManDetails");
////                                String role = addDeliveryMan.getEmpJbRole();
////                                if ( role == "Delevery Man")
////                                {
//                                    Intent activityPharmacist = new Intent(Login.this, PharmacistMainGUI.class);
//                                    startActivity(activityPharmacist);
////                                }
//                            }
//                            else {
////                                   Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        }

    }


}

