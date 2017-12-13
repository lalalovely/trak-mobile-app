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

//this activity contains the questions that may guide the user in using the application
public class HelpActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton help_btn_1, help_btn_2, help_btn_3, help_btn_4;

    //when the back button is clicked
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

        help_btn_1 = (ImageButton) findViewById(R.id.help_1);
        help_btn_2 = (ImageButton) findViewById(R.id.help_2);
        help_btn_3 = (ImageButton) findViewById(R.id.help_3);
        help_btn_4 = (ImageButton) findViewById(R.id.help_4);

        toolbar = (Toolbar) findViewById(R.id.toolbar_help);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_prev);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //on click listener for the first question
        help_btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, Question1Activity.class);
                startActivity(intent);

            }
        });

        //on click listener for the second question
        help_btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, Question2Activity.class);
                startActivity(intent);
            }
        });

        //on click listener for the third question
        help_btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, Question3Activity.class);
                startActivity(intent);
            }
        });

        //on click listener for the fourth question
        help_btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, Question4Activity.class);
                startActivity(intent);
            }
        });
    }
}

