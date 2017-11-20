package subai.trak2;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MessagingTab extends Fragment {

    private static final String TAG = "@string/messaging_tab";
    private EditText editMessage;
    private DatabaseReference mRef;
    private RecyclerView messageList;
    private FloatingActionButton send;
    private ChatMessage chat;
    private FirebaseAuth mAuth;
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

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    //startActivity( new Intent())
                }
            }
        };

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mCurrentUser = mAuth.getCurrentUser();
                mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Messages");
                final String messageValue = editMessage.getText().toString().trim();

                if (!TextUtils.isEmpty(messageValue)) {
                    chat.setMessageText(messageValue);
                    final DatabaseReference newPost = mRef.push();
                    newPost.child("content").setValue(messageValue);
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
                R.layout.list_item,
                MessageViewHolder.class,
                mRef
        ) {
                @Override
                protected void populateViewHolder(MessageViewHolder viewHolder, ChatMessage model, int position) {
                    model.setChat(chat);
                    viewHolder.setContent(model.getMessageText());
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
    }
 }

