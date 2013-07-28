package org.cgiar.ilri.mistro.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;

public class LandingActivity extends SherlockActivity implements View.OnClickListener
{
    private String localeCode;
    private Button loginButton;
    private Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        localeCode="en";//TODO:get local code from sharedPreferences

        //initialize child views
        loginButton=(Button)this.findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        registerButton=(Button)this.findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);


        //init text according to locale
        initTextInViews(localeCode);
    }

    private void initTextInViews(String localeCode)
    {
        if(localeCode.equals("en"))
        {
            loginButton.setText(R.string.login_en);
            registerButton.setText(R.string.register_en);
        }
    }

    @Override
    public void onClick(View view)
    {
        if(view==registerButton)
        {
            Intent intent=new Intent(LandingActivity.this,FarmerRegistrationActivity.class);
            startActivity(intent);
        }
    }
}
