package org.cgiar.ilri.mistro.farmer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import org.cgiar.ilri.mistro.farmer.backend.DataHandler;
import org.cgiar.ilri.mistro.farmer.carrier.Cow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MilkProductionActivity extends SherlockActivity implements View.OnClickListener
{
    private static final String TAG="MilkProductionActivity";
    private Button addProductionB;
    private Button productionHistoryB;
    private String localeCode;
    private Dialog addMilkProductionDialog;
    private TextView cowTV;
    private Spinner cowS;
    private TextView timeTV;
    private Spinner timeS;
    private TextView quantityTV;
    private EditText quantityET;
    private Button addMilkProductionAddB;
    private String[] cowNameArray;
    private String[] cowEarTagNumberArray;
    private String quantityETEmptyWarning;
    private String infoSuccessfullySent;
    private String problemInData;
    private String loadingPleaseWait;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk_production);

        localeCode="en";

        addProductionB=(Button)this.findViewById(R.id.add_production_b);
        addProductionB.setOnClickListener(this);
        productionHistoryB =(Button)this.findViewById(R.id.production_history_b);
        productionHistoryB.setOnClickListener(this);
        addMilkProductionDialog=new Dialog(this);
        addMilkProductionDialog.setContentView(R.layout.dialog_add_milk_production);
        cowTV=(TextView)addMilkProductionDialog.findViewById(R.id.cow_tv);
        cowS=(Spinner)addMilkProductionDialog.findViewById(R.id.cow_s);
        timeTV=(TextView)addMilkProductionDialog.findViewById(R.id.time_tv);
        timeS=(Spinner)addMilkProductionDialog.findViewById(R.id.time_s);
        quantityTV=(TextView)addMilkProductionDialog.findViewById(R.id.quantity_tv);
        quantityET=(EditText)addMilkProductionDialog.findViewById(R.id.quantity_et);
        addMilkProductionAddB=(Button)addMilkProductionDialog.findViewById(R.id.dialog_add_milk_add_b);
        addMilkProductionAddB.setOnClickListener(this);


        initTextInViews(localeCode);
        fetchCowIdentifiers();
    }

    public void initTextInViews(String localCode)
    {
        if(localCode.equals("en"))
        {
            this.setTitle(R.string.milk_production_en);
            addProductionB.setText(R.string.add_production_en);
            productionHistoryB.setText(R.string.production_history_en);
            addMilkProductionDialog.setTitle(R.string.add_production_en);
            cowTV.setText(R.string.cow_en);
            timeTV.setText(R.string.time_en);
            quantityTV.setText(R.string.quantity_en);
            addMilkProductionAddB.setText(R.string.add_en);
            ArrayAdapter<CharSequence> milkingTimesArrayAdapter=ArrayAdapter.createFromResource(this, R.array.milking_times_en, android.R.layout.simple_spinner_item);
            milkingTimesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            timeS.setAdapter(milkingTimesArrayAdapter);
            quantityETEmptyWarning=getResources().getString(R.string.enter_quantity_of_milk_produced_en);
            infoSuccessfullySent=getResources().getString(R.string.information_successfully_sent_to_server_en);
            problemInData=getResources().getString(R.string.problem_in_data_sent_en);
            loadingPleaseWait = getResources().getString(R.string.loading_please_wait_en);
        }
    }

    @Override
    public void onClick(View view)
    {
        if(view==addProductionB)
        {
            addMilkProductionDialog.show();
        }
        else if(view==addMilkProductionAddB)
        {
            sendMilkProductionData();
        }
        else if(view==productionHistoryB)
        {
            Intent intent=new Intent(MilkProductionActivity.this,MilkProcutionHistoryActivity.class);
            startActivity(intent);
        }
    }

    private void fetchCowIdentifiers()
    {
        if(DataHandler.checkNetworkConnection(this,localeCode))
        {
            CowIdentifierThread cowIdentifierThread=new CowIdentifierThread();
            TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            cowIdentifierThread.execute(telephonyManager.getSimSerialNumber());
        }
    }

    private void setCowIdentifiers(String[] cowArray)
    {
        if(cowS!=null)
        {
            ArrayAdapter<String> cowsArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,cowArray);
            cowsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cowS.setAdapter(cowsArrayAdapter);
        }
    }
    
    private class CowIdentifierThread extends AsyncTask<String,Integer,String>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MilkProductionActivity.this, "",loadingPleaseWait, true);
        }

        @Override
        protected String doInBackground(String... params)
        {
            JSONObject jsonObject=new JSONObject();
            try
            {
                jsonObject.put("simCardSN",params[0]);
                jsonObject.put("cowSex", Cow.SEX_FEMALE);
                String result=DataHandler.sendDataToServer(jsonObject.toString(),DataHandler.FARMER_FETCH_COW_IDENTIFIERS_URL);
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
            try
            {
                JSONObject jsonObject=new JSONObject(result);
                JSONArray cowNamesArray=jsonObject.getJSONArray("cowNames");
                JSONArray earTagNumbersArray=jsonObject.getJSONArray("earTagNumbers");
                String[] cowArray=new String[cowNamesArray.length()];
                String[] earTagArray=new String[cowNamesArray.length()];
                for(int i=0;i<cowNamesArray.length();i++)
                {
                    cowArray[i]=cowNamesArray.get(i).toString();
                    earTagArray[i]=earTagNumbersArray.get(i).toString();
                }
                //TODO: warn user if no cows
                if(cowArray.length==0)
                {
                    Toast.makeText(MilkProductionActivity.this,"no cows fetched",Toast.LENGTH_LONG).show();
                }
                MilkProductionActivity.this.cowNameArray =cowArray;
                MilkProductionActivity.this.cowEarTagNumberArray=earTagArray;
                String[] identifierArray=new String[cowArray.length];
                for (int i=0;i<cowArray.length;i++)
                {
                    if(cowArray[i]!=null&&!cowArray[i].equals(""))
                    {
                        identifierArray[i]=cowArray[i];
                    }
                    else
                    {
                        identifierArray[i]=earTagArray[i];
                    }
                }
                setCowIdentifiers(identifierArray);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
    }

    private void sendMilkProductionData()
    {
        if(DataHandler.checkNetworkConnection(this,localeCode))
        {
            if(validateInput())
            {
                addMilkProductionDialog.dismiss();
                TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
                MilkProductionDataAdditionThread milkProductionDataAdditionThread=new MilkProductionDataAdditionThread();
                milkProductionDataAdditionThread.execute(telephonyManager.getSimSerialNumber(),cowNameArray[cowS.getSelectedItemPosition()],cowEarTagNumberArray[cowS.getSelectedItemPosition()],String.valueOf(timeS.getSelectedItemPosition()),quantityET.getText().toString());
            }
        }

    }

    private boolean validateInput()
    {
        if(quantityET.getText().toString()==null)
        {
            Toast.makeText(this,quantityETEmptyWarning,Toast.LENGTH_LONG).show();
            return false;
        }
        else if(quantityET.getText().toString().length()<=0)
        {
            Toast.makeText(this,quantityETEmptyWarning,Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private class MilkProductionDataAdditionThread extends AsyncTask<String,Integer,String>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MilkProductionActivity.this, "",loadingPleaseWait, true);
        }

        @Override
        protected String doInBackground(String... params)
        {
            Log.d(TAG,"at milkProductionDataAdditionThread");
            JSONObject jsonObject=new JSONObject();
            try
            {
                jsonObject.put("simCardSN",params[0]);
                jsonObject.put("cowName",params[1]);
                jsonObject.put("cowEarTagNumber",params[2]);
                jsonObject.put("time",params[3]);
                jsonObject.put("quantity",params[4]);
                String result=DataHandler.sendDataToServer(jsonObject.toString(),DataHandler.FARMER_ADD_MILK_PRODUCTION_URL);
                Log.d(TAG,"data sent to server, result = "+result);
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
            if(result==null)
            {
                Toast.makeText(MilkProductionActivity.this,"server error",Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.ACKNOWLEDGE_OK))
            {
                Toast.makeText(MilkProductionActivity.this,infoSuccessfullySent,Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.DATA_ERROR))
            {
                Toast.makeText(MilkProductionActivity.this,problemInData,Toast.LENGTH_LONG).show();
            }
        }
    }
}
