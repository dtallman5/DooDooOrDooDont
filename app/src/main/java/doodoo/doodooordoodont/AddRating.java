package doodoo.doodooordoodont;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;


/*
 * Created by David on 3/11/2018.
 */

/**
 * This class is used to create the Add Restroom activity that can be accessed by the drawer menu.
 * It contains the logic in order to check the fields on the page and then create a Restroom
 * object from the information provided. Should eventually add the restroom to the database
 * rather than putting it in an intent and sending back to main activity.
 */
public class AddRating extends AppCompatActivity implements TextWatcher{

    private Button send;       //The button in the toolbar
    private RadioGroup gender; //The Radio Group for bathroom gender
    private EditText review;     //The edit text field for the bathroom review
    private RatingBar ratings; //The rating bar
    double lon, lat;           //Variables to hold the user's location when add restroom initiated
    private FirebaseFirestore db;
    private User user;
    private static final String TAG = "AddRating";
    private String rUID;
    private Rating toAdd;
    Restroom toRate;


    /**
     * onCreate
     *
     * This method is called every time the activity is created. It initializes all of the UI.
     *
     * @param savedInstanceState Holds data from previously saved states if it is not the first
     *                           time the activity has been created. Null on first creation.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Uses the activity's intent in order to get the restrooms UID
        Intent from = getIntent();
        rUID = from.getStringExtra("restroomUID");

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("restrooms").document(rUID);
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            //Creates the restroom object and then gets the additional info from the db
                            DocumentSnapshot document = task.getResult();
                            toRate = new Restroom(document.getId(), document.getData());
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        user = MainActivity.currUser;

        //Sets the content view and initializes the toolbar
        setContentView(R.layout.content_add_rating);
        Toolbar toolbar = findViewById(R.id.addtoolbar);
        toolbar.setTitle("Add Rating");
        setSupportActionBar(toolbar);

        //Saves the rating bar to a variable
        ratings = findViewById(R.id.ratingBar);

        //"Turns off" the send button and adds new onClickListener
        send = findViewById(R.id.sendButton);
        send.setClickable(false);
        send.setTextColor(getResources().getColor(R.color.colorLightGray));
        send.setAlpha(0.5f);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRating();
            }
        });


        //Adds a text Changed listener to the review field to check for when it is filled in
        review = findViewById(R.id.review);
        review.addTextChangedListener(this);

        //Initializes the spinner for the number of stalls
        Spinner stallSpinner = findViewById(R.id.stallSpinner);
        String[] stallitems = {"0","1","2","3","4","5","6","7+"};
        ArrayAdapter<String> stalladapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,stallitems);
        stallSpinner.setAdapter(stalladapter);

        //Initializes the spinner for which kind of hand drying mechanism the restroom has
        Spinner dryerSpinner = findViewById(R.id.dryerSpinner);
        String[] dryitems = {"Paper Towels","Air Dryers", "Both", "Other"};
        ArrayAdapter<String> dryadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,dryitems);
        dryerSpinner.setAdapter(dryadapter);

        //Adds an onClickListener to the showMore textView that will make the additionalInfo section
        //appear or disappear depending on its state
        TextView showMore = findViewById(R.id.showMore);
        final LinearLayout additionalInfo = findViewById(R.id.addtnInfo);
        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (additionalInfo.getVisibility() == View.GONE)
                    additionalInfo.setVisibility(View.VISIBLE);
                else if (additionalInfo.getVisibility() == View.VISIBLE)
                    additionalInfo.setVisibility(View.GONE);
            }
        });



        //This adds the X button in the upper left that takes user back
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

    }

    /**
     * createRestroom
     *
     * This method is called by the send button's onClickListener and creates the new Restroom.
     *
     */
    private void createRating() {
        //TODO need to make the restroom other data and restroom ratings be stored differently
        toAdd = new Rating(new Date(), user.getUserId(),ratings.getRating(), review.getText().toString());
        db.collection("reviews").document(rUID).collection("reviews")
                .add(toAdd.toMap())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        toAdd.setUID(documentReference.getId());
                        addRatingtoRestroom();
                        addRatingtoUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        Intent backHome = new Intent(this,MainActivity.class);
        startActivity(backHome);
    }

    private void addRatingtoRestroom() {
        int numRatings;
        double avgRating;
        if (user.getGender().equals("Male")) {
            numRatings = toRate.getmNumRatings();
            avgRating = toRate.getmAvgRating();
            avgRating = ((avgRating*numRatings) + toAdd.getRating())/(numRatings+1);
            numRatings++;
            db.collection("restrooms").document(rUID).update("mAvgRating",avgRating);
            db.collection("restrooms").document(rUID).update("mNumRatings",numRatings);
        }
        else if (user.getGender().equals("Female")){
            numRatings = toRate.getfNumRatings();
            avgRating = toRate.getfAvgRating();
            avgRating = ((avgRating*numRatings) + toAdd.getRating())/(numRatings+1);
            numRatings++;
            db.collection("restrooms").document(rUID).update("fAvgRating",avgRating);
            db.collection("restrooms").document(rUID).update("fNumRatings",numRatings);
        }
    }

    private void addRatingtoUser() {
        user.setNumReviews(user.getNumReviews() + 1);
        String fullRatingID = toRate.getUID()+"_"+toAdd.getUID();
        db.collection("users").document(user.getUserId()).collection("reviews")
                .document(fullRatingID).set(toAdd.toUserMap()).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

            }
        });
        db.collection("users").document(user.getUserId()).update("numReviews",user.getNumReviews());
    }

    /**
     * onOptionsItemSelected
     *
     * This method was overridden because I like the behavior of onBackPressed better than the
     * default behavior of pressing the Home/Up button as described in the comments of the method.
     *
     * @param item The menu item that was pressed
     * @return returns the result of the superclasses method
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home){
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * checkFields
     *
     * This method checks to make sure that the required fields are filled in. It is called during
     * an update from the review field or the radio buttons and it alters the appearance and the
     * clickability of the send button
     */
    public void checkFields(){
        if (!review.getText().toString().equals("")){
            send.setTextColor(getResources().getColor(R.color.colorWhite));
            send.setAlpha(1f);
            send.setClickable(true);
        }
        else{
            send.setTextColor(getResources().getColor(R.color.colorLightGray));
            send.setAlpha(0.5f);
            send.setClickable(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void afterTextChanged(Editable editable) {
        checkFields();
    }
}
