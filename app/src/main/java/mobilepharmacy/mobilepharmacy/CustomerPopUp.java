package mobilepharmacy.mobilepharmacy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerPopUp extends AppCompatActivity {

    SwipeMenuListView listView;
    FirebaseDatabase database;
    DatabaseReference reference;

    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    private List<AddCustomer> customerList = new ArrayList<>();

    AddCustomer customer;
    public Integer num;
    private static final int RequestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_pop_up);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width),(int)(height*.7));

        listView = (SwipeMenuListView)findViewById(R.id.custListVw);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Customers");

        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.activity_cust_layout,R.id.cus, list);

        customer = new AddCustomer();

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem callItem = new SwipeMenuItem(
                        getApplicationContext());
                callItem.setBackground(new ColorDrawable(Color.rgb(0x45,
                        0xD1, 0x61)));
                callItem.setWidth(170);
                callItem.setIcon(R.drawable.ic_call);
                menu.addMenuItem(callItem);
            }
        };

        listView.setMenuCreator(creator);
        listView.setMinimumHeight(30);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren() )
                {
                    customer = ds.getValue(AddCustomer.class);
                    Log.d("CustomerPopUp", "onDataChange: " + customer.getLat());
                    list.add(customer.getFname().toString() + " " + customer.getLname().toString()  );
                    customerList.add(customer);

                }
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(CustomerPopUp.this, MapsActivity.class);
                        intent.putExtra("CustomerData", customerList.get(position));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Log.d("CustomerPopup", "onItemClick: " + Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);


                    }
                });
                listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                        switch (index) {

                            case 0:
                                // call
                                num = customerList.get(position).getNum();
                                Log.wtf("TAG","ListViewClicked"+customerList.get(position).getNum());
                                Log.wtf("TAG","ListViewClicked"+customerList.get(position).getFname());
                                Log.d("CustomerListView", "onMenuItemClick: " + index);
                                MakePhoneCall();
                                break;
                        }
                        // false : close the menu; true : not close the menu
                        return false;
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void MakePhoneCall()
    {
        if(ActivityCompat.checkSelfPermission(CustomerPopUp.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(CustomerPopUp.this, new String[] {android.Manifest.permission.CALL_PHONE}, RequestCode);
        }
        else {
            Log.d("CustomerListView", "MakePhoneCall: " + num);
            String dial = "tel:" + num;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestCode)
        {
            if (grantResults.length > 0 && grantResults[0] ==  PackageManager.PERMISSION_GRANTED)
            {
                MakePhoneCall();
            }
            else {
                Toast.makeText(CustomerPopUp.this, "Persmission Denied. Cant make the call.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
