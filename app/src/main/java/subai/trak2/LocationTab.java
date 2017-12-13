package subai.trak2;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//this holds the first tab which is the location tab
public class LocationTab extends Fragment {

    private static final String TAG = "@string/location_tag";
    public static Button start;
    private int ctrClicks = 0;
    boolean spinnerSelected;
    private DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference();
    private String status = "";
    private boolean isClicked = false;
    private Bus bus;
    static Spinner spinner;
    static UserSessionManager sessionManager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;

    static String text;
    static int bg;
    static boolean spinnerState;
    static int position;
    static int spinner_bg;
    static boolean stopped;

    static boolean state;
    static int spinColor;

    public View v;

    //sets the bus
    public void setBus(Bus bus){
        this.bus = bus;
    }

    public static LocationTab newInstance(int b, String t, int pos, boolean state, int sbg, boolean stop) {
        Bundle bundle = new Bundle();
        bundle.putString("start_btn_txt", t);
        bundle.putInt("start_btn_bg", b);
        bundle.putBoolean("spinner_state", state);
        bundle.putInt("spinner_pos", pos);
        bundle.putInt("spinner_bg", sbg);
        bundle.putBoolean("stop", stop);
        LocationTab fragment = new LocationTab();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.location_frag, container, false);
        start = (Button) v.findViewById(R.id.start_button);
        sessionManager = new UserSessionManager(getActivity().getApplicationContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        edit = sharedPreferences.edit();
        edit.putBoolean("started", false);
        edit.commit();
        if (savedInstanceState == null) {
        } else {
            status = savedInstanceState.getString("stat");
            isClicked = savedInstanceState.getBoolean("clicked");
        }
        if(sessionManager.isUserLoggedIn()) {
            Intent notifIntent = new Intent(getActivity(), NotifService.class);
            getActivity().startService(notifIntent);
        }
        spinner = (Spinner) v.findViewById(R.id.route_list);
        ArrayAdapter<String> routeAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.custom_spinner_item, getResources().getStringArray(R.array.routes)){
            @Override
            public boolean isEnabled(int position){
                if(position == 0) {
                    return false;
                } else {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(routeAdapter);

        text = getArguments().getString("start_btn_txt");
        bg = getArguments().getInt("start_btn_bg");
        position = getArguments().getInt("spinner_pos");
        spinnerState = getArguments().getBoolean("spinner_state");
        spinner_bg = getArguments().getInt("spinner_bg");
        stopped = getArguments().getBoolean("stop");

        //listens to the what is selected in the drop down spinner which contains the list of routes
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String r = spinner.getSelectedItem().toString();
                sessionManager.setRoute(r);
                if (spinner.getSelectedItemPosition() <= 0 || position <= 0) {
                    spinnerSelected = false;
                    sessionManager.setHasSpinnerSelected(spinnerSelected);
                    position = spinner.getSelectedItemPosition();
                    sessionManager.setPosition(position);
                } else {
                    spinnerSelected = true;
                    position = spinner.getSelectedItemPosition();
                    sessionManager.setHasSpinnerSelected(spinnerSelected);
                    sessionManager.setPosition(position);
                }
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.warning_red));
                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.fontColor));
                }
                bus.setRoute(r);
                sessionManager.setRoute(r);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //on click listener for the start button
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bg == R.drawable.start_another) {
                    forStart();
                } else if (bg == R.drawable.stop_another) {
                    LastTripDialog stopping = LastTripDialog.newInstance("Are you sure you want to stop?", 0);
                    stopping.show(getActivity().getFragmentManager(), "stop_dialog");
                }
            }
        });
        start.setBackground(getResources().getDrawable(bg));
        start.setText(text);
        return v;
    }

    public void spinnerSetState(boolean state, int bg, int pos) {
        spinner.setEnabled(state);
        spinner.setClickable(state);
        spinner.setSelection(pos, false);
        spinner.setBackground(getResources().getDrawable(bg));
    }

    //this is called when the start button is clicked
    public void forStart() {
        if (spinnerSelected == false) {
            DialogFragment newFrag = AlertDialogFragment.newInstance("Please select Route.");
            newFrag.show(getActivity().getFragmentManager(), "dialog");
        } else {
            if (!isLocationEnabled(getActivity())){
                DialogFragment newFragment = AlertDialogFragment.newInstance("Please turn on Location.");
                newFragment.show(getActivity().getFragmentManager(), "dialog");
            } else {
                //prompt that asks the user if it the bus' last trip already
                LastTripDialog lastTrip = LastTripDialog.newInstance("Is this your last trip?", 1);
                lastTrip.show(getActivity().getFragmentManager(), "lastTrip_dialog");

                sessionManager.setHasStarted(true); //---uncomment this if di na mu-work
                sessionManager.setSpinnerState(false);
                if (sessionManager.hasStarted()){
                    state = false;
                    text = "";
                    bg = R.drawable.stop_another;
                    start.setBackground(getResources().getDrawable(bg));
                    start.setText(text);
                    spinColor = R.drawable.spinner_selected_item_bg;
                    spinnerState = false;
                    position = sessionManager.getPosition();
                    spinner_bg = R.drawable.spinner_selected_item_bg;
                    spinnerSetState(spinnerState, spinner_bg, position);
                } else {
                    start.setBackground(getResources().getDrawable(bg));
                    start.setText(text);
                    spinnerState = true;
                    spinnerSetState(spinnerState, spinner_bg, position);
                }
                Intent intent = new Intent(getActivity(), SendService.class);
                getActivity().startService(intent);
            }
        }
    }

    //checks if the location of the device is turned on
    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("stat", status);
        outState.putBoolean("clicked", isClicked);
        outState.putInt("counter", ctrClicks);
    }

    //used to save the states of the spinner
    @Override
    public void onPause() {
        super.onPause();
        Spinner spinner = (Spinner) v.findViewById(R.id.route_list);
        sessionManager.prefs.edit().putInt("spin_position", sessionManager.getPosition()).apply();
        sessionManager.prefs.edit().putBoolean("spin_state", sessionManager.hasStarted()).apply();
    }

    //used to save the states of the spinner
    @Override
    public void onResume() {
        super.onResume();
        Spinner spinner = (Spinner) v.findViewById(R.id.route_list);
        int in = sessionManager.prefs.getInt("spin_position", 0);
        boolean s = sessionManager.prefs.getBoolean("spin_state", false);
        spinner.setSelection(in);
        spinner.setEnabled(!s);
        spinner.setClickable(!s);
        if (s == false) {
            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_bg));
        } else {
            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_selected_item_bg));
        }
        sessionManager.setRoute(spinner.getItemAtPosition(in).toString());
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}