package mobilepharmacy.mobilepharmacy;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(170);
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_call);
                // add to menu
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
//                    num = customer.getNum();
                    Log.d("TAG", "onDataChange: " + customer.getNum());

//                    Log.wtf("Tag","cf" +customer.getFname().toString());
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
                                // open
                                Log.d("CustomerListView", "onMenuItemClick: " + index);
                                break;
                            case 1:
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
