package org.cgiar.ilri.mistro.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.Locale;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FarmerSelection extends SherlockActivity implements MistroActivity, View.OnClickListener {

    private static final String TAG = "FarmerSelection";
    public static final String KEY_ADMIN_DATA= "adminData";

    private Menu menu;

    private TextView selectFarmerTV;
    private Spinner selectFarmerS;
    private Button selectB;
    private Button backB;

    private List<Farmer> farmers;
    private JSONObject adminData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_selection);

        selectFarmerTV = (TextView)findViewById(R.id.select_farmer_tv);
        selectFarmerS = (Spinner)findViewById(R.id.select_farmer_s);

        selectB = (Button)findViewById(R.id.select_b);
        selectB.setOnClickListener(this);
        backB = (Button)findViewById(R.id.back_b);
        backB.setOnClickListener(this);

        initTextInViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle bundle=this.getIntent().getExtras();
        if(bundle != null){
            String adminJSONString = bundle.getString(KEY_ADMIN_DATA);
            try{
                adminData = new JSONObject(adminJSONString);
                JSONArray farmerData = adminData.getJSONArray("farmers");

                farmers = new ArrayList<Farmer>(farmerData.length());
                for(int i = 0; i < farmerData.length(); i++){
                    String ePersonnel = "";
                    if(!farmerData.getJSONObject(i).getString("extension_personnel_id").equals("NULL")){
                        ePersonnel = adminData.getString("name");
                    }
                    Farmer currFarmer = new Farmer(farmerData.getJSONObject(i), ePersonnel);
                    farmers.add(currFarmer);
                }

                List<String> farmerNames = new ArrayList<String>(farmers.size());
                for(int i = 0; i < farmers.size(); i++){
                    Farmer currFarmer = farmers.get(i);
                    farmerNames.add(currFarmer.getFullName());
                }

                ArrayAdapter<String> farmerArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, farmerNames);
                farmerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectFarmerS.setAdapter(farmerArrayAdapter);
            }
            catch (Exception e){

            }
        }
    }

    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.farmer_selection, menu);
        this.menu = menu;
        initMenuText();
        return true;
    }

    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        // Handle item selection
        if(Language.processLanguageMenuItemSelected(this, this, item)){
            return true;
        }
        else if(item.getItemId() == R.id.action_back_main_menu) {
            Intent intent = new Intent(this, MainMenu.class);
            intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_ADMIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void initTextInViews() {
        setTitle(Locale.getStringInLocale("select_farmer", this));

        selectFarmerTV.setText(Locale.getStringInLocale("select_farmer", this));
        selectB.setText(Locale.getStringInLocale("select", this));
        backB.setText(Locale.getStringInLocale("back", this));
    }

    private void initMenuText(){
        if(this.menu != null){
            MenuItem mainMenuMI = menu.findItem(R.id.action_back_main_menu);
            mainMenuMI.setTitle(Locale.getStringInLocale("back_to_main_menu", this));
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(selectB)){
            if(selectFarmerS.getSelectedItemPosition() != -1 && farmers.size() > selectFarmerS.getSelectedItemPosition()){
                Log.d(TAG, "Selected farmer index = "+String.valueOf(selectFarmerS.getSelectedItemPosition()));
                Farmer selectedFarmer = farmers.get(selectFarmerS.getSelectedItemPosition());
                Intent intent = new Intent(this, EditFarmer.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Farmer.PARCELABLE_KEY, selectedFarmer);
                intent.putExtras(bundle);
                intent.putExtra(KEY_ADMIN_DATA, adminData.toString());
                startActivity(intent);
            }
        }
        else if(v.equals(backB)){
            Intent intent = new Intent(this, MainMenu.class);
            intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_ADMIN);
            intent.putExtra(MainMenu.KEY_ADMIN_DATA, adminData.toString());
            startActivity(intent);
        }
    }
}
