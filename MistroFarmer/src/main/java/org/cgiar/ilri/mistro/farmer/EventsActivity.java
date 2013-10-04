package org.cgiar.ilri.mistro.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.Locale;

public class EventsActivity extends SherlockActivity implements View.OnClickListener
{

    private Button addEventB;
    private Button eventHistoryB;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        addEventB=(Button)findViewById(R.id.add_event_b);
        addEventB.setOnClickListener(this);
        eventHistoryB=(Button)findViewById(R.id.event_history_b);
        eventHistoryB.setOnClickListener(this);

        initTextInViews();
    }

    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.events, menu);
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

    private void initTextInViews()
    {
        setTitle(Locale.getStringInLocale("events",this));
        addEventB.setText(Locale.getStringInLocale("add_an_event",this));
        eventHistoryB.setText(Locale.getStringInLocale("past_events",this));
    }

    @Override
    public void onClick(View view)
    {
        if(view==addEventB)
        {
            Intent intent=new Intent(this,AddEventActivity.class);
            startActivity(intent);
        }
        else if(view == eventHistoryB)
        {
            Intent intent=new Intent(this,EventsHistoryActivity.class);
            startActivity(intent);
        }
    }
}
