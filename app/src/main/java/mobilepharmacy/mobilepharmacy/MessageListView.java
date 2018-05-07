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

import model.AddCustomerTexts;

public class MessageListView extends AppCompatActivity {

    ListView listViewMessage;
    FirebaseDatabase database;
    DatabaseReference reference;

    ArrayList<String> list;
    private List<AddCustomerTexts> customerMessageList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    AddCustomerTexts custText;
//    AddCustomer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list_view);

        listViewMessage = (ListView)findViewById(R.id.messageListView);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("AddMessageItems");

        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.message_view, R.id.messages, list);

//        customer = new AddCustomer();
        custText = new AddCustomerTexts();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren() )
                {
                    Log.d("MessageListView", "onDataChange: " + ds);
                    custText = ds.getValue(AddCustomerTexts.class);
                    list.add(custText.getFrom().toString() + "\n" + custText.getNotes().toString());
                    customerMessageList.add(custText);

                }
                listViewMessage.setAdapter(adapter);
                listViewMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MessageListView.this, ShowMessages.class);
                        intent.putExtra("MessageData", customerMessageList.get(position));
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
