package subai.trak2;

/**
 * Created by - on 15/11/2017.
 */

import java.util.Date;

/**
 * Created by - on 15/11/2017.
 */

public class ChatMessage {
    private String content;
    private String messageUser;
    private long messageTime;
    private boolean isSend;

    public ChatMessage(){
        content = "default";
        messageUser = "default_user";
        messageTime = new Date().getTime();
        isSend = false;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public ChatMessage(String content, String messageUser, boolean isSend) {
        this.content = content;
        this.messageUser = messageUser;
        this.isSend = isSend;
        messageTime = new Date().getTime();
    }

    public ChatMessage(String content){
        this.content = content;
        this.messageUser = "Michael";
        isSend = true;
        messageTime = new Date().getTime();
    }

    public void setChat(ChatMessage chat){
        this.content = chat.content;
        this.messageUser = chat.messageUser;
        this.messageTime = chat.messageTime;
        this.isSend = chat.isSend;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String messageText) {
        this.content = messageText;
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
