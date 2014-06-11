package org.cgiar.ilri.mistro.farmer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.Locale;
import org.cgiar.ilri.mistro.farmer.carrier.Cow;

public class FertilityActivity extends SherlockActivity implements MistroActivity, View.OnClickListener{

    private Menu menu;
    private Button servicingB;
    private Button calvingB;
    private Button backB;

    private Dialog servicingTypeDialog;
    private Button bullB;
    private Button aiB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fertility);

        servicingB = (Button) this.findViewById(R.id.servicing_b);
        servicingB.setOnClickListener(this);

        calvingB = (Button) this.findViewById(R.id.calving_b);
        calvingB.setOnClickListener(this);

        backB = (Button) this.findViewById(R.id.back_b);
        backB.setOnClickListener(this);

        servicingTypeDialog =new Dialog(this);
        servicingTypeDialog.setContentView(R.layout.dialog_servicing_type);
        bullB =(Button) servicingTypeDialog.findViewById(R.id.bull_b);
        bullB.setOnClickListener(this);

        aiB = (Button) servicingTypeDialog.findViewById(R.id.ai_b);
        aiB.setOnClickListener(this);
        initTextInViews();
    }

    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.milk_production, menu);
        this.menu = menu;
        initMenuText();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(Language.processLanguageMenuItemSelected(this, this, item)){
            return true;
        }
        else if(item.getItemId() == R.id.action_back_main_menu) {
            Intent intent = new Intent(this, MainMenu.class);
            intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_FARMER);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void initTextInViews(){
        this.setTitle(Locale.getStringInLocale("fertility",this));
        servicingB.setText(Locale.getStringInLocale("servicing",this));
        calvingB.setText(Locale.getStringInLocale("calving",this));
        servicingTypeDialog.setTitle(Locale.getStringInLocale("select_service_type",this));
        aiB.setText(Locale.getStringInLocale("artificial_inseminamtion",this));
        bullB.setText(Locale.getStringInLocale("bull_servicing",this));
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
    public void onClick(View view) {
        if(view == servicingB){
            servicingTypeDialog.show();
        }
        else if(view == calvingB){
            Intent intent = new Intent(this, AddEventActivity.class);
            intent.putExtra(AddEventActivity.KEY_MODE, AddEventActivity.MODE_CALVING);
            startActivity(intent);
        }
        else if(view == bullB){
            Intent intent = new Intent(this, AddEventActivity.class);
            intent.putExtra(AddEventActivity.KEY_MODE, AddEventActivity.MODE_SERVICING);
            intent.putExtra(AddEventActivity.KEY_SERVICING_TYPE, Cow.SERVICE_TYPE_BULL);
            servicingTypeDialog.dismiss();
            startActivity(intent);
        }
        else if(view == aiB){
            Intent intent = new Intent(this, AddEventActivity.class);
            intent.putExtra(AddEventActivity.KEY_MODE, AddEventActivity.MODE_SERVICING);
            intent.putExtra(AddEventActivity.KEY_SERVICING_TYPE, Cow.SERVICE_TYPE_AI);
            servicingTypeDialog.dismiss();
            startActivity(intent);
        }
        else if(view == backB){
            Intent intent = new Intent(this, MainMenu.class);
            intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_FARMER);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }
}
