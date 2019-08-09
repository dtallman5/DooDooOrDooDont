package doodoo.doodooordoodont;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class RestroomPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private Context context;
    private FirebaseFirestore db;
    private static final String TAG = "RestroomPage";
    String rUID;
    Restroom restroom;
    boolean downloadedReviews = false;
    private final int hideID = 56532;

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
        db = FirebaseFirestore.getInstance();

        //Saves the context for later usage
        context = this;

        //Gets the restroom that was associated with the marker whose info window was clicked.
        //by using the UID that was pass through the intent to get it from the database
        Intent from = getIntent();
        rUID = from.getStringExtra("Restroom");
        DocumentReference docRef = db.collection("restrooms").document(rUID);
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            //Creates the restroom object and then gets the additional info from the db
                            DocumentSnapshot document = task.getResult();
                            restroom = new Restroom(document.getId(), document.getData());
                            getOtherRestroomData();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        //Initializes the location client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * getOtherRestroomData:
     *
     * This method gets the additional information from the restroomData collection. It uses
     * the restroom objects UID in order to access the data.
     */
    private void getOtherRestroomData() {
        DocumentReference docRef = db.collection("restroomData").document(rUID);
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            //Adds the additional data to the object and updates the UI
                            DocumentSnapshot document = task.getResult();
                            restroom.setOtherData(document.getData());
                            updateUI();
                        }
                    }
                });
    }

    /**
     * updateUI:
     *
     * This method updates the UI of the restroom page and is called after the additional data
     * for the restroom is retrieved from the database.
     */
    private void updateUI() {
        //Sets the content view and initializes the toolbar
        setContentView(R.layout.drawer_restroom);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(restroom.getName());

        //Initializes the viewPager and sets the adapter to the custom class below and adds an
        //onPageChange listener so the indicator text can be updated.
        final ViewPager viewPager =  findViewById(R.id.pager);
        ImagePagerAdapter adapter = new ImagePagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //Updates the indicator when the image is scrolled.
            @Override
            public void onPageSelected(int position) {
                TextView indicator = findViewById(R.id.pagerIndicator);
                indicator.setText(((position+1)+ " of " + viewPager.getAdapter().getCount()));
            }

            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override public void onPageScrollStateChanged(int state) { }
        });

        //Indicator Textview that displays x of y on the photos
        TextView indicator = findViewById(R.id.pagerIndicator);
        String initText = "1 of " + viewPager.getAdapter().getCount();
        indicator.setText(initText);

        //Sets the rating for the Rating bar
        RatingBar ratingBar = findViewById(R.id.bathroomRating);
        ratingBar.setRating((float)restroom.getmAvgRating());

        // Sets the text for the number of ratings label
        TextView numRatings = findViewById(R.id.numReviews);
        String reviewsLabel = restroom.getmNumRatings() + " Reviews";
        numRatings.setText(reviewsLabel);

        //Initializes the drawer layout and its toggle
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Initializes the view at the top of the drawer
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Initializes the google map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Sets the title of the restroom
        TextView title = findViewById(R.id.restroomName);
        title.setText(restroom.getName());

        //Adds an onClickListener to the ratings button to display the ratings
        Button ratingBtn = findViewById(R.id.ratingsButton);
        ratingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleReviews();
            }
        });

        if (MainActivity.currUser != null){
            Button addRating = findViewById(R.id.addRating);
            addRating.setVisibility(View.VISIBLE);
            addRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addRating();
                }
            });
        }
    }

    /**
     * toggleReviews:
     *
     * This method is used to add a review to the restroom.
     */
    private void addRating() {
        final Intent nextScreen;
        nextScreen = new Intent(this, AddRating.class);
        nextScreen.putExtra("restroomUID", restroom.getUID());
        startActivity(nextScreen);
    }

    /**
     * toggleReviews:
     *
     * This method is used to display or hide the ratings for the restroom.
     */
    private void toggleReviews() {
        final LinearLayout reviews = findViewById(R.id.reviews);
        if (restroom.getmNumRatings() == 0){
            Toast alert = Toast.makeText(this,"No Reviews to Display",Toast.LENGTH_LONG);
            alert.show();
            return;
        }

        //Checks if the reviews have been downloaded from the database, and if not downloads them
        if (!downloadedReviews) {
            db.collection("reviews").document(restroom.getUID()).collection("reviews")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                //Creates each review and adds them to the layout
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Rating rev = new Rating(document.getData());
                                    createReviewLayout(rev, reviews);
                                }

                                //Creates the hide button and adds the onClickListener
                                Button hidebtn = new Button(RestroomPage.this);
                                hidebtn.setText("Hide Reviews");
                                hidebtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        toggleReviews();
                                    }
                                });
                                hidebtn.setId(hideID);

                                //Changes the visibility of the rating button and the review layout
                                Button btn = findViewById(R.id.ratingsButton);
                                btn.setVisibility(View.GONE);
                                reviews.addView(hidebtn);
                                reviews.setVisibility(View.VISIBLE);
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
            downloadedReviews=true;
        }
        //If the reviews have already been downloaded it changes the visibility of the layout
        else{
            Button btn = findViewById(R.id.ratingsButton);
            Button hidebtn = findViewById(hideID);
            if (btn.getVisibility()!=View.GONE) {
                btn.setVisibility(View.GONE);
                reviews.setVisibility(View.VISIBLE);
                hidebtn.setVisibility(View.VISIBLE);
            }
            else {
                btn.setVisibility(View.VISIBLE);
                reviews.setVisibility(View.GONE);
                hidebtn.setVisibility(View.GONE);
            }
        }
    }

    /**
     * createReviewLayout:
     *
     * This method creates the layout for the review and adds it to the layout that was passed in.
     *
     * @param rev   The review to be displayed
     * @param reviews   The layout to add the review to
     */
    private void createReviewLayout(Rating rev, LinearLayout reviews) {
        //Creates a text view for the username
        TextView user = new TextView(RestroomPage.this);
        user.setText(rev.getReviewer()+ ":");
        reviews.addView(user);

        //Creates the ratingBar and sets its value
        RatingBar rating = new RatingBar(RestroomPage.this);
        ViewGroup.LayoutParams params = reviews.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        rating.setLayoutParams(params);
        rating.setMax(5);
        rating.setStepSize(0.1f);
        rating.setRating((float) rev.getRating());
        rating.setIsIndicator(true);
        reviews.addView(rating);

        //Creates a textview for the review text.
        TextView review = new TextView(RestroomPage.this);
        review.setText(rev.getReview());
        reviews.addView(review);
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
        } else if (id == R.id.nav_login) {
            drawer.closeDrawer(GravityCompat.START);
            nextScreen = new Intent(this,LoginActivity.class);
            startActivity(nextScreen);
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Disables the map toolbar
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        // Adds my location but removes the my location button. Effectively together displays the
        //blue dot indicating the user's location
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        //Changes the onMapClick behavior so it no longer opens straight to google maps app
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast toast = Toast.makeText(context,"Open Just Map Page!",Toast.LENGTH_LONG);
                toast.show();
            }
        });

        //Adds the marker for the restroom
        LatLng restroomPos = new LatLng(restroom.getLat(), restroom.getLon());
        googleMap.addMarker(new MarkerOptions().position(restroomPos));

        //Moves the camera to the restroom's position and zooms in
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restroomPos, 17f));


    }

    /**
     * This is the custom PagerAdapter that is the brains behind the viewPager.
     */
    private class ImagePagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        ImagePagerAdapter(Context context){
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        //TODO: Remove hardcoded images and get images from database
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
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            ImageView imageView = itemView.findViewById(R.id.imageView);
            imageView.setImageResource(mImages[position]);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}