package mobilepharmacy.mobilepharmacy;


import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.renderscript.Double2;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import model.AddCustomer;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerMyAccount extends Fragment  {

    private EditText custFName;
    private EditText custLName;
    private EditText custPhoneNumber;
    private Button saveBtn;
    private Button editBtn;
    private int phoneNumber;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


    private SharedPreferences pref;
    private SharedPreferences.Editor editorSharedPreference;
    private static final String MY_PREF_NAME = "Phramacy";
    private static String userID;
    private static String userFiirstName;
    private static String userLastName;
    private static String userEmail;
    private static String userPassword;
    private static String userConfirmPassword;
    private static Double userLatitude;
    private static Double userLongitude;
    private static int userPhoneNumber;

    GoogleMap mMap;
    MapView mMapView;
    private static final String Fine_Location = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String Coarse_Location = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean permissionGranted = false;
    private static final int Location_Permission_Request_Code = 123;
    private FusedLocationProviderClient locationProviderClient;
    private static final float Default_Zoom = 15f;
    ArrayList<LatLng> markerPoints;
    Double lat;
    Double lng;


    private static final String TAG = "CustomerMyAccount";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_my_account, container, false);
        custFName = (EditText) view.findViewById(R.id.firstName);
        custLName = (EditText) view.findViewById(R.id.lastName);
        custPhoneNumber = (EditText) view.findViewById(R.id.phone);
        saveBtn = (Button) view.findViewById(R.id.save);
        editBtn=(Button) view.findViewById(R.id.edit);

        retreiveSharedVariableValues(getActivity());
        custFName.setText(userFiirstName);
        custLName.setText(userLastName);
        String retreivedPhoneNumber = String.valueOf(userPhoneNumber);

        custPhoneNumber.setText(retreivedPhoneNumber);
        custFName.setEnabled(false);
        custLName.setEnabled(false);
        custPhoneNumber.setEnabled(false);
        saveBtn.setEnabled(false);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.CustEditmap);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                markerPoints = new ArrayList<LatLng>();

                if (permissionGranted) {
                    getDeviceLocation();

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    LatLng location = new LatLng(userLatitude, userLongitude);
                    mMap.addMarker(new MarkerOptions().title("Home").position(location));
                }
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custFName.setEnabled(true);
                custLName.setEnabled(true);
                custPhoneNumber.setEnabled(true);
                saveBtn.setEnabled(true);

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

                if(isDataValid()){

                    saveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AddCustomer addCustomer = new AddCustomer(custFName.getText().toString(),custLName.getText().toString(),userEmail,userPassword,userConfirmPassword,phoneNumber,lat,lng,userID);
                            updateDB(addCustomer);
                            custFName.setEnabled(false);
                            custLName.setEnabled(false);
                            custPhoneNumber.setEnabled(false);
                            saveBtn.setEnabled(false);
                            getSharedPreference(userID,custFName.getText().toString(),custLName.getText().toString(),userPassword,phoneNumber,lat.toString(),lng.toString(),userEmail,userConfirmPassword,getActivity());
                        }
                    });
                }

            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLocationPermission();

        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();



    }
    private void updateDB(Object addCustomer){
        if(addCustomer!=null)
            databaseReference.child("Customers").child(userID).setValue(addCustomer);

    }

    public static void getSharedPreference(String id,String fname,String lname,String pass,int num,String lat,String longtitude,String email,String cPass,Context context){
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

        editor.commit();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private Boolean isDataValid(){
        if(custFName.getText()==null&&custFName.getText().length()<0){
            return false;
        }
        if(custLName.getText()==null&&custLName.getText().length()<0){
            return  false;
        }
        if(custPhoneNumber.getText()==null&&custPhoneNumber.getText().length()<0){
            return  false;
        }
        else{
            try{
                phoneNumber = Integer.parseInt(custPhoneNumber.getText().toString());
            }
            catch (Exception ex){
                Toast.makeText(getActivity(),"Please Check the PhoneNumber Field",Toast.LENGTH_LONG).show();
            }
        }

        return true;
    }

    public static void retreiveSharedVariableValues(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        userID = prefs.getString("USERID",null);

        userEmail = prefs.getString("USEREMAIL",null);
        userPassword = prefs.getString("USERPASSWORD",null);
        userFiirstName = prefs.getString("USERFIRSTNAME",null);
        userLastName = prefs.getString("USERLASTNAME",null);
        userConfirmPassword = prefs.getString("USERCONFIRMPASSWORD",null);
        userLatitude = Double.parseDouble(prefs.getString("USERLAT",null));
        userLongitude = Double.parseDouble(prefs.getString("USERLONGITUDE",null));
        userPhoneNumber = prefs.getInt("USERPHONENUMBER",0);
        if(userID!=null){
            Log.d("TAG","Shared Preference UserID :"+userID);
            Log.d("TAG","Shared Preference UserID :"+userEmail);
            Log.d("TAG","Shared Preference UserID :"+userPassword);
            Log.d("TAG","Shared Preference UserID :"+userConfirmPassword);
            Log.d("TAG","Shared Preference UserID :"+userLatitude);
            Log.d("TAG","Shared Preference UserID :"+userLongitude);
            Log.d("TAG","Shared Preference UserID :"+userPhoneNumber);
        }
    }


//    private void initMap()
//    {
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
//                .findFragmentById(R.id.CustEditmap);
//        mapFragment.getMapAsync(this);
//
//    }

    private void getLocationPermission()
    {
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),Fine_Location) == PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),Coarse_Location) == PackageManager.PERMISSION_GRANTED)
            {
                permissionGranted = true;
//                initMap();
            }
            else {
                ActivityCompat.requestPermissions(getActivity(), permissions, Location_Permission_Request_Code);
            }
        }
        else {
            ActivityCompat.requestPermissions(getActivity(), permissions, Location_Permission_Request_Code);
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
//                    initMap();
                }
            }
        }
    }


    private void getDeviceLocation() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

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
                            Toast.makeText(getActivity(), "Unable to find the current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException ex) {
            Log.d(TAG, "getDeviceLocation: SecurityException : " + ex.getMessage());

        }
    }





}
