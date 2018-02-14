package doodoo.doodooordoodont;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private Restroom mens,womens;

    public PagerAdapter(FragmentManager fm, Restroom mens, Restroom womens) {
        super(fm);
        this.mNumOfTabs = 2;
        this.mens=mens;
        this.womens=womens;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                RestroomTab tab1 = new RestroomTab();
                Bundle restBundle = new Bundle();
                restBundle.putString("UID", mens.getUID());
                restBundle.putString("name", mens.getName());
                tab1.setArguments(restBundle);
                return tab1;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}