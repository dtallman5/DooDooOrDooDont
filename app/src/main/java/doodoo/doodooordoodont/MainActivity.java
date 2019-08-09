package doodoo.doodooordoodont;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//Google Maps Imports
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static GoogleMap mMap;
    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    private FirebaseAuth mAuth;
    protected static User currUser;


    private FirebaseFirestore db;
    private static final int REQUEST_LOCATION_PERMISSION = 99;
    private static final String TAG = "MainActivity";


    /**
     * onCreate:
     * <p>
     * This method is called every time the activity is created. It initializes all of the UI
     * and starts the Google Map.
     *
     * @param savedInstanceState Holds data from previously saved states if it is not the first
     *                           time the activity has been created. Null on first creation.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this; //Saves the context so that it can be used in internal classes.

        //Sets the content view and initializes the toolbar
        setContentView(R.layout.drawer_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializes the drawer layout and its toggle
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Initializes the view at the top of the drawer
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Initializes the location client and the Firebase variables
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * onBackPressed:
     * <p>
     * Called when the back button is pressed. If the drawer is open it closes it.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //Checks whether the drawer is open and closes it if it is
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /**
     * onStart:
     *
     * This method is called every time the activity is started. This means that it is
     * not just called when the activity is created, but also when the app switches to
     * this activity through an intent. I use it here to update the navigation header to
     * display the current logged in users info.
     */
    @Override
    public void onStart() {
        super.onStart();

        //Grabs the navigation header and the current user
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currUser = null; // Resets the currUser object

        //If there is no user at all, then it signs them in as an anonymous user
        if (user == null) {
            //Signs them in and once complete, updates the map by calling getMapAsync
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");
                                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                        .findFragmentById(R.id.map);
                                mapFragment.getMapAsync(MainActivity.this);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            //Sets the menu to the non-logged in version and sets info to guest
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);
            ((TextView) headerView.findViewById(R.id.username)).setText("Guest");
            ((TextView) headerView.findViewById(R.id.userEmail)).setText("Please login");
            return;
        }
        //If the user is already anonymously logged in, set info to guest
        else if (user.isAnonymous()) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);
            ((TextView) headerView.findViewById(R.id.username)).setText("Guest");
            ((TextView) headerView.findViewById(R.id.userEmail)).setText("Please login");
        }
        //If the user is not anonymous, set the info to their info
        else {
             //Collects user info from the database
            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    currUser = new User(task.getResult().getData());
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);

                    navigationView.getMenu().clear();
                    navigationView.inflateMenu(R.menu.activity_main_drawer_loggedin);
                    ((TextView) headerView.findViewById(R.id.username)).setText(currUser.getName());
                    ((TextView) headerView.findViewById(R.id.userEmail)).setText(currUser.getEmail());
                }
            });
        }

        //Updates the map after changing the navigation header
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
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

    /**
     * onOptionsItemSelected
     *
     * This method is used to handle the action bar item clicks, namely the ... button. I have left
     * this in for now so that adding this feature will be very easy right now. Currently does
     * nothing.
     *
     * @param item The item that was selected
     * @return Returns the result of the superclasses method call
     */
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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Gets the id of the item selected
        int id = item.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final Intent nextScreen;

        //Checks which item was selected and handles the appropriate action
        if (id == R.id.nav_add_restroom) {
            if (FirebaseAuth.getInstance().getCurrentUser().isAnonymous())
            {
                Toast.makeText(this, "Must be logged in to Add Restroom", Toast.LENGTH_LONG).show();
                return true;
            }
            drawer.closeDrawer(GravityCompat.START);
            nextScreen = new Intent(this, AddRestroom.class);
            //Gets the user's location and sends that to the AddRestroom class
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null
                            if (location != null) {
                                nextScreen.putExtra("Lat", location.getLatitude());
                                nextScreen.putExtra("Lon", location.getLongitude());
                                startActivity(nextScreen);
                            }
                        }
                    });
            return true;
        }
        //If login is selected send the user to the login page
        else if (id == R.id.nav_login) {
            drawer.closeDrawer(GravityCompat.START);
            nextScreen = new Intent(this, LoginActivity.class);
            startActivity(nextScreen);
        }
        //If my_account is selected, send them to the my account page
        else if (id == R.id.nav_my_account) {
            drawer.closeDrawer(GravityCompat.START);
            nextScreen = new Intent(this, MyAccount.class);
            startActivity(nextScreen);
        }

        //Closes the drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * onMapReady:
     * <p>
     * Called when the google map fragment is ready. Used to initialize markers and other factors
     * associated with the map fragment. Called after getMapAsync() is called.
     *
     * @param googleMap The map that is ready
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation(); //Enables the users location

        //Sets the info window to the custom info window
        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));

        //Adds an onclicklistener that switches the displayed gender
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Restroom room = (Restroom) marker.getTag();
                if (Restroom.isMenDisplayed()) {
                    Restroom.setMenDisplayed(false);
                } else {
                    Restroom.setMenDisplayed(true);
                }
                marker.setTag(room);
                mMap.setInfoWindowAdapter(new CustomInfoWindow(context));
                marker.showInfoWindow();
            }
        });

        //OnLongClickListener that opens the Restrooms page
        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                Restroom rm = (Restroom) marker.getTag();
                Intent restroomPage = new Intent(context, RestroomPage.class);
                restroomPage.putExtra("Restroom", rm.getUID());
                startActivity(restroomPage);
            }
        });

        //Adds the restrooms from the database
        db.collection("restrooms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //Loops through all documents and adds a marker to the map
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Restroom toAdd = new Restroom(document.getId(), document.getData());
                                LatLng toAddPos = new LatLng(toAdd.getLat(), toAdd.getLon());
                                Marker m = mMap.addMarker(new MarkerOptions().position(toAddPos));
                                m.setTag(toAdd);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    /**
     * enableMyLocation
     *
     * This method is used in order to enable the users location on the main map.
     * It checks to make sure that the app has the correct permissions and if not it requests
     * those permissions.
     */
    private void enableMyLocation() {
        //Checks to see if the app has location permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Enables myLocation on the map
            mMap.setMyLocationEnabled(true);

            //Gets the users logation and moves the camera to that location
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(new Criteria(), false));
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude)));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
        //If permission is not granted, ask for it.
        else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    /**
     * onRequestPermissionsResult
     *
     * This overridden method checks to see if the permissions request went through and if it did
     * then it calls enableMyLocation again so that it can enable myLocation on the map.
     *
     * @param requestCode The code of the request that was made
     * @param permissions Which permissions were requested
     * @param grantResults The results of the permission requests
     */
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

}