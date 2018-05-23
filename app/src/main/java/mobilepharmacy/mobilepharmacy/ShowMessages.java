package mobilepharmacy.mobilepharmacy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import model.AddCustomerTexts;

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
    private String messageKEy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_messages);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("AddMessageItems");

        firebaseStorage = firebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("image");

        if(getIntent()!=null){
            CustomerMessages = (AddCustomerTexts) getIntent().getSerializableExtra("MessageData");
            messageKEy = getIntent().getStringExtra("messageKey");
        }

        frm = (TextView)findViewById(R.id.FromTV);
        frm.setText(CustomerMessages.getFrom());

        notes = (EditText)findViewById(R.id.NoteEtex);
        notes.setText(CustomerMessages.getNotes());
        notes.setEnabled(false);

        viewPresciption = (ImageView) findViewById(R.id.PrescriptionImgView);
        Log.d("CheckMessage", "onCreate: " + CustomerMessages.getImgKey());

        Log.d("CheckMessage", "onCreate: URL: " + CustomerMessages.getUrl());
        sendReply = (Button) findViewById(R.id.replyMessageBtn);

        sendReply.setEnabled(false);
        sendReply.setBackground(getResources().getDrawable(R.drawable.disabled_button_corner));
        Glide.with(this).load(CustomerMessages.getUrl()).into(viewPresciption);
        sendReply.setEnabled(true);
        sendReply.setBackground(getResources().getDrawable(R.drawable.button_corner));
        viewPresciption.getDisplay();



        viewPresciption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPresciption.setDrawingCacheEnabled(true);
                viewPresciption.buildDrawingCache();
                Bitmap bm=viewPresciption.getDrawingCache();

                OutputStream fOut = null;
                Uri outputFileUri;
                try {
                    File root = new File(Environment.getExternalStorageDirectory()
                            + File.separator + "Prescriptions" + File.separator + CustomerMessages.getFrom() +File.separator);
                    root.mkdirs();
                    File sdImageMainDirectory = new File(root, "Prescription.jpg");
                    outputFileUri = Uri.fromFile(sdImageMainDirectory);
                    fOut = new FileOutputStream(sdImageMainDirectory);
                    Toast.makeText(ShowMessages.this, "Image saved to files", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    sendReply.setEnabled(false);
                    sendReply.setBackground(getResources().getDrawable(R.drawable.disabled_button_corner));
                    Toast.makeText(ShowMessages.this, "Error occured. Please try again later.",
                            Toast.LENGTH_SHORT).show();
                }

                try {
                    bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e){
            }
            }
        });


        sendReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowMessages.this, ReplyCustomer.class);
//                intent.putExtra("Customer", CustomerMessages.g);
                intent.putExtra("Customer", CustomerMessages.getFrom());
                intent.putExtra("Customer", CustomerMessages.getFrom());
                if(messageKEy != null){
                    intent.putExtra("messageKey", messageKEy);
                }
                startActivity(intent);
                finish();
            }
        });

    }
}
