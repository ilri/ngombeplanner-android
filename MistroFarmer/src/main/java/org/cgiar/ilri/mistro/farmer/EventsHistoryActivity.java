package org.cgiar.ilri.mistro.farmer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import org.cgiar.ilri.mistro.farmer.backend.DataHandler;
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

public class EventsHistoryActivity extends SherlockActivity {

    private String localeCode;
    private static final String TAG="EventsHistoryActivity";

    private TableLayout eventsHistoryTL;
    private TextView dateTV;
    private TextView cowNameTV;
    private TextView eventTV;
    private DisplayMetrics metrics;

    private String noDataReceived;
    private String serverError;
    private List<String> eventHistoryIDs;
    private String todayText;
    private String yesterdayText;
    private String loadingPleaseWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_history);

        localeCode = "en";
        eventHistoryIDs = new ArrayList<String>();
        metrics=new DisplayMetrics();

        eventsHistoryTL = (TableLayout)findViewById(R.id.events_history_tl);
        dateTV = (TextView)findViewById(R.id.date_tv);
        cowNameTV = (TextView)findViewById(R.id.cow_name_tv);
        eventTV = (TextView)findViewById(R.id.event_tv);

        initTextViews(localeCode);
        fetchEventsHistory();
    }

    private void initTextViews(String localeCode) {
        if(localeCode.equals("en")) {
            setTitle(R.string.past_events_en);
            dateTV.setText(R.string.date_en);
            cowNameTV.setText(R.string.cow_en);
            eventTV.setText(R.string.event_en);
            noDataReceived = getResources().getString(R.string.no_data_received_en);
            serverError = getResources().getString(R.string.problem_connecting_to_server_en);
            todayText=getResources().getString(R.string.today_en);
            yesterdayText=getResources().getString(R.string.yesterday_en);
            loadingPleaseWait=getResources().getString(R.string.loading_please_wait_en);
        }
    }

    private void fetchEventsHistory() {
        if(DataHandler.checkNetworkConnection(this, localeCode)) {
            TelephonyManager telephonyManager=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            CowEventHistoryThread cowEventHistoryThread =new CowEventHistoryThread();
            if(eventHistoryIDs.size() == 0){//first time
                cowEventHistoryThread.execute(telephonyManager.getSimSerialNumber(), "-1");
            }
            else {
                cowEventHistoryThread.execute(telephonyManager.getSimSerialNumber(), eventHistoryIDs.get(eventHistoryIDs.size()-1));
            }


        }
    }

    private class CowEventHistoryThread extends AsyncTask<String, Integer, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EventsHistoryActivity.this, "",loadingPleaseWait, true);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("simCardSN",params[0]);
                jsonObject.put("fromID",params[1]);
                result = DataHandler.sendDataToServer(jsonObject.toString(),DataHandler.FARMER_FETCH_COW_EVENTS_HISTORY_URL);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result == null) {
                Toast.makeText(EventsHistoryActivity.this, serverError, Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.NO_DATA)) {
                Toast.makeText(EventsHistoryActivity.this, noDataReceived, Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray historyArray = jsonObject.getJSONArray("history");
                    addTableRows(historyArray);
                }
                catch (JSONException e) {
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
                String dateText=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.MONTH))+"/"+calendar.get(Calendar.YEAR);
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

                final  TextView event=generateTextView(jsonObject.getString("event_name"),5342+jsonObject.getInt("id")+554,tableRowHeight,tableTextSize);
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
