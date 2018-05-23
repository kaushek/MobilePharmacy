package mobilepharmacy.mobilepharmacy;

import android.app.NotificationManager;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import model.AddCustomerTexts;
import model.ReplyMessages;

public class CustomerMainGUI extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener   {

    public ImageButton but1;
    public ImageButton but2;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference reference;

    ReplyMessages replyMessages;

    public void showReceivedGUI()
    {
        but1 = (ImageButton) findViewById(R.id.inboxButton);
        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(CustomerMainGUI.this, ReceivedMessagesGUI.class);
                startActivity(activity);
            }
        });
    }

    public void showSendMessageGUI()
    {
        but2 = (ImageButton) findViewById(R.id.sendPrescription);
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity2 = new Intent(CustomerMainGUI.this, SendMessageGUI.class);
                startActivity(activity2);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main_gui);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("ReplyMessages");

        showReceivedGUI();
        showSendMessageGUI();

        drawerLayout = (DrawerLayout) findViewById(R.id.CusMainDrawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);


        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(ds!=null)
                    {
                        replyMessages = ds.getValue(ReplyMessages.class);
                        if(replyMessages.getStatus()==false){

                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            Intent notificationIntent = new Intent(getApplicationContext(), CustomerMainGUI.class);
                            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                                    notificationIntent, 0);
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder =
                                    new NotificationCompat.Builder(CustomerMainGUI.this)
                                            .setContentTitle("Message from " +replyMessages.getFrm())
                                            .setContentText(replyMessages.getSub())
                                            .setSmallIcon(R.drawable.messageicon)
                                            .setContentIntent(intent)
                                            .setSound(defaultSoundUri);

                            Log.d("PharmacistMainGUI", "onDataChange: Received from" + replyMessages.getFrm() );
                            notificationManager.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE) /* ID of notification */, notificationBuilder.build());


                            ReplyMessages replyMessages2 = new ReplyMessages(replyMessages.getTo(), replyMessages.getFrm(), replyMessages.getSub(),
                                    replyMessages.getMsg(), replyMessages.getDate(),true);
                            reference.child(ds.getKey()).setValue(replyMessages2);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home)
        {
            CustMainGUIFragment custFrag = new CustMainGUIFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.linerarLayout, custFrag).commit();
        }
        if (id == R.id.nav_account)
        {
            CustomerMyAccount fragmentCusMyAcc = new CustomerMyAccount();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.linerarLayout, fragmentCusMyAcc).commit();
        }
        else if(id == R.id.nav_pharmacy_details)
        {
            PharmacyDetails pharFrag = new PharmacyDetails();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.linerarLayout, pharFrag).commit();
        }
        else if (id == R.id.nav_logout)
        {
            getSharedPreference(this);
            Intent intent = new Intent(this,Login.class);
            startActivity(intent);
            Toast.makeText(this, "You have successfully logged out", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.CusMainDrawer);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static void getSharedPreference(Context context){
        SharedPreferences preferences  = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().commit();

        Log.d("Checking","CheckingSharedPreference"+preferences.getString("USERJOBROLE",null));
    }
}
