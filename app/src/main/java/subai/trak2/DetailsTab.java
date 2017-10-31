package subai.trak2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DetailsTab extends Fragment {
    private static final String TAG = "@string/det_tag";
    private Bus bus;

    public void setBus(Bus bus){
        this.bus = bus;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.details_tab , container, false);

        return v;
    }
}
