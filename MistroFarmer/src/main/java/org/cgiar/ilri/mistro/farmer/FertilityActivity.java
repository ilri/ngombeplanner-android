package org.cgiar.ilri.mistro.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.Locale;

public class FertilityActivity extends SherlockActivity implements View.OnClickListener{

    private Button servicingB;
    private Button calvingB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fertility);

        servicingB = (Button) this.findViewById(R.id.servicing_b);
        servicingB.setOnClickListener(this);

        calvingB = (Button) this.findViewById(R.id.calving_b);
        calvingB.setOnClickListener(this);

        initTextInViews();
    }

    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.milk_production, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(item.getItemId() == R.id.action_english) {
            Locale.switchLocale(Locale.LOCALE_ENGLISH, this);
            initTextInViews();
            return true;
        }
        else if(item.getItemId() == R.id.action_swahili) {
            Locale.switchLocale(Locale.LOCALE_SWAHILI, this);
            initTextInViews();
            Toast.makeText(this, "kazi katika maendeleo", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private void initTextInViews(){
        servicingB.setText(Locale.getStringInLocale("servicing",this));
        calvingB.setText(Locale.getStringInLocale("calving",this));
    }

    @Override
    public void onClick(View view) {
        if(view == servicingB){
            Intent intent = new Intent(this, AddEventActivity.class);
            startActivity(intent);
        }
        else if(view == calvingB){
            Intent intent = new Intent(this, AddEventActivity.class);
            startActivity(intent);
        }
    }
}
