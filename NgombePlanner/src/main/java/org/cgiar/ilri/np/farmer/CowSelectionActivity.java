package org.cgiar.ilri.np.farmer;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.cgiar.ilri.np.farmer.backend.DataHandler;
import org.cgiar.ilri.np.farmer.backend.Locale;
import org.cgiar.ilri.np.farmer.carrier.Cow;
import org.cgiar.ilri.np.farmer.carrier.Farmer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CowSelectionActivity extends SherlockActivity implements NPActivity, AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final String TAG = "CowSelectionActivity";
    public static final String KEY_ADMIN_DATA = "adminData";

    private Menu menu;

    private TextView filterFarmersTV;
    private Spinner filterFarmersS;
    private TextView selectFarmerTV;
    private Spinner selectFarmerS;
    private TextView selectCowTV;
    private Spinner selectCowS;
    private Button selectB;
    private Button backB;

    private JSONObject adminData;
    private List<Farmer> allFarmers;
    private List<Farmer> filteredFarmers;
    private List<String> filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_selection);

        filterFarmersTV = (TextView)findViewById(R.id.filter_farmers_tv);
        filterFarmersS = (Spinner)findViewById(R.id.filter_farmers_s);
        filterFarmersS.setOnItemSelectedListener(this);
        selectFarmerTV = (TextView)findViewById(R.id.select_farmer_tv);
        selectFarmerS = (Spinner)findViewById(R.id.select_farmer_s);
        selectFarmerS.setOnItemSelectedListener(this);
        selectCowTV = (TextView)findViewById(R.id.select_cow_tv);
        selectCowS = (Spinner)findViewById(R.id.select_cow_s);
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
        if(NPActivity.Language.processLanguageMenuItemSelected(this, this, item)){
            return true;
        }
        else if(item.getItemId() == R.id.action_back_main_menu) {
            Intent intent = new Intent(this, MainMenu.class);
            intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_ADMIN);
            intent.putExtra(MainMenu.KEY_ADMIN_DATA, adminData.toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        return false;
    }

    private void initMenuText(){
        if(this.menu != null){
            MenuItem mainMenuMI = menu.findItem(R.id.action_back_main_menu);
            mainMenuMI.setTitle(Locale.getStringInLocale("back_to_main_menu", this));
        }
    }

    @Override
    public void initTextInViews() {
        this.setTitle(Locale.getStringInLocale("select_cow", this));

        filterFarmersTV.setText(Locale.getStringInLocale("filter_farmers", this));
        selectFarmerTV.setText(Locale.getStringInLocale("select_farmer", this));
        selectCowTV.setText(Locale.getStringInLocale("select_cow", this));

        selectB.setText(Locale.getStringInLocale("select", this));
        backB.setText(Locale.getStringInLocale("back", this));
    }

    private void loadAdminData(String adminJSONString){
        try {
            adminData = new JSONObject(adminJSONString);

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

            JSONArray farmerData = adminData.getJSONArray("farmers");

            allFarmers = new ArrayList<Farmer>(farmerData.length());
            filteredFarmers = new ArrayList<Farmer>(farmerData.length());
            for (int i = 0; i < farmerData.length(); i++) {
                Farmer currFarmer = new Farmer(farmerData.getJSONObject(i));
                allFarmers.add(currFarmer);
                filteredFarmers.add(currFarmer);
            }

            List<String> farmerNames = new ArrayList<String>(filteredFarmers.size());
            for (int i = 0; i < filteredFarmers.size(); i++) {
                Farmer currFarmer = filteredFarmers.get(i);
                farmerNames.add(currFarmer.getFullName());
            }

            ArrayAdapter<String> farmerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, farmerNames);
            farmerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectFarmerS.setAdapter(farmerArrayAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent == selectFarmerS){

            List<String> blankList = new ArrayList<String>();
            ArrayAdapter<String> cowArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, blankList);
            cowArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectCowS.setAdapter(cowArrayAdapter);

            if(selectFarmerS.getSelectedItemPosition() != -1){
                Farmer selectedFarmer = filteredFarmers.get(selectFarmerS.getSelectedItemPosition());
                List<Cow> cows = selectedFarmer.getCows();
                if(cows == null){
                    GetCowDataThread getCowDataThread = new GetCowDataThread(selectFarmerS.getSelectedItemPosition());
                    getCowDataThread.execute(selectedFarmer.getId());
                }
                else{
                    loadCowData(cows);
                }
            }
        }

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
                        //Log.d(TAG, currFarmer.getExtensionPersonnel()+" does not qualify to be null");
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
                        //Log.d(TAG, currFarmer.getExtensionPersonnel()+" does not match "+selection);
                    }
                }

                setFilteredFarmerList(newlyFilteredFarmers);
            }
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

    private void loadCowData(List<Cow> cows){
        List<String> cowNames = new ArrayList<String>();
        for(int i = 0; i < cows.size(); i++){
            String name = cows.get(i).getName();
            String etn = cows.get(i).getEarTagNumber();

            String dispName = "";
            if(DataHandler.isNull(name) || name.length() == 0){
                dispName = etn;
            }
            else if(DataHandler.isNull(etn) || etn.length() == 0){
                dispName = name;
            }
            else{
                dispName = name + " (" + etn + ")";
            }
            cowNames.add(dispName);
        }

        ArrayAdapter<String> cowArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cowNames);
        cowArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCowS.setAdapter(cowArrayAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if(v == selectB){
            if(selectFarmerS.getSelectedItemPosition() != -1){
                Farmer selectedFarmer = filteredFarmers.get(selectFarmerS.getSelectedItemPosition());
                if(selectedFarmer != null){
                    if(selectCowS.getSelectedItemPosition() != -1){
                        List<Cow> cows = selectedFarmer.getCows();
                        if(cows != null){
                            Cow selectedCow = cows.get(selectCowS.getSelectedItemPosition());
                            if(selectedCow != null){
                                Log.d(TAG, "Cow index = "+selectCowS.getSelectedItemPosition());
                                Log.d(TAG, "Number of cows = "+cows.size());
                                Intent intent=new Intent(this, EditCowActivity.class);
                                intent.putExtra(MainMenu.KEY_ADMIN_DATA, adminData.toString());
                                intent.putExtra(EditCowActivity.KEY_INDEX, selectCowS.getSelectedItemPosition());
                                intent.putExtra(EditCowActivity.KEY_NUMBER_OF_COWS, cows.size());
                                Bundle bundle=new Bundle();
                                bundle.putParcelable(Farmer.PARCELABLE_KEY, selectedFarmer);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }
                    }
                }
            }
        }
        else if(v == backB){
            Intent intent = new Intent(this, MainMenu.class);
            intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_ADMIN);
            intent.putExtra(MainMenu.KEY_ADMIN_DATA, adminData.toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    private class GetCowDataThread extends AsyncTask<Integer, Integer, String>{

        private int farmerIndex;
        private ProgressDialog progressDialog;

        public GetCowDataThread(int farmerIndex){
            this.farmerIndex = farmerIndex;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(CowSelectionActivity.this, "", Locale.getStringInLocale("loading_please_wait", CowSelectionActivity.this), true);
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", params[0]);
                return DataHandler.sendDataToServer(CowSelectionActivity.this, jsonObject.toString(), DataHandler.ADMIN_GET_COWS_URL,true);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if(result==null){
                String httpError = DataHandler.getSharedPreference(CowSelectionActivity.this, "http_error", "No Error thrown to application. Something must be really wrong");
                Toast.makeText(CowSelectionActivity.this, httpError, Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_GENERIC_FAILURE)){
                Toast.makeText(CowSelectionActivity.this, Locale.getStringInLocale("generic_sms_error", CowSelectionActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_NO_SERVICE)){
                Toast.makeText(CowSelectionActivity.this, Locale.getStringInLocale("no_service", CowSelectionActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RADIO_OFF)){
                Toast.makeText(CowSelectionActivity.this, Locale.getStringInLocale("radio_off", CowSelectionActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RESULT_CANCELLED)){
                Toast.makeText(CowSelectionActivity.this, Locale.getStringInLocale("server_not_receive_sms", CowSelectionActivity.this), Toast.LENGTH_LONG).show();
            }
            else{
                try{
                    Log.d(TAG, "fetched cows  = "+result);
                    JSONArray cowJsonArray = new JSONArray(result);
                    filteredFarmers.get(farmerIndex).setCows(cowJsonArray);
                    loadCowData(filteredFarmers.get(farmerIndex).getCows());

                    //add the cow list to the copy of the farmer object in the allFarmers list
                    for(int i = 0; i < allFarmers.size(); i++){
                        if(allFarmers.get(i).getId() == filteredFarmers.get(farmerIndex).getId()){
                            allFarmers.get(i).setCows(cowJsonArray);
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
