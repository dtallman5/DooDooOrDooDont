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
 * This is used to display a custom InfoWindow to allow it to display the restroom info.
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

        TabHost host = v.findViewById(R.id.tab_host);
        host.setup();

        //Creates Male Tab
        TabHost.TabSpec spec = host.newTabSpec("Male");
        spec.setContent(R.id.tab1);
        RatingBar mAvg = v.findViewById(R.id.maleAvg);
        mAvg.setRating((float)rm.getmAvgRating());
        TextView mNum = v.findViewById(R.id.maleNum);
        mNum.setText(Integer.toString(rm.getmNumRatings()));
        TextView mName = v.findViewById(R.id.maleName);
        mName.setText(rm.getName());
        spec.setIndicator("Male");
        host.addTab(spec);


        //Creates Female Tab
        spec = host.newTabSpec("Female");
        spec.setContent(R.id.tab2);
        TextView fAvg = v.findViewById(R.id.femaleAvg);
        fAvg.setText(Double.toString(rm.getfAvgRating()));
        TextView fNum = v.findViewById(R.id.femaleNum);
        fNum.setText(Integer.toString(rm.getfNumRatings()));
        TextView fName = v.findViewById(R.id.femaleName);
        fName.setText(rm.getName());
        spec.setIndicator("Female");
        host.addTab(spec);


        if (Restroom.isMenDisplayed()){
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
