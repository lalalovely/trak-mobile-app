package subai.trak2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EventListener;

//Alert dialog pop up for the last trip and stopping prompts
public class LastTripDialog extends DialogFragment {

    public static LastTripDialog newInstance(String title, int type) {
        LastTripDialog frag = new LastTripDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("type", type);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = getArguments().getString("title");
        final int typ = getArguments().getInt("type");

        return new AlertDialog.Builder(getActivity())
                .setTitle("WARNING")
                .setMessage(title)
                .setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                UserSessionManager sessionManager = new UserSessionManager(getActivity());
                                sessionManager.setHasStarted(true);
                                sessionManager.setSpinnerState(false);
                            }
                        }
                )
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                if (typ == 0) {
                                    UserSessionManager sessionManager = new UserSessionManager(getActivity());
                                    sessionManager.setHasStarted(false);
                                    sessionManager.setSpinnerState(true);

                                    if (!sessionManager.hasStarted()){
                                        LocationTab.state = true;
                                        LocationTab.text = "";
                                        LocationTab.bg = R.drawable.start_another;

                                        LocationTab.start.setBackground(getResources().getDrawable(LocationTab.bg));
                                        LocationTab.start.setText(LocationTab.text);

                                        LocationTab.spinColor = R.drawable.spinner_bg;

                                        LocationTab.spinnerState = true;

                                        sessionManager.setPosition(0);
                                        LocationTab.position = sessionManager.getPosition();
                                        LocationTab.spinner_bg = R.drawable.spinner_bg;
                                        sessionManager.setSpinnerState(true);

                                        //set spinner states
                                        LocationTab.spinner.setEnabled(LocationTab.spinnerState);
                                        LocationTab.spinner.setClickable(LocationTab.spinnerState);
                                        LocationTab.spinner.setSelection(LocationTab.position);
                                        LocationTab.spinner.setBackground(getResources().getDrawable(LocationTab.spinColor));

                                        Intent intent = new Intent(getActivity(), SendService.class);
                                        getActivity().stopService(intent);
                                        Toast.makeText(getActivity().getApplicationContext(), "Sending Stopped", Toast.LENGTH_LONG).show();
                                        UserSessionManager sessionManager1 = new UserSessionManager(getActivity().getApplicationContext());
                                        DatabaseReference Bus_mRef = FirebaseDatabase.getInstance().getReference().child("Bus_Messages").child(sessionManager1.getBusNum());
                                        long x = System.currentTimeMillis();
                                        Calendar cal1 = Calendar.getInstance();
                                        cal1.setTimeInMillis(x);
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd hh:mm:ss a");
                                        String stringTime = dateFormat.format(cal1.getTime());
                                        String message = "Trip ended!";
                                        Bus_mRef.child(stringTime).child("content").setValue(message);
                                    } else {
                                        LocationTab.spinnerState = false;
                                        LocationTab.position = sessionManager.getPosition();
                                        LocationTab.spinner_bg = R.drawable.spinner_selected_item_bg;
                                        sessionManager.setSpinnerState(false);

                                        //set spinner states
                                        LocationTab.spinner.setEnabled(LocationTab.spinnerState);
                                        LocationTab.spinner.setClickable(LocationTab.spinnerState);
                                        LocationTab.spinner.setSelection(LocationTab.position);
                                        LocationTab.spinner.setBackground(getResources().getDrawable(LocationTab.spinColor));
                                    }
                                }
                                if (typ == 1) {
                                    ViewPager view = (ViewPager) getActivity().findViewById(R.id.container);
                                    view.setCurrentItem(1);
                                    UserSessionManager sessionManager = new UserSessionManager(getActivity().getApplicationContext());
                                    DatabaseReference Bus_mRef = FirebaseDatabase.getInstance().getReference().child("Bus_Messages").child(sessionManager.getBusNum());
                                    long x = System.currentTimeMillis();
                                    Calendar cal1 = Calendar.getInstance();
                                    cal1.setTimeInMillis(x);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd hh:mm:ss a");
                                    String stringTime = dateFormat.format(cal1.getTime());
                                    String message = "Hello SBT admin! This will be our last trip. Thanks!";
                                    Bus_mRef.child(stringTime).child("content").setValue(message);
                                    sessionManager.setStatus("Last Trip");

                                }
                            }
                        }
                )
                .create();
    }
}
