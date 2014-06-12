package org.cgiar.ilri.mistro.farmer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.DataHandler;
import org.cgiar.ilri.mistro.farmer.backend.Locale;
import org.cgiar.ilri.mistro.farmer.backend.database.DatabaseHelper;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class EditFarmer extends SherlockActivity implements MistroActivity, View.OnClickListener {
    private static final String TAG = "EditFarmer";


    private Menu menu;
    private TextView fullNameTV;
    private EditText fullNameET;
    private TextView preferredLanguageTV;
    private Spinner preferredLanguageS;
    private TextView extensionPersonnelTV;
    private Spinner extensionPersonnelS;
    private TextView mobileNumberTV;
    private EditText mobileNumberET;
    private Button editB;
    private Button cancelB;

    private String adminData;
    private Farmer farmer;
    private List<String> languages;
    private boolean cacheData;
    private List<String> vetNames;

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
        editB=(Button)this.findViewById(R.id.edit_b);
        editB.setOnClickListener(this);
        cancelB =(Button)this.findViewById(R.id.cancel_b);
        cancelB.setOnClickListener(this);

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

        List<String> tmpVetNames = new ArrayList<String>();
        tmpVetNames.add("");
        ArrayAdapter<String> epArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,tmpVetNames);
        epArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        extensionPersonnelS.setAdapter(epArrayAdapter);

        mobileNumberTV.setText(" * " + Locale.getStringInLocale("phone_number", this));
        preferredLanguageTV.setText(" * "+Locale.getStringInLocale("preferred_language", this));

        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, languages);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        preferredLanguageS.setAdapter(languageAdapter);

        editB.setText(Locale.getStringInLocale("edit", this));
        cancelB.setText(Locale.getStringInLocale("cancel", this));
    }

    private void initMenuText(){
        if(this.menu != null){
            MenuItem mainMenuMI = menu.findItem(R.id.action_back_main_menu);
            mainMenuMI.setTitle(Locale.getStringInLocale("back_to_main_menu", this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            adminData = bundle.getString(FarmerSelection.KEY_ADMIN_DATA);
            farmer = bundle.getParcelable(Farmer.PARCELABLE_KEY);
            if(farmer != null){
                fullNameET.setText(farmer.getFullName());
                mobileNumberET.setText(farmer.getMobileNumber());

                String prefLanguage = Locale.getLanguage(this, farmer.getPreferredLocale());

                for(int i = 0; i < languages.size(); i++){
                    if(languages.get(i).equals(prefLanguage)){
                        preferredLanguageS.setSelection(i);
                    }
                }

                //fetch extension personnel names from server. Do this after farmer object is initialized since the thread is going to use this object
                FetchVetsThread fetchVetsThread = new FetchVetsThread();
                fetchVetsThread.execute(0);
            }
            else{
                Log.e(TAG, "Parcelable farmer object from previous activity is null");
            }
        }
        else{
            Log.e(TAG, "Unable to get data from previous activity");
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
        if(v.equals(cancelB)){
            Intent intent = new Intent(this, FarmerSelection.class);
            intent.putExtra(FarmerSelection.KEY_ADMIN_DATA, adminData);
            startActivity(intent);
        }
    }

    private class FetchVetsThread extends AsyncTask<Integer, Integer, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EditFarmer.this, "", Locale.getStringInLocale("loading_please_wait", EditFarmer.this), true);
        }

        @Override
        protected String doInBackground(Integer... params) {
            Log.d(TAG, "Fetching extension personnel from server");

            return DataHandler.sendDataToServer(EditFarmer.this, "", DataHandler.FARMER_FETCH_VETS_URL, true, DatabaseHelper.TABLE_EXTENSION_PERSONNEL);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if(result == null){
                Toast.makeText(EditFarmer.this, Locale.getStringInLocale("unable_to_get_epersonnel", EditFarmer.this), Toast.LENGTH_LONG).show();
            }
            else{
                try{
                    Log.d(TAG, "result is "+result);
                    JSONArray vetJsonArray = new JSONArray(result);
                    vetNames = new ArrayList<String>();
                    vetNames.add("");
                    for(int i = 0; i < vetJsonArray.length(); i++){
                        vetNames.add(vetJsonArray.getJSONObject(i).getString("name"));
                    }

                    int selection = 0;//default selection is the blank one
                    if(farmer != null){
                        String selectedEP = farmer.getExtensionPersonnel();
                        if(selectedEP != null && selectedEP.length() > 0){
                            for(int i =0; i < vetNames.size(); i++){
                                if(vetNames.get(i).equals(selectedEP)){
                                    selection = i;
                                }
                            }
                        }
                    }

                    ArrayAdapter<String> epArrayAdapter = new ArrayAdapter<String>(EditFarmer.this, android.R.layout.simple_spinner_item, vetNames);
                    epArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    extensionPersonnelS.setAdapter(epArrayAdapter);
                    extensionPersonnelS.setSelection(selection);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
