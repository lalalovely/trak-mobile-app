package subai.trak2;

public class AdminMessage {
    private String response;
    private String msg;

    public AdminMessage(){

    }

    public AdminMessage(String response, String msg) {
        this.response = response;
        this.msg = msg;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
