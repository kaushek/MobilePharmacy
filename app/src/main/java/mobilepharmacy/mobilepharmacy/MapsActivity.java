package mobilepharmacy.mobilepharmacy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.AddCustomer;
import model.NotifyPharmacist;

import static mobilepharmacy.mobilepharmacy.R.id.map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList markerPoints= new ArrayList();

    private MarkerOptions mrkOpt = new MarkerOptions();
    private LatLng lalg;
    private LatLng colombo;

    private static final String TAG = "MapsActivity";
    private static final int Error_Dialog_Request = 901;

    private static final String Fine_Location = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String Coarse_Location = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int Location_Permission_Request_Code = 1234;
    private static final float Default_Zoom = 15f;

    //widgets
    private EditText search;
    private ImageView gps;
    private ImageView cust;
    private ImageView logout;
    private ImageView settings;
    private ImageView notification;

    //variables
    private boolean permissionGranted = false;
    private FusedLocationProviderClient locationProviderClient;

    FirebaseDatabase database;
    DatabaseReference reference;
    AddCustomer customer;

    public String from;
    public String DelStatus;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (permissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            try {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Customers");

                customer= (AddCustomer) getIntent().getSerializableExtra("CustomerData");
                lalg = new LatLng(customer.getLat(), customer.getLng());
                Log.d(TAG, "onCreate: " + customer.getLat() +" " + customer.getLng());
                mrkOpt.position(lalg);
                mrkOpt.title(customer.getFname() + " " + customer.getLname());
                markerPoints.add(lalg);
                mMap.addMarker(mrkOpt);

            }catch (NullPointerException ex)
            {
                Log.d(TAG, "onCreate: " + ex.getMessage());
            }
            init();
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {

                    try {
                        if (permissionGranted) {
                            final Task location = locationProviderClient.getLastLocation();
                            location.addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Location currentLocation = (Location) task.getResult();
                                        colombo = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())/* new LatLng(6.927079, 79.861244))*/;
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombo, Default_Zoom));
                                        mMap.addMarker(new MarkerOptions().position(colombo).title("My current location"));
                                        markerPoints.add(colombo);



                                        if (markerPoints.size() ==2) {

                                            LatLng origin = colombo;
                                            LatLng dest = lalg;

                                            // Getting URL to the Google Directions API
                                            String url = getDirectionsUrl(origin, dest);

                                            DownloadTask downloadTask = new DownloadTask();

                                            // Start downloading json data from Google Directions API
                                            downloadTask.execute(url);
                                        }

                                    } else {
                                        Toast.makeText(MapsActivity.this, "Unable to find the current location", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    } catch (SecurityException ex) {
                        Log.d(TAG, "getDeviceLocation: SecurityException : " + ex.getMessage());

                    }

                }
            });


        }


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        search = (EditText)findViewById(R.id.SearchText);
        gps =(ImageView) findViewById(R.id.GpsBtn);
        cust = (ImageView)findViewById(R.id.CusBtn);
        logout = (ImageView)findViewById(R.id.DeliveryLogout);
        settings = (ImageView)findViewById(R.id.DeliverySettings);
        notification = (ImageView)findViewById(R.id.sendNotification);

        isServiceFine();

        getLocationPermission();

        cust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, CustomerPopUp.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreference(MapsActivity.this);
                Intent intent = new Intent(MapsActivity.this,Login.class);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, DeliveryManMyAccount.class);
                startActivity(intent);
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference();

                from = "DeliveryMan";
                DelStatus = "Delivered";
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Do u want to notify the pharmacist?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        SendNotification(from, DelStatus);

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
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
                            Toast.makeText(MapsActivity.this, "Unable to find the current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException ex) {
            Log.d(TAG, "getDeviceLocation: SecurityException : " + ex.getMessage());

        }
    }

    public static void getSharedPreference(Context context){
        SharedPreferences preferences  = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().commit();

        Log.d("Checking","CheckingSharedPreference"+preferences.getString("USERJOBROLE",null));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void SendNotification(String frm, String stat)
    {
        NotifyPharmacist notifyPharmacist = new NotifyPharmacist(frm, stat, false);
        reference.child("DeliveryStatus").push().setValue(notifyPharmacist);
        Toast.makeText(this, "Notification sent", Toast.LENGTH_SHORT).show();
    }


    public boolean isServiceFine()
    {
        Log.d(TAG, "isServiceFine: checking google service version");

        int availabe = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);

        if(availabe == ConnectionResult.SUCCESS)
        {
            Log.d(TAG, "isServiceFine: Google Play Services is working" );
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(availabe))
        {
            Log.d(TAG, "isServiceFine: Google Play Services is not working" );
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapsActivity.this, availabe,Error_Dialog_Request);
            dialog.show();
        }
        else
        {
            Toast.makeText(this, "cant make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void initMap()
    {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    public void geoLocate()
    {
        String seachString = search.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(seachString,1);

        }catch (IOException ex)
        {
            Log.d(TAG,"geoLocate: "+ ex);
        }

        if (list.size() > 0)
        {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: Location Found: " + address.toString());

            LatLng addr = new LatLng(address.getLatitude(), address.getLongitude())/* new LatLng(6.927079, 79.861244))*/;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(addr, Default_Zoom));
            mMap.addMarker(new MarkerOptions().position(addr).title(addr.toString()));
        }
    }

    private void init()
    {
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER)
                {
                    geoLocate();
                }
                return false;
            }
        });
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
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

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            PaarserActivity parserTask = new PaarserActivity();


            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class PaarserActivity extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jsonObject;
            List<List<HashMap<String, String>>> route = null;

            try {
                jsonObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser jsonParser = new DirectionsJSONParser();

                route = jsonParser.parse(jsonObject);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return route;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList endPoints = null;
            PolylineOptions polylineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                endPoints = new ArrayList();
                polylineOptions = new PolylineOptions();

                List<HashMap<String, String>> hashMapsPath = result.get(i);

                for (int j = 0; j < hashMapsPath.size(); j++) {
                    HashMap<String, String> point = hashMapsPath.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng latLngPosition = new LatLng(lat, lng);

                    endPoints.add(latLngPosition);
                }

                polylineOptions.addAll(endPoints);
                polylineOptions.width(12);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(polylineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}

