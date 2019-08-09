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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


/*
 * Created by David on 3/11/2018.
 */

/**
 * This class is used to create the Add Restroom activity that can be accessed by the drawer menu.
 * It contains the logic in order to check the fields on the page and then create a Restroom
 * object from the information provided. Should eventually add the restroom to the database
 * rather than putting it in an intent and sending back to main activity.
 */
public class AddRestroom extends AppCompatActivity implements TextWatcher{

    private Button send;       //The button in the toolbar
    private RadioGroup gender; //The Radio Group for bathroom gender
    private EditText name;     //The edit text field for the bathroom name
    private RatingBar ratings; //The rating bar
    double lon, lat;           //Variables to hold the user's location when add restroom initiated
    private FirebaseFirestore db;
    private static final String TAG = "AddRestroom";


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

        //Uses the activity's intent in order to get the user's location
        db = FirebaseFirestore.getInstance();
        Intent in = this.getIntent();
        lon = in.getDoubleExtra("Lon",0);
        lat = in.getDoubleExtra("Lat", 0);


        //Sets the content view and initializes the toolbar
        setContentView(R.layout.content_add_restroom);
        Toolbar toolbar = findViewById(R.id.addtoolbar);
        toolbar.setTitle("Add Restroom");
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
                createRestroom();
            }
        });

        //Temporary!!! Used in order to make sure coordinates work correctly
        TextView coord = findViewById(R.id.coordinateText);
        String coordinateText = "Coordinates:  " + lat + ", " + lon;
        coord.setText(coordinateText);

        //Adds a text Changed listener to the name field to check for when it is filled in
        name = findViewById(R.id.review);
        name.addTextChangedListener(this);

        //Adds a checkedChangeListener to see when a radio button is selected
        gender = findViewById(R.id.genderGroup);
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                checkFields();
            }
        });

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
    private void createRestroom() {
        //TODO need to make the restroom other data and restroom ratings be stored differently
        final Restroom toAdd = new Restroom();
        toAdd.setName( name.getText().toString());
        toAdd.setRatings(ratings.getRating(),1,ratings.getRating(),1);
        toAdd.setLocation(lat, lon);
        String numStalls = (String)((Spinner)findViewById(R.id.stallSpinner)).getSelectedItem();
        toAdd.setfNumStalls(numStalls);
        db.collection("restrooms")
                .add(toAdd.toMap())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        toAdd.setUID(documentReference.getId());
                        db.collection("restroomData").document(toAdd.getUID()).set(toAdd.extractData());
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
     * an update from the name field or the radio buttons and it alters the appearance and the
     * clickability of the send button
     */
    public void checkFields(){
        if (!name.getText().toString().equals("") && gender.getCheckedRadioButtonId() != -1){
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
