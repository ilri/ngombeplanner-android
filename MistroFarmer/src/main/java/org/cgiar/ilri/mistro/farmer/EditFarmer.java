package org.cgiar.ilri.mistro.farmer;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
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


public class EditFarmer extends SherlockActivity implements MistroActivity, View.OnClickListener, LocationListener {
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
    private TextView farmLocTV;
    private Button recordLocB;
    private Button editB;
    private Button cancelB;

    private String adminData;
    private Farmer farmer;
    private List<String> languages;
    private boolean cacheData;
    private List<String> vetNames;
    private String longitude;
    private String latitude;
    private LocationManager locationManager;
    private ProgressDialog gpsProgressDialog;
    private boolean locationGotten;

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
        farmLocTV = (TextView)findViewById(R.id.farm_loc_tv);
        recordLocB = (Button)findViewById(R.id.record_loc_b);
        recordLocB.setOnClickListener(this);
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

        recordLocB.setText(Locale.getStringInLocale("rec_farm_loc", this));

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

                updateFarmLocation(farmer.getLongitude(), farmer.getLatitude());

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
        else if(v.equals(recordLocB)){
            buildGPSAlert();
        }
        else if(v.equals(editB)){
            if(validateInput()){
                cacheFarmer();

                EditFarmerThread editFarmerThread = new EditFarmerThread();
                editFarmerThread.execute(farmer.getJsonObject().toString());
            }
        }
    }

    private void cacheFarmer()
    {
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_FRA_FULL_NAME, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_FRA_EXTENSION_PERSONNEL, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_FRA_MOBILE_NUMBER, "");

        farmer.setFullName(fullNameET.getText().toString());
        if(vetNames != null && extensionPersonnelS.getSelectedItemPosition() != -1){
            farmer.setExtensionPersonnel(vetNames.get(extensionPersonnelS.getSelectedItemPosition()));
        }
        else{
            farmer.setExtensionPersonnel("");
        }

        if(languages != null && preferredLanguageS.getSelectedItemPosition() != -1){
            farmer.setPreferredLocale(Locale.getLocaleCode(this, languages.get(preferredLanguageS.getSelectedItemPosition())));
        }

        farmer.setMobileNumber(mobileNumberET.getText().toString());

        farmer.setMode(Farmer.MODE_EDIT_FARMER);
    }

    private void buildGPSAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Locale.getStringInLocale("are_you_in_farmers_farm",this));
        builder.setPositiveButton(Locale.getStringInLocale("yes",this), new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getGPSCoordinates();
            }
        });
        builder.setNegativeButton(Locale.getStringInLocale("no",this), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getGPSCoordinates() {
        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Toast.makeText(this,"gps started",Toast.LENGTH_LONG).show();
            locationGotten = false;
            gpsProgressDialog = ProgressDialog.show(EditFarmer.this, "", Locale.getStringInLocale("accuracy", EditFarmer.this) + " : " +Locale.getStringInLocale("unknown", EditFarmer.this), true);
            gpsProgressDialog.setCancelable(true);
            gpsProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    locationGotten = true;
                }
            });

            Criteria criteria=new Criteria();
            String provider=locationManager.getBestProvider(criteria,false);
            Location location=locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider,18000,1000,this);//If farmer  is moving at 200km/h, will still be able to update!
            if(location!=null)
            {
                onLocationChanged(location);
            }
        }
        else {
            AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(Locale.getStringInLocale("enable_gps",this));
            alertDialogBuilder
                    .setMessage(Locale.getStringInLocale("reason_for_enabling_gps", this))
                    .setCancelable(false)
                    .setPositiveButton(Locale.getStringInLocale("okay",this), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(Locale.getStringInLocale("cancel",this), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog=alertDialogBuilder.create();
            alertDialog.show();

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(!locationGotten){
            latitude=String.valueOf(location.getLatitude());
            longitude=String.valueOf(location.getLongitude());

            farmer.setLatitude(latitude);
            farmer.setLongitude(longitude);

            updateFarmLocation(longitude, latitude);

            Log.d(TAG, "latitude : " + latitude);
            Log.d(TAG, "longitude : " + longitude);
            if(latitude !=null && longitude != null){
                String lastDigitInLong = longitude.substring(longitude.length() - 1, longitude.length());
                String longCompare = longitude.substring(0, longitude.length() - 1) + String.valueOf(Integer.parseInt(lastDigitInLong) + 1);

                String lastDigitInLat = latitude.substring(latitude.length() - 1, latitude.length());
                String latCompare = latitude.substring(0, latitude.length() - 1) + String.valueOf(Integer.parseInt(lastDigitInLat) + 1);

                float[] accuracy = new float[1];
                double lat1 = Double.parseDouble(latitude);
                double lon1 = Double.parseDouble(longitude);
                double lat2 = Double.parseDouble(latCompare);
                double lon2 = Double.parseDouble(longCompare);
                Location.distanceBetween(lat1, lon1, lat2, lon2, accuracy);

                int accuracyInM =(int) (accuracy[0] * 0.000621371192f);
                Log.d(TAG, "Accuracy at "+String.valueOf(accuracyInM));

                gpsProgressDialog.setMessage(Locale.getStringInLocale("accuracy", this) + " : " + String.valueOf(accuracyInM) + " M");

                if(accuracyInM < 4){
                    locationGotten = true;
                    locationManager.removeUpdates(this);
                    gpsProgressDialog.dismiss();
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void updateFarmLocation(String longitude, String latitude){
        String farmLoc = Locale.getStringInLocale("farm_location", this);
        if(DataHandler.isNull(farmer.getLongitude()) || farmer.getLongitude().length() == 0){
            farmLoc = farmLoc + "\n \t Longitude: " + Locale.getStringInLocale("not_set", this);
        }
        else{
            farmLoc = farmLoc + "\n \t Longitude: " + longitude;
        }

        if(DataHandler.isNull(farmer.getLatitude()) || farmer.getLatitude().length() == 0){
            farmLoc = farmLoc + "\n \t Latitude: " + Locale.getStringInLocale("not_set", this);
        }
        else{
            farmLoc = farmLoc + "\n \t Latitude: " + latitude;
        }
        farmLocTV.setText(farmLoc);
    }

    private boolean validateInput()
    {
        String nameText=fullNameET.getText().toString();
        if(nameText==null || nameText.equals(""))
        {
            Toast.makeText(this,Locale.getStringInLocale("enter_farmer_name", this),Toast.LENGTH_LONG).show();
            return false;
        }
        else if(nameText.split(" ").length < 2){
            Toast.makeText(this, Locale.getStringInLocale("enter_two_names", this), Toast.LENGTH_LONG).show();
            return false;
        }

        String mobileNumberText=mobileNumberET.getText().toString();
        if(mobileNumberText==null||mobileNumberText.equals(""))
        {
            Toast.makeText(this,Locale.getStringInLocale("enter_farmer_mobile_no", this),Toast.LENGTH_LONG).show();
            return false;
        }
        else if(mobileNumberText.length() != 10){
            Toast.makeText(this, Locale.getStringInLocale("phone_number_not_valid", this), Toast.LENGTH_LONG).show();
            return false;
        }

        if(vetNames == null){
            Toast.makeText(this, Locale.getStringInLocale("epersonnel_tshoot", this), Toast.LENGTH_LONG).show();
            return false;
        }
        else if(extensionPersonnelS.getSelectedItemPosition() == -1 || vetNames.get(extensionPersonnelS.getSelectedItemPosition()).length() == 0){
            Toast.makeText(this, Locale.getStringInLocale("select_epersonnel", this), Toast.LENGTH_LONG).show();
            return  false;
        }
        return true;
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

    private class EditFarmerThread extends AsyncTask<String, Integer, String>{
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EditFarmer.this, "", Locale.getStringInLocale("loading_please_wait", EditFarmer.this), true);
        }

        @Override
        protected String doInBackground(String... params) {
            return DataHandler.sendDataToServer(EditFarmer.this, params[0], DataHandler.ADMIN_EDIT_FARMER_URL, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if(result == null) {
                String httpError = DataHandler.getSharedPreference(EditFarmer.this, "http_error", "No Error thrown to application. Something must be really wrong");
                Toast.makeText(EditFarmer.this,httpError,Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_GENERIC_FAILURE)){
                Toast.makeText(EditFarmer.this, Locale.getStringInLocale("generic_sms_error", EditFarmer.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_NO_SERVICE)){
                Toast.makeText(EditFarmer.this, Locale.getStringInLocale("no_service", EditFarmer.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RADIO_OFF)){
                Toast.makeText(EditFarmer.this, Locale.getStringInLocale("radio_off", EditFarmer.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RESULT_CANCELLED)){
                Toast.makeText(EditFarmer.this, Locale.getStringInLocale("server_not_receive_sms", EditFarmer.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.CODE_NUMBER_IN_USE)){
                Toast.makeText(EditFarmer.this, Locale.getStringInLocale("number_in_use", EditFarmer.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.ACKNOWLEDGE_OK)) {
                Toast.makeText(EditFarmer.this, Locale.getStringInLocale("farmer_profile_updated", EditFarmer.this), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(EditFarmer.this, FarmerSelection.class);
                //intent.putExtra(FarmerSelection.KEY_ADMIN_DATA, adminData);
                startActivity(intent);
            }
        }
    }
}
