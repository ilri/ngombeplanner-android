package org.cgiar.ilri.mistro.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import com.actionbarsherlock.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.Locale;

public class EventsActivity extends SherlockActivity implements View.OnClickListener
{

    private Menu menu;
    private Button addEventB;
    private Button eventHistoryB;
    private Button backB;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        addEventB=(Button)findViewById(R.id.add_event_b);
        addEventB.setOnClickListener(this);
        eventHistoryB=(Button)findViewById(R.id.event_history_b);
        eventHistoryB.setOnClickListener(this);
        backB = (Button)findViewById(R.id.back_b);
        backB.setOnClickListener(this);

        initTextInViews();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.events, menu);
        this.menu = menu;
        initMenuText();
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
        else if(item.getItemId() == R.id.action_back_main_menu) {
            Intent intent = new Intent(this, MainMenu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        return false;
    }

    private void initTextInViews()
    {
        setTitle(Locale.getStringInLocale("events",this));
        addEventB.setText(Locale.getStringInLocale("add_an_event",this));
        eventHistoryB.setText(Locale.getStringInLocale("past_events",this));
        backB.setText(Locale.getStringInLocale("back", this));
        initMenuText();
    }

    private void initMenuText(){
        if(this.menu != null){
            MenuItem mainMenuMI = menu.findItem(R.id.action_back_main_menu);
            mainMenuMI.setTitle(Locale.getStringInLocale("back_to_main_menu", this));
        }
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
        else if(view == backB){
            Intent intent = new Intent(this, MainMenu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }
}
