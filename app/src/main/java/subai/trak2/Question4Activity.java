package subai.trak2;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Question4Activity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView help_bar_view4;
    //private LinearLayout dotsLayout;
    //private TextView[] dots;
    private int[] layouts;
    private Button btnSkip4, btnNext4;
    private Toolbar q4_toolbar;
    private int curr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question4);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        q4_toolbar = (Toolbar) findViewById(R.id.toolbar_help4);
        setSupportActionBar(q4_toolbar);
        viewPager = (ViewPager) findViewById(R.id.q4_view_pager);
        help_bar_view4 = (TextView) findViewById(R.id.text_help4);
        //dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip4 = (Button) findViewById(R.id.prev_button4);
        btnNext4 = (Button) findViewById(R.id.next_button4);

        layouts = new int[]{
                R.layout.answer1_1,
                R.layout.answer1_2,
                R.layout.answer1_3};

        // adding bottom dots
        //addBottomDots(0);

        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        if (curr == layouts.length - 1) {
            btnNext4.setVisibility(View.GONE);
            btnSkip4.setVisibility(View.VISIBLE);
        } else {
            if (curr == 0) {
                btnNext4.setVisibility(View.VISIBLE);
                btnSkip4.setVisibility(View.GONE);
            } else {
                btnNext4.setVisibility(View.VISIBLE);
                btnSkip4.setVisibility(View.VISIBLE);
            }
        }

    }

    public void btnSkipClick4(View v) {
        viewPager.arrowScroll(View.FOCUS_LEFT);
    }

    public void btnNextClick4(View v) {
        viewPager.arrowScroll(View.FOCUS_RIGHT);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            curr = position;
            if (position == layouts.length - 1) {
                btnNext4.setVisibility(View.GONE);
                btnSkip4.setVisibility(View.VISIBLE);
            } else {
                if (position == 0) {
                    btnNext4.setVisibility(View.VISIBLE);
                    btnSkip4.setVisibility(View.GONE);
                } else {
                    btnNext4.setVisibility(View.VISIBLE);
                    btnSkip4.setVisibility(View.VISIBLE);
                }
            }
//            addBottomDots(position);
//            // changing the next button text 'NEXT' / 'GOT IT'
//            if (position == layouts.length - 1) {
//                // last page. make button text to GOT IT
//                btnNext.setText(getString(R.string.start));
//                btnSkip.setVisibility(View.GONE);
//            } else {
//                // still pages are left
//                btnNext.setText(getString(R.string.next));
//                btnSkip.setVisibility(View.VISIBLE);
//            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    public class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;


        public ViewPagerAdapter() {

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
