package doodoo.doodooordoodont;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

//Google Maps Imports

public class RestroomPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static GoogleMap mMap;
    private boolean menDisplayed;
    private Context context;
    private Location loc;
    Restroom restroom;

    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * onCreate:
     * <p>
     * This method is called every time the activity is created. It initializes all of the UI
     * and starts the Google Map.
     *
     * @param savedInstanceState Holds data from previously saved states if it is not the first
     *                           time the activity has been created. Null of first creation.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        Intent from = getIntent();
        restroom = from.getParcelableExtra("Restroom");

        //Sets the content view and initializes the toolbar
        setContentView(R.layout.drawer_restroom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        ImagePagerAdapter adapter = new ImagePagerAdapter(this);
        viewPager.setAdapter(adapter);

        RatingBar ratingBar = (RatingBar) findViewById(R.id.bathroomRating);
        ratingBar.setRating(restroom.getmAvgRating());

        TextView numRatings = (TextView) findViewById(R.id.numReviews);
        numRatings.setText(Integer.toString(restroom.getmNumRatings()) + " Reviews");

        //Initializes the drawer layout and its toggle
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Initializes the view at the top of the drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Initializes the google map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        TextView title = findViewById(R.id.restroomName);
        title.setText(restroom.getName());
    }

    /**
     * onBackPressed:
     * <p>
     * Called when the back button is pressed. If the drawer is open it closes it.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Checks whether the drawer is open and closes it if it is
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * onCreateOptionsMenu:
     * <p>
     * This method is called when the options menu is created.
     *
     * @param menu The menu that is being created
     * @return A boolean based on whether the menu was successfully created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * onNavigationItemSelected:
     * <p>
     * This method is called when an item in the navigation drawer is selected. It then initiates
     * the appropriate response based on which item was selected.
     *
     * @param item The item that was selected and initiated the method call.
     * @return A boolean based on whether the method executed successfully
     */
    @SuppressLint("MissingPermission")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Gets the id of the item selected
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final Intent nextScreen;

        //Checks which item was selected and handles the appropriate action
        if (id == R.id.nav_add_restroom) {
            drawer.closeDrawer(GravityCompat.START);
            nextScreen = new Intent(this,AddRestroom.class);
            mFusedLocationClient.getLastLocation()
                      .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null

                            if (location != null) {
                                nextScreen.putExtra("Lat",location.getLatitude() );
                                nextScreen.putExtra("Lon",location.getLongitude());
                                startActivity(nextScreen);
                            }
                        }
                    });


            return true;
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        //Closes the drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * onMapReady:
     * <p>
     * Called when the google map fragment is ready. Used to intialize markers and other factors
     * associated with the map fragment
     *
     * @param googleMap The map that is ready
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Restroom room = (Restroom) marker.getTag();
                if (room.isMenDisplayed()) {
                    room.setMenDisplayed(false);
                } else {
                    room.setMenDisplayed(true);
                }
                marker.setTag(room);
                mMap.setInfoWindowAdapter(new CustomInfoWindow(context));
                marker.showInfoWindow();
            }
        });


        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                Toast toast = Toast.makeText(context, "Go to Restroom Page", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        LatLng restroomPos = new LatLng(restroom.getLat(), restroom.getLon());
        mMap.addMarker(new MarkerOptions().position(restroomPos));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restroomPos, 17f));

    }

    private class ImagePagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public ImagePagerAdapter(Context context){
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        private int[] mImages = new int[] {
                R.drawable.circleroom,
                R.drawable.cityview,
                R.drawable.golden,
                R.drawable.makeuproom,
                R.drawable.silverroom
        };

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            /*Context context = RestroomPage.this;
            ImageView imageView = new ImageView(context);
            int padding = context.getResources().getDimensionPixelSize(
                    R.dimen.padding_small);
            imageView.setPadding(padding, padding, padding, padding);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(lp);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setAdjustViewBounds(true);
            imageView.setImageResource(mImages[position]);
            ((ViewPager) container).addView(imageView, 0);

            return imageView;*/

            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(mImages[position]);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //((ViewPager) container).removeView((ImageView) object);
            container.removeView((LinearLayout) object);
        }
    }
}