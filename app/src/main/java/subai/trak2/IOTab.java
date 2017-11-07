package subai.trak2;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class IOTab extends Fragment implements LocationListener {
    private static final String TAG = "@string/io_tag";

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;
    private TextView latitude, longitude;
    private Button display, update, send;
    private Location mLocation;
    private boolean updating = false;
    private int red = R.color.warning_red;
    private int green = R.color.okay_green;
    private int color = 0;
    private int ctrClicks = 0;
    public static int ctr = 0;
    private DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference();
    ; //root database

    private double fusedLatitude = 0.0;
    private  double fusedLongitude = 0.0;

    private String latiText = "";
    private String longiText = "";
    private String status = "";

    private String latiToDisplay = "";
    private String longiToDisplay = "";

    private boolean isClicked = false;

    private Bus bus;

    public void setBus(Bus bus){
        this.bus = bus;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.io_tab, container, false);

        latitude = (TextView) v.findViewById(R.id.textview_latitude);
        longitude = (TextView) v.findViewById(R.id.textview_longitude);
        display = (Button) v.findViewById(R.id.btnDisplay);

        if (savedInstanceState == null) {
        } else {
            latiToDisplay = savedInstanceState.getString("latitude");
            longiToDisplay = savedInstanceState.getString("longitude");
            status = savedInstanceState.getString("stat");
            color = savedInstanceState.getInt("textColor");
            ctrClicks = savedInstanceState.getInt("counter");
            isClicked = savedInstanceState.getBoolean("clicked");

            latitude = (TextView) v.findViewById(R.id.textview_latitude);
            longitude = (TextView) v.findViewById(R.id.textview_longitude);
            display = (Button) v.findViewById(R.id.btnDisplay);
            //bus.setStatus("In-transit");
            latitude.setText(latiToDisplay);
            longitude.setText(longiToDisplay);

            if (ctr >= 1) {
                onClickStart();
            }
            if (color == red) {
                latitude.setTextColor(getResources().getColor(R.color.warning_red));
                longitude.setTextColor(getResources().getColor(R.color.warning_red));
            } else if (color == green) {
                latitude.setTextColor(getResources().getColor(R.color.okay_green));
                longitude.setTextColor(getResources().getColor(R.color.okay_green));
            } else {
            }
        }

        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctr++;
                onClickStart();
            }
        });

        return v;
    }

    public void onClickStart() {
        if (checkPlayServices()) {
            if (!isLocationEnabled(getActivity())){
                    DialogFragment newFragment = AlertDialogFragment.newInstance("Please Turn On  Location");
                    newFragment.show(getActivity().getFragmentManager(), "dialog");
            } else {
                ctrClicks++;
                if (ctrClicks == 1) {
                    status = "In-transit";
                } else {
                    status = bus.getStatus();
                }
                bus.setStatus(status);
                Toast.makeText(getActivity().getApplicationContext(), "TRAK", Toast.LENGTH_LONG).show();
                startFusedLocation();
                registerRequestUpdate(this);
            }

        }
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
        outState.putString("latitude", latiToDisplay); //change lang diri latiText
        outState.putString("longitude", longiToDisplay); //change lang longiText
        outState.putString("stat", status);
        outState.putBoolean("clicked", isClicked);
        outState.putInt("textColor", color);
        outState.putInt("counter", ctrClicks);
    }

    @Override
    public void onStop() {
        stopFusedLocation();
        super.onStop();
    }

    // check if google play services is installed on the device
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "This device is supported. Please download google play services", Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                //finish();
            }
            return false;
        }
        return true;
    }

    public void sendLocation() {
        String latVal = String.valueOf(fusedLatitude);
        String longVal = String.valueOf(fusedLongitude);

        if(!(latVal.equals("") || longVal.equals("") || latVal.equals("Latitude not found") || longVal.equals("Longitude not found"))) {
            String lat = String.valueOf(getFusedLatitude());
            String lng = String.valueOf(getFusedLongitude());
            final String busNumber = LoginActivity.getBusNumber();
            DatabaseReference pushRef = mRoot.child("Bus").child(busNumber);
            bus.setPosition(lat,lng);
            pushRef.setValue(bus); //instead of new always, we only have 1 bus reference. //calling new buses is weird
            Toast.makeText(getActivity().getApplicationContext(), "DATA SENT", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "DATA NOT SENT", Toast.LENGTH_LONG).show();
        }
    }

    public void startFusedLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext()).addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnectionSuspended(int cause) {
                        }

                        @Override
                        public void onConnected(Bundle connectionHint) {

                        }
                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

                        @Override
                        public void onConnectionFailed(ConnectionResult result) {

                        }
                    }).build();
            mGoogleApiClient.connect();
        } else {
            mGoogleApiClient.connect();
        }
    }

    public void stopFusedLocation() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public void locationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5 * 1000); // every 5 seconds //1000
    }

    public void registerRequestUpdate(final LocationListener listener) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(20 * 1000); // every 3 seconds //1000

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!isGoogleApiClientConnected()) {
                        mGoogleApiClient.connect();
                    }
                    registerRequestUpdate(listener);
                }
            }
        }, 1000);
    }

    public void stopRequestUpdate() {
        //changed this to getActivity().getApplicationContext();
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public boolean isGoogleApiClientConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    public void updateLocation() {
        if (!updating) {
            update.setText("Done");
            updating = true;
            registerRequestUpdate(this);
        } else {
            update.setText("Update");
            Toast.makeText(getActivity().getApplicationContext(), "LOCATION UPDATED", Toast.LENGTH_LONG).show();
            updating = false;
            stopRequestUpdate();
        }
    }

    public void displayLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            setFusedLatitude(mLocation.getLatitude());
            setFusedLongitude(mLocation.getLongitude());
            String lati = "Latitude is " + String.valueOf(getFusedLatitude());
            String longi = "Longitude is " + String.valueOf(getFusedLongitude());

            latiText = lati;
            longiText = longi;

            latitude.setText(latiText);
            longitude.setText(longiText);
        } else {
            latiText = "Latitude not found";
            longiText = "Longitude not found";

            latitude.setText(latiText);
            longitude.setText(longiText);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        //add function here that would alert the user to turn on location
        if (location == null) {
            showMyDialog("Turn on Location");

            latiText = "Turn on Location";
            longiText = "Turn on Location";

            latiToDisplay = latiText;
            longiToDisplay = longiText;
            latitude.setText(latiToDisplay);
            longitude.setText(longiToDisplay);
            color = red;
            latitude.setTextColor(getResources().getColor(R.color.warning_red));
            longitude.setTextColor(getResources().getColor(R.color.warning_red));
        } else {
            color = green;
            setFusedLatitude(location.getLatitude());
            setFusedLongitude(location.getLongitude());
            Toast.makeText(getActivity().getApplicationContext(), "LOCATION UPDATED", Toast.LENGTH_LONG).show();
            String lati = String.valueOf(getFusedLatitude());
            String longi = String.valueOf(getFusedLongitude());
            latiText = lati;
            longiText = longi;

            latiToDisplay = LocationConverter.getLatitudeAsDMS(location, 4);
            longiToDisplay = LocationConverter.getLongitudeAsDMS(location, 4);

            latitude.setText(latiToDisplay);
            longitude.setText(longiToDisplay);
            latitude.setTextColor(getResources().getColor(R.color.okay_green));
            longitude.setTextColor(getResources().getColor(R.color.okay_green));
            sendLocation();
        }
//        latitude.setText("" + getFusedLatitude());
//        longitude.setText("" + getFusedLongitude());
//        latitude.setText(latiText);
//        longitude.setText(longiText);
    }

    void showMyDialog(String title) {
        DialogFragment newFragment = SOSTab.AlertDialogFrag.newInstance(title);
        newFragment.show(getActivity().getFragmentManager(), "dialog");
    }

    public void setFusedLatitude(double lat) { fusedLatitude = lat; }

    public void setFusedLongitude(double lon) {
        fusedLongitude = lon;
    }

    public double getFusedLatitude() {
        return fusedLatitude;
    }

    public double getFusedLongitude() {
        return fusedLongitude;
    }

}
