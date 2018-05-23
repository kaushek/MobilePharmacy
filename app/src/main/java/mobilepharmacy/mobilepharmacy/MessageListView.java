package mobilepharmacy.mobilepharmacy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.AddCustomerTexts;
import model.ReplyMessages;

public class MessageListView extends AppCompatActivity {

    ListView listViewMessage;
    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference reference2;

    ArrayList<String> list;
    public static List<AddCustomerTexts> customerMessageList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    private static String TAG = MessageListView.class.getSimpleName();
    AddCustomerTexts custText;
    ReplyMessages replyMessages;

    Query delete;
    int listviewPossition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list_view);

        listViewMessage = (ListView)findViewById(R.id.messageListView);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("AddMessageItems");
        reference2 = database.getReference("ReplyMessages");


        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.message_view, R.id.messages, list);

        custText = new AddCustomerTexts();
        replyMessages = new ReplyMessages();

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren() )
                {
                    Log.d("MessageListView", "onDataChange: " + ds);
                    custText = ds.getValue(AddCustomerTexts.class);
                    custText.setId(ds.getKey());
                    Log.wtf(TAG,"Message KEy is :"+custText.getId());
                    list.add(custText.getFrom().toString() + "\t\t\t\t\t\t\t\t\t\t" +custText.getDate().toString() + "\n" + custText.getOrder().toString());
                    customerMessageList.add(custText);


                }
                listViewMessage.setAdapter(adapter);
                listViewMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MessageListView.this, ShowMessages.class);
                        intent.putExtra("MessageData", customerMessageList.get(position));
                        intent.putExtra("messageKey",customerMessageList.get(position).getId());
                        startActivity(intent);
                        listviewPossition = position;
                        Log.d("MessageListView", "onItemClick: listPossition" + listviewPossition);
                        finish();

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        reference2.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                    replyMessages = dataSnapshot1.getValue(ReplyMessages.class);
//                    if (replyMessages.getStatus() == true) {
//
//                        listViewMessage.getSelectedItemPosition();
////                        reference.getRef().removeValue();
////                        deletefromDB();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
         this.finish();
    }

    private void deletefromDB() {
        delete = reference.orderByChild("from").equalTo(custText.getFrom());
        delete.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
