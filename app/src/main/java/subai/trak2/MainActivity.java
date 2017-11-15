package subai.trak2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

    public int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting...");

        mSectionPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("LOCATION");

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionPageAdapter);
        setupViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_location_selected);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_chat);
        mViewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        tabLayout.setOnTabSelectedListener(new
           TabLayout.OnTabSelectedListener()
           {
               @Override
               public void onTabSelected(TabLayout.Tab tab) {
                   int s = tab.getPosition();
                   switch (s) {
                       case 0:
                           getSupportActionBar().setTitle("LOCATION");
                           tab.setIcon(R.drawable.ic_location_selected);
                           break;
                       case 1:
                           getSupportActionBar().setTitle("MESSAGING");
                           tab.setIcon(R.drawable.ic_chat_selected);
                           break;
                       default:

                   }
                   //mViewPager.setCurrentItem(tab.getPosition());
               }

               @Override
               public void onTabUnselected(TabLayout.Tab tab) {
                   int s = tab.getPosition();
                   switch (s) {
                       case 0:
                           tab.setIcon(R.drawable.ic_location);
                           break;
                       case 1:
                           tab.setIcon(R.drawable.ic_chat);
                           break;
                       default:
                   }
               }

               @Override
               public void onTabReselected(TabLayout.Tab tab) {
                   int s = tab.getPosition();
                   switch (s) {
                       case 0:
                           tab.setIcon(R.drawable.ic_location_selected);
                           break;
                       case 1:
                           tab.setIcon(R.drawable.ic_chat_selected);
                           break;
                       default:

                   }
               }
           });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        locationTab = new LocationTab();
        messagingTab = new MessagingTab();
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
        if (id == R.id.details_btn) {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.help_btn) {
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else {}
        return super.onOptionsItemSelected(item);
    }

}
