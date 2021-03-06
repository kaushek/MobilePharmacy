package mobilepharmacy.mobilepharmacy;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import model.AddCustomer;
import model.AddCustomerTexts;

public class SendMessageGUI extends AppCompatActivity {

//    AddCustomer customer;

    private Button choose;
    private Button sendAll;
    private ImageView presView;

    private EditText to;
    private EditText from;
    private EditText subject;
    private EditText note;
    private Spinner selectOrder;

    private String addTo;
    private String addFrom;
    private String addSubject;
    private String addNote;
    private String downloadURL;
    private String dUrl;
    private String order;

    String fname;
    String lname;

    public static Uri FILE_PATH;
    private final int PICK_IMAGE_REQUEST = 10;

    private String imageKey = UUID.randomUUID().toString();


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private StorageReference storageRef;

    private static String userID;
    private static String userFiirstName;
    private static String userLastName;
    private static String userEmail;
    private static String userPassword;
    private static String userConfirmPassword;
    private static Double userLatitude;
    private static Double userLongitude;
    private static int userPhoneNumber;
    String formattedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_gui);

        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseStorage = firebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        retreiveSharedVariableValues(SendMessageGUI.this);

        Bundle bundle = getIntent().getExtras();

        choose = (Button)findViewById(R.id.attach);
        sendAll = (Button) findViewById(R.id.sendDetailsImgBtn);
        presView = (ImageView)findViewById(R.id.prescreptionView);
        to = (EditText)findViewById(R.id.toTxt);
        from = (EditText)findViewById(R.id.fromTxt);
        subject = (EditText)findViewById(R.id.subTxt);
        note = (EditText)findViewById(R.id.notesTxt);
        selectOrder = (Spinner)findViewById(R.id.selectMedicineSpnr);


        to.setText("Pharmacy");
        to.setEnabled(false);
        to.setVisibility(View.INVISIBLE);

        from.setText(userFiirstName);
        from.setEnabled(false);

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
//                AddCustomerTexts addCustomerTexts1 = new AddCustomerTexts(to.getText().toString(),from.getText().toString(),subject.getText().toString(),note.getText().toString(),imageKey,downloadURL,formattedDate, order,false);

            }

        });
        clearFields();

    }

    private void clearFields()
    {
        subject.setText("");
        note.setText("");
    }

    private void dbfunction()
    {
        addFrom = from.getText().toString();
        addTo = to.getText().toString();
        addSubject = subject.getText().toString();
        addNote = note.getText().toString();
        order =  selectOrder.getSelectedItem().toString();

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c);

        if (addSubject != null && addNote != null) {
            AddTextToDB(addTo,addFrom, addSubject, addNote, imageKey, downloadURL, formattedDate, order);

            Toast.makeText(SendMessageGUI.this, "Message sent", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(SendMessageGUI.this, "Sorry! Unable to send your message", Toast.LENGTH_SHORT).show();
        }
    }

    public static void getSharedPreference(String id,String fname,String lname,String email, String date, String orderMedi, Context context){
        SharedPreferences preferences  = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("USERID",id);
        editor.putString("USERFIRSTNAME", fname);
        editor.putString("USERLASTNAME", lname);
        editor.putString("USEREMAIL", email);
        editor.putString("DATE", date);
        editor.putString("ORDER", orderMedi);

        editor.commit();
    }

    public static void retreiveSharedVariableValues(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        userID = prefs.getString("USERID",null);
        userEmail = prefs.getString("USEREMAIL",null);
        userFiirstName = prefs.getString("USERFIRSTNAME",null);
        userLastName = prefs.getString("USERLASTNAME",null);

        if(userID!=null){
            Log.d("TAG","Shared Preference UserID :"+userID);
            Log.d("TAG","Shared Preference UserID :"+userEmail);
            Log.d("TAG","Shared Preference UserID :"+userPassword);
            Log.d("TAG","Shared Preference UserID :"+userConfirmPassword);
            Log.d("TAG","Shared Preference UserID :"+userLatitude);
            Log.d("TAG","Shared Preference UserID :"+userLongitude);
            Log.d("TAG","Shared Preference UserID :"+userPhoneNumber);
        }
    }

    private void AddTextToDB(String Add_to, String Add_from, String Add_Subject, String Add_Note, String imageKey, String Url, String date, String ord) {
        AddCustomerTexts addDetails = new AddCustomerTexts(Add_to, Add_from, Add_Subject, Add_Note, imageKey, Url,date,ord,false);
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
