package org.cgiar.ilri.mistro.farmer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.DataHandler;
import org.cgiar.ilri.mistro.farmer.backend.Locale;
import org.cgiar.ilri.mistro.farmer.carrier.Cow;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.carrier.MilkProduction;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MilkProcutionHistoryActivity extends SherlockActivity implements View.OnClickListener
{
    private static final String TAG="MIlkProductionHistoryActivity";
    private Menu menu;
    private TextView dateTV;
    private TextView cowNameTV;
    private TextView timeTV;
    private TextView quantityTV;
    private List<String> productionHistoryIDs;
    private String noDataWarning;
    private DisplayMetrics metrics;
    private TableLayout productionHistoryTL;
    private TableLayout productionTotalTL;
    private TextView totalDateTV;
    private TextView totalCowNameTV;
    private TextView totalQuantityTV;
    private Button backB;

    private String[] times;
    private String todayText;
    private String yesterdayText;
    private String loadingPleaseWait;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk_production_history);
        DataHandler.requestPermissionToUseSMS(this);

        productionHistoryIDs=new ArrayList<String>();
        metrics=new DisplayMetrics();

        dateTV=(TextView)findViewById(R.id.date_tv);
        cowNameTV=(TextView)findViewById(R.id.cow_name_tv);
        timeTV=(TextView)findViewById(R.id.time_tv);
        quantityTV=(TextView)findViewById(R.id.quantity_tv);
        productionHistoryTL=(TableLayout)findViewById(R.id.production_history_tl);
        productionTotalTL = (TableLayout)findViewById(R.id.production_total_tl);
        totalDateTV = (TextView)findViewById(R.id.total_date_tv);
        totalCowNameTV = (TextView)findViewById(R.id.total_cow_name_tv);
        totalQuantityTV = (TextView)findViewById(R.id.total_quantity_tv);
        backB = (Button)findViewById(R.id.back_b);
        backB.setOnClickListener(this);

        initTextInViews();
        fetchProductionHistory();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.milk_procution_history, menu);
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
            return true;
        }
        else if(item.getItemId() == R.id.action_luhya) {
            Locale.switchLocale(Locale.LOCALE_LUHYA, this);
            initTextInViews();
            return true;
        }
        else if(item.getItemId() == R.id.action_kalenjin) {
            Locale.switchLocale(Locale.LOCALE_KALENJIN, this);
            initTextInViews();
            return true;
        }
        else if(item.getItemId() == R.id.action_back_main_menu) {
            Intent intent = new Intent(this, MainMenu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        return false;
    }

    private void initTextInViews()
    {
        dateTV.setText(Locale.getStringInLocale("date",this));
        cowNameTV.setText(Locale.getStringInLocale("cow",this));
        timeTV.setText(Locale.getStringInLocale("time",this));
        quantityTV.setText(Locale.getStringInLocale("quantity",this));

        totalDateTV.setText(Locale.getStringInLocale("date",this));
        totalCowNameTV.setText(Locale.getStringInLocale("cow",this));
        totalQuantityTV.setText(Locale.getStringInLocale("total", this));

        noDataWarning=Locale.getStringInLocale("no_data_received",this);
        times=Locale.getArrayInLocale("milking_times",this);
        if(times == null) {
            times = new String[1];
            times[0] = "";
        }
        todayText=Locale.getStringInLocale("today",this);
        yesterdayText=Locale.getStringInLocale("yesterday",this);
        loadingPleaseWait=Locale.getStringInLocale("loading_please_wait",this);
        backB.setText(Locale.getStringInLocale("back", this));
        initMenuText();
    }

    private void initMenuText(){
        if(this.menu != null){
            MenuItem mainMenuMI = menu.findItem(R.id.action_back_main_menu);
            mainMenuMI.setTitle(Locale.getStringInLocale("back_to_main_menu", this));
        }
    }

    private void fetchProductionHistory()
    {
        TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        ProductionHistoryThread productionHistoryThread=new ProductionHistoryThread();
        if(productionHistoryIDs.size()==0)
        {
            productionHistoryThread.execute(telephonyManager.getSimSerialNumber(),"-1");
        }
        else
        {
            productionHistoryThread.execute(telephonyManager.getSimSerialNumber(),productionHistoryIDs.get(productionHistoryIDs.size()-1));
        }
    }

    @Override
    public void onClick(View view) {
        if(view == backB){
            Intent intent = new Intent(this, MilkProductionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    private class ProductionHistoryThread extends AsyncTask<String,Integer,Farmer>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MilkProcutionHistoryActivity.this, "",loadingPleaseWait, true);
        }

        @Override
        protected Farmer doInBackground(String... params)
        {
            /*JSONObject jsonObject=new JSONObject();
            try
            {
                jsonObject.put("simCardSN",params[0]);
                jsonObject.put("fromID",params[1]);
                String result=DataHandler.sendDataToServer(MilkProcutionHistoryActivity.this, jsonObject.toString(),DataHandler.FARMER_FETCH_MILK_PRODUCTION_HISTORY_URL, true);
                Log.d(TAG,"result gotten from server = "+result);
                return result;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return null;*/


            String result = DataHandler.sendCachedRequests(MilkProcutionHistoryActivity.this, true);//send cached data and receive updated farmer data
            if(result != null && !result.equals(DataHandler.CODE_USER_NOT_AUTHENTICATED)){//no data fetched for this farmer
                try {//try converting the response into a jsonobject. It might not work if the DataHandler returns a response code
                    Log.d(TAG, "response is "+result);
                    DataHandler.saveFarmerData(MilkProcutionHistoryActivity.this, new JSONObject(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return DataHandler.getFarmerData(MilkProcutionHistoryActivity.this);
        }

        @Override
        protected void onPostExecute(Farmer farmer)
        {
            super.onPostExecute(farmer);
            progressDialog.dismiss();
            if(farmer==null)
            {
                Toast.makeText(MilkProcutionHistoryActivity.this,"server error",Toast.LENGTH_LONG).show();
            }
            /*else if(result.equals(DataHandler.SMS_ERROR_GENERIC_FAILURE)){
                Toast.makeText(MilkProcutionHistoryActivity.this, Locale.getStringInLocale("generic_sms_error", MilkProcutionHistoryActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_NO_SERVICE)){
                Toast.makeText(MilkProcutionHistoryActivity.this, Locale.getStringInLocale("no_service", MilkProcutionHistoryActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RADIO_OFF)){
                Toast.makeText(MilkProcutionHistoryActivity.this, Locale.getStringInLocale("radio_off", MilkProcutionHistoryActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RESULT_CANCELLED)){
                Toast.makeText(MilkProcutionHistoryActivity.this, Locale.getStringInLocale("server_not_receive_sms", MilkProcutionHistoryActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.NO_DATA))
            {
                Toast.makeText(MilkProcutionHistoryActivity.this,noDataWarning,Toast.LENGTH_LONG).show();
            }*/
            else
            {
                /*try
                {
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray historyArray=jsonObject.getJSONArray("history");
                    addTotalTableRows(historyArray);
                    addHistoryTableRows(historyArray);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }*/
                try{
                    List<Cow> allCows = farmer.getCows();
                    JSONArray historyArray = new JSONArray();
                    for(int cowIndex = 0; cowIndex < allCows.size(); cowIndex++){
                        //append all events for this cow to the UI
                        List<MilkProduction> cowMP = allCows.get(cowIndex).getMilkProduction();
                        for(int mpIndex = 0; mpIndex < cowMP.size(); mpIndex++){
                            JSONObject currMP = new JSONObject();
                            currMP.put("id", cowMP.get(mpIndex).getId());
                            currMP.put("date", cowMP.get(mpIndex).getDate());
                            currMP.put("time", cowMP.get(mpIndex).getTime());
                            currMP.put("name", allCows.get(cowIndex).getName());
                            currMP.put("ear_tag_number", allCows.get(cowIndex).getEarTagNumber());
                            currMP.put("quantity_type", cowMP.get(mpIndex).getQuantityType());
                            currMP.put("quantity", cowMP.get(mpIndex).getQuantity());

                            historyArray.put(currMP);
                        }
                    }

                    addTotalTableRows(historyArray);
                    addHistoryTableRows(historyArray);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private TextView generateTextView(String text, int id,int tableRowHeight,int tableTextSize)
    {
        TextView textView=new TextView(this);
        TableRow.LayoutParams dateLP=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,tableRowHeight);
        textView.setId(id);
        textView.setText(text);
        textView.setTextSize(tableTextSize);
        textView.setTextColor(getResources().getColor(R.color.text_input_color));
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(dateLP);
        return textView;
    }

    private View generateRowSeparator(int id,int tableRowHeight)
    {
        final View rowSeperator=new View(this);
        rowSeperator.setId(id);
        rowSeperator.setLayoutParams(new TableRow.LayoutParams(1,tableRowHeight));
        rowSeperator.setBackgroundColor(getResources().getColor(R.color.pressed_mistro));
        return rowSeperator;
    }

    private void addHistoryTableRows(JSONArray history)
    {
        for (int i=0;i<history.length();i++)
        {
            try
            {
                JSONObject jsonObject=history.getJSONObject(i);
                productionHistoryIDs.add(jsonObject.getString("id"));
                final TableRow tableRow=new TableRow(this);
                tableRow.setId(3424+Integer.parseInt(jsonObject.getString("id")));
                int tableRowHeight=0;
                int tableTextSideMargin=0;//4dp
                int tableTextSize=0;//14dp
                if(metrics.densityDpi==DisplayMetrics.DENSITY_XHIGH)
                {
                    tableRowHeight=58;//initially 30
                    tableTextSideMargin=14;//initially 6
                    tableTextSize=16;//initially 21
                }
                else if(metrics.densityDpi==DisplayMetrics.DENSITY_HIGH)
                {
                    tableRowHeight=44;//initially 30
                    tableTextSideMargin=14;//initially 6
                    tableTextSize=16;//initially 21
                }
                else if(metrics.densityDpi==DisplayMetrics.DENSITY_MEDIUM)
                {
                    tableRowHeight=27;//initially 20
                    tableTextSideMargin=10;//initially 4
                    tableTextSize=15;//initially 14
                }
                else if(metrics.densityDpi==DisplayMetrics.DENSITY_LOW)
                {
                    tableRowHeight=16;//initially 15
                    tableTextSideMargin=6;//initially 3
                    tableTextSize=15;//initially 11
                }
                else
                {
                    tableRowHeight=58;//initially 30
                    tableTextSideMargin=14;//initially 6
                    tableTextSize=16;//initially 21
                }
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,tableRowHeight));

                SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
                Date milkingDate=dateFormat.parse(jsonObject.getString("date"));
                Date today=new Date();
                long dateDifference=(today.getTime()-milkingDate.getTime())/86400000;

                Calendar calendar=new GregorianCalendar();
                calendar.setTime(milkingDate);
                String dateText=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR);
                if(dateDifference<1)
                {
                    dateText=todayText;
                }
                else if(dateDifference>=1&&dateDifference<2)
                {
                    dateText=yesterdayText;
                }

                final TextView date=generateTextView(dateText,3424+jsonObject.getInt("id")+332,tableRowHeight,tableTextSize);
                tableRow.addView(date);

                final View rowSeparator1=generateRowSeparator(3424+jsonObject.getInt("id")+3322,tableRowHeight);
                tableRow.addView(rowSeparator1);

                String[] milkingTimesInEN = Locale.getArrayInLocale("milking_times",this,Locale.LOCALE_ENGLISH);
                String milkingTime = "";
                for(int j = 0; j < milkingTimesInEN.length; j++) {
                    if(jsonObject.getString("time").equals(milkingTimesInEN[j])) {
                        if(milkingTimesInEN.length == times.length){
                            milkingTime = times[j];
                        }
                        break;
                    }
                }
                final  TextView time=generateTextView(milkingTime,3424+jsonObject.getInt("id")+554,tableRowHeight,tableTextSize);
                tableRow.addView(time);

                final View rowSeparator2=generateRowSeparator(3424+jsonObject.getInt("id")+3432,tableRowHeight);
                tableRow.addView(rowSeparator2);

                //check if name is null and use ear tag number
                String nameText=jsonObject.getString("name");
                if(nameText==null||nameText.equals(""))
                {
                    nameText=jsonObject.getString("ear_tag_number");
                }
                final TextView name=generateTextView(nameText,3424+jsonObject.getInt("id")+432,tableRowHeight,tableTextSize);
                tableRow.addView(name);

                final View rowSeparator3=generateRowSeparator(3424+jsonObject.getInt("id")+3532,tableRowHeight);
                tableRow.addView(rowSeparator3);

                String quantityType = jsonObject.getString("quantity_type");
                String quantityString = jsonObject.getString("quantity");
                String[] quantityTypesInEN = Locale.getArrayInLocale("quantity_types",this, Locale.LOCALE_ENGLISH);
                String[] quantityTypes = Locale.getArrayInLocale("quantity_types", this);
                if(quantityTypes != null && quantityTypesInEN.length == quantityTypes.length) {
                    for(int j = 0; j < quantityTypesInEN.length; j++) {
                        if(quantityType.equals(quantityTypesInEN[j])) {
                            quantityString = quantityString +" "+quantityTypes[j];
                            break;
                        }
                    }
                }
                final TextView quantity=generateTextView(quantityString,3424+jsonObject.getInt("id")+564,tableRowHeight,tableTextSize);
                tableRow.addView(quantity);

                productionHistoryTL.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,tableRowHeight));

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void addTotalTableRows(JSONArray history){
        //get totals for each cow

        // array["cowName-ear_tag_number"]{"name","ear_tag_number","startDateMilliseconds","endDateMilliseconds","total"}
        HashMap<String, String[]> cowMilkTotalArray = new HashMap<String, String[]>();

        for(int i = 0; i < history.length(); i++){
            try {
                JSONObject jsonObject=history.getJSONObject(i);
                String cowKey = jsonObject.getString("name")+"-"+jsonObject.getString("ear_tag_number");

                //2. Initialize the cow data
                //TODO: do conversions for other quantity types if they come up
                    /*
                    Since 1KG = 1L of milk you don't need to do any quantity conversions as of now
                     */
                String convertedQuantity = jsonObject.getString("quantity");

                SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
                Date milkingDate=dateFormat.parse(jsonObject.getString("date"));
                long milkingDateMilliseconds = milkingDate.getTime();

                //1. check if the cow is in the cowMilkTotalArray
                String[] currCowData = null;
                if(cowMilkTotalArray.containsKey(cowKey)){
                    currCowData = cowMilkTotalArray.get(cowKey);
                }

                if(currCowData == null){//no milk data from cow gotten until now in the loop
                    /*
                    Cow Data structure looks like:
                        {"name","ear_tag_number","startDateMilliseconds","endDateMilliseconds","total", "quantity_type"}
                     */

                    Log.d(TAG, "Adding first time data for "+cowKey);
                    String[] quantityTypesInEN = Locale.getArrayInLocale("quantity_types", this, Locale.LOCALE_ENGLISH);
                    String[] quantityTypes = Locale.getArrayInLocale("quantity_types", this);
                    //get translation of Litres in current locale
                    String litresInLocale = null;
                    for(int j = 0; j < quantityTypesInEN.length; j++){
                        if(quantityTypesInEN[j].equals("Litres")){
                            litresInLocale = quantityTypes[j];
                        }
                    }

                    currCowData = new String[]{
                            jsonObject.getString("name"),
                            jsonObject.getString("ear_tag_number"),
                            String.valueOf(milkingDateMilliseconds),
                            String.valueOf(milkingDateMilliseconds),
                            convertedQuantity,
                            litresInLocale
                    };
                }
                else{//Milk production from this cow already iterated through before i
                    //3. Increment quantity and dates
                    Log.d(TAG, "Updating data for "+cowKey);
                    currCowData[4] = String.valueOf(Integer.parseInt(currCowData[4])+Integer.parseInt(convertedQuantity));

                    //4. Check if current milk reading is in the date extremes (first date or last date)
                    Log.d(TAG, "start time string = "+currCowData[2]);
                    Log.d(TAG, "end time string = "+currCowData[3]);
                    long startDateMilliseconds = Long.parseLong(currCowData[2]);
                    long endDateMilliseconds = Long.parseLong(currCowData[3]);

                    Log.d(TAG, "Current milking Date Milliseconds = "+String.valueOf(milkingDateMilliseconds));
                    Log.d(TAG, "Start Date Milliseconds = "+String.valueOf(startDateMilliseconds));
                    Log.d(TAG, "End Date Milliseconds = "+String.valueOf(endDateMilliseconds));

                    if(milkingDateMilliseconds < startDateMilliseconds){
                        startDateMilliseconds = milkingDateMilliseconds;
                    }
                    else if(milkingDateMilliseconds > endDateMilliseconds){
                        endDateMilliseconds = milkingDateMilliseconds;
                    }

                    currCowData[2] = String.valueOf(startDateMilliseconds);
                    currCowData[3] = String.valueOf(endDateMilliseconds);
                }

                cowMilkTotalArray.put(cowKey, currCowData);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        //render cow milk totals data to the user
        int index = 0;
        for(Map.Entry currCow : cowMilkTotalArray.entrySet()){

            String[] currCowData = (String[]) currCow.getValue();

            final TableRow tableRow=new TableRow(this);
            tableRow.setId(1522+index);
            int tableRowHeight=0;
            int tableTextSideMargin=0;//4dp
            int tableTextSize=0;//14dp
            if(metrics.densityDpi==DisplayMetrics.DENSITY_XHIGH)
            {
                tableRowHeight=58;//initially 30
                tableTextSideMargin=14;//initially 6
                tableTextSize=16;//initially 21
            }
            else if(metrics.densityDpi==DisplayMetrics.DENSITY_HIGH)
            {
                tableRowHeight=44;//initially 30
                tableTextSideMargin=14;//initially 6
                tableTextSize=16;//initially 21
            }
            else if(metrics.densityDpi==DisplayMetrics.DENSITY_MEDIUM)
            {
                tableRowHeight=27;//initially 20
                tableTextSideMargin=10;//initially 4
                tableTextSize=15;//initially 14
            }
            else if(metrics.densityDpi==DisplayMetrics.DENSITY_LOW)
            {
                tableRowHeight=16;//initially 15
                tableTextSideMargin=6;//initially 3
                tableTextSize=15;//initially 11
            }
            else
            {
                tableRowHeight=58;//initially 30
                tableTextSideMargin=14;//initially 6
                tableTextSize=16;//initially 21
            }
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,tableRowHeight));

            Calendar startDate=new GregorianCalendar();
            startDate.setTime(new Date(Long.valueOf(currCowData[2])));
            Calendar endDate = new GregorianCalendar();
            endDate.setTime(new Date(Long.valueOf(currCowData[3])));

            String startDateText=String.valueOf(startDate.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(startDate.get(Calendar.MONTH)+1)+"/"+startDate.get(Calendar.YEAR);
            String endDateText=String.valueOf(endDate.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(endDate.get(Calendar.MONTH) + 1)+"/"+endDate.get(Calendar.YEAR);

            final TextView date=generateTextView(startDateText + " - " + endDateText, 3343 + index,tableRowHeight,tableTextSize);
            tableRow.addView(date);

            final View rowSeparator1=generateRowSeparator(index + 3322, tableRowHeight);
            tableRow.addView(rowSeparator1);

            String cowName = currCowData[0];
            if(cowName == null || cowName.length() == 0){//if cow doesnt have a name, use its ear tag number
                cowName = currCowData[1];
            }
            final TextView cow = generateTextView(cowName, 7343 + index,tableRowHeight,tableTextSize);
            tableRow.addView(cow);

            final View rowSeparator2=generateRowSeparator(index + 2322, tableRowHeight);
            tableRow.addView(rowSeparator2);

            final TextView quantity = generateTextView(currCowData[4]+" "+currCowData[5] ,922+index, tableRowHeight, tableTextSize);
            tableRow.addView(quantity);

            productionTotalTL.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,tableRowHeight));

            index++;
        }
    }
}
