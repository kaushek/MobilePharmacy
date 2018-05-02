package mobilepharmacy.mobilepharmacy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerPopUp extends AppCompatActivity {

    ListView listView;
    FirebaseDatabase database;
    DatabaseReference reference;

    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    private List<AddCustomer> customerList = new ArrayList<>();

    AddCustomer customer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_pop_up);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width),(int)(height*.7));

        listView = (ListView)findViewById(R.id.custListVw);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Customers");

        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.activity_cust_layout,R.id.cus, list);

        customer = new AddCustomer();
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
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
