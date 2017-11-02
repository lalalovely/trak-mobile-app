package subai.trak2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
    private DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference();
    ; //root database

    private double fusedLatitude = 0.0;
    private  double fusedLongitude = 0.0;

    private String latiText = "";
    private String longiText = "";

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
        update = (Button) v.findViewById(R.id.btnUpdate);
        send = (Button) v.findViewById(R.id.btnSend);

        if (savedInstanceState == null) {
        } else {
            latiText = savedInstanceState.getString("latitude");
            longiText = savedInstanceState.getString("longitude");
            latitude = (TextView) v.findViewById(R.id.textview_latitude);
            longitude = (TextView) v.findViewById(R.id.textview_longitude);

            latitude.setText(latiText);
            longitude.setText(longiText);
        }

        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStart();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLocation();
            }
        });

        return v;
    }

    public void onClickStart() {
        if (checkPlayServices()) {
            startFusedLocation();
            registerRequestUpdate(this);
            display.setText("IN-TRANSIT");
            display.setClickable(false);
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
        outState.putString("latitude", latiText);
        outState.putString("longitude", longiText);
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
        String latVal = latitude.getText().toString();
        String longVal = longitude.getText().toString();

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
        mLocationRequest.setInterval(3 * 1000); // every 3 seconds //1000

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
        //mLocation = location;
        if (location == null) {
            latiText = "Location not founc";
            longiText = "Longitude not found";
        } else {
            setFusedLatitude(location.getLatitude());
            setFusedLongitude(location.getLongitude());
            Toast.makeText(getActivity().getApplicationContext(), "NEW LOCATION RECEIVED", Toast.LENGTH_LONG).show();
            String lati = "Latitude is " + String.valueOf(getFusedLatitude());
            String longi = "Longitude is " + String.valueOf(getFusedLongitude());
            latiText = lati;
            longiText = longi;
        }
//        latitude.setText("" + getFusedLatitude());
//        longitude.setText("" + getFusedLongitude());
        latitude.setText(latiText);
        longitude.setText(longiText);
    }

    public void setFusedLatitude(double lat) {
        fusedLatitude = lat;
    }

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
