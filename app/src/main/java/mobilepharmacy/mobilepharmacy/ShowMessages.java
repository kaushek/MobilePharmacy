package mobilepharmacy.mobilepharmacy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class ShowMessages extends AppCompatActivity implements Serializable {

    FirebaseDatabase database;
    DatabaseReference reference;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    AddCustomerTexts CustomerMessages;

    public static TextView frm;
    private EditText notes;
    private ImageView viewPresciption;
    private Button sendReply;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_messages);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("AddMessageItems");


        firebaseStorage = firebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("image");
//        String x = (storageRef.getDownloadUrl()).toString();

        CustomerMessages = (AddCustomerTexts) getIntent().getSerializableExtra("MessageData");

        frm = (TextView)findViewById(R.id.FromTV);
        frm.setText(CustomerMessages.getFrom());

        notes = (EditText)findViewById(R.id.NoteEtex);
        notes.setText(CustomerMessages.getNotes());

        viewPresciption = (ImageView) findViewById(R.id.PrescriptionImgView);
//        Glide.with(ShowMessages.this).load(CustomerMessages.getUrl()).into(viewPresciption);
        Log.d("CheckMessage", "onCreate: " + CustomerMessages.getImgKey());

//        String url = "https://firebasestorage.googleapis.com/v0/b/mobilepharmacyfinal.appspot.com/o/image%2Fjpg?alt=media&token=8a3086bf-8c13-436a-b585-e3c14d4d1771";
        Glide.with(this).load(CustomerMessages.getUrl()).into(viewPresciption);
        viewPresciption.getDisplay();
//        viewPresciption.setImageURI(SendMessageGUI.FILE_PATH);
//        viewPresciption.setImageBitmap();

        sendReply = (Button) findViewById(R.id.replyMessageBtn);
        sendReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowMessages.this, ReplyCustomer.class);
//                intent.putExtra("CustomerName", CustomerMessages.getFrom());
                startActivity(intent);
            }
        });

    }
}
