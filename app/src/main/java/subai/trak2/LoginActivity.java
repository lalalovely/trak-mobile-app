package subai.trak2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;


public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static EditText busNumber;
    private static EditText route;
    private Button login;
    private static DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private static Spinner routeSpinner;
    private static String selectedRoute = "";
    private String busCompany;
    private String bus_number;

    UserSessionManager sessionManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        sessionManager = new UserSessionManager(this);

        busNumber = (EditText) findViewById(R.id.busNumText);
        login = (Button) findViewById(R.id.btnLogin);

        if (sessionManager.isUserLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
            checkBusNumber();
            if (busNumber.getText().toString().isEmpty()){
                //showMyDialog("PLEASE INPUT BUS NUMBER.");
                Toast.makeText(getApplicationContext(), "PLEASE INPUT BUS NUMBER", Toast.LENGTH_LONG).show();
            } else {
                ref.child("Bus_Accounts").child(busNumber.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> d = dataSnapshot.getChildren();
                        for (DataSnapshot data: d){
                            if (data.getKey().equals("busCompany")){
                                setBusCompany(data.getValue().toString());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
                sessionManager.setLoggedIn(true);
                sendMessage();
            }
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //    void showMyDialog(String title) {
//        DialogFragment newFragment = AlertDialogFragment.newInstance(title);
//        newFragment.show(getFragmentManager(), "dialog");
//    }

    public void sendMessage() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void checkBusNumber(){
        DatabaseReference busRef = ref.child("Bus_Accounts");
        busRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bus_number = busNumber.getText().toString();
                if(!bus_number.isEmpty()){
                    if (!dataSnapshot.hasChild(bus_number)) {
                        busNumber.setText("");
                        //Toast.makeText(getApplicationContext(), "BUS DOES NOT EXIST", Toast.LENGTH_LONG).show();
                        //showMyDialog("BUS DOES NOT EXIST.");
                    } else {
                        sessionManager.setBusNumber(bus_number);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setRouteOption(){
        DatabaseReference route = ref.child("Route").child(busCompany);
        route.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> routes = dataSnapshot.getChildren();
                ArrayList<String> x = new ArrayList<String>();
                x.add("Select Route");
                for (DataSnapshot d: routes){
                    Log.d("ROUTES----", d.getKey());
                    x.add(d.getKey());
                }

                setRoute(x);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setRoute(ArrayList<String> x){
        Log.d("VALUESS", x.get(0));
        String[] val = x.toArray(new String[0]);
        ArrayAdapter<String> routeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, val);
        routeSpinner.setAdapter(routeAdapter);
    }

    public void setBusCompany(String company){
        busCompany = company;
    }

    public static String getBusNumber(){
        return busNumber.getText().toString();
    }

    public static String getRoute(){
        return selectedRoute;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedRoute = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}


