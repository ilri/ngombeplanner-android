package org.cgiar.ilri.mistro.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;

public class MainMenu extends SherlockActivity implements View.OnClickListener
{
    private static final String TAG="MainMenu";
    private String localeCode;
    private Button milkProductionB;
    private Button eventsB;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        localeCode="en";

        milkProductionB=(Button)this.findViewById(R.id.milk_production_b);
        milkProductionB.setOnClickListener(this);
        eventsB =(Button)this.findViewById(R.id.events_b);
        eventsB.setOnClickListener(this);

        initTextInViews(localeCode);
    }

    private void initTextInViews(String localeCode)
    {
        if(localeCode.equals("en"))
        {
            this.setTitle(R.string.main_menu_en);
            milkProductionB.setText(R.string.milk_production_en);
            eventsB.setText(R.string.events_en);
        }
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
