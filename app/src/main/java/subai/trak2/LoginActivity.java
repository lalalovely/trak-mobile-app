package subai.trak2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        busNumber = (EditText) findViewById(R.id.busNumText);
        login = (Button) findViewById(R.id.btnLogin);

        routeSpinner = (Spinner) findViewById(R.id.routeList);

        routeSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (busNumber.getText().toString().isEmpty()){
                    showMyDialog("PLEASE INPUT BUS NUMBER.");
                    //Toast.makeText(getApplicationContext(), "PLEASE INPUT BUS NUMBER", Toast.LENGTH_LONG).show();
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
                    checkBusNumber();
                }
                return false;
            }
        });
        String[] r = {"Select Route"};
        ArrayAdapter<String> routeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, r);

        routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        routeSpinner.setAdapter(routeAdapter);
        routeSpinner.setOnItemSelectedListener(this);

        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //changed
                if (busNumber.getText().toString().isEmpty() && selectedRoute.equals("Select Route")){
                    showMyDialog("PLEASE INPUT BUS NUMBER AND SELECT ROUTE.");
                } else if (!selectedRoute.equals("Select Route")){
                    sendMessage();
                } else {
                    showMyDialog("PLEASE SELECT YOUR ROUTE.");
                    //Toast.makeText(getApplicationContext(), "PLEASE SELECT YOUR ROUTE", Toast.LENGTH_LONG).show();
                }
            }

        });


    }

    void showMyDialog(String title) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(title);
        newFragment.show(getFragmentManager(), "dialog");
    }

    public void sendMessage() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void checkBusNumber(){
        DatabaseReference busRef = ref.child("Bus_Accounts");
        busRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String busNum = busNumber.getText().toString();
                if(!busNum.isEmpty()){
                    if (!dataSnapshot.hasChild(busNum)) {
                        busNumber.setText("");
                        showMyDialog("BUS DOES NOT EXIST.");
                        //Toast.makeText(getApplicationContext(), "BUS DOES NOT EXIST", Toast.LENGTH_LONG).show();
                    } else {
                        setRouteOption();
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


