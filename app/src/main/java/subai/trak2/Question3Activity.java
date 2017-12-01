package subai.trak2;

import android.content.Context;
import android.content.Intent;
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

public class Question3Activity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView help_bar_view;
    //private LinearLayout dotsLayout;
    //private TextView[] dots;
    private int[] layouts;
    private Button btnSkip3, btnNext3;
    private Toolbar q3_toolbar;
    private int curr;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
        this.finish();
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question3);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        q3_toolbar = (Toolbar) findViewById(R.id.toolbar_help3);
        setSupportActionBar(q3_toolbar);

        q3_toolbar.setNavigationIcon(R.drawable.ic_prev);
        q3_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.q3_view_pager);
        help_bar_view = (TextView) findViewById(R.id.text_help3);
        //dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip3 = (Button) findViewById(R.id.prev_button3);
        btnNext3 = (Button) findViewById(R.id.next_button3);

        layouts = new int[]{
                R.layout.answer3_1,
                R.layout.answer3_2};

        // adding bottom dots
        //addBottomDots(0);

        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        if (curr == layouts.length - 1) {
            btnNext3.setVisibility(View.GONE);
            btnSkip3.setVisibility(View.VISIBLE);
        } else {
            if (curr == 0) {
                btnNext3.setVisibility(View.VISIBLE);
                btnSkip3.setVisibility(View.GONE);
            } else {
                btnNext3.setVisibility(View.VISIBLE);
                btnSkip3.setVisibility(View.VISIBLE);
            }
        }

    }

    public void btnSkipClick3(View v) {
        viewPager.arrowScroll(View.FOCUS_LEFT);
    }

    public void btnNextClick3(View v) {
        viewPager.arrowScroll(View.FOCUS_RIGHT);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            curr = position;
            if (position == layouts.length - 1) {
                btnNext3.setVisibility(View.GONE);
                btnSkip3.setVisibility(View.VISIBLE);
            } else {
                if (position == 0) {
                    btnNext3.setVisibility(View.VISIBLE);
                    btnSkip3.setVisibility(View.GONE);
                } else {
                    btnNext3.setVisibility(View.VISIBLE);
                    btnSkip3.setVisibility(View.VISIBLE);
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
