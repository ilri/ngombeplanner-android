package org.cgiar.ilri.mistro.farmer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.view.Menu;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.DataHandler;
import org.cgiar.ilri.mistro.farmer.backend.Locale;
import org.json.JSONException;
import org.json.JSONObject;

public class LandingActivity extends SherlockActivity implements View.OnClickListener
{
    private static final String TAG="LandingActivity";
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
    private String loadingPleaseWait;
    private String serverError;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

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
        initTextInViews();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.landing_activity, menu);
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
            Toast.makeText(this, "kazi katika maendeleo",Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    private void initTextInViews()
    {
        loginButton.setText(Locale.getStringInLocale("login", this));
        registerButton.setText(Locale.getStringInLocale("register", this));
        loginText=Locale.getStringInLocale("login", this);
        unsuccessfulAuthText=Locale.getStringInLocale("sim_card_not_registered", this);
        okayText=Locale.getStringInLocale("okay", this);
        fromAnotherDevWarning=Locale.getStringInLocale("logging_in_from_different_device", this);
        yesText=Locale.getStringInLocale("yes", this);
        noText=Locale.getStringInLocale("no", this);
        loginAnywayText=Locale.getStringInLocale("login_anyway", this);
        registerText=Locale.getStringInLocale("register", this);
        oldMobileNumberTV.setText(Locale.getStringInLocale("old_mobile_number", this));
        newMobileNumberTV.setText(Locale.getStringInLocale("new_mobile_number", this));
        changeSystemSimCardDialog.setTitle(Locale.getStringInLocale("sim_card_registration", this));
        changeSystemSimCardB.setText(Locale.getStringInLocale("okay", this));
        simCardRegistrationText=Locale.getStringInLocale("sim_card_registration", this);
        oldNumberNotInSystemText=Locale.getStringInLocale("old_number_not_in_system", this);
        welcomeText=Locale.getStringInLocale("welcome", this);
        loadingPleaseWait=Locale.getStringInLocale("loading_please_wait", this);
        serverError=Locale.getStringInLocale("server_error", this);
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
            if(loginSessionOn == false)
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
        if(DataHandler.checkNetworkConnection(this,null))
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
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            loginSessionOn=true;
            progressDialog= ProgressDialog.show(LandingActivity.this, "",loadingPleaseWait, true);

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
            progressDialog.dismiss();
            loginSessionOn=false;
            if(result==null)
            {
                Toast.makeText(LandingActivity.this,serverError,Toast.LENGTH_LONG).show();
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
        if(DataHandler.checkNetworkConnection(this,null))
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
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LandingActivity.this, "",loadingPleaseWait, true);
        }

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
            progressDialog.dismiss();
            Log.d(TAG,"sim card registration *****"+result);
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
