package org.cgiar.ilri.mistro.farmer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import org.cgiar.ilri.mistro.farmer.backend.DataHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddEventActivity extends SherlockActivity
{
    private String localeCode;

    private TextView cowIdentifierTV;
    private Spinner cowIdentifierS;
    private TextView dateTV;
    private EditText dateET;
    private TextView eventTypeTV;
    private Spinner eventTypeS;
    private TextView remarksTV;
    private EditText remarksET;
    private Button okayB;

    private String[] cowNameArray;
    private String[] cowEarTagNumberArray;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        localeCode="en";

        cowIdentifierTV=(TextView)findViewById(R.id.cow_identifier_tv);
        cowIdentifierS=(Spinner)findViewById(R.id.cow_identifier_s);
        dateTV=(TextView)findViewById(R.id.date_tv);
        dateET=(EditText)findViewById(R.id.date_et);
        eventTypeTV=(TextView)findViewById(R.id.event_type_tv);
        eventTypeS=(Spinner)findViewById(R.id.event_type_s);
        remarksTV=(TextView)findViewById(R.id.remarks_tv);
        remarksET=(EditText)findViewById(R.id.remarks_et);
        okayB=(Button)findViewById(R.id.okay_b);

        initTextInViews(localeCode);
        fetchCowIdentifiers();
    }

    private void initTextInViews(String localeCode)
    {
        if(localeCode.equals("en"))
        {
            setTitle(R.string.add_an_event_en);
            cowIdentifierTV.setText(R.string.cow_en);
            dateTV.setText(R.string.date_en);
            eventTypeTV.setText(R.string.event_en);
            ArrayAdapter<CharSequence> eventTypeArrayAdapter=ArrayAdapter.createFromResource(this, R.array.cow_event_types_en, android.R.layout.simple_spinner_item);
            eventTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            eventTypeS.setAdapter(eventTypeArrayAdapter);
            remarksTV.setText(R.string.remarks_en);
            okayB.setText(R.string.okay_en);
        }
    }

    private void fetchCowIdentifiers()
    {
        CowIdentifierThread cowIdentifierThread=new CowIdentifierThread();
        TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        cowIdentifierThread.execute(telephonyManager.getSimSerialNumber());
    }

    private void setCowIdentifiers(String[] cowIdentifiers)
    {
        if(cowIdentifierS!=null)
        {
            ArrayAdapter<String> cowsArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,cowIdentifiers);
            cowsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cowIdentifierS.setAdapter(cowsArrayAdapter);
        }
    }

    private class CowIdentifierThread extends AsyncTask<String,Integer,String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            JSONObject jsonObject=new JSONObject();
            try
            {
                jsonObject.put("simCardSN",params[0]);
                String result= DataHandler.sendDataToServer(jsonObject.toString(), DataHandler.FARMER_FETCH_COW_IDENTIFIERS_URL);
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
                    Toast.makeText(AddEventActivity.this, "no cows fetched", Toast.LENGTH_LONG).show();
                }
                AddEventActivity.this.cowNameArray =cowArray;
                AddEventActivity.this.cowEarTagNumberArray=earTagArray;
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
    
}
