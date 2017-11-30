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

public class LocationTab extends Fragment {// implements LocationListener {

    private static final String TAG = "@string/location_tag";
    public String toDisplay = "";

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;
    private TextView latitude, longitude;
    public static Button start, stop;
    private Location mLocation;
    private boolean updating = false;
    private int color = 0;
    private int ctrClicks = 0;
    public static int ctr = 0;
    public static boolean send = true;
    private String route = "";
    boolean spinnerSelected;
    private DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference();
    ; //root database

    private double fusedLatitude = 0.0;
    private  double fusedLongitude = 0.0;

    private TextView display;

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
        //stop = (Button) v.findViewById(R.id.stop_button);

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
        //sessionManager.setHasSpinnerSelected(false);

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
        //android.R.layout.simple_spinner_dropdown_item
        routeAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(routeAdapter);

        text = getArguments().getString("start_btn_txt");
        bg = getArguments().getInt("start_btn_bg");
        position = getArguments().getInt("spinner_pos");
        spinnerState = getArguments().getBoolean("spinner_state");
        spinner_bg = getArguments().getInt("spinner_bg");
        stopped = getArguments().getBoolean("stop");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String r = spinner.getSelectedItem().toString();
                sessionManager.setRoute(r);
                if (spinner.getSelectedItemPosition() <= 0 || position <= 0) {
                    spinnerSelected = false;
                    //spinnerState = false;
                    sessionManager.setHasSpinnerSelected(spinnerSelected);
                    position = spinner.getSelectedItemPosition();
                    sessionManager.setPosition(position);
                } else {
                    //sessionManager.setPosition(spinner.getSelectedItemPosition());
                    spinnerSelected = true;
                    //spinnerState = true;
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
                //sessionManager.setHasSpinnerSelected(true);
                sessionManager.setRoute(r);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start.getText().equals("START")) {
                    forStart();
                } else if (start.getText().equals("STOP")) {
                    LastTripDialog stopping = LastTripDialog.newInstance("Are you sure you want to stop?", 0);
                    stopping.show(getActivity().getFragmentManager(), "stop_dialog");
                    //forStop();
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

    public void forStart() {
        //spinnerSelected == false
        if (spinnerSelected == false) {
            DialogFragment newFrag = AlertDialogFragment.newInstance("Please select Route.");
            newFrag.show(getActivity().getFragmentManager(), "dialog");
        } else {
            if (!isLocationEnabled(getActivity())){
                DialogFragment newFragment = AlertDialogFragment.newInstance("Please turn on Location.");
                newFragment.show(getActivity().getFragmentManager(), "dialog");
            } else {

                //ask if last trip na
                LastTripDialog lastTrip = LastTripDialog.newInstance("Is this your last trip?", 1);
                lastTrip.show(getActivity().getFragmentManager(), "lastTrip_dialog");

                sessionManager.setHasStarted(true); //---uncomment this if di na mu-work
                sessionManager.setSpinnerState(false);
                if (sessionManager.hasStarted()){
                    state = false;
                    text = "STOP";
                    bg = R.drawable.stop_btn_bg;
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

    public static void forStop() {
       // sessionManager.setHasStarted(false);
        //spinner.setSelection(sessionManager.getPosition());
        //sessionManager.setSpinnerState(true);

        //uncomment this part
//        if (sessionManager.getSpinnerState() == true) {
//            spinnerState = true;
//            position = sessionManager.getPosition();
//            spinner_bg = R.drawable.spinner_bg;
//            sessionManager.setSpinnerState(true);
//            spinnerSetState(spinnerState, spinner_bg, position);
//            //spinnerSetState(true, R.drawable.spinner_bg, sessionManager.getPosition());
//        } else {
//            spinnerSetState(spinnerState, spinner_bg, position);
//        }


//        if (sessionManager.getSpinnerState() == true) {
//            spinnerEnable();
//        }

        /*
        if (!sessionManager.hasStarted()){

            state = true;

            text = "START";
            bg = R.drawable.circle_back;

            //start.setBackground(getResources().getDrawable(bg));
            start.setText(text);

            spinColor = R.drawable.spinner_bg;

            spinnerState = true;
            position = sessionManager.getPosition();
            spinner_bg = R.drawable.spinner_bg;
            sessionManager.setSpinnerState(true);
            //spinnerSetState(spinnerState, spinner_bg, position);
        } else {
            spinnerState = false;
            position = sessionManager.getPosition();
            spinner_bg = R.drawable.spinner_selected_item_bg;
            sessionManager.setSpinnerState(false);
            //spinnerSetState(spinnerState, spinner_bg, position);
        }

        if (!sessionManager.hasStarted()) {
            Intent intent = new Intent(getActivity(), SendService.class);
            getActivity().stopService(intent);
            Toast.makeText(getActivity().getApplicationContext(), "Sending Stopped", Toast.LENGTH_LONG).show();
        } else {
        }
        */
    }

    public void setToStart() {
        start.setBackground(getResources().getDrawable(R.drawable.start_button_background));
        start.setText("START");
        start.setClickable(true);
    }

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

        }else{
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

    @Override
    public void onPause() {
        super.onPause();

        Spinner spinner = (Spinner) v.findViewById(R.id.route_list);
        //sessionManager.setPosition(spinner.getSelectedItemPosition());
        //sessionManager.prefs.edit().putInt("spin_position", spinner.getSelectedItemPosition()).apply();

        sessionManager.prefs.edit().putInt("spin_position", sessionManager.getPosition()).apply();
        sessionManager.prefs.edit().putBoolean("spin_state", sessionManager.hasStarted()).apply();
        //sessionManager.prefs.edit().putInt("spin_color", spinColor).apply();
        //sessionManager.prefs.edit().putInt("spin_state", spinner.getSelectedItemPosition()).apply();
    }

    @Override
    public void onResume() {
        super.onResume();

        Spinner spinner = (Spinner) v.findViewById(R.id.route_list);
        int in = sessionManager.prefs.getInt("spin_position", 0);
        //int in = sessionManager.getPosition();
        boolean s = sessionManager.prefs.getBoolean("spin_state", false);
        //int b = sessionManager.prefs.getInt("spin_color", 0);
        spinner.setSelection(in);
        spinner.setEnabled(!s);
        spinner.setClickable(!s);

        if (s == false) {
            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_bg));
        } else {
            spinner.setBackground(getResources().getDrawable(R.drawable.spinner_selected_item_bg));
        }

        //String str = spinner.getItemAtPosition(in).toString();
        sessionManager.setRoute(spinner.getItemAtPosition(in).toString());
    }

    @Override
    public void onStop() {
//        stopFusedLocation();
        super.onStop();
    }

    // check if google play services is installed on the device
//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                Toast.makeText(getActivity().getApplicationContext(),
//                        "This device is supported. Please download google play services", Toast.LENGTH_LONG)
//                        .show();
//            } else {
//                Toast.makeText(getActivity().getApplicationContext(),
//                        "This device is not supported.", Toast.LENGTH_LONG)
//                        .show();
//                //finish();
//            }
//            return false;
//        }
//        return true;
//    }
//
//    public void sendLocation() {
//        String latVal = String.valueOf(fusedLatitude);
//        String longVal = String.valueOf(fusedLongitude);
//
//        if(!(latVal.equals("") || longVal.equals("") || latVal.equals("Latitude not found") || longVal.equals("Longitude not found"))) {
//            String lat = String.valueOf(getFusedLatitude());
//            String lng = String.valueOf(getFusedLongitude());
//            final String busNumber = LoginActivity.getBusNumber();
//            DatabaseReference pushRef = mRoot.child("Bus").child(busNumber);
//            bus.setPosition(lat,lng);
//            pushRef.setValue(bus); //instead of new always, we only have 1 bus reference. //calling new buses is weird
//            Toast.makeText(getActivity().getApplicationContext(), "DATA SENT", Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(getActivity().getApplicationContext(), "DATA NOT SENT", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    public void startFusedLocation() {
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext()).addApi(LocationServices.API)
//                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//                        @Override
//                        public void onConnectionSuspended(int cause) {
//                        }
//
//                        @Override
//                        public void onConnected(Bundle connectionHint) {
//
//                        }
//                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
//
//                        @Override
//                        public void onConnectionFailed(ConnectionResult result) {
//
//                        }
//                    }).build();
//            mGoogleApiClient.connect();
//        } else {
//            mGoogleApiClient.connect();
//        }
//    }
//
//    public void stopFusedLocation() {
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.disconnect();
//        }
//    }
//
//    public void registerRequestUpdate(final LocationListener listener) {
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(20 * 1000); // every 3 seconds //1000
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                try {
//                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);
//                } catch (SecurityException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    if (!isGoogleApiClientConnected()) {
//                        mGoogleApiClient.connect();
//                    }
//                    registerRequestUpdate(listener);
//                }
//            }
//        }, 1000);
//    }
//
//    public void stopRequestUpdate() {
//        //changed this to getActivity().getApplicationContext();
//        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//    }
//
//    public boolean isGoogleApiClientConnected() {
//        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        mLocation = location;
//        if (location == null) {
//            showMyDialog("Turn on Location");
//        } else {
//            setFusedLatitude(location.getLatitude());
//            setFusedLongitude(location.getLongitude());
//            Toast.makeText(getActivity().getApplicationContext(), "LOCATION UPDATED", Toast.LENGTH_LONG).show();
//            sendLocation();
//        }
//    }
//
//    void showMyDialog(String title) {
//        DialogFragment newFragment = AlertDialogFragment.newInstance(title);
//        newFragment.show(getActivity().getFragmentManager(), "dialog");
//    }
//
//    public void setFusedLatitude(double lat) { fusedLatitude = lat; }
//
//    public void setFusedLongitude(double lon) {
//        fusedLongitude = lon;
//    }
//
//    public double getFusedLatitude() {
//        return fusedLatitude;
//    }
//
//    public double getFusedLongitude() {
//        return fusedLongitude;
//    }


}