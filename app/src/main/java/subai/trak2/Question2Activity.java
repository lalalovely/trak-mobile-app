package subai.trak2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

//this activity holds the contents for the answers in the second question that appears in the help page
public class Question2Activity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView help_bar_view2;
    private int[] layouts;
    private Button btnSkip2, btnNext2;
    private Toolbar q2_toolbar;
    private int curr;

    //when back button is clicked
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
        setContentView(R.layout.question2);

        q2_toolbar = (Toolbar) findViewById(R.id.toolbar_help2);
        setSupportActionBar(q2_toolbar);

        q2_toolbar.setNavigationIcon(R.drawable.ic_prev);
        q2_toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.q2_view_pager);
        help_bar_view2 = (TextView) findViewById(R.id.text_help2);
        btnSkip2 = (Button) findViewById(R.id.prev_button2);
        btnNext2 = (Button) findViewById(R.id.next_button2);

        layouts = new int[]{
                R.layout.answer2_1,
                R.layout.answer2_2,
                R.layout.answer2_3};

        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        if (curr == layouts.length - 1) {
            btnNext2.setVisibility(View.GONE);
            btnSkip2.setVisibility(View.VISIBLE);
        } else {
            if (curr == 0) {
                btnNext2.setVisibility(View.VISIBLE);
                btnSkip2.setVisibility(View.GONE);
            } else {
                btnNext2.setVisibility(View.VISIBLE);
                btnSkip2.setVisibility(View.VISIBLE);
            }
        }
    }

    //this function shows the previous page when the back button represented by back arrow is clicked
    public void btnSkipClick2(View v) {
        viewPager.arrowScroll(View.FOCUS_LEFT);
    }

    //this function shows the next page when the next button represented by next arrow is clicked
    public void btnNextClick2(View v) {
        viewPager.arrowScroll(View.FOCUS_RIGHT);
    }

    //listens to the pages that are being swiped by the user
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            curr = position;
            if (position == layouts.length - 1) {
                btnNext2.setVisibility(View.GONE);
                btnSkip2.setVisibility(View.VISIBLE);
            } else {
                if (position == 0) {
                    btnNext2.setVisibility(View.VISIBLE);
                    btnSkip2.setVisibility(View.GONE);
                } else {
                    btnNext2.setVisibility(View.VISIBLE);
                    btnSkip2.setVisibility(View.VISIBLE);
                }
            }
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

    //adapter of view pager
    //necessary when using viewpager
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
