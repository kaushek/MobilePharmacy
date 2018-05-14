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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import model.AddCustomer;

public class CustomerListView extends AppCompatActivity {

    SwipeMenuListView listView;
    FirebaseDatabase database;
    DatabaseReference reference;

    ArrayList<String> list;
    private List<AddCustomer> customerList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    AddCustomer customer;

    public static final String KEY_FNAME="fname";

    private static final int RequestCode = 1;
    public Integer num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list_view);

        listView = (SwipeMenuListView)findViewById(R.id.cusListView);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Customers");

        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.customer_view,R.id.cusInfo, list);

        customer = new AddCustomer();



        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0x45,
                        0xD1, 0x61)));
                deleteItem.setWidth(170);
                deleteItem.setIcon(R.drawable.ic_call);
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setMenuCreator(creator);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren() )
                {
                    customer = ds.getValue(AddCustomer.class);
                    list.add(customer.getFname().toString() + " " + customer.getLname().toString() + "\n" + customer.getNum());
                    customerList.add(customer);
                    Log.d("TAG", "onDataChange: " + customer.getNum());

                }
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Intent intent = new Intent(CustomerListView.this, ShowCustomer.class);
                            intent.putExtra("data", customerList.get(position));
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
        if(ActivityCompat.checkSelfPermission(CustomerListView.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(CustomerListView.this, new String[] {android.Manifest.permission.CALL_PHONE}, RequestCode);
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
                Toast.makeText(CustomerListView.this, "Persmission Denied. Cant make the call.", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
