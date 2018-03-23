package doodoo.doodooordoodont;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
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


/**
 * Created by David on 3/11/2018.
 */

public class AddRestroom extends AppCompatActivity implements TextWatcher{

    private Button send;
    private RadioGroup gender;
    private EditText name;
    private RatingBar ratings;
    double lon, lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent in = this.getIntent();

        lon = in.getDoubleExtra("Lon",0);
        lat = in.getDoubleExtra("Lat", 0);


        //Sets the content view and initializes the toolbar
        setContentView(R.layout.content_add_restroom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.addtoolbar);
        toolbar.setTitle("Add Restroom");
        setSupportActionBar(toolbar);

        ratings = (RatingBar) findViewById(R.id.ratingBar);

        send = (Button) findViewById(R.id.sendButton);
        send.setClickable(false);
        send.setTextColor(getResources().getColor(R.color.colorLightGray));
        send.setAlpha(0.5f);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRestroom();
            }
        });


        TextView coord = (TextView) findViewById(R.id.coordinateText);
        coord.setText("Coordinates:  " + Double.toString(lat) + ", " + Double.toString(lon));

        name = (EditText) findViewById(R.id.nameField);
        name.addTextChangedListener(this);

        gender = (RadioGroup) findViewById(R.id.genderGroup);
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                checkFields();
            }
        });

        Spinner stallSpinner = (Spinner) findViewById(R.id.stallSpinner);
        String[] stallitems = {"0","1","2","3","4","5","6","7+"};
        ArrayAdapter<String> stalladapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,stallitems);
        stallSpinner.setAdapter(stalladapter);

        Spinner dryerSpinner = (Spinner) findViewById(R.id.dryerSpinner);
        String[] dryitems = {"Paper Towels","Air Dryers", "Both", "Other"};
        ArrayAdapter<String> dryadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,dryitems);
        dryerSpinner.setAdapter(dryadapter);

        TextView showMore = (TextView) findViewById(R.id.showMore);
        final LinearLayout additionalInfo = (LinearLayout) findViewById(R.id.addtnInfo);
        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (additionalInfo.getVisibility() == View.GONE)
                    additionalInfo.setVisibility(View.VISIBLE);
                else if (additionalInfo.getVisibility() == View.VISIBLE)
                    additionalInfo.setVisibility(View.GONE);
            }
        });



        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

    }

    private void createRestroom() {
        Restroom toAdd = new Restroom("003", name.getText().toString());
        toAdd.setRatings(ratings.getRating(),1,ratings.getRating(),1);
        toAdd.setLocation(lat, lon);
        MainActivity.addMarker(toAdd);
        Intent backHome = new Intent(this,MainActivity.class);
        backHome.putExtra("Restroom", toAdd);
        startActivity(backHome);
    }


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
