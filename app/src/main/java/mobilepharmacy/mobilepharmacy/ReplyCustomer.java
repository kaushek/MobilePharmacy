package mobilepharmacy.mobilepharmacy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import model.ReplyMessages;

public class ReplyCustomer extends AppCompatActivity {

    private TextView to;
    private TextView from;
    private Spinner subject;
    private EditText message;
    private Button sendreply;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String To;
    private String From;
    private String Subject;
    private String Message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_customer);

        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        to = (TextView)findViewById(R.id.toTV);
        from = (TextView)findViewById(R.id.frmTv);
        subject = (Spinner)findViewById(R.id.subjectspnr);
        message = (EditText)findViewById(R.id.messageEtxt);
        sendreply = (Button)findViewById(R.id.sendreplyBtn);


//        ArrayAdapter<String> adapter;
//        List<String> list;
//
//        list = new ArrayList<String>();
//        list.add("Your order has been dispatched.");
//        list.add("Sorry. Stocks are not available at the moment");
//
//        adapter = new ArrayAdapter<String>(getApplicationContext(),
//                android.R.layout.simple_spinner_item, list);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        subject.setAdapter(adapter);

        if (Message != null) {
            sendreply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    To = to.getText().toString();
                    From = from.getText().toString();
                    Subject = subject.getSelectedItem().toString();
                    Message = message.getText().toString();
                    sendReplyToCustomer(To, From, Subject, Message);
                    Toast.makeText(ReplyCustomer.this, "Message Sent", Toast.LENGTH_SHORT).show();
                    clearFields();
                }
            });
        }
        else {
            Toast.makeText(this, "Please type a message to send", Toast.LENGTH_SHORT).show();
        }
    }


    public void sendReplyToCustomer(String to, String frm, String sub, String msg)
    {
        ReplyMessages replyMessages = new ReplyMessages(to, frm, sub, msg);
        databaseReference.child("ReplyMessages").push().setValue(replyMessages);

    }

    private void clearFields()
    {
        message.setText("");
    }
}
