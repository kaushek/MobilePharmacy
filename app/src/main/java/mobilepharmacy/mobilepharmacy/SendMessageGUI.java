package mobilepharmacy.mobilepharmacy;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class SendMessageGUI extends AppCompatActivity {

//    AddCustomer customer;

    private Button choose;
    private Button sendAll;
    private ImageView presView;

    private EditText to;
    private EditText from;
    private EditText subject;
    private EditText note;

    private String addTo;
    private String addFrom;
    private String addSubject;
    private String addNote;
    private String downloadURL;
    private String dUrl;

    public static Uri FILE_PATH;
    private final int PICK_IMAGE_REQUEST = 10;

    private String imageKey = UUID.randomUUID().toString();


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_gui);

        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseStorage = firebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

//        customer= (AddCustomer) getIntent().getSerializableExtra("CustomerDetails");
//        from.setText(customer.getFname() + "\t" + customer.getLname() );


        Bundle bundle = getIntent().getExtras();

        choose = (Button)findViewById(R.id.attach);
        sendAll = (Button) findViewById(R.id.sendDetailsImgBtn);
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
                uploadImage();

            }
        });

    }

    private void dbfunction()
    {
        addFrom = from.getText().toString();
        addTo = to.getText().toString();
        addSubject = subject.getText().toString();
        addNote = note.getText().toString();
//        dUrl = downloadURL;
//        Log.d("checkdUrl", "onClick: " + dUrl);
//                downloadURL = (storageRef.getDownloadUrl()).toString();


        if (addTo != null && addSubject != null && addNote != null) {
//            uploadImage();
            AddTextToDB(addTo,addFrom, addSubject, addNote, imageKey, downloadURL);



//                    FirebaseAuthException e = (FirebaseAuthException )task.getException();
//                    Toast.makeText(SendMessageGUI.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            Toast.makeText(SendMessageGUI.this, "Message sent", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(SendMessageGUI.this, "Sorry! Unable to send your message", Toast.LENGTH_SHORT).show();
        }
    }

    private void AddTextToDB(String Add_To, String Add_from, String Add_Subject, String Add_Note, String imageKey, String Url) {
        AddCustomerTexts addDetails = new AddCustomerTexts(Add_To, Add_from, Add_Subject, Add_Note, imageKey, Url);
        databaseReference.child("AddMessageItems").push().setValue(addDetails);
    }

    private void chooseImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "select Prescription"), PICK_IMAGE_REQUEST);
    }

    public String getImageExt(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        if (FILE_PATH != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Sending...");
            progressDialog.show();

//            StorageReference ref = storageReference.child("image/" + imageKey);
            StorageReference ref = storageReference.child("image/" + getImageExt(FILE_PATH));

            ref.putFile(FILE_PATH)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Log.d("CHECK", "onSuccess: "+ taskSnapshot.getDownloadUrl().toString());
                            downloadURL = taskSnapshot.getDownloadUrl().toString();
                            dbfunction();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SendMessageGUI.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Completed " + (int)progress + "%");
                        }
                    });

        }
        else{
            Toast.makeText(SendMessageGUI.this, "Please select your prescription", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            FILE_PATH = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FILE_PATH);
                presView.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }


}
