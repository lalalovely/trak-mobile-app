package subai.trak2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class HelpActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Bus bus;

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

        toolbar = (Toolbar) findViewById(R.id.toolbar_d);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back); // your drawable
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //Intent intent = new Intent(this, MainActivity.class);
                //startActivity(intent);
                //finish();
            }
        });
    }



    public void setBus(Bus b){
        bus = b;
    }
}
