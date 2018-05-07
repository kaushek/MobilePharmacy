package mobilepharmacy.mobilepharmacy;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

import static mobilepharmacy.mobilepharmacy.R.id.map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList markerPoints= new ArrayList();

    private MarkerOptions mrkOpt = new MarkerOptions();
    private LatLng lalg;
    private LatLng colombo;
//    private LatLng lalg2;

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

    //variables
    private boolean permissionGranted = false;
    private FusedLocationProviderClient locationProviderClient;

    FirebaseDatabase database;
    DatabaseReference reference;
    AddCustomer customer;

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
                                        mMap.addMarker(new MarkerOptions().position(colombo));
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

        isServiceFine();

        getLocationPermission();

        cust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, CustomerPopUp.class);
                startActivity(intent);
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




   /* public void moveCam(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: lat: " + latLng.latitude + "lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


        mMap.addMarker(new MarkerOptions().title("Marker in colombo"));

        mMap.getCameraPosition();

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        }
*/

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */






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
          //  Toast.makeText(this, address.toString(),Toast.LENGTH_SHORT).show();

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

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
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

