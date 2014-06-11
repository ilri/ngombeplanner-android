package org.cgiar.ilri.mistro.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.Locale;


public class FarmerSelection extends SherlockActivity implements MistroActivity, View.OnClickListener {

    private Menu menu;

    private TextView selectFarmerTV;
    private Spinner selectFarmerS;
    private Button selectB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_selection);

        selectFarmerTV = (TextView)findViewById(R.id.select_farmer_tv);
        selectFarmerS = (Spinner)findViewById(R.id.select_farmer_s);

        selectB = (Button)findViewById(R.id.select_b);
        selectB.setOnClickListener(this);

        initTextInViews();
    }


    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.farmer_selection, menu);
        this.menu = menu;
        initMenuText();
        return true;
    }

    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        // Handle item selection
        if(Language.processLanguageMenuItemSelected(this, this, item)){
            return true;
        }
        else if(item.getItemId() == R.id.action_back_main_menu) {
            Intent intent = new Intent(this, MainMenu.class);
            intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_ADMIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void initTextInViews() {
        setTitle(Locale.getStringInLocale("select_farmer", this));

        selectFarmerTV.setText(Locale.getStringInLocale("select_farmer", this));
        selectB.setText(Locale.getStringInLocale("select", this));
    }

    private void initMenuText(){
        if(this.menu != null){
            MenuItem mainMenuMI = menu.findItem(R.id.action_back_main_menu);
            mainMenuMI.setTitle(Locale.getStringInLocale("back_to_main_menu", this));
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(selectB)){
            Intent intent = new Intent(this, EditFarmer.class);
            startActivity(intent);
        }
    }
}
