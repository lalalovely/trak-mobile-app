package subai.trak2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;

public class LastTripDialog extends DialogFragment {

    public boolean lastTripBool = false;

    public static LastTripDialog newInstance(String title) {
        LastTripDialog frag = new LastTripDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                lastTripBool = false;
                            }
                        }
                )
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                lastTripBool = true;
                            }
                        }
                )
                .create();
    }
}
