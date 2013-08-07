package org.cgiar.ilri.mistro.farmer;

import android.app.Dialog;
import android.content.Context;
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
    private Dialog loginDialog;
    private TextView farmerIdTV;
    private EditText farmerIdET;
    private Button dialogLoginLoginB;
    private boolean loginSessionOn=false;
    private String loginText;
    private String unsuccessfulAuthText;
    private String okayText;
    private String fromAnotherDevWarning;
    private String yesText;
    private String noText;

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

        loginDialog=new Dialog(this);
        loginDialog.setContentView(R.layout.dialog_login);
        farmerIdTV=(TextView)loginDialog.findViewById(R.id.farmer_id_tv);
        farmerIdET=(EditText)loginDialog.findViewById(R.id.farmer_id_et);
        dialogLoginLoginB=(Button)loginDialog.findViewById(R.id.dialog_login_login_b);
        dialogLoginLoginB.setOnClickListener(this);

        //init text according to locale
        initTextInViews(localeCode);
    }

    private void initTextInViews(String localeCode)
    {
        if(localeCode.equals("en"))
        {
            loginButton.setText(R.string.login_en);
            registerButton.setText(R.string.register_en);
            loginDialog.setTitle(R.string.login_en);
            farmerIdTV.setText(R.string.your_mistro_id_en);
            dialogLoginLoginB.setText(R.string.login_en);
            loginText=getResources().getString(R.string.login_en);
            unsuccessfulAuthText=getResources().getString(R.string.failed_to_authenticate_en);
            okayText=getResources().getString(R.string.okay_en);
            fromAnotherDevWarning=getResources().getString(R.string.logging_in_from_different_device_en);
            yesText=getResources().getString(R.string.yes_en);
            noText=getResources().getString(R.string.no_en);
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
        else if(view==loginButton)
        {
            if(!loginSessionOn)
            {
                loginDialog.show();
            }
        }
        else if(view==dialogLoginLoginB)
        {
            authenticateUser();
        }
    }

    private void authenticateUser()
    {
        if(DataHandler.checkNetworkConnection(this,localeCode))
        {
            if(farmerIdET.getText().toString()!=null&&farmerIdET.getText().toString().length()>0)
            {
                TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
                UserAuthenticationThread authenticationThread=new UserAuthenticationThread();
                authenticationThread.execute(farmerIdET.getText().toString(), telephonyManager.getLine1Number());
            }
            else
            {
                Toast.makeText(this,getResources().getString(R.string.enter_your_mistro_id_en),Toast.LENGTH_LONG).show();
            }

        }
    }

    private class UserAuthenticationThread extends AsyncTask<String,Integer,Integer>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            loginSessionOn=true;
        }

        @Override
        protected Integer doInBackground(String... params)
        {
            JSONObject jsonObject=new JSONObject();
            try
            {
                jsonObject.put("farmerID",params[0]);
                jsonObject.put("mobileNumber",params[1]);
                String result = DataHandler.sendDataToServer(jsonObject.toString(),DataHandler.FARMER_AUTHENTICATION_URL);
                return Integer.parseInt(result);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            super.onPostExecute(result);
            loginSessionOn=false;
            if(result==0)//user not authenticated
            {
                Utils.showGenericAlertDialog(LandingActivity.this,loginText,unsuccessfulAuthText,okayText,null,null,null);
            }
            else if(result==1)
            {
                Utils.showGenericAlertDialog(LandingActivity.this,loginText,fromAnotherDevWarning,yesText,noText,MainMenu.class,null);
            }
            else if(result==2)
            {
                loginDialog.dismiss();
                Intent intent=new Intent(LandingActivity.this,MainMenu.class);
                startActivity(intent);
                Toast.makeText(LandingActivity.this,"User authenticated in normal phone",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(LandingActivity.this,"Server Error",Toast.LENGTH_LONG).show();
            }
        }
    }
}
