package subai.trak2;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

    private IOTab ioTab;
    private SOSTab sosTab;
    private Bus bus;
    private DetailsTab detailsTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting...");

        mSectionPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("TRAK");

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionPageAdapter);
        setupViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

//        final TabLayout.Tab io = tabLayout.newTab();
//        final TabLayout.Tab sos = tabLayout.newTab();
//        final TabLayout.Tab deet = tabLayout.newTab();
//
//        io.setIcon(R.drawable.io_icon);
//        sos.setIcon(R.drawable.sos_icon);
//        deet.setIcon(R.drawable.details_icon);
//
//        tabLayout.addTab(io, 0);
//        tabLayout.addTab(sos, 1);
//        tabLayout.addTab(deet, 2);

        tabLayout.setupWithViewPager(mViewPager);

        try {
            View view1 = getLayoutInflater().inflate(R.layout.customtab, null);
            view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.io_icon);
            tabLayout.getTabAt(0).setCustomView(view1);

            View view2 = getLayoutInflater().inflate(R.layout.customtab, null);
            view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.sos_icon);
            tabLayout.getTabAt(1).setCustomView(view2);

            View view3 = getLayoutInflater().inflate(R.layout.customtab, null);
            view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.details_icon);
            tabLayout.getTabAt(2).setCustomView(view3);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        ioTab = new IOTab();
        sosTab = new SOSTab();
        detailsTab = new DetailsTab();
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

        // set route
        bus.setRoute(LoginActivity.getRoute());

        ioTab.setBus(bus);
        sosTab.setBus(bus);
        detailsTab.setBus(bus);
        //make a details tab

        adapter.addFragment(ioTab, "I/O");
        adapter.addFragment(sosTab, "SOS");
        adapter.addFragment(detailsTab, "DETAILS");
        viewPager.setAdapter(adapter);
    }

}
