package mobilepharmacy.mobilepharmacy;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustMainGUIFragment extends Fragment {


    public Button but1;
    public Button but2;

    public CustMainGUIFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cust_main_gui, container, false);
        but1 = (Button)view.findViewById(R.id.sendPres);
        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openSendMsgGUI = new Intent(getActivity(),SendMessageGUI.class);
                openSendMsgGUI.putExtra("some", "somedata");
                startActivity(openSendMsgGUI);
            }
        });

        but2 = (Button)view.findViewById(R.id.inbox);
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openReceivedMsgGUI = new Intent(getActivity(), ReceivedMessagesGUI.class);
                openReceivedMsgGUI.putExtra("some","somedata");
                startActivity(openReceivedMsgGUI);
            }
        });
        return view;
    }

}
