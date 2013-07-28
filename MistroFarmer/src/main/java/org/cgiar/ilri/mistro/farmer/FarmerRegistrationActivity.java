package org.cgiar.ilri.mistro.farmer;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class FarmerRegistrationActivity extends SherlockActivity
{
    private String localeCode;
    private TextView fullNameTV;
    private TextView extensionPersonnelTV;
    private TextView mobileNumberTV;
    private TextView numberOfCowsTV;
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
        registerButton=(Button)this.findViewById(R.id.register_button);

        //init text according to locale
        initTextInViews(localeCode);

    }

    private void initTextInViews(String localeCode)
    {
        if(localeCode.equals("en"))
        {
            fullNameTV.setText(R.string.full_name_en);
            extensionPersonnelTV.setText(R.string.extension_p_en);
            mobileNumberTV.setText(R.string.mobile_number_en);
            numberOfCowsTV.setText(R.string.number_of_cows_en);
            registerButton.setText(R.string.register_en);
        }
    }
}