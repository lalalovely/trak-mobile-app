package subai.trak2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class SectionsAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentListIntro = new ArrayList<>();

    public SectionsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentListIntro.get(position);
    }

    public void addFragment(Fragment fragment) {
        mFragmentListIntro.add(fragment);
    }

    @Override
    public int getCount() {
        return mFragmentListIntro.size();
    }
}
