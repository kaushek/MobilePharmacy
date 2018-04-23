package mobilepharmacy.mobilepharmacy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class SendMessageGUI extends AppCompatActivity {

    private Button choose;
    private ImageButton sendAll;
    private ImageView presView;

    private EditText to;
    private EditText from;
    private EditText subject;
    private EditText note;

    private String addTo;
    private String addFrom;
    private String addSubject;
    private String addNote;

    private Uri filepath;
    private final int PICK_IMAGE_REQUEST = 10;

    private String imageKey = UUID.randomUUID().toString();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_gui);

        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseStorage = firebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        Bundle bundle = getIntent().getExtras();

        choose = (Button)findViewById(R.id.attach);
        sendAll = (ImageButton)findViewById(R.id.sendDetailsImgBtn);
        presView = (ImageView)findViewById(R.id.prescreptionView);
        to = (EditText)findViewById(R.id.toTxt);
        from = (EditText)findViewById(R.id.fromTxt);
        subject = (EditText)findViewById(R.id.subTxt);
        note = (EditText)findViewById(R.id.notesTxt);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();


            }
        });

        sendAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFrom = from.getText().toString();
                addTo = to.getText().toString();
                addSubject = subject.getText().toString();
                addNote = note.getText().toString();

                if (addTo != null && addSubject != null && addNote != null) {
                    AddTextToDB(addTo,addFrom, addSubject, addNote, imageKey);
                    uploadImage();


//                    FirebaseAuthException e = (FirebaseAuthException )task.getException();
//                    Toast.makeText(SendMessageGUI.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(SendMessageGUI.this, "Message sent", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SendMessageGUI.this, "Sorry! Unable to send your message", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void AddTextToDB(String Add_To, String Add_from, String Add_Subject, String Add_Note, String imageKey) {
        AddCustomerTexts addDetails = new AddCustomerTexts(Add_To, Add_from, Add_Subject, Add_Note, imageKey);
        databaseReference.child("AddMessageItems").push().setValue(addDetails);
    }

    private void chooseImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "select Prescription"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if (filepath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Sending...");
            progressDialog.show();

            StorageReference ref = storageReference.child("image/" + imageKey);
            ref.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    });
        }
        else{
            Toast.makeText(SendMessageGUI.this, "Failed to send your prescription", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            filepath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                presView.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }


}
