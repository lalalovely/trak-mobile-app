package subai.trak2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class TwoOptionsDialog extends DialogFragment {

    static UserSessionManager sessionManager;

    public static TwoOptionsDialog newInstance(String title, UserSessionManager ses) {
        TwoOptionsDialog frag = new TwoOptionsDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);

        sessionManager = ses;
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = getArguments().getString("title");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (title.equals("Are you sure you want to stop?")) {
                                    //sessionManager.setStop(true);
                                    sessionManager.setHasStarted(false);
                                    sessionManager.setSpinnerState(true);
                                }
                            }
                        }
                )
                .setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (title.equals("Are you sure you want to stop?")) {
                                    sessionManager.setHasStarted(true);
                                    sessionManager.setSpinnerState(false);
                                }
                            }
                        }
                )
                .create();
    }
}


