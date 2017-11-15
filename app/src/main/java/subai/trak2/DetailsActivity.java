package subai.trak2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class DetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar_d);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back); // your drawable
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
                startActivity(intent);
//                //finish();
                //onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setBus(Bus b){
        bus = b;
    }
}
