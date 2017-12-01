package subai.trak2;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SendService extends Service implements LocationListener  {
    private double fusedLatitude = 0.0;
    private  double fusedLongitude = 0.0;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;
    private DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference();
    private String busNumber = "1";
    UserSessionManager sessionManager;
    @Override
    public void onCreate() {
        super.onCreate();
         sessionManager = new UserSessionManager(this);
        busNumber =  sessionManager.getBusNum();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startFusedLocation();
        registerRequestUpdate(this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopFusedLocation();
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public void sendLocation() {
        String latVal = String.valueOf(fusedLatitude);
        String longVal = String.valueOf(fusedLongitude);

        if(!(latVal.equals("") || longVal.equals("") || latVal.equals("Latitude not found") || longVal.equals("Longitude not found"))) {
            String lat = String.valueOf(getFusedLatitude());
            String lng = String.valueOf(getFusedLongitude());
            DatabaseReference pushRef = mRoot.child("Bus").child(busNumber);
            Bus bus = new Bus();
            bus.setAccommodation(sessionManager.getAccomodation());
            bus.setBusCompany(sessionManager.getBusCompany());
            bus.setRoute(sessionManager.getRoute());
            bus.setPosition(lat,lng);
            bus.setStatus(sessionManager.getStatus());
            Log.d("!!!!!!!!!!!!!!!", sessionManager.getStatus());
            pushRef.setValue(bus);
            Toast.makeText(getApplicationContext(), "DATA SENT", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "DATA NOT SENT", Toast.LENGTH_LONG).show();
        }
    }

    public void startFusedLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(LocationServices.API)
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



    public boolean isGoogleApiClientConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        if (location == null) {
//            showMyDialog("Turn on Location");
        } else {
            setFusedLatitude(location.getLatitude());
            setFusedLongitude(location.getLongitude());
            Toast.makeText(getApplicationContext(), "LOCATION UPDATED", Toast.LENGTH_LONG).show();
            sendLocation();
        }
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

