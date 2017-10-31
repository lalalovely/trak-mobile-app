package subai.trak2;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionPageAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private Toolbar mToolBar;

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
        adapter.addFragment(new IOTab(), "I/O");
        adapter.addFragment(new SOSTab(), "SOS");
        adapter.addFragment(new DetailsTab(), "DETAILS");
        viewPager.setAdapter(adapter);
    }

}
