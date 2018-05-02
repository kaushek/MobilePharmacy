package mobilepharmacy.mobilepharmacy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class CustomerListView extends AppCompatActivity {

    ListView listView;
    FirebaseDatabase database;
    DatabaseReference reference;

    ArrayList<String> list;
    private List<AddCustomer> customerList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    AddCustomer customer;
    public static final String KEY_FNAME="fname";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list_view);

        listView = (ListView)findViewById(R.id.cusListView);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Customers");

        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.customer_view,R.id.cusInfo, list);

        customer = new AddCustomer();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren() )
                {
                    customer = ds.getValue(AddCustomer.class);
                    list.add(customer.getFname().toString() + " " + customer.getLname().toString() + "\n" + customer.getNum());
                    customerList.add(customer);
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

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


}
