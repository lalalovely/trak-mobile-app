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

    public ChatMessage(){
        content = "default";
    }


    public ChatMessage(String content){
        this.content = content;
    }

    public void setChat(ChatMessage chat){
        this.content = chat.content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String messageText) {
        this.content = messageText;
    }

}
