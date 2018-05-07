package mobilepharmacy.mobilepharmacy;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.renderscript.Double2;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import model.AddCustomer;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerMyAccount extends Fragment {

    private EditText custFName;
    private EditText custLName;
    private EditText custPhoneNumber;
    private Button saveBtn;
    private Button editBtn;
    private int phoneNumber;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


    private SharedPreferences pref;
    private SharedPreferences.Editor editorSharedPreference;
    private static final String MY_PREF_NAME = "Phramacy";
    private static String userID;
    private static String userFiirstName;
    private static String userLastName;
    private static String userEmail;
    private static String userPassword;
    private static String userConfirmPassword;
    private static Double userLatitude;
    private static Double userLongitude;
    private static int userPhoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_my_account, container, false);
        custFName = (EditText) view.findViewById(R.id.firstName);
        custLName = (EditText) view.findViewById(R.id.lastName);
        custPhoneNumber = (EditText) view.findViewById(R.id.phone);
        saveBtn = (Button) view.findViewById(R.id.save);
        editBtn=(Button) view.findViewById(R.id.edit);

        retreiveSharedVariableValues(getActivity());
        custFName.setText(userFiirstName);
        custLName.setText(userLastName);
        String retreivedPhoneNumber = String.valueOf(userPhoneNumber);

        custPhoneNumber.setText(retreivedPhoneNumber);
        custFName.setEnabled(false);
        custLName.setEnabled(false);
        custPhoneNumber.setEnabled(false);
        saveBtn.setEnabled(false);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custFName.setEnabled(true);
                custLName.setEnabled(true);
                custPhoneNumber.setEnabled(true);
                saveBtn.setEnabled(true);

                if(isDataValid()){

                    saveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AddCustomer addCustomer = new AddCustomer(custFName.getText().toString(),custLName.getText().toString(),userEmail,userPassword,userConfirmPassword,phoneNumber,userLatitude,userLongitude,userID);
                            updateDB(addCustomer);
                            custFName.setEnabled(false);
                            custLName.setEnabled(false);
                            custPhoneNumber.setEnabled(false);
                            saveBtn.setEnabled(false);
                            getSharedPreference(userID,custFName.getText().toString(),custLName.getText().toString(),userPassword,phoneNumber,userLatitude.toString(),userLongitude.toString(),userEmail,userConfirmPassword,getActivity());
                        }
                    });
                }

            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

    }
    private void updateDB(Object addCustomer){
        if(addCustomer!=null)
            databaseReference.child("Customers").child(userID).setValue(addCustomer);

    }

    public static void getSharedPreference(String id,String fname,String lname,String pass,int num,String lat,String longtitude,String email,String cPass,Context context){
        SharedPreferences preferences  = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("USERID",id);
        editor.putString("USERFIRSTNAME", fname);
        editor.putString("USERLASTNAME", lname);
        editor.putString("USERPASSWORD", pass);
        editor.putInt("USERPHONENUMBER", num);
        editor.putString("USERLAT", lat);
        editor.putString("USERLONGITUDE", longtitude);
        editor.putString("USEREMAIL", email);
        editor.putString("USERCONFIRMPASSWORD", cPass);

        editor.commit();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private Boolean isDataValid(){
        if(custFName.getText()==null&&custFName.getText().length()<0){
            return false;
        }
        if(custLName.getText()==null&&custLName.getText().length()<0){
            return  false;
        }
        if(custPhoneNumber.getText()==null&&custPhoneNumber.getText().length()<0){
            return  false;
        }
        else{
            try{
                phoneNumber = Integer.parseInt(custPhoneNumber.getText().toString());
            }
            catch (Exception ex){
                Toast.makeText(getActivity(),"Please Check the PhoneNumber Field",Toast.LENGTH_LONG).show();
            }
        }

        return true;
    }

    public static void retreiveSharedVariableValues(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        userID = prefs.getString("USERID",null);

        userEmail = prefs.getString("USEREMAIL",null);
        userPassword = prefs.getString("USERPASSWORD",null);
        userFiirstName = prefs.getString("USERFIRSTNAME",null);
        userLastName = prefs.getString("USERLASTNAME",null);
        userConfirmPassword = prefs.getString("USERCONFIRMPASSWORD",null);
        userLatitude = Double.parseDouble(prefs.getString("USERLAT",null));
        userLongitude = Double.parseDouble(prefs.getString("USERLONGITUDE",null));
        userPhoneNumber = prefs.getInt("USERPHONENUMBER",0);
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
//    private void addtoDatabase(String Fn, String Ln, int Nm, String Add, String CC, String CA)
//    {
//     //   AddCustomer addCus = new AddCustomer(Fn, Ln, Nm, Add, CC, CA);
//    //    databaseReference.child("Customers").push().setValue(addCus);
//    }

}
