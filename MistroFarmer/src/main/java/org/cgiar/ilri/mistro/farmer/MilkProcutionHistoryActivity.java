package org.cgiar.ilri.mistro.farmer;

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
import java.util.Date;
import java.util.List;

public class MilkProcutionHistoryActivity extends SherlockActivity
{
    private static final String TAG="MIlkProductionHistoryActivity";
    private String localeCode;
    private TextView dateTV;
    private TextView cowNameTV;
    private TextView timeTV;
    private  TextView quantityTV;
    private List<String> productionHistoryIDs;
    private String noDataWarning;
    private DisplayMetrics metrics;
    private TableLayout productionHistoryTL;
    private String[] times;
    private String todayText;
    private String yesterdayText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk_production_history);

        localeCode="en";
        productionHistoryIDs=new ArrayList<String>();
        metrics=new DisplayMetrics();

        dateTV=(TextView)findViewById(R.id.date_tv);
        cowNameTV=(TextView)findViewById(R.id.cow_name_tv);
        timeTV=(TextView)findViewById(R.id.time_tv);
        quantityTV=(TextView)findViewById(R.id.quantity_tv);
        productionHistoryTL=(TableLayout)findViewById(R.id.production_history_tl);

        initTextInViews(localeCode);
        fetchProductionHistory();
    }

    private void initTextInViews(String localeCode)
    {
        if(localeCode.equals("en"))
        {
            dateTV.setText(R.string.date_en);
            cowNameTV.setText(R.string.cow_en);
            timeTV.setText(R.string.time_en);
            quantityTV.setText(R.string.quantity_en);
            noDataWarning=getResources().getString(R.string.no_data_received_en);
            times=getResources().getStringArray(R.array.milking_times_en);
            todayText=getResources().getString(R.string.today_en);
            yesterdayText=getResources().getString(R.string.yesterday_en);
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

    private class ProductionHistoryThread extends AsyncTask<String,Integer,String>
    {

        @Override
        protected String doInBackground(String... params)
        {
            JSONObject jsonObject=new JSONObject();
            try
            {
                jsonObject.put("simCardSN",params[0]);
                jsonObject.put("fromID",params[1]);
                String result=DataHandler.sendDataToServer(jsonObject.toString(),DataHandler.FARMER_FETCH_MILK_PRODUCTION_HISTORY_URL);
                Log.d(TAG,"result gotten from server = "+result);
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
            if(result==null)
            {
                Toast.makeText(MilkProcutionHistoryActivity.this,"server error",Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.NO_DATA))
            {
                Toast.makeText(MilkProcutionHistoryActivity.this,noDataWarning,Toast.LENGTH_LONG).show();
            }
            else
            {
                try
                {
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray historyArray=jsonObject.getJSONArray("history");
                    addHistoryTableRows(historyArray);
                }
                catch (JSONException e)
                {
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
                    tableRowHeight=20;//initially 15
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
                String dateText=jsonObject.getString("date");
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

                //TODO: check if name is null and use ear tag number
                final TextView name=generateTextView(jsonObject.getString("name"),3424+jsonObject.getInt("id")+432,tableRowHeight,tableTextSize);
                tableRow.addView(name);

                final View rowSeparator2=generateRowSeparator(3424+jsonObject.getInt("id")+3432,tableRowHeight);
                tableRow.addView(rowSeparator2);

                final  TextView time=generateTextView(times[jsonObject.getInt("time")],3424+jsonObject.getInt("id")+554,tableRowHeight,tableTextSize);
                tableRow.addView(time);

                final View rowSeparator3=generateRowSeparator(3424+jsonObject.getInt("id")+3532,tableRowHeight);
                tableRow.addView(rowSeparator3);

                final TextView quantity=generateTextView(jsonObject.getString("quantity"),3424+jsonObject.getInt("id")+564,tableRowHeight,tableTextSize);
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
}
