package subai.trak2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

//adapter for fragments (onboarding pages) for the intro activity
public class SectionsPageAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    //adds fragment to the viewpager
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    //constructor
    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    //returns the title of the fragment
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    //gets the fragment based on its position on the viewpager
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    //returns the number of fragments in the viewpager
    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
