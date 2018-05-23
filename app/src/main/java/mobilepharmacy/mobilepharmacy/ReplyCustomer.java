package mobilepharmacy.mobilepharmacy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import model.ReplyMessages;

public class ReplyCustomer extends AppCompatActivity {

    private TextView to;
    private TextView from;
    private Spinner subject;
    private EditText message;
    private Button sendreply;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference1;
    private static String TAG = ReplyCustomer.class.getSimpleName();
    private String To;
    private String From;
    private String Subject;
    private String Message;
    String formattedDate;
    private String messageKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_customer);

        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference1 = firebaseDatabase.getReference("ReplyMessages");

        to = (TextView)findViewById(R.id.toTV);
        from = (TextView)findViewById(R.id.frmTv);
        subject = (Spinner)findViewById(R.id.subjectspnr);
        message = (EditText)findViewById(R.id.messageEtxt);
        sendreply = (Button)findViewById(R.id.sendreplyBtn);

        if(getIntent()!=null){
            messageKey = getIntent().getStringExtra("messageKey");
        }
        Bundle bundle = getIntent().getExtras();
        String replyto = bundle.getString("Customer");

        to.setText(replyto);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c);

        if (message.getText() != null) {
            sendreply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    To = to.getText().toString();
                    From = from.getText().toString();
                    Subject = subject.getSelectedItem().toString();
                    Message = message.getText().toString();
                    sendReplyToCustomer(To, From, Subject, Message, formattedDate);
                    Toast.makeText(ReplyCustomer.this, "Message Sent", Toast.LENGTH_SHORT).show();
                    clearFields();
                }
            });
        }
        else {
            Toast.makeText(this, "Please type a message to send", Toast.LENGTH_SHORT).show();
        }


        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void sendReplyToCustomer(String to, String frm, String sub, String msg, String date)
    {
        ReplyMessages replyMessages = new ReplyMessages(to, frm, sub, msg, date, false);
        databaseReference.child("ReplyMessages").push().setValue(replyMessages);
        if(messageKey!=null){
            Log.wtf(TAG,"MessageKEy is :"+messageKey);
            databaseReference.child("AddMessageItems").child(messageKey).removeValue();
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void clearFields()
    {
        message.setText("");
    }
}
