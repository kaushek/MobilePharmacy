package mobilepharmacy.mobilepharmacy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class PharmacistMainGUI extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public ImageButton but1;
    public ImageButton but2;
    public ImageButton but3;

    private DrawerLayout drawerLayout2;
    private ActionBarDrawerToggle toggle2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacist_main_gui);

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
            //     Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
            PharmacyHome pHomefrag = new PharmacyHome();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.pharmaLinLayout, pHomefrag).commit();
        }
        if(id == R.id.nav_P_account)
        {
            this.closeContextMenu();
            //    Toast.makeText(this, "account", Toast.LENGTH_SHORT).show();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.pharMainDrawer);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}
