package subai.trak2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private DatabaseReference New_message;
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
    private ImageButton emergency;
    private static int scroll;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.messaging_frag, container, false);
        messages = new ArrayList<>();

        sessionManager = new UserSessionManager(getActivity().getApplicationContext());
        Bus_mRef = FirebaseDatabase.getInstance().getReference().child("Bus_Messages").child(sessionManager.getBusNum());
        Admin_mRef = FirebaseDatabase.getInstance().getReference().child("Admin_Messages").child(sessionManager.getBusNum());
        New_message = FirebaseDatabase.getInstance().getReference().child("Bus_Accounts").child(sessionManager.getBusNum()).child("newMessage");

        layout = (LinearLayout) v.findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)v.findViewById(R.id.layout2);
        emergency = (ImageButton)v.findViewById(R.id.emer_button);
        sendButton = (ImageView)v.findViewById(R.id.sendButton);
        messageArea = (EditText)v.findViewById(R.id.edit_txt_msg);
        scrollView = (ScrollView)v.findViewById(R.id.scrollView);
        view = inflater.inflate(R.layout.send_user, container, false);
        bus = 0;
        admin = 0;
        scroll = 20;

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

        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Animation aAnim = new AlphaAnimation(1.0f, 0.0f);
                aAnim.setDuration(100);
                aAnim.setFillAfter(true);
                emergency.startAnimation(aAnim);
                emergency.clearAnimation();
                PopupMenu popup = new PopupMenu(getContext(), emergency);
                popup.getMenuInflater().inflate(R.menu.emergency_popup, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                     @Override
                     public boolean onMenuItemClick(MenuItem item) {
                         TwoOptionsDialog opts = null;
                         if (item.getTitle().equals("Bus Failure")) {
                             opts = TwoOptionsDialog.newInstance("Accident");
                         } else if (item.getTitle().equals("Road Accident")) {
                             opts = TwoOptionsDialog.newInstance("Bus failure");
                         } else {

                         }
                         opts.show(getActivity().getFragmentManager(), "dialog");
                         return true;
                     }
                });
                MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) popup.getMenu(), emergency);
                menuHelper.setForceShowIcon(true);
                menuHelper.show();
            }
        });
        Bus_mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(bus == 1) {
                    New_message.setValue("True");
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
        Bus_mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (bus == 0) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        String m = d.child("content").getValue().toString();
                        messages.add(new Message(m, d.getKey(), "user"));
                    }
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
                    arrange();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
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
            bus += 1;
            admin += 1;
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
        content.setTextSize(15);
        //left top right bottom
        content.setPadding(15, 15, 15, 15);

        time.setText(t);
        time.setTextSize(10);
        time.setTextColor(Color.BLACK);
        if(type == 1) {
            lp1.gravity = Gravity.RIGHT;
            lp2.gravity = Gravity.RIGHT;
            content.setTextColor(getResources().getColor(R.color.white));
            content.setBackground(getResources().getDrawable(R.drawable.chat_bubble_dark_blue));
            //content.setBackgroundResource(R.drawable.s_chat_bloat);
            //content.setBackgroundColor(Color.BLUE);
        }
        else{
            lp1.gravity = Gravity.LEFT;
            lp2.gravity = Gravity.LEFT;
            content.setTextColor(getResources().getColor(R.color.fontColor));
            content.setBackground(getResources().getDrawable(R.drawable.received_chat));
            //content.setBackgroundColor(Color.GRAY);
        }
        content.setLayoutParams(lp1);
        time.setLayoutParams(lp2);
        layout.addView(content);
        layout.addView(time);
    }
}
