package org.cgiar.ilri.mistro.farmer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.DataHandler;
import org.cgiar.ilri.mistro.farmer.backend.Locale;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FarmerSelectionActivity extends SherlockActivity implements MistroActivity, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "FarmerSelectionActivity";
    public static final String KEY_ADMIN_DATA= "adminData";

    private Menu menu;

    private TextView filterFarmersTV;
    private Spinner filterFarmersS;
    private TextView selectFarmerTV;
    private Spinner selectFarmerS;
    private Button selectB;
    private Button backB;

    private List<Farmer> allFarmers;
    private List<Farmer> filteredFarmers;
    private JSONObject adminData;
    private List<String> filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_selection);

        filterFarmersTV = (TextView)findViewById(R.id.filter_farmers_tv);
        filterFarmersS = (Spinner)findViewById(R.id.filter_farmers_s);
        filterFarmersS.setOnItemSelectedListener(this);
        selectFarmerTV = (TextView)findViewById(R.id.select_farmer_tv);
        selectFarmerS = (Spinner)findViewById(R.id.select_farmer_s);

        selectB = (Button)findViewById(R.id.select_b);
        selectB.setOnClickListener(this);
        backB = (Button)findViewById(R.id.back_b);
        backB.setOnClickListener(this);

        initTextInViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle bundle=this.getIntent().getExtras();
        if(bundle != null){
            String adminJSONString = bundle.getString(KEY_ADMIN_DATA);
            loadAdminData(adminJSONString);
        }
        else{
            TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            if(telephonyManager != null){
                GetFarmerDataThread getFarmerDataThread = new GetFarmerDataThread();
                getFarmerDataThread.execute(telephonyManager.getSimSerialNumber());
            }
            else{
                Toast.makeText(this,Locale.getStringInLocale("no_sim_card",this),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadAdminData(String adminJSONString){
        try {
            adminData = new JSONObject(adminJSONString);
            JSONArray farmerData = adminData.getJSONArray("farmers");

            filters = new ArrayList<String>();
            filters.add(Locale.getStringInLocale("all_farmers", this));
            filters.add(Locale.getStringInLocale("farmers_no_epersonnel", this));
            if(adminData.getInt("is_super") == 1){//admin is super
                JSONArray allEPersonnel = adminData.getJSONArray("extension_personnel");
                for(int i = 0; i < allEPersonnel.length(); i++){
                    JSONObject currEPersonnel = allEPersonnel.getJSONObject(i);
                    filters.add(Locale.getStringInLocale("farmers_tied_to", this) + " " + currEPersonnel.getString("name"));
                }
            }
            else{
                filters.add(Locale.getStringInLocale("farmers_tied_to", this) +" "+ adminData.getString("name"));
            }
            ArrayAdapter<String> filterArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filters);
            filterArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            filterFarmersS.setAdapter(filterArrayAdapter);

            allFarmers = new ArrayList<Farmer>(farmerData.length());
            filteredFarmers = new ArrayList<Farmer>(farmerData.length());
            for (int i = 0; i < farmerData.length(); i++) {
                Farmer currFarmer = new Farmer(farmerData.getJSONObject(i));
                allFarmers.add(currFarmer);
                filteredFarmers.add(currFarmer);
            }

            setFilteredFarmerList(filteredFarmers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFilteredFarmerList(List<Farmer> filteredFarmers){
        List<String> farmerNames = new ArrayList<String>(filteredFarmers.size());
        for (int i = 0; i < filteredFarmers.size(); i++) {
            Farmer currFarmer = filteredFarmers.get(i);
            farmerNames.add(currFarmer.getFullName());
        }

        ArrayAdapter<String> farmerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, farmerNames);
        farmerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectFarmerS.setAdapter(farmerArrayAdapter);

        if(this.filteredFarmers != filteredFarmers) {
            this.filteredFarmers = filteredFarmers;
        }
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

        filterFarmersTV.setText(Locale.getStringInLocale("filter_farmers", this));
        selectFarmerTV.setText(Locale.getStringInLocale("select_farmer", this));
        selectB.setText(Locale.getStringInLocale("select", this));
        backB.setText(Locale.getStringInLocale("back", this));
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
            if(selectFarmerS.getSelectedItemPosition() != -1 && filteredFarmers.size() > selectFarmerS.getSelectedItemPosition()){
                Log.d(TAG, "Selected farmer index = "+String.valueOf(selectFarmerS.getSelectedItemPosition()));
                Farmer selectedFarmer = filteredFarmers.get(selectFarmerS.getSelectedItemPosition());
                Intent intent = new Intent(this, EditFarmerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Farmer.PARCELABLE_KEY, selectedFarmer);
                intent.putExtras(bundle);
                intent.putExtra(KEY_ADMIN_DATA, adminData.toString());
                startActivity(intent);
            }
        }
        else if(v.equals(backB)){
            Intent intent = new Intent(this, MainMenu.class);
            intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_ADMIN);
            intent.putExtra(MainMenu.KEY_ADMIN_DATA, adminData.toString());
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent == filterFarmersS){
            if(filterFarmersS.getSelectedItemPosition() == 0){//all farmers
                List<Farmer> newlyFilteredFarmers = this.allFarmers;
                setFilteredFarmerList(newlyFilteredFarmers);
            }
            else if(filterFarmersS.getSelectedItemPosition() == 1){//farmers without extension personnel
                List<Farmer> newlyFilteredFarmers = new ArrayList<Farmer>();
                for(int i =0; i<allFarmers.size(); i++){
                    Farmer currFarmer = allFarmers.get(i);
                    if(DataHandler.isNull(currFarmer.getExtensionPersonnel()) || currFarmer.getExtensionPersonnel().length() == 0){
                        newlyFilteredFarmers.add(currFarmer);
                    }
                    else{
                        Log.d(TAG, currFarmer.getExtensionPersonnel()+" does not qualify to be null");
                    }
                }

                setFilteredFarmerList(newlyFilteredFarmers);
            }
            else if(filterFarmersS.getSelectedItemPosition() != -1){
                String selection = filters.get(filterFarmersS.getSelectedItemPosition());

                List<Farmer> newlyFilteredFarmers = new ArrayList<Farmer>();
                for(int i = 0; i < allFarmers.size(); i++){
                    Farmer currFarmer = allFarmers.get(i);
                    if(currFarmer.getExtensionPersonnel() != null && currFarmer.getExtensionPersonnel().length() > 0 &&
                            selection.contains(currFarmer.getExtensionPersonnel())){
                        newlyFilteredFarmers.add(currFarmer);
                    }
                    else{
                        Log.d(TAG, currFarmer.getExtensionPersonnel()+" does not match "+selection);
                    }
                }

                setFilteredFarmerList(newlyFilteredFarmers);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class GetFarmerDataThread extends AsyncTask<String, Integer, String>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(FarmerSelectionActivity.this, "", Locale.getStringInLocale("loading_please_wait", FarmerSelectionActivity.this), true);
        }

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject=new JSONObject();
            try
            {
                jsonObject.put("simCardSN",params[0]);
                jsonObject.put("deviceType", "Android");
                //jsonObject.put("mobileNumber",params[1]);
                String result = DataHandler.sendDataToServer(FarmerSelectionActivity.this, jsonObject.toString(), DataHandler.ADMIN_AUTHENTICATION_URL, true);
                return result;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result==null){
                String httpError = DataHandler.getSharedPreference(FarmerSelectionActivity.this, "http_error", "No Error thrown to application. Something must be really wrong");
                Toast.makeText(FarmerSelectionActivity.this, httpError, Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_GENERIC_FAILURE)){
                Toast.makeText(FarmerSelectionActivity.this, Locale.getStringInLocale("generic_sms_error", FarmerSelectionActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_NO_SERVICE)){
                Toast.makeText(FarmerSelectionActivity.this, Locale.getStringInLocale("no_service", FarmerSelectionActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RADIO_OFF)){
                Toast.makeText(FarmerSelectionActivity.this, Locale.getStringInLocale("radio_off", FarmerSelectionActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RESULT_CANCELLED)){
                Toast.makeText(FarmerSelectionActivity.this, Locale.getStringInLocale("server_not_receive_sms", FarmerSelectionActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.CODE_USER_NOT_AUTHENTICATED)){
                Log.w(TAG, "Admin not authenticated. May mean he/she changed sim cards after logging in");
                Toast.makeText(FarmerSelectionActivity.this, Locale.getStringInLocale("sim_card_not_admin", FarmerSelectionActivity.this), Toast.LENGTH_LONG).show();
            }
            else{
                loadAdminData(result);
            }
        }
    }
}
