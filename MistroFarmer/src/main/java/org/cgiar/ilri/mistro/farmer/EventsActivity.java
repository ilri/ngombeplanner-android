package org.cgiar.ilri.mistro.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class EventsActivity extends SherlockActivity implements View.OnClickListener
{
    private String localeCode;

    private Button addEventB;
    private Button eventHistoryB;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        localeCode="en";

        addEventB=(Button)findViewById(R.id.add_event_b);
        addEventB.setOnClickListener(this);
        eventHistoryB=(Button)findViewById(R.id.event_history_b);
        eventHistoryB.setOnClickListener(this);

        initTextInViews(localeCode);
    }

    private void initTextInViews(String localeCode)
    {
        if(localeCode.equals("en"))
        {
            setTitle(R.string.events_en);
            addEventB.setText(R.string.add_an_event_en);
            eventHistoryB.setText(R.string.past_events_en);
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
    }
}
