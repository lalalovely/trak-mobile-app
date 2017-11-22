package subai.trak2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Bus bus;

    public TextView txtCompany, txtNumber, txtRoute, txtAcc, txtStatus;

    public DetailsActivity(){
        bus = new Bus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        txtCompany = (TextView) findViewById(R.id.bus_company_txt);
        txtNumber = (TextView) findViewById(R.id.bus_number_txt);
        txtRoute = (TextView) findViewById(R.id.route_txt);
        txtAcc = (TextView) findViewById(R.id.accommodation_txt);
        txtStatus = (TextView) findViewById(R.id.status_txt);

        toolbar = (Toolbar) findViewById(R.id.toolbar_d);

        //txtStatus.setText(stat);
        txtCompany.setText(bus.getBusCompany());
        txtRoute.setText(bus.getRoute());
        txtAcc.setText(bus.getAccommodation());
        txtNumber.setText(LoginActivity.getBusNumber());

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

    public void setBusDetails(Bus b){
        this.bus = b;
    }
}
