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
import org.cgiar.ilri.mistro.farmer.carrier.Event;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class EventsHistoryActivity extends SherlockActivity implements View.OnClickListener {
    private static final String TAG="EventsHistoryActivity";

    private Menu menu;
    private TableLayout eventsHistoryTL;
    private TextView dateTV;
    private TextView cowNameTV;
    private TextView eventTV;
    private DisplayMetrics metrics;
    private Button backB;

    private String noDataReceived;
    private String serverError;
    private List<String> eventHistoryIDs;
    private String todayText;
    private String yesterdayText;
    private String loadingPleaseWait;
    private String[] eventTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_history);
        DataHandler.requestPermissionToUseSMS(this);

        eventHistoryIDs = new ArrayList<String>();
        metrics=new DisplayMetrics();

        eventsHistoryTL = (TableLayout)findViewById(R.id.events_history_tl);
        dateTV = (TextView)findViewById(R.id.date_tv);
        cowNameTV = (TextView)findViewById(R.id.cow_name_tv);
        eventTV = (TextView)findViewById(R.id.event_tv);
        backB = (Button)findViewById(R.id.back_b);
        backB.setOnClickListener(this);

        initTextInViews();
        fetchEventsHistory();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.events_history, menu);
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

    private void initTextInViews() {
        setTitle(Locale.getStringInLocale("past_events",this));
        dateTV.setText(Locale.getStringInLocale("date",this));
        cowNameTV.setText(Locale.getStringInLocale("cow",this));
        eventTV.setText(Locale.getStringInLocale("event",this));
        noDataReceived = Locale.getStringInLocale("no_data_received",this);
        serverError = Locale.getStringInLocale("problem_connecting_to_server",this);
        todayText=Locale.getStringInLocale("today",this);
        yesterdayText=Locale.getStringInLocale("yesterday",this);
        loadingPleaseWait=Locale.getStringInLocale("loading_please_wait",this);
        eventTypes = Locale.getArrayInLocale("cow_event_types",this);
        if(eventTypes == null) {
            eventTypes = new String[1];
            eventTypes[0] = "";
        }
        backB.setText(Locale.getStringInLocale("back", this));

        initMenuText();
    }

    private void initMenuText(){
        if(this.menu != null){
            MenuItem mainMenuMI = menu.findItem(R.id.action_back_main_menu);
            mainMenuMI.setTitle(Locale.getStringInLocale("back_to_main_menu", this));
        }
    }

    private void fetchEventsHistory() {
        TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        CowEventHistoryThread cowEventHistoryThread =new CowEventHistoryThread();
        if(eventHistoryIDs.size() == 0){//first time
            cowEventHistoryThread.execute(telephonyManager.getSimSerialNumber(), "-1");
        }
        else {
            cowEventHistoryThread.execute(telephonyManager.getSimSerialNumber(), eventHistoryIDs.get(eventHistoryIDs.size()-1));
        }
    }

    @Override
    public void onClick(View view) {
        if(view == backB){
            Intent intent = new Intent(this, EventsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    private class CowEventHistoryThread extends AsyncTask<String, Integer, Farmer> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EventsHistoryActivity.this, "",loadingPleaseWait, true);
        }

        @Override
        protected Farmer doInBackground(String... params) {
            /*JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("simCardSN",params[0]);
                jsonObject.put("fromID",params[1]);
                result = DataHandler.sendDataToServer(EventsHistoryActivity.this, jsonObject.toString(),DataHandler.FARMER_FETCH_COW_EVENTS_HISTORY_URL, true);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }*/
            String result = DataHandler.sendCachedRequests(EventsHistoryActivity.this, true);//send cached data and receive updated farmer data
            if(result != null && !result.equals(DataHandler.CODE_USER_NOT_AUTHENTICATED)){//no data fetched for this farmer
                try {//try converting the response into a jsonobject. It might not work if the DataHandler returns a response code
                    Log.d(TAG, "response is "+result);
                    DataHandler.saveFarmerData(EventsHistoryActivity.this, new JSONObject(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return DataHandler.getFarmerData(EventsHistoryActivity.this);
        }

        @Override
        protected void onPostExecute(Farmer farmer) {
            super.onPostExecute(farmer);
            progressDialog.dismiss();

            if(farmer == null) {
                Toast.makeText(EventsHistoryActivity.this, Locale.getStringInLocale("unable_to_fetch_cached_data", EventsHistoryActivity.this), Toast.LENGTH_LONG).show();
            }

            /*else if(result.equals(DataHandler.SMS_ERROR_GENERIC_FAILURE)){
                Toast.makeText(EventsHistoryActivity.this, Locale.getStringInLocale("generic_sms_error", EventsHistoryActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_NO_SERVICE)){
                Toast.makeText(EventsHistoryActivity.this, Locale.getStringInLocale("no_service", EventsHistoryActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RADIO_OFF)){
                Toast.makeText(EventsHistoryActivity.this, Locale.getStringInLocale("radio_off", EventsHistoryActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RESULT_CANCELLED)){
                Toast.makeText(EventsHistoryActivity.this, Locale.getStringInLocale("server_not_receive_sms", EventsHistoryActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.NO_DATA)) {
                Toast.makeText(EventsHistoryActivity.this, noDataReceived, Toast.LENGTH_LONG).show();
            }*/
            else {
                /*try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray historyArray = jsonObject.getJSONArray("history");
                    addTableRows(historyArray);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }*/
                try{
                    List<Cow> allCows = farmer.getCows();
                    JSONArray historyArray = new JSONArray();
                    for(int cowIndex = 0; cowIndex < allCows.size(); cowIndex++){
                        //append all events for this cow to the UI
                        List<Event> cowEvents = allCows.get(cowIndex).getEvents();
                        for(int eventIndex = 0; eventIndex < cowEvents.size(); eventIndex++){
                            //cow_name, ear_tag_number, event.*,
                            JSONObject currEventJSON = new JSONObject();
                            currEventJSON.put("cow_name", allCows.get(cowIndex).getName());
                            currEventJSON.put("ear_tag_number", allCows.get(cowIndex).getEarTagNumber());
                            currEventJSON.put("id", cowEvents.get(eventIndex).getId());
                            currEventJSON.put("remarks", cowEvents.get(eventIndex).getRemarks());
                            currEventJSON.put("event_date", cowEvents.get(eventIndex).getEventDate());
                            currEventJSON.put("event_name", cowEvents.get(eventIndex).getType());
                            currEventJSON.put("birth_type", cowEvents.get(eventIndex).getBirthType());
                            currEventJSON.put("parent_cow_event", cowEvents.get(eventIndex).getParentCowEventID());
                            currEventJSON.put("servicing_days", cowEvents.get(eventIndex).getServicingDays());
                            historyArray.put(currEventJSON);
                        }
                    }

                    addTableRows(historyArray);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void addTableRows(JSONArray historyArray) {
        for (int i = 0; i < historyArray.length(); i++) {
            try {
                Log.d(TAG, "called");
                JSONObject jsonObject=historyArray.getJSONObject(i);
                eventHistoryIDs.add(jsonObject.getString("id"));
                final TableRow tableRow=new TableRow(this);
                tableRow.setId(5342+Integer.parseInt(jsonObject.getString("id")));
                int tableRowHeight=0;
                int tableTextSideMargin=0;//4dp
                int tableTextSize=0;//14dp
                if(metrics.densityDpi== DisplayMetrics.DENSITY_XHIGH) {
                    tableRowHeight=58;//initially 30
                    tableTextSideMargin=14;//initially 6
                    tableTextSize=16;//initially 21
                }
                else if(metrics.densityDpi==DisplayMetrics.DENSITY_HIGH) {
                    tableRowHeight=44;//initially 30
                    tableTextSideMargin=14;//initially 6
                    tableTextSize=16;//initially 21
                }
                else if(metrics.densityDpi==DisplayMetrics.DENSITY_MEDIUM) {
                    tableRowHeight=27;//initially 20
                    tableTextSideMargin=10;//initially 4
                    tableTextSize=15;//initially 14
                }
                else if(metrics.densityDpi==DisplayMetrics.DENSITY_LOW) {
                    tableRowHeight=20;//initially 15
                    tableTextSideMargin=6;//initially 3
                    tableTextSize=15;//initially 11
                }
                else {
                    tableRowHeight=58;//initially 30
                    tableTextSideMargin=14;//initially 6
                    tableTextSize=16;//initially 21
                }
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,tableRowHeight));

                SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
                Date eventDate=dateFormat.parse(jsonObject.getString("event_date"));
                Date today=new Date();
                long dateDifference=(today.getTime()-eventDate.getTime())/86400000;

                Calendar calendar=new GregorianCalendar();
                calendar.setTime(eventDate);
                String dateText=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR);
                if(dateDifference<1) {
                    dateText=todayText;
                }
                else if(dateDifference>=1&&dateDifference<2) {
                    dateText=yesterdayText;
                }

                final TextView date=generateTextView(dateText,5342+jsonObject.getInt("id")+332,tableRowHeight,tableTextSize);
                tableRow.addView(date);

                final View rowSeparator1=generateRowSeparator(5342+jsonObject.getInt("id")+3322,tableRowHeight);
                tableRow.addView(rowSeparator1);

                String nameText=jsonObject.getString("cow_name");
                if(nameText==null||nameText.equals("")) {
                    nameText=jsonObject.getString("ear_tag_number");
                }
                final TextView name=generateTextView(nameText,5342+jsonObject.getInt("id")+432,tableRowHeight,tableTextSize);
                tableRow.addView(name);

                final View rowSeparator2=generateRowSeparator(5342+jsonObject.getInt("id")+3432,tableRowHeight);
                tableRow.addView(rowSeparator2);

                String[] eventTypesInEN = Locale.getArrayInLocale("cow_event_types",this,Locale.LOCALE_ENGLISH);
                String eventTypeString = "";
                int index = -1;
                for(int j = 0; j< eventTypesInEN.length; j++) {
                    if(eventTypesInEN[j].equals(jsonObject.getString("event_name"))) {
                        index = j;
                    }
                }
                if(eventTypesInEN.length == eventTypes.length && index != -1) {
                    eventTypeString = eventTypes[index];
                }
                final  TextView event=generateTextView(eventTypeString,5342+jsonObject.getInt("id")+554,tableRowHeight,tableTextSize);
                tableRow.addView(event);

                eventsHistoryTL.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,tableRowHeight));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private TextView generateTextView(String text, int id,int tableRowHeight,int tableTextSize) {
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

    private View generateRowSeparator(int id,int tableRowHeight) {
        final View rowSeperator=new View(this);
        rowSeperator.setId(id);
        rowSeperator.setLayoutParams(new TableRow.LayoutParams(1,tableRowHeight));
        rowSeperator.setBackgroundColor(getResources().getColor(R.color.pressed_mistro));
        return rowSeperator;
    }
    
}
