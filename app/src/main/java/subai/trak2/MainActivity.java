package subai.trak2;

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

    private IOTab ioTab;
    private SOSTab sosTab;
    private Bus bus;
    private DetailsTab detailsTab;

    public int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting...");

        mSectionPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setLogo(R.drawable.trak_appbar);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //getSupportActionBar().setTitle("TRAK");
        //getSupportActionBar().setIcon(R.drawable.trak_appbar);

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

        tabLayout.getTabAt(0).setIcon(R.drawable.unselected_io);
        tabLayout.getTabAt(1).setIcon(R.drawable.unselected_sos);
        tabLayout.getTabAt(2).setIcon(R.drawable.unselected_details);
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
                           tab.setIcon(R.drawable.selected_io);
                           break;
                       case 1:
                           tab.setIcon(R.drawable.selected_sos);
                           break;
                       case 2:
                           tab.setIcon(R.drawable.selected_details);
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
                           tab.setIcon(R.drawable.unselected_io);
                           break;
                       case 1:
                           tab.setIcon(R.drawable.unselected_sos);
                           break;
                       case 2:
                           tab.setIcon(R.drawable.unselected_details);
                           break;
                       default:

                   }
               }

               @Override
               public void onTabReselected(TabLayout.Tab tab) {

               }
           });
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
