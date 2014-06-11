package org.cgiar.ilri.mistro.farmer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class LandingActivity extends SherlockActivity implements MistroActivity, View.OnClickListener
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
    private CheckBox adminFuncCB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        //DataHandler.requestPermissionToUseSMS(this);

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

        adminFuncCB = (CheckBox)this.findViewById(R.id.admin_func_cb);

        //init text according to locale
        initTextInViews();

        //get version name for the application and toast it
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Toast.makeText(this, "Version "+pInfo.versionName, Toast.LENGTH_LONG).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.landing_activity, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(Language.processLanguageMenuItemSelected(this, this, item)){
            return true;
        }
        return false;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void initTextInViews()
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
        serverError=Locale.getStringInLocale("problem_connecting_to_server", this);
        adminFuncCB.setText(Locale.getStringInLocale("admin_functions", this));
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
            Log.d(TAG,"Login button clicked");
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
        TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        if(!adminFuncCB.isChecked()){
            Log.d(TAG, "Trying to log in as normal farmer");
            if(telephonyManager.getSimSerialNumber()!=null)
            {
                UserAuthenticationThread authenticationThread=new UserAuthenticationThread();
                authenticationThread.execute(telephonyManager.getSimSerialNumber());
            }
            else{
                Toast.makeText(this,Locale.getStringInLocale("no_sim_card",this),Toast.LENGTH_LONG).show();
            }
        }
        else{
            Log.d(TAG, "Trying to log in as admin");
            if(telephonyManager.getSimSerialNumber()!=null)
            {
                AdminAuthenticationThread authenticationThread=new AdminAuthenticationThread();
                authenticationThread.execute(telephonyManager.getSimSerialNumber());
            }
            else{
                Toast.makeText(this,Locale.getStringInLocale("no_sim_card",this),Toast.LENGTH_LONG).show();
            }
        }
    }

    private class UserAuthenticationThread extends AsyncTask<String,Integer,String> {
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
                jsonObject.put("deviceType", "Android");
                //jsonObject.put("mobileNumber",params[1]);
                String result = DataHandler.sendDataToServer(LandingActivity.this, jsonObject.toString(),DataHandler.FARMER_AUTHENTICATION_URL, true);
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
                String httpError = DataHandler.getSharedPreference(LandingActivity.this, "http_error", "No Error thrown to application. Something must be really wrong");
                Toast.makeText(LandingActivity.this,httpError,Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_GENERIC_FAILURE)){
                Toast.makeText(LandingActivity.this, Locale.getStringInLocale("generic_sms_error", LandingActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_NO_SERVICE)){
                Toast.makeText(LandingActivity.this, Locale.getStringInLocale("no_service", LandingActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RADIO_OFF)){
                Toast.makeText(LandingActivity.this, Locale.getStringInLocale("radio_off", LandingActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RESULT_CANCELLED)){
                Toast.makeText(LandingActivity.this, Locale.getStringInLocale("server_not_receive_sms", LandingActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.CODE_USER_NOT_AUTHENTICATED))
            {
                showRegisterOrLoginDialog();
            }
            else {
                //save farmer data in the database
                SaveFarmerDataThread saveFarmerDataThread = new SaveFarmerDataThread();
                saveFarmerDataThread.execute(result);

                Intent intent=new Intent(LandingActivity.this,MainMenu.class);
                Log.d(TAG, result);
                intent.putExtra(MainMenu.KEY_FARMER_DATA, result);
                intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_FARMER);
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

    private class AdminAuthenticationThread extends AsyncTask<String,Integer,String> {
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
                jsonObject.put("deviceType", "Android");
                //jsonObject.put("mobileNumber",params[1]);
                String result = DataHandler.sendDataToServer(LandingActivity.this, jsonObject.toString(),DataHandler.ADMIN_AUTHENTICATION_URL, true);
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
                String httpError = DataHandler.getSharedPreference(LandingActivity.this, "http_error", "No Error thrown to application. Something must be really wrong");
                Toast.makeText(LandingActivity.this,httpError,Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_GENERIC_FAILURE)){
                Toast.makeText(LandingActivity.this, Locale.getStringInLocale("generic_sms_error", LandingActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_NO_SERVICE)){
                Toast.makeText(LandingActivity.this, Locale.getStringInLocale("no_service", LandingActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RADIO_OFF)){
                Toast.makeText(LandingActivity.this, Locale.getStringInLocale("radio_off", LandingActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RESULT_CANCELLED)){
                Toast.makeText(LandingActivity.this, Locale.getStringInLocale("server_not_receive_sms", LandingActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.CODE_USER_NOT_AUTHENTICATED))
            {
                Log.w(TAG, "User trying to log in as admin with an unregistered Sim Card");
                Toast.makeText(LandingActivity.this, Locale.getStringInLocale("sim_card_not_admin", LandingActivity.this), Toast.LENGTH_LONG).show();
            }
            else{
                Intent intent=new Intent(LandingActivity.this,MainMenu.class);
                intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_ADMIN);
                intent.putExtra(MainMenu.KEY_ADMIN_DATA, result);
                startActivity(intent);
            }
        }
    }

    private void registerSimCard()
    {
        if(oldMobileNumberET.getText().toString().length() == 0){
            Toast.makeText(this, Locale.getStringInLocale("enter_old_phone_no", this), Toast.LENGTH_LONG).show();
        }
        else if(newMobileNumberET.getText().toString().length() == 0){
            Toast.makeText(this, Locale.getStringInLocale("enter_new_phone_no", this), Toast.LENGTH_LONG).show();
        }
        else{
            TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            if(telephonyManager.getSimSerialNumber()!=null) {
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
                result= DataHandler.sendDataToServer(LandingActivity.this, jsonObject.toString(),DataHandler.FARMER_SIM_CARD_REGISTRATION_URL, true);
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
                Toast.makeText(LandingActivity.this, Locale.getStringInLocale("problem_connecting_to_server", LandingActivity.this),Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.CODE_USER_NOT_AUTHENTICATED)) {
                Utils.showGenericAlertDialog(LandingActivity.this,simCardRegistrationText,oldNumberNotInSystemText,okayText,null,null,null);
            }
            else if(result.equals(DataHandler.CODE_SIM_CARD_REGISTERED))
            {
                changeSystemSimCardDialog.dismiss();
                authenticateUser();
            }
        }
    }

    private class SaveFarmerDataThread extends AsyncTask<String, Integer, Integer>{

        @Override
        protected Integer doInBackground(String... params) {
            Log.d(TAG, "About to save farmer data in the database");

            try {
                JSONObject farmerData = new JSONObject(params[0]);
                DataHandler.saveFarmerData(LandingActivity.this, farmerData);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            Log.d(TAG, "Finished caching farmer data in SQLite database");
        }
    }

}
