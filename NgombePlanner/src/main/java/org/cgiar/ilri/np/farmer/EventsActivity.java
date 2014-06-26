package org.cgiar.ilri.np.farmer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.np.farmer.backend.DataHandler;
import org.cgiar.ilri.np.farmer.backend.Locale;
import org.json.JSONException;
import org.json.JSONObject;

public class EventsActivity extends SherlockActivity implements NPActivity, View.OnClickListener
{
    private String TAG = "EventsActivity";

    private Menu menu;
    private Button addEventB;
    private Button eventHistoryB;
    private Button backB;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        addEventB=(Button)findViewById(R.id.add_event_b);
        addEventB.setOnClickListener(this);
        eventHistoryB=(Button)findViewById(R.id.event_history_b);
        eventHistoryB.setOnClickListener(this);
        backB = (Button)findViewById(R.id.back_b);
        backB.setOnClickListener(this);

        initTextInViews();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.events, menu);
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
        setTitle(Locale.getStringInLocale("events",this));
        addEventB.setText(Locale.getStringInLocale("add_an_event",this));
        eventHistoryB.setText(Locale.getStringInLocale("past_events",this));
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
        if(view==addEventB)
        {
            Intent intent=new Intent(this,AddEventActivity.class);
            startActivity(intent);
        }
        else if(view == eventHistoryB)
        {
            Intent intent=new Intent(this,EventsHistoryActivity.class);
            startActivity(intent);
        }
        else if(view == backB){
            SendCachedDataThread sendCachedDataThread = new SendCachedDataThread();
            sendCachedDataThread.execute(1);

            Intent intent = new Intent(this, MainMenu.class);
            intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_FARMER);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    private class SendCachedDataThread extends AsyncTask<Integer, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(Integer... params) {
            String result = DataHandler.sendCachedRequests(EventsActivity.this, true);//send cached data and receive updated farmer data
            if(result != null && !result.equals(DataHandler.CODE_USER_NOT_AUTHENTICATED)){//no data fetched for this farmer
                try {//try converting the response into a jsonobject. It might not work if the DataHandler returns a response code
                    Log.d(TAG, "response is " + result);
                    DataHandler.saveFarmerData(EventsActivity.this, new JSONObject(result));
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
                Toast.makeText(EventsActivity.this, Locale.getStringInLocale("information_successfully_sent_to_server", EventsActivity.this), Toast.LENGTH_LONG).show();
            }
        }
    }
}
