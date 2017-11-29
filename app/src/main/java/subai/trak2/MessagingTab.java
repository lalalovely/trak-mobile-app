package subai.trak2;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessagingTab extends Fragment {

    private static final String TAG = "@string/messaging_tab";
    private DatabaseReference Bus_mRef;
    private DatabaseReference Admin_mRef;
    private LinearLayout layout;
    private RelativeLayout layout_2;
    private ImageView sendButton;
    private EditText messageArea;
    private ScrollView scrollView;
    private UserSessionManager sessionManager;
    private View view;
    private static int bus = 0;
    private static int admin = 0;
    private List<Message> messages;
    private int initialize = 0;
    private FloatingActionButton accident;
    private FloatingActionButton engFail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.messaging_frag, container, false);
        messages = new ArrayList<>();

        sessionManager = new UserSessionManager(getActivity().getApplicationContext());
        Bus_mRef = FirebaseDatabase.getInstance().getReference().child("Bus_Messages").child(sessionManager.getBusNum());
        Admin_mRef = FirebaseDatabase.getInstance().getReference().child("Admin_Messages").child(sessionManager.getBusNum());

        layout = (LinearLayout) v.findViewById(R.id.layout1);
        accident = (FloatingActionButton) v.findViewById(R.id.road_acc);
        engFail = (FloatingActionButton) v.findViewById(R.id.engFail);
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
                    long x = System.currentTimeMillis();
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTimeInMillis(x);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd hh:mm:ss a");
                    String t = dateFormat.format(cal1.getTime());


                    DatabaseReference pushRef = Bus_mRef.child(t).child(("content"));
                    pushRef.setValue(messageText);
                    messageArea.setText("");
                }
            }
        });

        //message dialogs
        accident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoOptionsDialog opts = TwoOptionsDialog.newInstance("Accident");
                opts.show(getActivity().getFragmentManager(), "dialog");

            }
        });

        engFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoOptionsDialog opts2 = TwoOptionsDialog.newInstance("Bus failure");
                opts2.show(getActivity().getFragmentManager(), "dialog");
            }
        });
        Bus_mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (bus == 0) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        String m = d.child("content").getValue().toString();
                        messages.add(new Message(m, d.getKey(), "user"));
                    }
                    bus += 1;
                    arrange();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Admin_mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (admin == 0) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        String m = d.child("content").getValue().toString();
                        messages.add(new Message(m, d.getKey(), "admin"));
                    }
                    admin += 1;
                    arrange();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Bus_mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(bus == 1) {
                    String message = dataSnapshot.child("content").getValue().toString();
                    addMessageBox(message, getCurrTime(), 1);
                }

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        Admin_mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (admin == 1){
                    String message = dataSnapshot.child("content").getValue().toString();
                    addMessageBox(message,getCurrTime(), 2);
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void arrange(){
        class compare implements Comparator<Message>{

            @Override
            public int compare(Message m1, Message m2) {
                return m1.time.compareTo(m2.time);
            }
        }
        if (initialize == 0){
            initialize = 1;
        } else {
            Collections.sort(messages, new compare());
            for (Message m: messages){
                if(m.type.equals("user")){
                    addMessageBox(m.content, m.time, 1);
                }
                if (m.type.equals("admin")){
                    addMessageBox(m.content, m.time, 2);
                }
            }
        }
    }

    public String getCurrTime(){
        long x = System.currentTimeMillis();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(x);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        return dateFormat.format(cal1.getTime());
    }

    public void addMessageBox(String message, String t,int type){
        TextView content = new TextView(getActivity().getApplicationContext());
        TextView time = new TextView(getActivity().getApplicationContext());
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.setMargins(0,30,0,0);
        lp1.weight = 1.0f;
        lp2.weight = 1.0f;
        content.setText(message);

        time.setText(t);
        time.setTextColor(Color.BLACK);
        if(type == 1) {
            lp1.gravity = Gravity.RIGHT;
            lp2.gravity = Gravity.RIGHT;
            //content.setBackgroundResource(R.drawable.forsend_chat_bubble);
            content.setBackgroundColor(Color.BLUE);
        }
        else{
            lp1.gravity = Gravity.LEFT;
            lp2.gravity = Gravity.LEFT;
            //content.setBackgroundResource(R.drawable.receive_chat_bubble);
            content.setBackgroundColor(Color.BLUE);
        }
        content.setLayoutParams(lp1);
        time.setLayoutParams(lp2);
        layout.addView(content);
        layout.addView(time);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}