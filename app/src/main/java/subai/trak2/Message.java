package subai.trak2;

/**
 * Created by dcs-madl11 on 11/20/17.
 */

public class Message {
    public String content,time, type;

    Message(String content, String time, String type){
        this.content = content;
        this.type = type;
        String t = " ";
        String month = time.substring(0, 2);
        if (month.equals("01")){
            t = "January " + time.substring(3);
        } else if (month.equals("02")){
            t = "Febuary " + time.substring(3);
        } else if (month.equals("03")){
            t = "March " + time.substring(3);
        } else if (month.equals("04")){
            t = "April " + time.substring(3);
        } else if (month.equals("05")){
            t = "May " + time.substring(3);
        } else if (month.equals("06")){
            t = "June " + time.substring(3);
        } else if (month.equals("07")){
            t = "July " + time.substring(3);
        } else if (month.equals("08")){
            t = "August " + time.substring(3);
        } else if (month.equals("09")){
            t = "September " + time.substring(3);
        } else if (month.equals("10")){
            t = "October " + time.substring(3);
        } else if (month.equals("11")){
            t = "November " + time.substring(3);
        } else if (month.equals("12")){
            t = "December " + time.substring(3);
        }


        this.time = t;
    }

}
