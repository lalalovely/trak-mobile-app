package subai.trak2;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//it holds the tabs that which are the Location tab and the Messaging tab
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionPageAdapter;
    private ViewPager mViewPager;       //enables the switching of tabs (fragments) inside the activity
    private TabLayout tabLayout;        //holds the two main tabs
    private Toolbar mToolBar;           //acts as the action bar

    private Bus bus;                    //bus object

    //two main tabs found in the main activity
    private LocationTab locationTab;
    private MessagingTab messagingTab;

    UserSessionManager sessionManager;  //object used to save things in the SharedPreferences for persistent data
    int bg;                             //background of the button
    String strTxt;
    int p;                              //indicates the position of an item in the drop down spinner
    int sbg;                            //background style of the spinner
    boolean ss;                         //state of the spinner, true if an item has been selected, and false if nothing has been selected
    boolean abtStop = false;            //if the user clicked stop button

    //initiliazed when the main activity is shown
    //this contains the initialization of variables
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new UserSessionManager(this);

        //checks if teh user is not logged in
        if (!sessionManager.isUserLoggedIn()) {
            logout();
        }
        mSectionPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("");

        //checks if the user has clicked the start button
        if (!sessionManager.hasStarted()) {
            bg = R.drawable.start_another;
            strTxt = "";
            p = sessionManager.getPosition();
            sbg = R.drawable.spinner_selected_item_bg;
            ss = false;
            abtStop = true;
        } else if (sessionManager.hasStarted()) {
            bg = R.drawable.stop_another;
            strTxt = "";
            p = sessionManager.getPosition();
            sbg = R.drawable.spinner_bg;
            ss = true;
            abtStop = false;
        }
        setSupportActionBar(mToolBar);      //sets the toolbar as the action bar
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionPageAdapter);
        setupViewPager(mViewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //listens to the switching of tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            //when a certain tab is selected
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View focus = getCurrentFocus();
                if (focus != null) {
                    hiddenKeyboard(focus);
                }
            }

            //when a certain tab is unselected
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View focus = getCurrentFocus();
                if (focus != null) {
                    hiddenKeyboard(focus);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                View focus = getCurrentFocus();
                if (focus != null) {
                    hiddenKeyboard(focus);
                }
            }
        });
    }

    //when the soft keyboard is displayed in the Messaging tab then the user switches to another tab, this function
    //hides the soft keyboard
    private void hiddenKeyboard(View v) {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    //when the notification message is clicked, it automatically shows the message tab
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String data = intent.getStringExtra("fromNotif");
            if (data != null) {
                ViewPager view = (ViewPager) this.findViewById(R.id.container);
                view.setCurrentItem(1);
            }
        }
    }

    //called when the user opts to logout from the app, shows log in page
    //resets the necessary information
    public void logout() {
        sessionManager.setPosition(0);
        sessionManager.setSpinnerState(true);
        sessionManager.setLoggedIn(false);
        sessionManager.setHasStarted(false);
        sessionManager.setStatus("In-transit");
        finish();
        Intent notifIntent = new Intent(this, NotifService.class);
        this.stopService(notifIntent);
        Intent intent = new Intent(this, SendService.class);
        this.stopService(intent);
        DatabaseReference statRef = FirebaseDatabase.getInstance().getReference().child("Bus_Accounts").child(sessionManager.getBusNum());
        statRef.child("status").setValue("Offline");
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        //initializes the tab fragments
        locationTab = LocationTab.newInstance(bg, strTxt, p, ss, sbg, abtStop);
        messagingTab = new MessagingTab();
        bus = new Bus();

        // set accomodation
        // set busCompany
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Bus_Accounts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.child(sessionManager.getBusNum()).getChildren();
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
        locationTab.setBus(bus);
        adapter.addFragment(locationTab, "Location");
        adapter.addFragment(messagingTab, "Messaging");
        viewPager.setAdapter(adapter);
    }

    //overflow menu located on the rightmost part of the toolbar or the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    //handles user clicks on the overflow menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.help_btn) {          //if the user clicks help, the Help activity is shown
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.logout) {     //when the user logs out from the app, it calls the log out function
            logout();
            return true;
        } else {}
        return super.onOptionsItemSelected(item);
    }

}
