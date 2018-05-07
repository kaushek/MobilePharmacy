package mobilepharmacy.mobilepharmacy;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class PharmacyHome extends Fragment {

    private ImageButton ViewMessage;
    private ImageButton ViewCustomer;

    public PharmacyHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_pharmacy_home, container, false);

        ViewMessage = (ImageButton)view.findViewById(R.id.receivedMessagefrag);
        ViewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MessageListView.class);
                startActivity(intent);
            }
        });


        ViewCustomer = (ImageButton)view.findViewById(R.id.customerDetailsfrag);
        ViewCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CustomerListView.class);
                startActivity(intent);
            }
        });

        return  view;
    }

}
