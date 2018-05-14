package mobilepharmacy.mobilepharmacy;


import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class PharmacyDetails extends Fragment {

    FloatingActionButton butCall;
    String num = "0777532068";
    private static final int RequestCode = 1;

    public PharmacyDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pharmacy_details, container, false);
        butCall = (FloatingActionButton)view.findViewById(R.id.fabCall);


        try {
            butCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MakePhoneCall();

                }
            });
        }catch (Exception ex)
        {
            Log.d("PharmacyDetails", "onCreateView: " + ex.getMessage());
        }
        return view;
    }

    private void MakePhoneCall()
    {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CALL_PHONE}, RequestCode);
        }
        else {
            String dial = "tel:" + num;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestCode)
        {
            if (grantResults.length > 0 && grantResults[0] ==  PackageManager.PERMISSION_GRANTED)
            {
                MakePhoneCall();
            }
            else {
                Toast.makeText(getContext(), "Persmission Denied. Cant make the call.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
