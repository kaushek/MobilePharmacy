package mobilepharmacy.mobilepharmacy;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.app.NotificationManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import model.AddCustomerTexts;
import model.NotifyPharmacist;


public class PharmacistMainGUI extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public ImageButton but1;
    public ImageButton but2;
    public ImageButton but3;
    private DrawerLayout drawerLayout2;
    private ActionBarDrawerToggle toggle2;

    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference reference2;

    AddCustomerTexts CustomerMessages;
    NotifyPharmacist notifyPharmacist;

    String dataTitle, dataMessage;
    String title, message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacist_main_gui);


        title = "Delivered";
        message = "Successful";

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("AddMessageItems");
        reference2 = database.getReference().child("DeliveryStatus");

        CustomerMessages = new AddCustomerTexts();


        but1 = (ImageButton)findViewById(R.id.receivedMessage);
        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PharmacistMainGUI.this, MessageListView.class);
                startActivity(intent);
            }
        });

        but2 = (ImageButton)findViewById(R.id.customerDetails);
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PharmacistMainGUI.this, CustomerListView.class);
                startActivity(intent);
            }
        });


        drawerLayout2 = (DrawerLayout) findViewById(R.id.pharMainDrawer);
        toggle2 = new ActionBarDrawerToggle(this, drawerLayout2, R.string.open, R.string.close);

        drawerLayout2.addDrawerListener(toggle2);
        toggle2.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView2 = (NavigationView) findViewById(R.id.navigationPhar);
        navigationView2.setNavigationItemSelectedListener(this);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds!=null){
                        CustomerMessages = ds.getValue(AddCustomerTexts.class);
                        if(CustomerMessages.getStatus()==false){

                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            Intent notificationIntent = new Intent(getApplicationContext(), MessageListView.class);
                            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                                    notificationIntent, 0);
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder =
                                    new NotificationCompat.Builder(PharmacistMainGUI.this)
                                            .setContentTitle("Prescription from " +CustomerMessages.getFrom())
                                            .setSmallIcon(R.drawable.messageicon)
                                            .setContentIntent(intent)
                                            .setSound(defaultSoundUri);

                            Log.d("PharmacistMainGUI", "onDataChange: Received from" + CustomerMessages.getFrom() );
                            notificationManager.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE) /* ID of notification */, notificationBuilder.build());


                            AddCustomerTexts addCustomerTexts1 = new AddCustomerTexts(CustomerMessages.getTo(),CustomerMessages.getFrom()
                            ,CustomerMessages.getSubject(),CustomerMessages.getNotes(),CustomerMessages.getImgKey(),CustomerMessages.getUrl(), CustomerMessages.getDate(), CustomerMessages.getOrder(), true);
                             reference.child(ds.getKey()).setValue(addCustomerTexts1);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if (ds != null)
                    {
                        notifyPharmacist = ds.getValue(NotifyPharmacist.class);
                        if (notifyPharmacist.getStatus() ==  false)
                        {
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder =
                                    new NotificationCompat.Builder(PharmacistMainGUI.this)
                                            .setContentTitle("Delivery status " + notifyPharmacist.getFrom())
                                            .setContentText("Status: " + notifyPharmacist.getDelStatus())
                                            .setSmallIcon(R.drawable.messageicon)
//                                            .setContentIntent(intent)
                                            .setSound(defaultSoundUri);

                            Log.d("PharmacistMainGUI", "onDataChange: Received from" + notifyPharmacist.getFrom());
                            notificationManager.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE) /* ID of notification */, notificationBuilder.build());

                            NotifyPharmacist notifyPharmacist1 = new NotifyPharmacist(notifyPharmacist.getFrom(), notifyPharmacist.getDelStatus(),true);
                            Log.d("PharmacistMainGUI", "onDataChange: " +ds.getKey());
                            reference2.child(ds.getKey()).setValue(notifyPharmacist1);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void receiveNotification(){

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
//                    Log.d("PharmacistMainGUI: ", "onChildAdded: Datasnapshot: " + ds);
//                    CustomerMessages = ds.getValue(AddCustomerTexts.class);
//                    s = CustomerMessages.getFrom();
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(PharmacistMainGUI.this)
                            .setContentTitle("Prescription Received from " + s).setSmallIcon(R.drawable.messageicon).setSound(defaultSoundUri);

                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message");
        builder.setMessage("title: " + dataTitle + "\n" + "message: " + dataMessage);
        builder.setPositiveButton("OK", null);
        builder.show();
    }



    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle2.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_P_home)
        {
            PharmacyHome pHomefrag = new PharmacyHome();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.pharmaLinLayout, pHomefrag).commit();
        }
        if(id == R.id.nav_P_account)
        {
            this.closeContextMenu();
            PharmacistMyAccount pharfragment = new PharmacistMyAccount();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.pharmaLinLayout, pharfragment).commit();
        }

        if(id == R.id.nav_P_AddDeliveryMan)
        {
            RegisterEmployee deliveryManFrag = new RegisterEmployee();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.pharmaLinLayout, deliveryManFrag).commit();
        }
        if (id == R.id.nav_P_logout)
        {
            getSharedPreference(this);
            Intent intent = new Intent(this,Login.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.pharMainDrawer);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public static void getSharedPreference(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().commit();

        Log.d("Checking", "CheckingSharedPreference" + preferences.getString("USERJOBROLE", null));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
