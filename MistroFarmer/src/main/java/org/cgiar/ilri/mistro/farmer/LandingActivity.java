package org.cgiar.ilri.mistro.farmer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import org.cgiar.ilri.mistro.farmer.backend.DataHandler;
import org.json.JSONException;
import org.json.JSONObject;

public class LandingActivity extends SherlockActivity implements View.OnClickListener
{
    private String localeCode;
    private Button loginButton;
    private Button registerButton;
    private boolean loginSessionOn=false;
    private String loginText;
    private String unsuccessfulAuthText;
    private String okayText;
    private String fromAnotherDevWarning;
    private String yesText;
    private String noText;
    private String loginAnywayText;
    private String registerText;
    private Dialog changeSystemSimCardDialog;
    private TextView oldMobileNumberTV;
    private EditText oldMobileNumberET;
    private TextView newMobileNumberTV;
    private EditText newMobileNumberET;
    private Button changeSystemSimCardB;
    private String simCardRegistrationText;
    private String oldNumberNotInSystemText;
    private String welcomeText;

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
        changeSystemSimCardDialog=new Dialog(this);
        changeSystemSimCardDialog.setContentView(R.layout.dialog_change_system_sim_card);
        oldMobileNumberTV =(TextView)changeSystemSimCardDialog.findViewById(R.id.old_mobile_number_tv);
        oldMobileNumberET =(EditText)changeSystemSimCardDialog.findViewById(R.id.old_mobile_number_et);
        changeSystemSimCardB=(Button)changeSystemSimCardDialog.findViewById(R.id.dialog_change_system_ok_b);
        changeSystemSimCardB.setOnClickListener(this);
        newMobileNumberTV=(TextView)changeSystemSimCardDialog.findViewById(R.id.new_mobile_number_tv);
        newMobileNumberET=(EditText)changeSystemSimCardDialog.findViewById(R.id.new_mobile_number_et);
        TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        newMobileNumberET.setText(telephonyManager.getLine1Number());

        //init text according to locale
        initTextInViews(localeCode);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    private void initTextInViews(String localeCode)
    {
        if(localeCode.equals("en"))
        {
            loginButton.setText(R.string.login_en);
            registerButton.setText(R.string.register_en);
            loginText=getResources().getString(R.string.login_en);
            unsuccessfulAuthText=getResources().getString(R.string.sim_card_not_registered_en);
            okayText=getResources().getString(R.string.okay_en);
            fromAnotherDevWarning=getResources().getString(R.string.logging_in_from_different_device_en);
            yesText=getResources().getString(R.string.yes_en);
            noText=getResources().getString(R.string.no_en);
            loginAnywayText=getResources().getString(R.string.login_anyway_en);
            registerText=getResources().getString(R.string.register_en);
            oldMobileNumberTV.setText(R.string.old_mobile_number_en);
            newMobileNumberTV.setText(R.string.new_mobile_number_en);
            changeSystemSimCardDialog.setTitle(R.string.sim_card_registration_en);
            changeSystemSimCardB.setText(R.string.okay_en);
            simCardRegistrationText=getResources().getString(R.string.sim_card_registration_en);
            oldNumberNotInSystemText=getResources().getString(R.string.old_number_not_in_system_en);
            welcomeText=getResources().getString(R.string.welcome_en);
        }
    }

    private void startRegistrationActivity()
    {
        Intent intent=new Intent(LandingActivity.this,FarmerRegistrationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view)
    {
        if(view==registerButton)
        {
            startRegistrationActivity();
        }
        else if(view==loginButton)
        {
            if(!loginSessionOn)
            {
                //loginDialog.show();
                authenticateUser();
            }
        }
        else if(view==changeSystemSimCardB)
        {
            registerSimCard();
        }
    }



    private void authenticateUser()
    {
        if(DataHandler.checkNetworkConnection(this,localeCode))
        {
            TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            if(telephonyManager.getSimSerialNumber()!=null)
            {
                UserAuthenticationThread authenticationThread=new UserAuthenticationThread();
                authenticationThread.execute(telephonyManager.getSimSerialNumber());
            }

        }
    }

    private class UserAuthenticationThread extends AsyncTask<String,Integer,String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            loginSessionOn=true;
        }

        @Override
        protected String doInBackground(String... params)
        {
            JSONObject jsonObject=new JSONObject();
            try
            {
                jsonObject.put("simCardSN",params[0]);
                //jsonObject.put("mobileNumber",params[1]);
                String result = DataHandler.sendDataToServer(jsonObject.toString(),DataHandler.FARMER_AUTHENTICATION_URL);
                return result;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            loginSessionOn=false;
            if(result==null)
            {
                Toast.makeText(LandingActivity.this,"Server Error",Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.CODE_USER_NOT_AUTHENTICATED))
            {
                showRegisterOrLoginDialog();
            }
            else
            {
                Toast.makeText(LandingActivity.this, welcomeText+" "+result,Toast.LENGTH_LONG).show();
                Intent intent=new Intent(LandingActivity.this,MainMenu.class);
                startActivity(intent);
            }
        }

        private void showRegisterOrLoginDialog()
        {
            AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(LandingActivity.this);
            alertDialogBuilder.setTitle(loginText);
            alertDialogBuilder
                    .setMessage(unsuccessfulAuthText)
                    .setCancelable(true)
                    .setPositiveButton(loginAnywayText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            changeSystemSimCardDialog.show();
                        }
                    })
                    .setNegativeButton(registerText, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startRegistrationActivity();
                        }
                    });
            AlertDialog alertDialog=alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void registerSimCard()
    {
        if(DataHandler.checkNetworkConnection(this,localeCode))
        {
            TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            if(telephonyManager.getSimSerialNumber()!=null)
            {
                SimCardRegistrationThread simCardRegistrationThread=new SimCardRegistrationThread();
                simCardRegistrationThread.execute(oldMobileNumberET.getText().toString(),newMobileNumberET.getText().toString(),telephonyManager.getSimSerialNumber());
            }

        }
    }

    private class SimCardRegistrationThread extends AsyncTask<String,Integer,String>
    {

        @Override
        protected String doInBackground(String... params)
        {

            JSONObject jsonObject=new JSONObject();
            try
            {
                String result=null;
                jsonObject.put("oldMobileNumber",params[0]);
                jsonObject.put("newMobileNumber",params[1]);
                jsonObject.put("newSimCardSN",params[2]);
                result= DataHandler.sendDataToServer(jsonObject.toString(),DataHandler.FARMER_SIM_CARD_REGISTRATION_URL);
                return result;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            if(result==null)
            {
                Toast.makeText(LandingActivity.this,"Server Error",Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.CODE_USER_NOT_AUTHENTICATED))
            {
                Utils.showGenericAlertDialog(LandingActivity.this,simCardRegistrationText,oldNumberNotInSystemText,okayText,null,null,null);
            }
            else if(result.equals(DataHandler.CODE_SIM_CARD_REGISTERED))
            {
                changeSystemSimCardDialog.dismiss();
                authenticateUser();
            }
        }
    }
}
