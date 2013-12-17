package org.cgiar.ilri.mistro.farmer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.Locale;
import org.cgiar.ilri.mistro.farmer.carrier.Cow;

public class FertilityActivity extends SherlockActivity implements View.OnClickListener{

    private Button servicingB;
    private Button calvingB;

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
        this.setTitle(Locale.getStringInLocale("fertility",this));
        servicingB.setText(Locale.getStringInLocale("servicing",this));
        calvingB.setText(Locale.getStringInLocale("calving",this));
        servicingTypeDialog.setTitle(Locale.getStringInLocale("select_service_type",this));
        aiB.setText(Locale.getStringInLocale("artificial_inseminamtion",this));
        bullB.setText(Locale.getStringInLocale("bull_servicing",this));
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
    }
}
