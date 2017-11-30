package subai.trak2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EventListener;

public class LastTripDialog extends DialogFragment {

    //ALERT DIALOG POP UP FOR LAST TRIP AND STOPPING PROMPTS

    public static final int LASTTRIP = 1;
    public static final int ISSTOPPED = 0;

    private EventListener el;

    public void setListener(EventListener e) {
        el = e;
    }

    //UserSessionManager session = new UserSessionManager(getActivity());

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
                                        //LocationTab.spinnerSetState(LocationTab.spinnerState, LocationTab.spinner_bg, LocationTab.position);

                                        Intent intent = new Intent(getActivity(), SendService.class);
                                        getActivity().stopService(intent);
                                        Toast.makeText(getActivity().getApplicationContext(), "Sending Stopped", Toast.LENGTH_LONG).show();
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
                                        //spinnerSetState(spinnerState, spinner_bg, position);
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
                                }

                                //LocationTab.doPositiveClick(typ);
//                                if (typ == 0) {
//                                    Toast.makeText(getActivity(), "You clicked yes", Toast.LENGTH_LONG).show();
//                                    LocationTab.sessionManager.setHasStarted(false);
//                                    String gaga = String.valueOf(LocationTab.sessionManager.hasStarted());
//                                    Toast.makeText(getActivity(), gaga, Toast.LENGTH_LONG).show();
//
//                                    if (!LocationTab.sessionManager.hasStarted()) {
//                                        String gaga1 = String.valueOf(LocationTab.sessionManager.hasStarted());
//                                        Toast.makeText(getActivity(), gaga1, Toast.LENGTH_LONG).show();
//                                        if (!LocationTab.sessionManager.hasStarted()){
//
//                                            LocationTab.state = true;
//                                            LocationTab.text = "START";
//                                            LocationTab.bg = R.drawable.circle_back;
//                                            LocationTab.start.setBackground(getResources().getDrawable(LocationTab.bg));
//                                            LocationTab.start.setText(LocationTab.text);
//                                            LocationTab.spinColor = R.drawable.spinner_bg;
//                                            LocationTab.spinnerState = true;
//                                            LocationTab.position = LocationTab.sessionManager.getPosition();
//                                            LocationTab.spinner_bg = R.drawable.spinner_bg;
//                                            LocationTab.sessionManager.setSpinnerState(true);
//
//                                            //LocationTab.spinnerSetState(spinnerState, spinner_bg, position);
//                                        } else {
//                                            LocationTab.state = false;
//                                            LocationTab.text = "STOP";
//                                            LocationTab.bg = R.drawable.stop_btn_bg;
//
//                                            LocationTab.start.setText(LocationTab.text);
//                                            LocationTab.start.setBackground(getResources().getDrawable(LocationTab.bg));
//
//                                            LocationTab.spinnerState = false;
//                                            LocationTab.position = LocationTab.sessionManager.getPosition();
//                                            LocationTab.spinner_bg = R.drawable.spinner_selected_item_bg;
//                                            LocationTab.sessionManager.setSpinnerState(false);
//                                            //spinnerSetState(spinnerState, spinner_bg, position);
//
//                                            LocationTab.spinner.setEnabled(LocationTab.spinnerState);
//                                            LocationTab.spinner.setClickable(LocationTab.spinnerState);
//                                            LocationTab.spinner.setSelection(LocationTab.position, false);
//                                            LocationTab.spinner.setBackground(getResources().getDrawable(LocationTab.spinner_bg));
//                                        }
//                                        Intent intent = new Intent(getActivity(), SendService.class);
//                                        getActivity().stopService(intent);
//                                        Toast.makeText(getActivity(), "Sending Stopped", Toast.LENGTH_LONG).show();
//                                    }
//                                }
                            }
                        }
                )
                .create();
    }
}
