package subai.trak2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsTab extends Fragment {
    private static final String TAG = "@string/det_tag";
    private Bus bus;

    private TextView txtCompany, txtNumber, txtRoute, txtAcc, txtStatus;


    public void setBus(Bus bus){
        this.bus = bus;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.details_tab , container, false);

        txtCompany = (TextView) v.findViewById(R.id.bus_company_txt);
        txtNumber = (TextView) v.findViewById(R.id.bus_number_txt);
        txtRoute = (TextView) v.findViewById(R.id.route_txt);
        txtAcc = (TextView) v.findViewById(R.id.accommodation_txt);
        txtStatus = (TextView) v.findViewById(R.id.status_txt);

        txtStatus.setText(bus.status);

        return v;
    }
}
