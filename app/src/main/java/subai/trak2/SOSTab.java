package subai.trak2;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SOSTab extends Fragment {
    private static final String TAG = "@string/sos_tag";
    private ImageButton roadAccident, emerStop, engineFail;
    private static Bus bus;

    public void setBus(Bus bus){
        this.bus = bus;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sos_tab , container, false);

        final Animation animAlpha = AnimationUtils.loadAnimation(getContext(), R.anim.anim_alpha);

        this.roadAccident = (ImageButton) v.findViewById(R.id.roadAcc);
        this.emerStop = (ImageButton) v.findViewById(R.id.emerStop);
        this.engineFail = (ImageButton) v.findViewById(R.id.engFail);

        roadAccident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.startAnimation(animAlpha);
                roadAccClick();
            }
        });

        emerStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.startAnimation(animAlpha);
                emerStopClick();
            }
        });

        engineFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                v.startAnimation(animAlpha);
                engineFailCLick();
            }
        });

        return v;
    }

    public void roadAccClick() {
        showMyDialog("Road Accident");
    }

    public void emerStopClick() {
        showMyDialog("Emergency Stop");
    }

    public void engineFailCLick() {
        showMyDialog("Engine Failure");
    }

    public static void sendBus(){
        DatabaseReference pushRef = FirebaseDatabase.getInstance().getReference().child("Bus").child(LoginActivity.getBusNumber());
        pushRef.setValue(bus);
    }

    void showMyDialog(String title) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(title);
        newFragment.show(getActivity().getFragmentManager(), "dialog");
    }

    public static class AlertDialogFragment extends DialogFragment {

        public static AlertDialogFragment newInstance(String title) {
            AlertDialogFragment frag = new AlertDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String title = getArguments().getString("title");
            int icon = 0;
            if (title.equals("Emergency Stop")) {
                icon = R.drawable.emergency_stop_icon;
            } else if (title.equals("Engine Failure")) {
                icon = R.drawable.engine_failure;
            } else if (title.equals("Road Accident")) {
                icon = R.drawable.road_accident_icon;
            } else {
            }

            return new android.app.AlertDialog.Builder(getActivity())
                    .setIcon(icon)
                    .setTitle(title + ". Are you sure?")
                    .setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    SOSTab.bus.setStatus(title);
                                    SOSTab.sendBus();
                                }
                            }
                    )
                    .setNegativeButton("CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }
                    )
                    .create();
        }
    }


}