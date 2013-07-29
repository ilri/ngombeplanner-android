package org.cgiar.ilri.mistro.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class FarmerRegistrationActivity extends SherlockActivity implements View.OnClickListener
{
    private String localeCode;
    private TextView fullNameTV;
    private TextView extensionPersonnelTV;
    private TextView mobileNumberTV;
    private TextView numberOfCowsTV;
    private EditText numberOfCowsET;
    private Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_registration);

        localeCode="en";//TODO:get the locale code from sharedPreferences

        //init child views
        fullNameTV=(TextView)this.findViewById(R.id.full_name_tv);
        extensionPersonnelTV=(TextView)this.findViewById(R.id.extension_personnel_tv);
        mobileNumberTV=(TextView)this.findViewById(R.id.mobile_number_tv);
        numberOfCowsTV=(TextView)this.findViewById(R.id.number_of_cows_tv);
        numberOfCowsET=(EditText)this.findViewById(R.id.number_of_cows_et);
        registerButton=(Button)this.findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);

        //init text according to locale
        initTextInViews(localeCode);

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

    @Override
    public void onClick(View view)
    {
        if(view==registerButton)
        {
            String numberOfCowsString=numberOfCowsET.getText().toString();
            if(numberOfCowsString.length()>0)
            {
                int numberOfCows=Integer.valueOf(numberOfCowsString);
                Intent intent=new Intent(FarmerRegistrationActivity.this,CowRegistrationActivity.class);
                intent.putExtra(CowRegistrationActivity.KEY_MODE,CowRegistrationActivity.MODE_COW);
                intent.putExtra(CowRegistrationActivity.KEY_INDEX,0);
                intent.putExtra(CowRegistrationActivity.KEY_NUMBER_OF_COWS,numberOfCows);
                startActivity(intent);
            }
            else
            {
                Intent intent=new Intent(FarmerRegistrationActivity.this,LandingActivity.class);
                startActivity(intent);
            }
        }
    }
}