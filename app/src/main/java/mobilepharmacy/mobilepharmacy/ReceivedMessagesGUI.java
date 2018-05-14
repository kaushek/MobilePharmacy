package mobilepharmacy.mobilepharmacy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.AddCustomer;
import model.AddCustomerTexts;
import model.ReplyMessages;

public class ReceivedMessagesGUI extends AppCompatActivity {

    ListView listViewMessage;
    FirebaseDatabase database;
    DatabaseReference reference;

    ArrayList<String> list;
    private List<ReplyMessages> MessageList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    ReplyMessages pharReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_messages_gui);

        Bundle bundle = getIntent().getExtras();

        listViewMessage = (ListView)findViewById(R.id.customrMessageListView);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("ReplyMessages");

        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.customer_received_message_view, R.id.receivedmessagefromphar, list);

        pharReply = new ReplyMessages();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren() )
                {

                    pharReply = ds.getValue(ReplyMessages.class);
                    list.add(pharReply.getFrm().toString() + "\t\t\t\t\t\t\t\t" + pharReply.getDate().toString()+ "\n" + pharReply.getSub().toString());
                    MessageList.add(pharReply);
                    Log.d("TAG", "onDataChange: " + pharReply.getFrm().toString());

                }
                listViewMessage.setAdapter(adapter);




            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
