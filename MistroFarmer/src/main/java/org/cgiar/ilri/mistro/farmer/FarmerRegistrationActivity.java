package org.cgiar.ilri.mistro.farmer;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.DataHandler;
import org.cgiar.ilri.mistro.farmer.backend.Locale;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.json.JSONObject;

public class FarmerRegistrationActivity extends SherlockActivity implements View.OnClickListener,LocationListener
{
    public static final String TAG="FarmerRegistrationActivity";

    private String latitude;
    private String longitude;
    private TextView fullNameTV;
    private EditText fullNameET;
    private TextView extensionPersonnelTV;
    private EditText extensionPersonnelET;
    private TextView mobileNumberTV;
    private EditText mobileNumberET;
    private TextView numberOfCowsTV;
    private EditText numberOfCowsET;
    private Button registerButton;
    private String gpsAlertDialogTitle;
    private String gpsAlertDialogText;
    private String okayText;
    private String cancelText;
    private String networkAlertTitle;
    private String networkAlertText;
    private String nameETEmptyWarning;
    private String mobileNoETEmptyWarning;
    private  LocationManager locationManager;
    private String loadingPleaseWait;

    private Farmer farmer;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_registration);
        DataHandler.requestPermissionToUseSMS(this);

        //init child views
        fullNameTV=(TextView)this.findViewById(R.id.full_name_tv);
        fullNameET=(EditText)this.findViewById(R.id.full_name_et);
        extensionPersonnelTV=(TextView)this.findViewById(R.id.extension_personnel_tv);
        extensionPersonnelET=(EditText)this.findViewById(R.id.extension_personnel_et);
        mobileNumberTV=(TextView)this.findViewById(R.id.mobile_number_tv);
        mobileNumberET=(EditText)this.findViewById(R.id.mobile_number_et);
        TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        //Toast.makeText(this,telephonyManager.getSimSerialNumber(),Toast.LENGTH_LONG).show();
        mobileNumberET.setText(telephonyManager.getLine1Number());
        numberOfCowsTV=(TextView)this.findViewById(R.id.number_of_cows_tv);
        numberOfCowsET=(EditText)this.findViewById(R.id.number_of_cows_et);
        registerButton=(Button)this.findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);

        //init text according to locale
        initTextInViews();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.farmer_registration, menu);
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

    @Override
    protected void onResume()
    {
        super.onResume();

        fullNameET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_FRA_FULL_NAME, ""));
        extensionPersonnelET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_FRA_EXTENSION_PERSONNEL, ""));
        mobileNumberET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_FRA_MOBILE_NUMBER, ""));

        Bundle bundle=this.getIntent().getExtras();
        if(bundle!=null)
        {
            farmer=bundle.getParcelable(Farmer.PARCELABLE_KEY);
        }
        if(farmer!=null)
        {
            fullNameET.setText(farmer.getFullName());
            Log.d(TAG, "Full name: " + farmer.getFullName());
            extensionPersonnelET.setText(farmer.getExtensionPersonnel());
            Log.d(TAG,"Extension Personnel: "+farmer.getExtensionPersonnel());
            mobileNumberET.setText(farmer.getMobileNumber());
            Log.d(TAG,"Mobile number: "+farmer.getMobileNumber());
            numberOfCowsET.setText(String.valueOf(farmer.getCowNumber()));
            Log.d(TAG,"Number of Cows: "+String.valueOf(farmer.getCowNumber()));
            if((farmer.getLatitude()==null||farmer.getLatitude().length()==0)||(farmer.getLongitude()==null||farmer.getLongitude().length()==0))
            {
                getGPSCoordinates();
            }
        }
        else
        {
            getGPSCoordinates();
            Log.d(TAG, "Farmer object is null");
        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();

        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_FRA_FULL_NAME, fullNameET.getText().toString());
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_FRA_EXTENSION_PERSONNEL, extensionPersonnelET.getText().toString());
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_FRA_MOBILE_NUMBER, mobileNumberET.getText().toString());

        if(locationManager!=null)
        {
            locationManager.removeUpdates(this);
        }
    }

    private void initTextInViews()
    {
        setTitle(Locale.getStringInLocale("farmer_registration",this));
        fullNameTV.setText(" * "+Locale.getStringInLocale("full_name",this));
        extensionPersonnelTV.setText(Locale.getStringInLocale("extension_p",this));
        mobileNumberTV.setText(" * "+Locale.getStringInLocale("mobile_number",this));
        numberOfCowsTV.setText(" * "+Locale.getStringInLocale("number_of_cows",this));
        registerButton.setText(Locale.getStringInLocale("register",this));
        gpsAlertDialogTitle=Locale.getStringInLocale("enable_gps",this);
        gpsAlertDialogText=Locale.getStringInLocale("reason_for_enabling_gps",this);
        okayText=Locale.getStringInLocale("okay",this);
        cancelText=Locale.getStringInLocale("cancel",this);
        networkAlertTitle=Locale.getStringInLocale("enable_network",this);
        networkAlertText=Locale.getStringInLocale("reason_for_enabling_network",this);
        nameETEmptyWarning=Locale.getStringInLocale("enter_your_name",this);
        mobileNoETEmptyWarning=Locale.getStringInLocale("enter_your_mobile_no",this);
        loadingPleaseWait=Locale.getStringInLocale("loading_please_wait",this);
    }

    private void cacheFarmer(boolean isInFarm)
    {
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_FRA_FULL_NAME, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_FRA_EXTENSION_PERSONNEL, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_FRA_MOBILE_NUMBER, "");

        if(farmer==null)
        {
            farmer=new Farmer();
        }
        farmer.setFullName(fullNameET.getText().toString());
        farmer.setExtensionPersonnel(extensionPersonnelET.getText().toString());
        farmer.setMobileNumber(mobileNumberET.getText().toString());
        farmer.setCowNumber((numberOfCowsET.getText().toString()==null||numberOfCowsET.getText().toString().length()==0) ? 0:Integer.parseInt(numberOfCowsET.getText().toString()));//Integer.parseInt(numberOfCowsET.getText().toString())
        if(isInFarm) {
            farmer.setLatitude(latitude);
            farmer.setLongitude(longitude);
        }
        else {
            farmer.setLatitude("");
            farmer.setLongitude("");
        }
        farmer.setMode(Farmer.MODE_INITIAL_REGISTRATION);
        TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        farmer.setSimCardSN(telephonyManager.getSimSerialNumber());
        //TODO:save gps coordinates
    }

    @Override
    public void onClick(View view)
    {
        if(view==registerButton)
        {
            buildGPSAlert();
        }
    }

    public void registerButtonClicked(boolean isInFarm){
        if(validateInput(isInFarm)) {
            cacheFarmer(isInFarm);
            String numberOfCowsString=numberOfCowsET.getText().toString();
            if(numberOfCowsString!=null && numberOfCowsString.length()>0 && Integer.parseInt(numberOfCowsString)>0)
            {
                int numberOfCows=Integer.valueOf(numberOfCowsString);
                Intent intent=new Intent(FarmerRegistrationActivity.this,CowRegistrationActivity.class);
                //intent.putExtra(CowRegistrationActivity.KEY_MODE,CowRegistrationActivity.MODE_COW);
                intent.putExtra(CowRegistrationActivity.KEY_INDEX,0);
                intent.putExtra(CowRegistrationActivity.KEY_NUMBER_OF_COWS,numberOfCows);
                Bundle bundle=new Bundle();
                bundle.putParcelable(Farmer.PARCELABLE_KEY, farmer);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            else
            {
                Log.d(TAG, farmer.getJsonObject().toString());
                sendDataToServer(farmer.getJsonObject());
            }
        }
    }

    private void getGPSCoordinates()
    {
        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            //Toast.makeText(this,"gps started",Toast.LENGTH_LONG).show();
            Criteria criteria=new Criteria();
            String provider=locationManager.getBestProvider(criteria,false);
            Location location=locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider,18000,1000,this);//If farmer  is moving at 200km/h, will still be able to update!
            if(location!=null)
            {
                onLocationChanged(location);
            }
        }
        else
        {
            AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(gpsAlertDialogTitle);
            alertDialogBuilder
                    .setMessage(gpsAlertDialogText)
                    .setCancelable(false)
                    .setPositiveButton(okayText, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(cancelText, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                            Intent intent=new Intent(FarmerRegistrationActivity.this,LandingActivity.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog alertDialog=alertDialogBuilder.create();
            alertDialog.show();

        }
    }

    private void sendDataToServer(JSONObject jsonObject)
    {
        ServerRegistrationThread serverRegistrationThread=new ServerRegistrationThread();
        serverRegistrationThread.execute(jsonObject);
    }

    private boolean validateInput(boolean isInFarm)
    {
        String nameText=fullNameET.getText().toString();
        if(nameText==null || nameText.equals(""))
        {
            Toast.makeText(this,nameETEmptyWarning,Toast.LENGTH_LONG).show();
            return false;
        }
        String mobileNumberText=mobileNumberET.getText().toString();
        if(mobileNumberText==null||mobileNumberText.equals(""))
        {
            Toast.makeText(this,mobileNoETEmptyWarning,Toast.LENGTH_LONG).show();
            return false;
        }
        /*if(extensionPersonnelET.getText().toString() == null || extensionPersonnelET.getText().toString().length() == 0){
            Toast.makeText(this, Locale.getStringInLocale("enter_extension_personnel_name", this), Toast.LENGTH_LONG).show();
            return  false;
        }*/
        if(isInFarm && (longitude == null || longitude.length() == 0 || latitude == null || latitude.length() == 0)) {
            Toast.makeText(this,Locale.getStringInLocale("gps_narrowing_down_on_loc",this),Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        latitude=String.valueOf(location.getLatitude());
        longitude=String.valueOf(location.getLongitude());
        Log.d(TAG,"latitude : "+latitude);
        Log.d(TAG,"longitude : "+longitude);
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

    private void buildGPSAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Locale.getStringInLocale("are_you_in_farm",this));
        builder.setPositiveButton(Locale.getStringInLocale("yes",this), new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                registerButtonClicked(true);
            }
        });
        builder.setNegativeButton(Locale.getStringInLocale("no",this), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                registerButtonClicked(false);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private class ServerRegistrationThread extends AsyncTask<JSONObject,Integer,String>
    {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(FarmerRegistrationActivity.this, "",loadingPleaseWait, true);
        }

        @Override
        protected String doInBackground(JSONObject... params)
        {
            Log.d(TAG,"sending registration data to server");
            String responseString=DataHandler.sendDataToServer(FarmerRegistrationActivity.this, params[0].toString(),DataHandler.FARMER_REGISTRATION_URL, true);

            return responseString;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if(result == null){
                String httpError = DataHandler.getSharedPreference(FarmerRegistrationActivity.this, "http_error", "No Error thrown to application. Something must be really wrong");
                Toast.makeText(FarmerRegistrationActivity.this,httpError,Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_GENERIC_FAILURE)){
                Toast.makeText(FarmerRegistrationActivity.this, Locale.getStringInLocale("generic_sms_error", FarmerRegistrationActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_NO_SERVICE)){
                Toast.makeText(FarmerRegistrationActivity.this, Locale.getStringInLocale("no_service", FarmerRegistrationActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RADIO_OFF)){
                Toast.makeText(FarmerRegistrationActivity.this, Locale.getStringInLocale("radio_off", FarmerRegistrationActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RESULT_CANCELLED)){
                Toast.makeText(FarmerRegistrationActivity.this, Locale.getStringInLocale("server_not_receive_sms", FarmerRegistrationActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.ACKNOWLEDGE_OK))
            {
                Log.d(TAG,"data successfully sent to server");
                Utils.showSuccessfullRegistration(FarmerRegistrationActivity.this,null);
                //Intent intent=new Intent(FarmerRegistrationActivity.this,LandingActivity.class);
                //startActivity(intent);
            }
        }
    }

}
