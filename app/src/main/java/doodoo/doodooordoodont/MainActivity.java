package doodoo.doodooordoodont;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

//Google Maps Imports
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static GoogleMap mMap;
    private boolean menDisplayed;
    private Context context;
    private Location loc;
    private FusedLocationProviderClient mFusedLocationClient;
    Restroom toAdd;

    public static final int REQUEST_LOCATION_PERMISSION = 99;

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

        Intent here = getIntent();
        if (here.getParcelableExtra("Restroom") != null){
            toAdd = here.getParcelableExtra("Restroom");
        }

        context = this;

        //Sets the content view and initializes the toolbar
        setContentView(R.layout.drawer_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        enableMyLocation();

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
                Restroom rm = (Restroom) marker.getTag();
                Intent restroomPage = new Intent(context, RestroomPage.class);
                restroomPage.putExtra("Restroom", rm);
                startActivity(restroomPage);
            }
        });


        LatLng sydney = new LatLng(-34, 151);
        Restroom rm = new Restroom("001", "Test Bathroom");
        rm.setRatings(4.5f, 100, 2.1f, 5);
        Marker m = mMap.addMarker(new MarkerOptions().position(sydney));
        rm.setLocation(sydney.latitude,sydney.longitude);
        m.setTag(rm);

        LatLng homePos = new LatLng(32.878695, -117.212936);
        Marker m2 = mMap.addMarker(new MarkerOptions().position(homePos));
        Restroom home = new Restroom("002","Apartment Bathroom");
        home.setRatings(2.5f,4,2.5f,4);
        home.setLocation(homePos.latitude, homePos.longitude);
        m2.setTag(home);

        if (toAdd != null){
            LatLng toAddPos = new LatLng(toAdd.getLat(), toAdd.getLon());
            Marker m3 = mMap.addMarker(new MarkerOptions().position(toAddPos));
            m3.setTag(toAdd);
        }

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
        }
    }

    public static void addMarker(Restroom toAdd) {
        Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(toAdd.getLat(),toAdd.getLon())));
        m.setTag(toAdd);
    }
}