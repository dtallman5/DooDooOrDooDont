package doodoo.doodooordoodont;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * Created by David on 3/11/2018.
 */

public class AddRestroom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Sets the content view and initializes the toolbar
        setContentView(R.layout.content_add_restroom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.addtoolbar);
        toolbar.setTitle("Add Restroom");
        setSupportActionBar(toolbar);

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

}
