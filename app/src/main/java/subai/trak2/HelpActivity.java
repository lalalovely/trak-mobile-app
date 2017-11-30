package subai.trak2;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton help_btn_1, help_btn_2, help_btn_3, help_btn_4;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        toolbar = (Toolbar) findViewById(R.id.toolbar_help);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_prev);

        help_btn_1 = (ImageButton) findViewById(R.id.help_1);
        help_btn_2 = (ImageButton) findViewById(R.id.help_2);
        help_btn_3 = (ImageButton) findViewById(R.id.help_3);
        help_btn_4 = (ImageButton) findViewById(R.id.help_4);

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        help_btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, Question1Activity.class);
                startActivity(intent);
            }
        });

        help_btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, Question2Activity.class);
                startActivity(intent);
            }
        });

    }

    public void clickQuestion1() {
        Intent intent = new Intent(this, Question1Activity.class);
        startActivity(intent);
    }

}

