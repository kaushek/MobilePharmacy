package mobilepharmacy.mobilepharmacy;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import model.AddCustomer;
import model.UserRole;

public class CustomerSignUp extends AppCompatActivity implements OnMapReadyCallback {


    private static final String TAG = "CustomersignUp";

    GoogleMap mMap;
    private static final String Fine_Location = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String Coarse_Location = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean permissionGranted = false;
    private static final int Location_Permission_Request_Code = 123;
    private FusedLocationProviderClient locationProviderClient;
    private static final float Default_Zoom = 15f;

    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText password;
    private EditText confirmpassword;
    private EditText mobile;
    private EditText address;
    private EditText city;
    private EditText area;
    private EditText custRole;
    private Button signUp;

    private String fnme;
    private String lnme;
    private String eml;
    private String pwd;
    private String confpwd;
    private int num;
    private String role;

    Double lat;
    Double lng;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


    ArrayList<LatLng> markerPoints;

    ProgressDialog progressDialog;

    private static final String EMAIL_REGEX  = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private Boolean b;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        markerPoints = new ArrayList<LatLng>();

        if (permissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(CustomerSignUp.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(CustomerSignUp.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    if(markerPoints.size()>1){
                        markerPoints.clear();
                        mMap.clear();
                    }

                    markerPoints.add(latLng);

                    LatLng location = new LatLng(latLng.latitude, latLng.longitude);
                    lat = location.latitude;
                    lng = location.longitude;

                    if(markerPoints.size()==1) {
                        mMap.addMarker(new MarkerOptions().title("Home").position(location));
                    }
                    Log.d(TAG, "onMapLongClick: Lat: " + lat + "Lng: " + lng);
                }
            });
        }

    }

    private void initUIFields(){

        signUp = (Button) findViewById(R.id.signUpCustomer);
        firstname = (EditText) findViewById(R.id.fnameTxt);
        lastname = (EditText) findViewById(R.id.lnameTxt);
        email = (EditText) findViewById(R.id.emailTxt);
        password = (EditText) findViewById(R.id.passwordTxt);
        confirmpassword = (EditText) findViewById(R.id.confirmPasswordTxt);
        mobile = (EditText) findViewById(R.id.mobileTxt);
        address = (EditText) findViewById(R.id.AddressTxt);
        city = (EditText) findViewById(R.id.cityTxt);
        area = (EditText) findViewById(R.id.areaTxt);
        custRole = (EditText) findViewById(R.id.customerRoleTxt);
    }
    private void getDataFromFields(){
        fnme = firstname.getText().toString();
        lnme = lastname.getText().toString();
        eml = email.getText().toString();
        pwd = password.getText().toString();
        confpwd = confirmpassword.getText().toString();
        num = Integer.parseInt(mobile.getText().toString());
        role = custRole.getText().toString();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sign_up);

        getLocationPermission();

        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        initUIFields();

        custRole.setText("Customer");
        custRole.setVisibility(View.INVISIBLE);
        address.setVisibility(View.INVISIBLE);
        city.setVisibility(View.INVISIBLE);
        area.setVisibility(View.INVISIBLE);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                b = (email.getText().toString()).matches(EMAIL_REGEX);

                try {

                    if (firstname.getText() != null && lastname.getText() != null && email.getText() != null && password.getText() != null
                            && confirmpassword.getText() != null && mobile.getText() != null && custRole.getText() != null) {

                        if (b == true) {

                            if(markerPoints.size()==1) {

                                if (validateMobilenum()) {
                                    progressDialog = ProgressDialog.show(CustomerSignUp.this, "Please wait..", "Your Profile is being created", true);
                                    getDataFromFields();
                                    firebaseAuth.createUserWithEmailAndPassword(eml, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            progressDialog.dismiss();
                                            if (task.isSuccessful()) {

                                                Toast.makeText(CustomerSignUp.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();
                                                String userId = userInfo.getUid();
                                                customerRole(userId, role);
                                                addtoDatabase(fnme, lnme, eml, pwd, confpwd, num, lat, lng, userId);
                                                Intent intent = new Intent(CustomerSignUp.this, Login.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(CustomerSignUp.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(CustomerSignUp.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(CustomerSignUp.this, "Please pin your current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(CustomerSignUp.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CustomerSignUp.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {
                    progressDialog.dismiss();
                    Toast.makeText(CustomerSignUp.this, "Please fill all the fields to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void addtoDatabase(String fname, String lname, String email, String password, String conPassword, int number, Double lat, Double lng,String userID) {
        AddCustomer addCustomer = new AddCustomer(fname, lname, email, password, conPassword, number,lat, lng,userID);
        databaseReference.child("Customers").child(userID).setValue(addCustomer);
    }

    private void customerRole(String Id, String Cusrole) {
        UserRole customerRole = new UserRole(Cusrole);
        databaseReference.child("UserRole").child(Id).setValue(customerRole);
    }

    public boolean validateMobilenum()
    {
        try
        {
            if (mobile.getText() == null && mobile.getText().length() < 0 && mobile.getText().length() > 10)
            {
                return false;
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    private void initMap()
    {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.Custmap);
        mapFragment.getMapAsync(CustomerSignUp.this);
    }

    private void getLocationPermission()
    {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),Fine_Location) == PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),Coarse_Location) == PackageManager.PERMISSION_GRANTED)
            {
                permissionGranted = true;
                initMap();
            }
            else {
                ActivityCompat.requestPermissions(this, permissions, Location_Permission_Request_Code);
            }
        }
        else {
            ActivityCompat.requestPermissions(this, permissions, Location_Permission_Request_Code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionGranted = false;

        switch (requestCode)
        {
            case Location_Permission_Request_Code: {
                if (grantResults.length > 0 )
                {
                    for (int i = 0; i <grantResults.length; i ++)
                    {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            permissionGranted = false;
                            return;
                        }
                    }
                    permissionGranted = true;
                    initMap();
                }
            }
        }
    }

    private void getDeviceLocation() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (permissionGranted) {
                final Task location = locationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            LatLng colombo = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())/* new LatLng(6.927079, 79.861244))*/;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombo, Default_Zoom));

                        } else {
                            Toast.makeText(CustomerSignUp.this, "Unable to find the current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException ex) {
            Log.d(TAG, "getDeviceLocation: SecurityException : " + ex.getMessage());

        }
    }

}
