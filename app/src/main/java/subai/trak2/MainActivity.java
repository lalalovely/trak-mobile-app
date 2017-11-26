package subai.trak2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionPageAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private Toolbar mToolBar;

    private Bus bus;
    private LocationTab locationTab;
    private MessagingTab messagingTab;
//<<<<<<< HEAD
//=======
    private DetailsActivity detailsActivity; //details for the bus
    private Spinner spinner;

//>>>>>>> 73607e5213982b3fa2686cf4cd379a1e04217dce
    UserSessionManager sessionManager;
    int bg;
    String strTxt;
    int p;
    int sbg;
    boolean ss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting...");
        sessionManager = new UserSessionManager(this);

        if (!sessionManager.isUserLoggedIn()) {
            logout();
        }

        mSectionPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("Trak");

        if (!sessionManager.hasStarted()) {
            bg = R.drawable.circle_back;
            strTxt = "START";

            p = sessionManager.getPosition();
            sbg = R.drawable.spinner_selected_item_bg;
            ss = false;
        } else if (sessionManager.hasStarted()) {
            bg = R.drawable.stop_btn_bg;
            strTxt = "STOP";

            p = sessionManager.getPosition();
            sbg = R.drawable.spinner_bg;
            ss = true;
        }

        //uncomment this part if sayop
//        if (!sessionManager.getSpinnerState()) {
//            p = sessionManager.getPosition();
//            sbg = R.drawable.spinner_selected_item_bg;
//            ss = false;
//        } else {
//            p = sessionManager.getPosition();
//            sbg = R.drawable.spinner_bg;
//            ss = true;
//        }

        setSupportActionBar(mToolBar);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionPageAdapter);
        setupViewPager(mViewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void logout() {
        //sessionManager.setPosition(0);
        sessionManager.setSpinnerState(true);
        sessionManager.setLoggedIn(false);
        sessionManager.setHasStarted(false);
        finish();

        Intent intent = new Intent(this, SendService.class);
        this.stopService(intent);
        //Toast.makeText(this.getApplicationContext(), "Sending Stopped", Toast.LENGTH_LONG).show();

        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        locationTab = LocationTab.newInstance(bg, strTxt, p, ss, sbg);
        messagingTab = new MessagingTab();
        detailsActivity = new DetailsActivity();
        bus = new Bus();

        // set accomodation
        // set busCompany
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Bus_Accounts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.child(LoginActivity.getBusNumber()).getChildren();
                for (DataSnapshot child : children){
                    if (child.getKey().equals("accommodation")){
                        bus.setAccommodation(child.getValue().toString());
                    }
                    if (child.getKey().equals("busCompany")){
                        bus.setBusCompany(child.getValue().toString());
                    }
                }
            }
            public void onCancelled(DatabaseError databaseError) {}
        });

       // bus.setRoute(LoginActivity.getRoute());

       //set bus for each tab here
        //make a details tab
        locationTab.setBus(bus);
        detailsActivity.setBusDetails(bus);
        adapter.addFragment(locationTab, "Location");
        adapter.addFragment(messagingTab, "Messaging");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.help_btn) {
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.logout) {
            logout();
            return true;
        } else {}
        return super.onOptionsItemSelected(item);
    }

}
