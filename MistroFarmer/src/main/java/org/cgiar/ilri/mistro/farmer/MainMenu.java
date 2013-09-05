package org.cgiar.ilri.mistro.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.Locale;

public class MainMenu extends SherlockActivity implements View.OnClickListener
{
    private static final String TAG="MainMenu";
    private Button milkProductionB;
    private Button eventsB;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        milkProductionB=(Button)this.findViewById(R.id.milk_production_b);
        milkProductionB.setOnClickListener(this);
        eventsB =(Button)this.findViewById(R.id.events_b);
        eventsB.setOnClickListener(this);

        initTextInViews();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
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
        }
        return false;
    }

    private void initTextInViews()
    {
        /*if(localeCode.equals("en"))
        {
            this.setTitle(R.string.main_menu_en);
            milkProductionB.setText(R.string.milk_production_en);
            eventsB.setText(R.string.events_en);
        }*/
        this.setTitle(Locale.getStringInLocale("main_menu",this));
        milkProductionB.setText(Locale.getStringInLocale("milk_production",this));
        eventsB.setText(Locale.getStringInLocale("events",this));
    }

    @Override
    public void onClick(View view)
    {
        if(view==milkProductionB)
        {
            Intent intent=new Intent(this,MilkProductionActivity.class);
            startActivity(intent);
        }
        else if(view==eventsB)
        {
            Intent intent=new Intent(this,EventsActivity.class);
            startActivity(intent);
        }
    }
}
