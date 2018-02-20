package doodoo.doodooordoodont;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
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


        TabHost host = (TabHost) v.findViewById(R.id.tab_host);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("Male");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Male");
        host.addTab(spec);

        spec = host.newTabSpec("Female");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Female");
        host.addTab(spec);

        Restroom rm = (Restroom) marker.getTag();

        return v;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}
