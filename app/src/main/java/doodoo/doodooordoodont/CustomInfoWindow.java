package doodoo.doodooordoodont;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by David on 2/13/2018.
 *
 * Website for live info window
 * https://stackoverflow.com/questions/14123243/google-maps-android-api-v2-interactive-infowindow-like-in-original-android-go/15040761#15040761
 */

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    Context context;
    LayoutInflater inflater;
    public CustomInfoWindow(Context context) {
        this.context = context;
    }
    @Override
    public View getInfoContents(Marker marker) {
        inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // R.layout.echo_info_window is a layout in my
        // res/layout folder. You can provide your own
        View v = inflater.inflate(R.layout.infowindow_layout, null);

        Restroom rm = (Restroom) marker.getTag();

        TabHost host = (TabHost) v.findViewById(R.id.tab_host);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("Male");
        spec.setContent(R.id.tab1);
        RatingBar mAvg = (RatingBar) v.findViewById(R.id.maleAvg);
        mAvg.setRating(rm.getmAvgRating());
        TextView mNum = (TextView) v.findViewById(R.id.maleNum);
        mNum.setText(Integer.toString(rm.getmNumRatings()));
        TextView mName = (TextView) v.findViewById(R.id.maleName);
        mName.setText(rm.getName());
        spec.setIndicator("Male");
        host.addTab(spec);

        spec = host.newTabSpec("Female");
        spec.setContent(R.id.tab2);
        TextView fAvg = (TextView) v.findViewById(R.id.femaleAvg);
        fAvg.setText(Float.toString(rm.getfAvgRating()));
        TextView fNum = (TextView) v.findViewById(R.id.femaleNum);
        fNum.setText(Integer.toString(rm.getfNumRatings()));
        TextView fName = (TextView) v.findViewById(R.id.femaleName);
        fName.setText(rm.getName());
        spec.setIndicator("Female");
        host.addTab(spec);


        if (rm.isMenDisplayed()){
            host.setCurrentTab(0);
        }
        else{
            host.setCurrentTab(1);
        }

        return v;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}
