package org.cgiar.ilri.mistro.farmer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import org.cgiar.ilri.mistro.farmer.carrier.Farmer;

public class FarmerRegistrationActivity extends SherlockActivity implements View.OnClickListener
{
    public static final String TAG="FarmerRegistrationActivity";

    private String localeCode;
    private TextView fullNameTV;
    private EditText fullNameET;
    private TextView extensionPersonnelTV;
    private EditText extensionPersonnelET;
    private TextView mobileNumberTV;
    private EditText mobileNumberET;
    private TextView numberOfCowsTV;
    private EditText numberOfCowsET;
    private Button registerButton;

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
        }
        else
        {
            Log.d(TAG,"Farmer object is null");
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
        //TODO:save gps coordinates
    }

    @Override
    public void onClick(View view)
    {
        if(view==registerButton)
        {
            cacheFarmer();
            String numberOfCowsString=numberOfCowsET.getText().toString();
            if(numberOfCowsString!=null && numberOfCowsString.length()>0)
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
                Intent intent=new Intent(FarmerRegistrationActivity.this,LandingActivity.class);
                startActivity(intent);
            }
        }
    }
}