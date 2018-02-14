package doodoo.doodooordoodont;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by David on 2/13/2018.
 * http://www.truiton.com/2015/06/android-tabs-example-fragments-viewpager/
 */

public class RestroomTab extends Fragment {

    private Restroom restroom;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.tab_layout);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        TextView name = (TextView) getView().findViewById(R.id.name);
        name.setText(bundle.getString("name"));
        TextView UID = (TextView) getView().findViewById(R.id.UID);
        UID.setText(bundle.getString("UID"));
        return inflater.inflate(R.layout.restroom_tab, container, false);
    }
}
