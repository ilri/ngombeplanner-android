package org.cgiar.ilri.mistro.farmer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.DataHandler;
import org.cgiar.ilri.mistro.farmer.backend.Locale;

import java.util.List;


public class EditFarmer extends SherlockActivity implements MistroActivity, View.OnClickListener {

    private Menu menu;

    private TextView fullNameTV;
    private EditText fullNameET;
    private TextView preferredLanguageTV;
    private Spinner preferredLanguageS;
    private TextView extensionPersonnelTV;
    private Spinner extensionPersonnelS;
    private TextView mobileNumberTV;
    private EditText mobileNumberET;
    private TextView numberOfCowsTV;
    private EditText numberOfCowsET;
    private Button editB;

    private List<String> languages;
    private boolean cacheData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_farmer);

        languages = Locale.getAllLanguages(this);
        cacheData = true;

        fullNameTV=(TextView)this.findViewById(R.id.full_name_tv);
        fullNameET=(EditText)this.findViewById(R.id.full_name_et);
        preferredLanguageTV = (TextView)this.findViewById(R.id.preferred_language_tv);
        preferredLanguageS = (Spinner)this.findViewById(R.id.preferred_language_s);
        extensionPersonnelTV=(TextView)this.findViewById(R.id.extension_personnel_tv);
        extensionPersonnelS=(Spinner)this.findViewById(R.id.extension_personnel_s);
        mobileNumberTV=(TextView)this.findViewById(R.id.mobile_number_tv);
        mobileNumberET=(EditText)this.findViewById(R.id.mobile_number_et);
        TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        //Toast.makeText(this,telephonyManager.getSimSerialNumber(),Toast.LENGTH_LONG).show();
        mobileNumberET.setText(telephonyManager.getLine1Number());
        numberOfCowsTV=(TextView)this.findViewById(R.id.number_of_cows_tv);
        numberOfCowsET=(EditText)this.findViewById(R.id.number_of_cows_et);
        editB=(Button)this.findViewById(R.id.edit_b);
        editB.setOnClickListener(this);

        //init text according to locale
        initTextInViews();
    }


    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.edit_farmer, menu);
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
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which==DialogInterface.BUTTON_POSITIVE){
                        dialog.dismiss();

                        clearEditTextDataCache();

                        Intent intent = new Intent(EditFarmer.this, MainMenu.class);
                        intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_ADMIN);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                    else{
                        dialog.cancel();
                    }
                }
            };
            AlertDialog mainMenuDialog = Utils.createMainMenuDialog(this, onClickListener);
            mainMenuDialog.show();
        }
        return false;
    }

    @Override
    public void initTextInViews() {
        setTitle(Locale.getStringInLocale("edit_farmer", this));
        fullNameTV.setText(" * " + Locale.getStringInLocale("full_name", this));
        extensionPersonnelTV.setText(Locale.getStringInLocale("extension_p", this));
        mobileNumberTV.setText(" * " + Locale.getStringInLocale("mobile_number", this));
        numberOfCowsTV.setText(" * "+Locale.getStringInLocale("number_of_cows",this));
        preferredLanguageTV.setText(" * "+Locale.getStringInLocale("preferred_language", this));

        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, languages);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        preferredLanguageS.setAdapter(languageAdapter);

        editB.setText(Locale.getStringInLocale("edit", this));
    }

    private void initMenuText(){
        if(this.menu != null){
            MenuItem mainMenuMI = menu.findItem(R.id.action_back_main_menu);
            mainMenuMI.setTitle(Locale.getStringInLocale("back_to_main_menu", this));
        }
    }

    private void cacheEditTextData(){
        if(cacheData) {
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_FRA_FULL_NAME, fullNameET.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_FRA_MOBILE_NUMBER, mobileNumberET.getText().toString());
        }
    }

    private void restoreEditTextData(){
        fullNameET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_FRA_FULL_NAME, ""));
        mobileNumberET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_FRA_MOBILE_NUMBER, ""));
    }

    private void clearEditTextDataCache(){
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_FRA_FULL_NAME, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_FRA_MOBILE_NUMBER, "");

        cacheData = false;
    }


    @Override
    public void onClick(View v) {

    }
}
