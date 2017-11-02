package subai.trak2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static EditText busNumber;
    private static EditText route;
    private Button login;
    private DatabaseReference ref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        busNumber = (EditText) findViewById(R.id.busNumText);
        route = (EditText) findViewById(R.id.routeText);
        login = (Button) findViewById(R.id.btnLogin);
        ref = FirebaseDatabase.getInstance().getReference().child("Bus_Accounts");

        login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String busNum = busNumber.getText().toString();
                        if(busNum.isEmpty() || route.getText().toString().isEmpty()){
                            Toast.makeText(getApplicationContext(), "INCOMPLETE DATA", Toast.LENGTH_LONG).show();
                        } else {
                            if (dataSnapshot.hasChild(busNum)) {
                                sendMessage();
                            } else {
                                busNumber.setText("");
                                Toast.makeText(getApplicationContext(), "BUS DOES NOT EXIST", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        });
    }

    public void sendMessage() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
    public static String getBusNumber(){
        return busNumber.getText().toString();
    }

    public static String getRoute(){
        return route.getText().toString();
    }

}


