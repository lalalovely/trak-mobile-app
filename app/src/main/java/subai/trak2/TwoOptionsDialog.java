package subai.trak2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TwoOptionsDialog extends DialogFragment {

    public static TwoOptionsDialog newInstance(String title) {
        TwoOptionsDialog frag = new TwoOptionsDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = getArguments().getString("title");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage("Are you sure?")
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                UserSessionManager sessionManager = new UserSessionManager(getActivity());
                                DatabaseReference Bus_mRef = FirebaseDatabase.getInstance().getReference().child("Bus_Messages").child(sessionManager.getBusNum());
                                long x = System.currentTimeMillis();
                                Calendar cal1 = Calendar.getInstance();
                                cal1.setTimeInMillis(x);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd hh:mm:ss a");
                                String t = dateFormat.format(cal1.getTime());


                                DatabaseReference pushRef = Bus_mRef.child(t).child(("content"));

                                if (title.equals("Accident")){
                                    String m = "Hello SBT admin! We had a road accident. Our arrival will be delayed";
                                    pushRef.setValue(m);
                                }
                                if (title.equals("Bus failure")){
                                    String m = "Hello SBT admin! The bus is currently experiencing problems. There will be a slight delay in our arrival";
                                    pushRef.setValue(m);

                                }
                            }
                        }
                )
                .setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }
                )
                .create();
    }
}


