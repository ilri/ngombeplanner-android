package org.cgiar.ilri.mistro.farmer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import org.cgiar.ilri.mistro.farmer.backend.DataHandler;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.json.JSONObject;

public class FarmerRegistrationActivity extends SherlockActivity implements View.OnClickListener,LocationListener
{
    public static final String TAG="FarmerRegistrationActivity";

    private String localeCode;
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
    private  LocationManager locationManager;

    private Farmer farmer;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_registration);

        localeCode="en";//TODO:get the locale code from sharedPreferences

        //init child views
        fullNameTV=(TextView)this.findViewById(R.id.full_name_tv);
        fullNameET=(EditText)this.findViewById(R.id.full_name_et);
        extensionPersonnelTV=(TextView)this.findViewById(R.id.extension_personnel_tv);
        extensionPersonnelET=(EditText)this.findViewById(R.id.extension_personnel_et);
        mobileNumberTV=(TextView)this.findViewById(R.id.mobile_number_tv);
        mobileNumberET=(EditText)this.findViewById(R.id.mobile_number_et);
        TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        Toast.makeText(this,telephonyManager.getSimSerialNumber(),Toast.LENGTH_LONG).show();
        mobileNumberET.setText(telephonyManager.getLine1Number());
        numberOfCowsTV=(TextView)this.findViewById(R.id.number_of_cows_tv);
        numberOfCowsET=(EditText)this.findViewById(R.id.number_of_cows_et);
        registerButton=(Button)this.findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);

        //init text according to locale
        initTextInViews(localeCode);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
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
        if(locationManager!=null)
        {
            locationManager.removeUpdates(this);
        }
    }

    private void initTextInViews(String localeCode)
    {
        if(localeCode.equals("en"))
        {
            setTitle(R.string.farmer_registration_en);
            fullNameTV.setText(R.string.full_name_en);
            extensionPersonnelTV.setText(R.string.extension_p_en);
            mobileNumberTV.setText(R.string.mobile_number_en);
            numberOfCowsTV.setText(R.string.number_of_cows_en);
            registerButton.setText(R.string.register_en);
            gpsAlertDialogTitle=getResources().getString(R.string.enable_gps_en);
            gpsAlertDialogText=getResources().getString(R.string.reason_for_enabling_gps_en);
            okayText=getResources().getString(R.string.okay_en);
            cancelText=getResources().getString(R.string.cancel_en);
            networkAlertTitle=getResources().getString(R.string.enable_network_en);
            networkAlertText=getResources().getString(R.string.reason_for_enabling_network_en);
        }
    }

    private void cacheFarmer()
    {
        if(farmer==null)
        {
            farmer=new Farmer();
        }
        farmer.setFullName(fullNameET.getText().toString());
        farmer.setExtensionPersonnel(extensionPersonnelET.getText().toString());
        farmer.setMobileNumber(mobileNumberET.getText().toString());
        farmer.setCowNumber((numberOfCowsET.getText().toString()==null||numberOfCowsET.getText().toString().length()==0) ? 0:Integer.parseInt(numberOfCowsET.getText().toString()));//Integer.parseInt(numberOfCowsET.getText().toString())
        farmer.setLatitude(latitude);
        farmer.setLongitude(longitude);
        //TODO:save gps coordinates
    }

    @Override
    public void onClick(View view)
    {
        if(view==registerButton)
        {
            cacheFarmer();
            String numberOfCowsString=numberOfCowsET.getText().toString();
            if(numberOfCowsString!=null && numberOfCowsString.length()>0 && Integer.parseInt(numberOfCowsString)>0)
            {
                int numberOfCows=Integer.valueOf(numberOfCowsString);
                Intent intent=new Intent(FarmerRegistrationActivity.this,CowRegistrationActivity.class);
                intent.putExtra(CowRegistrationActivity.KEY_MODE,CowRegistrationActivity.MODE_COW);
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
                if (DataHandler.checkNetworkConnection(this, localeCode))
                {
                    sendDataToServer(farmer.getJsonObject());
                }
            }
        }
    }

    private void getGPSCoordinates()
    {
        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Toast.makeText(this,"gps started",Toast.LENGTH_LONG).show();
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

    private class ServerRegistrationThread extends AsyncTask<JSONObject,Integer,Boolean>
    {

        @Override
        protected Boolean doInBackground(JSONObject... params)
        {
            Log.d(TAG,"sending registration data to server");
            String responseString=DataHandler.sendDataToServer(params[0].toString(),DataHandler.FARMER_REGISTRATION_URL);
            if(responseString!=null && responseString.equals(DataHandler.ACKNOWLEDGE_OK))
            {
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
            if(result)
            {
                Log.d(TAG,"data successfully sent to server");
                Utils.showSuccessfullRegistration(FarmerRegistrationActivity.this,localeCode);
                //Intent intent=new Intent(FarmerRegistrationActivity.this,LandingActivity.class);
                //startActivity(intent);
            }
            else
            {
                Toast.makeText(FarmerRegistrationActivity.this,"something went wrong",Toast.LENGTH_LONG).show();
            }
        }
    }

}
