package subai.trak2;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
    public Button start, stop;
    private Location mLocation;
    private boolean updating = false;
    private int color = 0;
    private int ctrClicks = 0;
    public static int ctr = 0;
    public static boolean send = true;
    private String route;
    private DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference();
    ; //root database

    private double fusedLatitude = 0.0;
    private  double fusedLongitude = 0.0;

    private TextView display;

    private String status = "";

    private boolean isClicked = false;

    private Bus bus;

    private Spinner spinner;

    UserSessionManager sessionManager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;

    String text;
    int bg;
    boolean cl;

    public View v;

    public void setBus(Bus bus){
        this.bus = bus;
    }

    public static LocationTab newInstance(int b, String t, boolean c) {
        Bundle bundle = new Bundle();
        bundle.putString("start_btn_txt", t);
        bundle.putInt("start_btn_bg", b);
        bundle.putBoolean("start_btn_click", c);

        LocationTab fragment = new LocationTab();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.location_frag, container, false);

        start = (Button) v.findViewById(R.id.start_button);
        stop = (Button) v.findViewById(R.id.stop_button);

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

        text = getArguments().getString("start_btn_txt");
        bg = getArguments().getInt("start_btn_bg");
        cl = getArguments().getBoolean("start_btn_click");

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLocationEnabled(getActivity())){
                    DialogFragment newFragment = AlertDialogFragment.newInstance("Please turn on Location.");
                    newFragment.show(getActivity().getFragmentManager(), "dialog");
                } else {
//                    if (bus.getRoute().equals("Route")) {
//                        DialogFragment newFragment = AlertDialogFragment.newInstance("Please select Route.");
//                        newFragment.show(getActivity().getFragmentManager(), "dialog");
//                    } else {
                        sessionManager.setHasStarted(true);
                        if (sessionManager.hasStarted()){
                            text = "TRAK";
                            bg = R.drawable.started_btn_background;
                            cl = false;

                            start.setBackground(getResources().getDrawable(bg));
                            start.setText(text);
                            start.setClickable(cl);
                        } else {
                            start.setBackground(getResources().getDrawable(bg));
                            start.setText(text);
                            start.setClickable(cl);
                        }
                    //}
                        Intent intent = new Intent(getActivity(), SendService.class);
                        getActivity().startService(intent);
                    //}
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.setHasStarted(false);
                if (!sessionManager.hasStarted()){
                    text = "START";
                    bg = R.drawable.start_button_background;
                    cl = true;

                    start.setBackground(getResources().getDrawable(bg));
                    start.setText(text);
                    start.setClickable(cl);
                }
                Intent intent = new Intent(getActivity(), SendService.class);
                getActivity().stopService(intent);
                Toast.makeText(getActivity().getApplicationContext(), "Sending Stopped", Toast.LENGTH_LONG).show();
            }
        });

        start.setBackground(getResources().getDrawable(bg));
        start.setText(text);
        start.setClickable(cl);

        return v;
    }

    public void setToStart() {
        start.setBackground(getResources().getDrawable(R.drawable.start_button_background));
        start.setText("START");
        start.setClickable(true);
    }

//    public void onClickStart() {
//        if (checkPlayServices()) {
//            if (!isLocationEnabled(getActivity())){
//                DialogFragment newFragment = AlertDialogFragment.newInstance("Please Turn On  Location");
//                newFragment.show(getActivity().getFragmentManager(), "dialog");
//            } else {
//                isClicked = true;
//                ctrClicks++;
//                //display.setText("Location is being tracked");
//                DialogFragment lastTrip = LastTripDialog.newInstance("Is it your last trip?");
//                lastTrip.show(getActivity().getFragmentManager(), "dialog");
//                //display.setText("");
//                if (ctrClicks == 1) {
//                    status = "In-transit";
//                } else {
////                    status = bus.getStatus();
//                }
////                bus.setStatus(status);
//                Toast.makeText(getActivity().getApplicationContext(), "TRAK", Toast.LENGTH_LONG).show();
//                startFusedLocation();
//                start.setText("Trak");
//                start.setClickable(false);
//                registerRequestUpdate(this);
//            }
//
//        }
//    }

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
