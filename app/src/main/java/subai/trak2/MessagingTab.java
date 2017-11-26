package subai.trak2;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MessagingTab extends Fragment {

    private static final String TAG = "@string/messaging_tab";
    private EditText editMessage;
    private DatabaseReference mRef;
    private RecyclerView messageList;
    private FloatingActionButton send;
    private FloatingActionButton accident;
    private FloatingActionButton engFail;
    private static String customMess;
    private static boolean custom_mess_flag = false;
    UserSessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.messaging_frag, container, false);
        editMessage = (EditText) v.findViewById(R.id.edit_txt_msg);
        messageList = (RecyclerView) v.findViewById(R.id.chat_view);
        engFail = (FloatingActionButton) v.findViewById(R.id.engFail);
        accident = (FloatingActionButton) v.findViewById(R.id.road_acc);
        send = (FloatingActionButton) v.findViewById(R.id.sendButton);
        sessionManager = new UserSessionManager(getActivity().getApplicationContext());

        mRef = FirebaseDatabase.getInstance().getReference();
        messageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setStackFromEnd(true);
        messageList.setLayoutManager(linearLayoutManager);

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String messageValue = editMessage.getText().toString().trim();
                if (!TextUtils.isEmpty(messageValue)) {
                    String t = String.valueOf(new Date().getTime());
                    DatabaseReference push = mRef.child("Bus_Messages").child(sessionManager.getBusNum());
                    push.getRef().child(t).child("content").setValue(messageValue);
                    messageList.scrollToPosition(messageList.getAdapter().getItemCount());
                }
                editMessage.setText("");
            }
        });

        //message dialogs
        accident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyDialog("Road Accident");
                if(custom_mess_flag){
                    editMessage.setText(customMess);
                    custom_mess_flag = false;
                    customMess = "";
                }
            }
        });

        engFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyDialog("Bus Failure");
                editMessage.setText(customMess);
                if(custom_mess_flag) {
                    editMessage.setText(customMess);
                    custom_mess_flag = false;
                    customMess = "";
                }
            }
        });
        return v;
    }

    public void showMyDialog(String title){
        DialogFragment newFragment = AlertDialogFrag.newInstance(title);
        newFragment.show(getActivity().getFragmentManager(), "dialog");
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> FBRA = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(
                ChatMessage.class,
                R.layout.receive_user,
                MessageViewHolder.class,
                mRef.child("Admin_Messages").child(sessionManager.getBusNum())
        ) {
                @Override
                protected void populateViewHolder(MessageViewHolder viewHolder, ChatMessage model, int position) {
                    viewHolder.setContent(model.getContent());
                    viewHolder.setTime();
                }
            };
            messageList.setAdapter(FBRA);
        }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public MessageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setContent(String content){
            BubbleTextView message_content = (BubbleTextView) mView.findViewById(R.id.message_text);
            message_content.setText(content);
        }
        public void setTime(){
            TextView time = (TextView) mView.findViewById(R.id.message_time);
            long x = System.currentTimeMillis();
            Calendar cal1 = Calendar.getInstance();
            cal1.setTimeInMillis(x);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String t = dateFormat.format(cal1.getTime());
            time.setText(t);
        }
    }

    public static class AlertDialogFrag extends DialogFragment {
        public static AlertDialogFrag newInstance(String title) {
            AlertDialogFrag frag = new AlertDialogFrag();
            Bundle args = new Bundle();
            args.putString("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final String title = getArguments().getString("title");
            final String[] eng_failure = {"There are problems with the engine", "Problems with the tires",
                                      "maluoy mo"};
            final String[] acc = {"Crashed into a tree", "Crashed into a barrier",
                                       "Crashed with another vehicle", "Hit a person" };
            int icon = 0;
            if (title.equals("Bus Failure")) {
                builder.setTitle(title)
                        .setItems(eng_failure, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                customMess = eng_failure[which];
                                custom_mess_flag = true;
                            }
                        }).setNegativeButton("Back",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }
                        );
            } else if (title.equals("Road Accident")) {
                builder.setTitle(title)
                        .setItems(acc, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                customMess = acc[which];
                                custom_mess_flag = true;
                            }
                        })
                        .setNegativeButton("Back",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }
                );
            } else {

            }
            return builder.create();
        }
    }
 }

