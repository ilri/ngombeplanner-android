package org.cgiar.ilri.mistro.farmer;

import android.os.Bundle;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;

public class MainMenu extends SherlockActivity
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
        eventsB =(Button)this.findViewById(R.id.events_b);

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
}
