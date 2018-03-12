package doodoo.doodooordoodont;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


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
        String[] items = {"0","1","2","3","4","5","6","7+"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,items);
        stallSpinner.setAdapter(adapter);


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
