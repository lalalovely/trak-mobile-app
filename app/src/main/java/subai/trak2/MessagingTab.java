package subai.trak2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;


public class MessagingTab extends Fragment {

    private static final String TAG = "@string/messaging_tab";
    private EditText editMessage;
    private DatabaseReference mRef;
    private RecyclerView messageList;
    private FloatingActionButton send;
    private ChatMessage chat;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUsers;
    UserSessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.messaging_frag, container, false);
        editMessage = (EditText) v.findViewById(R.id.edit_txt_msg);
        messageList = (RecyclerView) v.findViewById(R.id.chat_view);
        send = (FloatingActionButton) v.findViewById(R.id.sendButton);
        //chat = new ChatMessage();
        sessionManager = new UserSessionManager(getActivity().getApplicationContext());

        mRef = FirebaseDatabase.getInstance().getReference().child("Bus_Messages").child(sessionManager.getBusNum());
        //setting the message list
        messageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setStackFromEnd(true);
        messageList.setLayoutManager(linearLayoutManager);

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Bus_Accounts").child(mCurrentUser.getUid());
                final String messageValue = editMessage.getText().toString().trim();
                if (!TextUtils.isEmpty(messageValue)) {
                    //chat.setContent(messageValue);
                    String t = String.valueOf(new Date().getTime());
                    //DatabaseReference pushRef = FirebaseDatabase.getInstance().getReference().child("Bus_Messages").child(sessionManager.getBusNum())/*.child(t)*/;
                    Message m = new Message(messageValue);
                    mRef.getRef().child(t).setValue(m);
                    messageList.scrollToPosition(messageList.getAdapter().getItemCount());
                    //messageList.scroll
//                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
//
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            newPost.child("content").setValue(messageValue);
//                            newPost.child("username").setValue(dataSnapshot.child("Name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//
//                                }
//                            });
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
                }
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> FBRA = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(
                ChatMessage.class,
                R.layout.send_user,
                MessageViewHolder.class,
                mRef
        ) {
                private final int ITEM_ADMIN = 0;
                private final int ITEM_USER = 1;

                @Override
                protected void populateViewHolder(MessageViewHolder viewHolder, ChatMessage model, int position) {
                    viewHolder.setContent(model.getContent());
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
    }

 }

