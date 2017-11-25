package subai.trak2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MessagingTab extends Fragment {

    private static final String TAG = "@string/messaging_tab";
    private DatabaseReference Bus_mRef;
    private DatabaseReference Admin_mRef;
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    UserSessionManager sessionManager;
    View view;
    static int i = 0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.messaging_frag, container, false);

        sessionManager = new UserSessionManager(getActivity().getApplicationContext());
        Bus_mRef = FirebaseDatabase.getInstance().getReference().child("Bus_Messages").child(sessionManager.getBusNum());
        Admin_mRef = FirebaseDatabase.getInstance().getReference().child("Admin_Messages").child(sessionManager.getBusNum());

        layout = (LinearLayout) v.findViewById(R.id.layout1);

        layout_2 = (RelativeLayout)v.findViewById(R.id.layout2);
        sendButton = (ImageView)v.findViewById(R.id.sendButton);
        messageArea = (EditText)v.findViewById(R.id.edit_txt_msg);
        scrollView = (ScrollView)v.findViewById(R.id.scrollView);
        view = inflater.inflate(R.layout.send_user, container, false);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    String t = String.valueOf(new Date().getTime());
                    DatabaseReference pushRef = Bus_mRef.child(t).child(("content"));
                    pushRef.setValue(messageText);
//                    addMessageBox(messageText, 1);
                    messageArea.setText("");
                }
            }
        });
        Bus_mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (i == 0) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        String message = d.child("content").getValue().toString();
                        addMessageBox(message, 1);
                    }
                    i += 1;
//                } else {
//                    Log.d(TAG, "TIS IS WHAT I WANT BOY");
//                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Admin_mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    String message = d.child("content").getValue().toString();
                    addMessageBox(message, 2);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void addMessageBox(String message, int type){
        TextView t = new TextView(getActivity().getApplicationContext());
        TextView time = new TextView(getActivity().getApplicationContext());
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.setMargins(0,30,0,0);
        lp1.weight = 1.0f;
        lp2.weight = 1.0f;
        t.setText(message);
        t.setBackgroundColor(Color.BLUE);
        long x = System.currentTimeMillis();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(x);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String stringTime = dateFormat.format(cal1.getTime());
        time.setText(stringTime);
        time.setTextColor(Color.BLACK);
        if(type == 1) {
            lp1.gravity = Gravity.RIGHT;
            lp2.gravity = Gravity.RIGHT;

        }
        else{
            lp1.gravity = Gravity.LEFT;
            lp2.gravity = Gravity.LEFT;
        }
        t.setLayoutParams(lp1);
        time.setLayoutParams(lp2);
        layout.addView(t);
        layout.addView(time);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}