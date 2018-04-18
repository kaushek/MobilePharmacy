package mobilepharmacy.mobilepharmacy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

import static mobilepharmacy.mobilepharmacy.CustomerListView.KEY_FNAME;

public class ShowCustomer extends AppCompatActivity  {

    FirebaseDatabase database;
    DatabaseReference reference;
    AddCustomer customer;

    //private EditText fname;
    private TextView fname;
    private TextView lname;
    private TextView email;
    private TextView mobile;
    private TextView address;
    private TextView area;
    private TextView city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_customer);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Customers");

       // final String id = getIntent().getExtras().getString ("data");
        customer= (AddCustomer) getIntent().getSerializableExtra("data");

        fname = (TextView)findViewById(R.id.fnameTV);
        fname.setText(customer.getFname());

        lname = (TextView)findViewById(R.id.lnameTV);
        lname.setText(customer.getLname());

        email = (TextView)findViewById(R.id.emailTV);
        email.setText(customer.getEmail());

        mobile = (TextView)findViewById(R.id.numTV);
        mobile.setText(String.valueOf(customer.getNum()));

        address = (TextView)findViewById(R.id.addressTV);
        address.setText(customer.getAdd());

        area = (TextView)findViewById(R.id.areaTV);
        area.setText(customer.getCarea());

        city = (TextView)findViewById(R.id.cityTV);
        city.setText(customer.getCcity());


    }
}
