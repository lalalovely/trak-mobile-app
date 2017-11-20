package subai.trak2;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.messaging_frag, container, false);
        editMessage = (EditText) v.findViewById(R.id.edit_txt_msg);
        mRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        messageList = (RecyclerView) v.findViewById(R.id.chat_view);
        send = (FloatingActionButton) v.findViewById(R.id.sendButton);
        chat = new ChatMessage();

        //setting the message list
        messageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setStackFromEnd(true);
        messageList.setLayoutManager(linearLayoutManager);


        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Bus_Accounts").child(mCurrentUser.getUid());
                final String messageValue = editMessage.getText().toString().trim();

                if (!TextUtils.isEmpty(messageValue)) {
                    chat.setMessageText(messageValue);

                    final DatabaseReference newPost = mRef.push();
                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("content").setValue(messageValue);
                            newPost.child("username").setValue(dataSnapshot.child("Name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    messageList.scrollToPosition(messageList.getAdapter().getItemCount());
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
                R.layout.sendUser,
                MessageViewHolder.class,
                mRef
        ) {
                @Override
                protected void populateViewHolder(MessageViewHolder viewHolder, ChatMessage model, int position) {
                    model.setChat(chat);
                    viewHolder.setContent(model.getMessageText());
                    viewHolder.setContent(model.getMessageUser());

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
            TextView message_content = (TextView) mView.findViewById(R.id.message_text);
            message_content.setText(content);
        }

        public void setUserName(String username){
            TextView userName_content = (TextView) mView.findViewById(R.id.message_user);
            userName_content.setText(username);
        }

    }
 }

