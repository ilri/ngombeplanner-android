package org.cgiar.ilri.mistro.farmer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.actionbarsherlock.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.DataHandler;
import org.cgiar.ilri.mistro.farmer.backend.Locale;
import org.cgiar.ilri.mistro.farmer.carrier.Cow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddMilkProductionActivity extends SherlockActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private final String dateFormat="dd/MM/yyyy";
    private static final String TAG="AddMilkProductionActivity";

    private TextView cowTV;
    private Spinner cowS;
    private TextView dateTV;
    private EditText dateET;
    private TextView timeTV;
    private Spinner timeS;
    private TextView quantityTV;
    private EditText quantityET;
    private TextView quantityTypeTV;
    private Spinner quantityTypeS;
    private Button addMilkProductionAddB;
    private Button cancelB;
    /*private TextView noMilkingTV;
    private EditText noMilkingET;
    private TextView calfSucklingTV;
    private Spinner calfSucklingS;*/
    private DatePickerDialog datePickerDialog;
    private Menu menu;

    private String[] cowNameArray;
    private String[] cowEarTagNumberArray;
    private String[] quantityTypes;
    private String[] calfSucklingTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_milk_production);
        DataHandler.requestPermissionToUseSMS(this);

        cowTV=(TextView)this.findViewById(R.id.cow_tv);
        cowS=(Spinner)this.findViewById(R.id.cow_s);
        dateTV=(TextView)this.findViewById(R.id.date_tv);
        dateET=(EditText)this.findViewById(R.id.date_et);
        dateET.setOnClickListener(this);
        timeTV=(TextView)this.findViewById(R.id.time_tv);
        timeS=(Spinner)this.findViewById(R.id.time_s);
        quantityTV=(TextView)this.findViewById(R.id.quantity_tv);
        quantityET=(EditText)this.findViewById(R.id.quantity_et);
        quantityTypeTV=(TextView)this.findViewById(R.id.quantity_type_tv);
        quantityTypeS=(Spinner)this.findViewById(R.id.quantity_type_s);
        addMilkProductionAddB=(Button)this.findViewById(R.id.dialog_add_milk_add_b);
        /*noMilkingTV = (TextView)this.findViewById(R.id.no_milking_tv);
        noMilkingET = (EditText)this.findViewById(R.id.no_milking_et);
        calfSucklingTV = (TextView)this.findViewById(R.id.calf_suckling_tv);
        calfSucklingS = (Spinner)this.findViewById(R.id.calf_suckling_s);*/
        addMilkProductionAddB.setOnClickListener(this);
        cancelB = (Button)this.findViewById(R.id.cancel_b);
        cancelB.setOnClickListener(this);

        initTextInViews();
        fetchCowIdentifiers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AMPA_DATE, dateET.getText().toString());
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_AMPA_QUANTITY, quantityET.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        dateET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_AMPA_DATE, ""));
        quantityET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_AMPA_QUANTITY, ""));
    }

    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.milk_production, menu);
        this.menu = menu;
        initMenuText();
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
            Toast.makeText(this, "kazi katika maendeleo", Toast.LENGTH_LONG).show();
            return true;
        }
        else if(item.getItemId() == R.id.action_back_main_menu) {
            Intent intent = new Intent(this, MainMenu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        return false;
    }

    public void initTextInViews() {
        this.setTitle(Locale.getStringInLocale("add_production",this));

        cowTV.setText(Locale.getStringInLocale("cow",this));
        dateTV.setText(Locale.getStringInLocale("date",this));
        timeTV.setText(Locale.getStringInLocale("time",this));
        quantityTV.setText(Locale.getStringInLocale("quantity",this));
        addMilkProductionAddB.setText(Locale.getStringInLocale("add",this));
        int milkingTimesArrayID = Locale.getArrayIDInLocale("milking_times",this);
        if(milkingTimesArrayID != 0) {
            ArrayAdapter<CharSequence> milkingTimesArrayAdapter=ArrayAdapter.createFromResource(this, milkingTimesArrayID, android.R.layout.simple_spinner_item);
            milkingTimesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            timeS.setAdapter(milkingTimesArrayAdapter);
        }

        quantityTypeTV.setText(Locale.getStringInLocale("measurement_type",this));

        quantityTypes = Locale.getArrayInLocale("quantity_types",this);
        int defaultQuantityTypeIndex = 0;
        if(quantityTypes == null ) {
            quantityTypes = new String[1];
            quantityTypes[0] = "";
        }
        else {
            defaultQuantityTypeIndex = Integer.parseInt(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_MILK_QUANTITY_TYPE,"0"));
        }
        ArrayAdapter<String> quantityTypesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,quantityTypes);
        quantityTypesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantityTypeS.setAdapter(quantityTypesArrayAdapter);
        if(defaultQuantityTypeIndex < quantityTypes.length)
            quantityTypeS.setSelection(defaultQuantityTypeIndex);

        cancelB.setText(Locale.getStringInLocale("cancel", this));

        //noMilkingTV.setText(Locale.getStringInLocale("no_times_milked_in_a_day",this));
        //calfSucklingTV.setText(Locale.getStringInLocale("calf_suckling",this));

        /*calfSucklingTypes = Locale.getArrayInLocale("calf_suckling_types",this);
        ArrayAdapter<String> calfSucklingTypesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,calfSucklingTypes);
        calfSucklingTypesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calfSucklingS.setAdapter(calfSucklingTypesArrayAdapter);*/

        initMenuText();
    }

    private void initMenuText(){
        if(this.menu != null){
            MenuItem mainMenuMI = menu.findItem(R.id.action_back_main_menu);
            mainMenuMI.setTitle(Locale.getStringInLocale("back_to_main_menu", this));
        }
    }

    @Override
    public void onClick(View view) {
        if(view==addMilkProductionAddB) {
            sendMilkProductionData();
        }
        else if(view==dateET) {
            dateETClicked();
        }
        else if(view == cancelB){
            Intent intent = new Intent(this, MilkProductionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    private void dateETClicked() {
        Date date=new Date();
        Calendar calendar=new GregorianCalendar();
        calendar.setTime(date);
        datePickerDialog=new DatePickerDialog(this,this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        //datePickerDialog=createDialogWithoutDateField(this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String dateString=String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year);
        dateET.setText(dateString);
        if(!validateDate()) {
            dateET.setText("");
        }
    }

    private void sendMilkProductionData()
    {
        if(validateInput())
        {
            TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            MilkProductionDataAdditionThread milkProductionDataAdditionThread=new MilkProductionDataAdditionThread();
            String[] quantityTypesInEN = Locale.getArrayInLocale("quantity_types",this,Locale.LOCALE_ENGLISH);
            String[] milkingTimesInEN = Locale.getArrayInLocale("milking_times", this, Locale.LOCALE_ENGLISH);
            String quantityType = "";
            if(quantityTypesInEN.length == quantityTypes.length) {
                DataHandler.setSharedPreference(AddMilkProductionActivity.this, DataHandler.SP_KEY_MILK_QUANTITY_TYPE, String.valueOf(quantityTypeS.getSelectedItemPosition()));
                quantityType = quantityTypesInEN[quantityTypeS.getSelectedItemPosition()];
            }
            String milkingTime = "";
            if(milkingTimesInEN.length > 0){
                milkingTime = milkingTimesInEN[timeS.getSelectedItemPosition()];
            }

            /*String[] calfSucklingTypesInEN = Locale.getArrayInLocale("calf_suckling_types",this,Locale.LOCALE_ENGLISH);
            String calfSucklingType = calfSucklingTypesInEN[calfSucklingS.getSelectedItemPosition()];*/
            //milkProductionDataAdditionThread.execute(telephonyManager.getSimSerialNumber(),cowNameArray[cowS.getSelectedItemPosition()],cowEarTagNumberArray[cowS.getSelectedItemPosition()],milkingTime,quantityET.getText().toString(),quantityType,dateET.getText().toString(),noMilkingET.getText().toString(),calfSucklingType);
            milkProductionDataAdditionThread.execute(telephonyManager.getSimSerialNumber(),cowNameArray[cowS.getSelectedItemPosition()],cowEarTagNumberArray[cowS.getSelectedItemPosition()],milkingTime,quantityET.getText().toString(),quantityType,dateET.getText().toString());
        }
    }

    private boolean validateInput()
    {
        String[] quantityTypesInEN = Locale.getArrayInLocale("quantity_types",this,Locale.LOCALE_ENGLISH);
        String quantityType = quantityTypesInEN[quantityTypeS.getSelectedItemPosition()];
        if(quantityET.getText().toString()==null) {
            Toast.makeText(this, Locale.getStringInLocale("enter_quantity_of_milk_produced",this), Toast.LENGTH_LONG).show();
            return false;
        }
        else if(quantityET.getText().toString().length()<=0) {
            Toast.makeText(this, Locale.getStringInLocale("enter_quantity_of_milk_produced",this), Toast.LENGTH_LONG).show();
            return false;
        }
        else if(dateET.getText().toString()==null||dateET.getText().toString().length()==0) {
            Toast.makeText(this,Locale.getStringInLocale("enter_date",this),Toast.LENGTH_LONG).show();
            return false;
        }
        else if(!validateDate()) {
            return false;
        }
        else if(quantityType.equals("Litres") || quantityType.equals("KGs")) {
            if(Integer.parseInt(quantityET.getText().toString()) > 30) {
                Toast.makeText(this, Locale.getStringInLocale("milk_too_much",this),Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else if(quantityType.equals("Cups")) {
            if(Integer.parseInt(quantityET.getText().toString()) > (30*3.3)) {
                Toast.makeText(this, Locale.getStringInLocale("milk_too_much",this),Toast.LENGTH_LONG).show();
                return false;
            }
        }
       /* if(noMilkingET.getText().toString()==null || noMilkingET.getText().toString().trim().length()==0) {
            Toast.makeText(this, Locale.getStringInLocale("enter_number_times_cow_milked",this),Toast.LENGTH_LONG).show();
            return false;
        }
        else if(Integer.parseInt(noMilkingET.getText().toString())>10) {
            Toast.makeText(this, Locale.getStringInLocale("milking_times_too_much",this), Toast.LENGTH_LONG).show();
            return false;
        }*/
        return true;
    }

    private boolean validateDate() {
        try {
            Date dateEntered=new SimpleDateFormat(dateFormat, java.util.Locale.ENGLISH).parse(dateET.getText().toString());
            Date today=new Date();
            long milisecondDifference = today.getTime() - dateEntered.getTime();
            long days = milisecondDifference / 86400000;
            if((today.getTime()-dateEntered.getTime())<0) {
                Toast.makeText(this,Locale.getStringInLocale("date_in_future",this),Toast.LENGTH_LONG).show();
                return false;
            }
            else if(days > 15) {//more than 15 days
                Toast.makeText(this,Locale.getStringInLocale("milk_data_too_old",this),Toast.LENGTH_LONG).show();
                return false;
            }
            else {
                return true;
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void fetchCowIdentifiers() {
        CowIdentifierThread cowIdentifierThread=new CowIdentifierThread();
        TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        cowIdentifierThread.execute(telephonyManager.getSimSerialNumber());
    }

    private void setCowIdentifiers(String[] cowArray) {
        if(cowS!=null)
        {
            ArrayAdapter<String> cowsArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,cowArray);
            cowsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cowS.setAdapter(cowsArrayAdapter);
        }
    }

    private class MilkProductionDataAdditionThread extends AsyncTask<String,Integer,String>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AddMilkProductionActivity.this, "",Locale.getStringInLocale("loading_please_wait",AddMilkProductionActivity.this), true);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "at milkProductionDataAdditionThread");
            JSONObject jsonObject=new JSONObject();
            try
            {
                jsonObject.put("simCardSN",params[0]);
                jsonObject.put("cowName",params[1]);
                jsonObject.put("cowEarTagNumber",params[2]);
                jsonObject.put("time",params[3]);
                jsonObject.put("quantity",params[4]);
                jsonObject.put("quantityType",params[5]);
                jsonObject.put("date", params[6]);
                /*jsonObject.put("noMilkingTimes",params[7]);
                jsonObject.put("calfSuckling",params[8]);*/
                String result=DataHandler.sendDataToServer(AddMilkProductionActivity.this, jsonObject.toString(),DataHandler.FARMER_ADD_MILK_PRODUCTION_URL, true);
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
            if(result==null) {
                Toast.makeText(AddMilkProductionActivity.this,"server error",Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.ACKNOWLEDGE_OK)) {
                Toast.makeText(AddMilkProductionActivity.this,Locale.getStringInLocale("information_successfully_sent_to_server", AddMilkProductionActivity.this), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddMilkProductionActivity.this, MilkProductionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            else if(result.equals(DataHandler.DATA_ERROR)) {
                Toast.makeText(AddMilkProductionActivity.this, Locale.getStringInLocale("production_for_time_already_exists", AddMilkProductionActivity.this), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class CowIdentifierThread extends AsyncTask<String,Integer,String>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AddMilkProductionActivity.this, "",Locale.getStringInLocale("loading_please_wait", AddMilkProductionActivity.this), true);
        }

        @Override
        protected String doInBackground(String... params)
        {
            JSONObject jsonObject=new JSONObject();
            try
            {
                jsonObject.put("simCardSN",params[0]);
                jsonObject.put("cowSex", Cow.SEX_FEMALE);
                String result=DataHandler.sendDataToServer(AddMilkProductionActivity.this, jsonObject.toString(),DataHandler.FARMER_FETCH_COW_IDENTIFIERS_URL, true);
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
            if(result == null ){
                Toast.makeText(AddMilkProductionActivity.this,"server error",Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_GENERIC_FAILURE)){
                Toast.makeText(AddMilkProductionActivity.this, Locale.getStringInLocale("generic_sms_error", AddMilkProductionActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_NO_SERVICE)){
                Toast.makeText(AddMilkProductionActivity.this, Locale.getStringInLocale("no_service", AddMilkProductionActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RADIO_OFF)){
                Toast.makeText(AddMilkProductionActivity.this, Locale.getStringInLocale("radio_off", AddMilkProductionActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RESULT_CANCELLED)){
                Toast.makeText(AddMilkProductionActivity.this, Locale.getStringInLocale("server_not_receive_sms", AddMilkProductionActivity.this), Toast.LENGTH_LONG).show();
            }
            else{
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
                        Toast.makeText(AddMilkProductionActivity.this,"no cows fetched",Toast.LENGTH_LONG).show();
                    }
                    AddMilkProductionActivity.this.cowNameArray =cowArray;
                    AddMilkProductionActivity.this.cowEarTagNumberArray=earTagArray;
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
}
