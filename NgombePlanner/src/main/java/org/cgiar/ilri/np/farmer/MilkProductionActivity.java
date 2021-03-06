package org.cgiar.ilri.np.farmer;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.np.farmer.backend.DataHandler;
import org.cgiar.ilri.np.farmer.backend.Locale;
import org.cgiar.ilri.np.farmer.carrier.Cow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MilkProductionActivity extends SherlockActivity implements NPActivity, View.OnClickListener, DatePickerDialog.OnDateSetListener
{
    private final String dateFormat="dd/MM/yyyy";

    private static final String TAG="MilkProductionActivity";
    private Menu menu;
    private Button addProductionB;
    private Button productionHistoryB;
    private Button backB;
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
    private TextView quantityTypeTV;
    private Spinner quantityTypeS;
    private String[] quantityTypes;
    private TextView dateTV;
    private EditText dateET;
    private DatePickerDialog datePickerDialog;
    private TextView noMilkingTV;
    private EditText noMilkingET;
    private TextView calfSucklingTV;
    private String[] calfSucklingTypes;
    private Spinner calfSucklingS;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk_production);
        //DataHandler.requestPermissionToUseSMS(this);

        addProductionB=(Button)this.findViewById(R.id.add_production_b);
        addProductionB.setOnClickListener(this);
        productionHistoryB =(Button)this.findViewById(R.id.production_history_b);
        productionHistoryB.setOnClickListener(this);
        backB = (Button)this.findViewById(R.id.back_b);
        backB.setOnClickListener(this);
        addMilkProductionDialog=new Dialog(this);
        addMilkProductionDialog.setContentView(R.layout.dialog_add_milk_production);
        cowTV=(TextView)addMilkProductionDialog.findViewById(R.id.cow_tv);
        cowS=(Spinner)addMilkProductionDialog.findViewById(R.id.cow_s);
        dateTV=(TextView)addMilkProductionDialog.findViewById(R.id.date_tv);
        dateET=(EditText)addMilkProductionDialog.findViewById(R.id.date_et);
        dateET.setOnClickListener(this);
        timeTV=(TextView)addMilkProductionDialog.findViewById(R.id.time_tv);
        timeS=(Spinner)addMilkProductionDialog.findViewById(R.id.time_s);
        quantityTV=(TextView)addMilkProductionDialog.findViewById(R.id.quantity_tv);
        quantityET=(EditText)addMilkProductionDialog.findViewById(R.id.quantity_et);
        quantityTypeTV=(TextView)addMilkProductionDialog.findViewById(R.id.quantity_type_tv);
        quantityTypeS=(Spinner)addMilkProductionDialog.findViewById(R.id.quantity_type_s);
        addMilkProductionAddB=(Button)addMilkProductionDialog.findViewById(R.id.dialog_add_milk_add_b);
        noMilkingTV = (TextView)addMilkProductionDialog.findViewById(R.id.no_milking_tv);
        noMilkingET = (EditText)addMilkProductionDialog.findViewById(R.id.no_milking_et);
        calfSucklingTV = (TextView)addMilkProductionDialog.findViewById(R.id.calf_suckling_tv);
        calfSucklingS = (Spinner)addMilkProductionDialog.findViewById(R.id.calf_suckling_s);
        addMilkProductionAddB.setOnClickListener(this);

        initTextInViews();
        //fetchCowIdentifiers();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.milk_production, menu);
        this.menu = menu;
        initMenuText();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if(Language.processLanguageMenuItemSelected(this, this, item)){
            return true;
        }
        else if(item.getItemId() == R.id.action_back_main_menu) {
            Intent intent = new Intent(this, MainMenu.class);
            intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_FARMER);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void initTextInViews()
    {
        this.setTitle(Locale.getStringInLocale("milk_production",this));
        addProductionB.setText(Locale.getStringInLocale("add_production",this));
        productionHistoryB.setText(Locale.getStringInLocale("production_history",this));
        addMilkProductionDialog.setTitle(Locale.getStringInLocale("add_production",this));
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
        quantityETEmptyWarning=Locale.getStringInLocale("enter_quantity_of_milk_produced",this);
        infoSuccessfullySent=Locale.getStringInLocale("information_successfully_sent_to_server",this);
        problemInData=Locale.getStringInLocale("production_for_time_already_exists",this);
        loadingPleaseWait = Locale.getStringInLocale("loading_please_wait",this);
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


        noMilkingTV.setText(Locale.getStringInLocale("no_times_milked_in_a_day",this));
        calfSucklingTV.setText(Locale.getStringInLocale("calf_suckling",this));

        calfSucklingTypes = Locale.getArrayInLocale("calf_suckling_types",this);
        ArrayAdapter<String> calfSucklingTypesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,calfSucklingTypes);
        calfSucklingTypesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calfSucklingS.setAdapter(calfSucklingTypesArrayAdapter);
        backB.setText(Locale.getStringInLocale("back", this));

        initMenuText();
    }

    private void initMenuText(){
        if(this.menu != null){
            MenuItem mainMenuMI = menu.findItem(R.id.action_back_main_menu);
            mainMenuMI.setTitle(Locale.getStringInLocale("back_to_main_menu", this));
        }
    }

    @Override
    public void onClick(View view)
    {
        if(view==addProductionB)
        {
            //addMilkProductionDialog.show();
            Intent intent = new Intent(MilkProductionActivity.this, AddMilkProductionActivity.class);
            startActivity(intent);
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
        else if(view==dateET) {
            dateETClicked();
        }
        else if(view==backB){
            SendCachedDataThread sendCachedDataThread = new SendCachedDataThread();
            sendCachedDataThread.execute(1);

            Intent intent = new Intent(this, MainMenu.class);
            intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_FARMER);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    private void dateETClicked()
    {
        Date date=new Date();
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
        if(!validateDate()){
            dateET.setText("");
        }
    }

    private void fetchCowIdentifiers()
    {
        CowIdentifierThread cowIdentifierThread=new CowIdentifierThread();
        TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        cowIdentifierThread.execute(telephonyManager.getSimSerialNumber());
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
                String result=DataHandler.sendDataToServer(MilkProductionActivity.this, jsonObject.toString(),DataHandler.FARMER_FETCH_COW_IDENTIFIERS_URL, true);
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
                Toast.makeText(MilkProductionActivity.this,"server error",Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_GENERIC_FAILURE)){
                Toast.makeText(MilkProductionActivity.this, Locale.getStringInLocale("generic_sms_error", MilkProductionActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_NO_SERVICE)){
                Toast.makeText(MilkProductionActivity.this, Locale.getStringInLocale("no_service", MilkProductionActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RADIO_OFF)){
                Toast.makeText(MilkProductionActivity.this, Locale.getStringInLocale("radio_off", MilkProductionActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RESULT_CANCELLED)){
                Toast.makeText(MilkProductionActivity.this, Locale.getStringInLocale("server_not_receive_sms", MilkProductionActivity.this), Toast.LENGTH_LONG).show();
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
                DataHandler.setSharedPreference(MilkProductionActivity.this, DataHandler.SP_KEY_MILK_QUANTITY_TYPE, String.valueOf(quantityTypeS.getSelectedItemPosition()));
                quantityType = quantityTypesInEN[quantityTypeS.getSelectedItemPosition()];
            }
            String milkingTime = "";
            if(milkingTimesInEN.length > 0){
                milkingTime = milkingTimesInEN[timeS.getSelectedItemPosition()];
            }

            String[] calfSucklingTypesInEN = Locale.getArrayInLocale("calf_suckling_types",this,Locale.LOCALE_ENGLISH);
            String calfSucklingType = calfSucklingTypesInEN[calfSucklingS.getSelectedItemPosition()];
            milkProductionDataAdditionThread.execute(telephonyManager.getSimSerialNumber(),cowNameArray[cowS.getSelectedItemPosition()],cowEarTagNumberArray[cowS.getSelectedItemPosition()],milkingTime,quantityET.getText().toString(),quantityType,dateET.getText().toString(),noMilkingET.getText().toString(),calfSucklingType);
        }
    }

    private boolean validateInput()
    {
        String[] quantityTypesInEN = Locale.getArrayInLocale("quantity_types",this,Locale.LOCALE_ENGLISH);
        String quantityType = quantityTypesInEN[quantityTypeS.getSelectedItemPosition()];
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
        else if(dateET.getText().toString()==null||dateET.getText().toString().length()==0)
        {
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
        if(noMilkingET.getText().toString()==null || noMilkingET.getText().toString().trim().length()==0){
            Toast.makeText(this, Locale.getStringInLocale("enter_number_times_cow_milked",this),Toast.LENGTH_LONG).show();
            return false;
        }
        else if(Integer.parseInt(noMilkingET.getText().toString())>10){
            Toast.makeText(this, Locale.getStringInLocale("milking_times_too_much",this), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean validateDate() {
        try
        {
            Date dateEntered=new SimpleDateFormat(dateFormat, java.util.Locale.ENGLISH).parse(dateET.getText().toString());
            Date today=new Date();
            long milisecondDifference = today.getTime() - dateEntered.getTime();
            long days = milisecondDifference / 86400000;
            if((today.getTime()-dateEntered.getTime())<0)
            {
                Toast.makeText(this,Locale.getStringInLocale("date_in_future",this),Toast.LENGTH_LONG).show();
                return false;
            }
            else if(days > 30) {//more than one month
                Toast.makeText(this,Locale.getStringInLocale("milk_data_too_old",this),Toast.LENGTH_LONG).show();
                return false;
            }
            else {
                return true;
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return false;
        }
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
                jsonObject.put("quantityType",params[5]);
                jsonObject.put("date", params[6]);
                jsonObject.put("noMilkingTimes",params[7]);
                jsonObject.put("calfSuckling",params[8]);
                String result=DataHandler.sendDataToServer(MilkProductionActivity.this, jsonObject.toString(),DataHandler.FARMER_ADD_MILK_PRODUCTION_URL, true);
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
                addMilkProductionDialog.dismiss();
                Toast.makeText(MilkProductionActivity.this,infoSuccessfullySent,Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.DATA_ERROR))
            {
                Toast.makeText(MilkProductionActivity.this,problemInData,Toast.LENGTH_LONG).show();
            }
        }
    }

    private class SendCachedDataThread extends AsyncTask<Integer, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(Integer... params) {
            String result = DataHandler.sendCachedRequests(MilkProductionActivity.this, true);//send cached data and receive updated farmer data
            if(result != null && !result.equals(DataHandler.CODE_USER_NOT_AUTHENTICATED)){//no data fetched for this farmer
                try {//try converting the response into a jsonobject. It might not work if the DataHandler returns a response code
                    Log.d(TAG, "response is " + result);
                    DataHandler.saveFarmerData(MilkProductionActivity.this, new JSONObject(result));
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if(result == true){
                Toast.makeText(MilkProductionActivity.this, Locale.getStringInLocale("information_successfully_sent_to_server", MilkProductionActivity.this), Toast.LENGTH_LONG).show();
            }
        }
    }
}
