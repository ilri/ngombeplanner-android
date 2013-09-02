package org.cgiar.ilri.mistro.farmer;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import org.cgiar.ilri.mistro.farmer.backend.DataHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AddEventActivity extends SherlockActivity implements View.OnClickListener,View.OnFocusChangeListener,DatePickerDialog.OnDateSetListener
{
    private String localeCode;
    private final String dateFormat="dd/MM/yyyy";

    private TextView cowIdentifierTV;
    private Spinner cowIdentifierS;
    private TextView dateTV;
    private EditText dateET;
    private TextView eventTypeTV;
    private Spinner eventTypeS;
    private TextView remarksTV;
    private EditText remarksET;
    private Button okayB;
    private DatePickerDialog datePickerDialog;

    private String[] cowNameArray;
    private String[] cowEarTagNumberArray;
    private String enterDate;
    private String dateInFuture;
    private String eventRecorded;
    private String sendUnsuccessfulWarning;
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
        dateET.setOnFocusChangeListener(this);
        dateET.setOnClickListener(this);
        eventTypeTV=(TextView)findViewById(R.id.event_type_tv);
        eventTypeS=(Spinner)findViewById(R.id.event_type_s);
        remarksTV=(TextView)findViewById(R.id.remarks_tv);
        remarksET=(EditText)findViewById(R.id.remarks_et);
        okayB=(Button)findViewById(R.id.okay_b);
        okayB.setOnClickListener(this);

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
            enterDate=getResources().getString(R.string.enter_date_en);
            dateInFuture=getResources().getString(R.string.date_in_future_en);
            eventRecorded=getResources().getString(R.string.event_successfully_recorded_en);
            sendUnsuccessfulWarning=getResources().getString(R.string.something_went_wrong_try_again_en);
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

    @Override
    public void onClick(View view)
    {
        if(view==okayB)
        {
            sendEvent();
        }
        else if(view==dateET)
        {
            dateETClicked();
        }
    }

    private boolean validateInput()
    {
        if(dateET.getText().toString()==null||dateET.getText().toString().length()==0)
        {
            Toast.makeText(this,enterDate,Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            try
            {
                Date dateEntered=new SimpleDateFormat(dateFormat, Locale.ENGLISH).parse(dateET.getText().toString());
                Date today=new Date();
                if((today.getTime()-dateEntered.getTime())<0)
                {
                    Toast.makeText(this,dateInFuture,Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void sendEvent()
    {
        if(validateInput() && DataHandler.checkNetworkConnection(this,localeCode))
        {
            String[] eventTypes = getResources().getStringArray(R.array.cow_event_types_en);
            String selectedEvent = eventTypes[eventTypeS.getSelectedItemPosition()];
            String selectedCowETN = cowEarTagNumberArray[cowIdentifierS.getSelectedItemPosition()];
            String selectedCowName = cowNameArray[cowIdentifierS.getSelectedItemPosition()];
            TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            String serialNumber = telephonyManager.getSimSerialNumber();
            JSONObject jsonObject = new JSONObject();
            try
            {
                jsonObject.put("simCardSN", serialNumber);
                jsonObject.put("cowEarTagNumber", selectedCowETN);
                jsonObject.put("cowName", selectedCowName);
                jsonObject.put("date", dateET.getText().toString());
                jsonObject.put("eventType", selectedEvent);
                jsonObject.put("remarks", remarksET.getText().toString());
                CowEventAdditionThread cowEventAdditionThread=new CowEventAdditionThread();
                cowEventAdditionThread.execute(jsonObject);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    private class CowEventAdditionThread extends AsyncTask<JSONObject, Integer, String>
    {

        @Override
        protected String doInBackground(JSONObject... params)
        {
            String result = DataHandler.sendDataToServer(params[0].toString(), DataHandler.FARMER_ADD_COW_EVENT_URL);
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            if(result.equals(DataHandler.ACKNOWLEDGE_OK))
            {
                Toast.makeText(AddEventActivity.this, eventRecorded, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddEventActivity.this, EventsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(AddEventActivity.this, sendUnsuccessfulWarning, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus)
    {
        if(view==dateET && hasFocus)
        {
            dateETClicked();
        }
    }

    private void dateETClicked()
    {
        Date date=null;

        if(dateET.getText().toString().length()>0)
        {
            try
            {
                date=new SimpleDateFormat(dateFormat, Locale.ENGLISH).parse(dateET.getText().toString());
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        if(date==null)
        {
            date=new Date();
        }

        Calendar calendar=new GregorianCalendar();
        calendar.setTime(date);
        datePickerDialog=new DatePickerDialog(this,this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        //datePickerDialog=createDialogWithoutDateField(this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        String dateString=String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year);
        dateET.setText(dateString);
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
