package subai.trak2;

/**
 * Created by - on 15/11/2017.
 */

import android.support.v7.app.AppCompatActivity;

import java.util.Date;

/**
 * Created by - on 15/11/2017.
 */

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private long messageTime;

    public ChatMessage(){
        messageText = "default";
        messageUser = "default_user";
        messageTime = new Date().getTime();
    }

    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();
    }


    public ChatMessage(String messageText){
        this.messageText = messageText;
        this.messageUser = "bruhh";
        messageTime = new Date().getTime();
    }

    public void setChat(ChatMessage chat){
        this.messageText = chat.messageText;
        this.messageUser = chat.messageUser;
        this.messageTime = chat.messageTime;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
