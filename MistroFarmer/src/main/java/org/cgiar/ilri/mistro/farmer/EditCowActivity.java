package org.cgiar.ilri.mistro.farmer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.cgiar.ilri.mistro.farmer.backend.DataHandler;
import org.cgiar.ilri.mistro.farmer.backend.Locale;
import org.cgiar.ilri.mistro.farmer.carrier.Cow;
import org.cgiar.ilri.mistro.farmer.carrier.Dam;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.carrier.Sire;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class EditCowActivity extends SherlockActivity implements MistroActivity,
        View.OnClickListener, DatePickerDialog.OnDateSetListener, ListView.OnItemClickListener,
        Spinner.OnItemSelectedListener, View.OnFocusChangeListener, CheckBox.OnCheckedChangeListener, View.OnLongClickListener{

    private static final String TAG = "EditCowActivity";
    public static final String KEY_INDEX = "cowIndex";
    public static final String KEY_NUMBER_OF_COWS = "numberOfCows";

    private Menu menu;

    private final String dateFormat="dd/MM/yyyy";
    private TextView nameETNHintTV;
    private TextView nameTV;
    private EditText nameET;
    private TextView earTagNumberTV;
    private EditText earTagNumberET;
    private TextView ageOrDOBHintTV;
    private TextView ageTV;
    private Spinner ageS;
    private EditText ageET;
    private TextView dateOfBirthTV;
    private EditText dateOfBirthET;
    private TextView breedTV;
    private EditText breedET;
    private TextView sexTV;
    private Spinner sexS;
    private TextView milkingStatusTV;
    private Spinner milkingStatusS;
    private TextView inCalfStatusTV;
    private Spinner inCalfStatusS;
    private TextView deformityTV;
    private EditText deformityET;
    private TextView serviceTypeTV;
    private Spinner serviceTypeS;
    private TextView sireTV;
    private Spinner sireS;
    private AutoCompleteTextView sireACTV;
    private TextView sireOwnerTV;
    private Spinner sireOwnerS;
    private TextView sireOwnerNameTV;
    private EditText sireOwnerNameET;
    private TextView strawNumberTV;
    private EditText strawNumberET;
    private TextView damTV;
    private Spinner damS;
    private AutoCompleteTextView damACTV;
    private TextView embryoNumberTV;
    private EditText embryoNumberET;
    private TextView countryOfOriginTV;
    private AutoCompleteTextView countryOfOriginACTV;
    private TextView commonCountriesTV;
    private Spinner commonCountriesS;
    private Button cancelB;
    private Button editB;
    private DatePickerDialog datePickerDialog;
    private Dialog breedDialog;
    private ScrollView breedDialogSV;
    private ListView breedLV;
    private Button dialogBreedOkayB;
    private Dialog deformityDialog;
    private ScrollView deformitySV;
    private ListView deformityLV;
    private CheckBox noDeformityCB;
    private EditText specifyET;
    private Button dialogDeformityOkayB;

    private boolean cacheData;
    private String[] breeds;
    private String[] deformities;
    private String deformityOSpecifyText;
    private int selectedBreeds;
    private Farmer farmer;
    private Cow thisCow;
    private int index;
    private int numberOfCows;
    private List<Cow> validSires;
    private List<Cow> validDams;
    private int selectedSireOwner;
    private String adminData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cow);

        cacheData = true;
        selectedBreeds = 0;
        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null) {
            index=bundle.getInt(KEY_INDEX);
            numberOfCows=bundle.getInt(KEY_NUMBER_OF_COWS);
        }

        nameETNHintTV = (TextView) this.findViewById(R.id.name_eta_hint_tv);
        nameTV=(TextView)this.findViewById(R.id.name_tv);
        nameET=(EditText)this.findViewById(R.id.name_et);
        earTagNumberTV=(TextView)this.findViewById(R.id.ear_tag_number_tv);
        earTagNumberET=(EditText)this.findViewById(R.id.ear_tag_number_et);
        ageOrDOBHintTV = (TextView) this.findViewById(R.id.age_or_dob_hint_tv);
        ageTV=(TextView)this.findViewById(R.id.age_tv);
        ageS=(Spinner)this.findViewById(R.id.age_s);
        ageET=(EditText)this.findViewById(R.id.age_et);
        dateOfBirthTV=(TextView)this.findViewById(R.id.date_of_birth_tv);
        //dateOfBirthTV.setVisibility(TextView.GONE); //date of birth appears not to be necessary
        dateOfBirthET=(EditText)this.findViewById(R.id.date_of_birth_et);
        dateOfBirthET.setOnFocusChangeListener(this);
        dateOfBirthET.setOnClickListener(this);
        dateOfBirthET.setOnLongClickListener(this);
        //dateOfBirthET.setVisibility(TextView.GONE); //date of birth appears not to be necessary
        breedTV=(TextView)this.findViewById(R.id.breed_tv);
        breedET=(EditText)this.findViewById(R.id.breed_et);
        breedET.setOnFocusChangeListener(this);
        breedET.setOnClickListener(this);
        sexTV=(TextView)this.findViewById(R.id.sex_tv);
        sexS=(Spinner)this.findViewById(R.id.sex_s);
        sexS.setOnItemSelectedListener(this);
        milkingStatusTV = (TextView)this.findViewById(R.id.milking_status_tv);
        milkingStatusS = (Spinner)this.findViewById(R.id.milking_status_s);
        inCalfStatusTV = (TextView)this.findViewById(R.id.in_calf_status_tv);
        inCalfStatusS = (Spinner)this.findViewById(R.id.in_calf_status_s);
        deformityTV=(TextView)this.findViewById(R.id.deformity_tv);
        deformityET=(EditText)this.findViewById(R.id.deformity_et);
        deformityET.setOnFocusChangeListener(this);
        deformityET.setOnClickListener(this);
        serviceTypeTV = (TextView)this.findViewById(R.id.service_type_tv);
        serviceTypeS = (Spinner)this.findViewById(R.id.service_type_s);
        serviceTypeS.setOnItemSelectedListener(this);
        sireTV = (TextView)this.findViewById(R.id.sire_tv);
        sireS = (Spinner)this.findViewById(R.id.sire_s);
        sireACTV = (AutoCompleteTextView)this.findViewById(R.id.sire_actv);
        sireOwnerTV = (TextView)this.findViewById(R.id.sire_owner_tv);
        sireOwnerS = (Spinner)this.findViewById(R.id.sire_owner_s);
        sireOwnerS.setOnItemSelectedListener(this);
        sireOwnerNameTV = (TextView)this.findViewById(R.id.sire_owner_name_tv);
        sireOwnerNameET = (EditText)this.findViewById(R.id.sire_owner_name_et);
        strawNumberTV = (TextView)this.findViewById(R.id.straw_number_tv);
        strawNumberET = (EditText)this.findViewById(R.id.straw_number_et);
        damTV = (TextView)this.findViewById(R.id.dam_tv);
        damS = (Spinner)this.findViewById(R.id.dam_s);
        damACTV = (AutoCompleteTextView)this.findViewById(R.id.dam_actv);
        embryoNumberTV = (TextView)this.findViewById(R.id.embryo_number_tv);
        embryoNumberET = (EditText)this.findViewById(R.id.embryo_number_et);
        countryOfOriginTV = (TextView)this.findViewById(R.id.country_of_origin_tv);
        countryOfOriginACTV = (AutoCompleteTextView)this.findViewById(R.id.country_of_origin_actv);
        commonCountriesTV = (TextView)this.findViewById(R.id.common_countries_tv);
        commonCountriesS = (Spinner)this.findViewById(R.id.common_countries_s);
        commonCountriesS.setOnItemSelectedListener(this);
        cancelB = (Button)this.findViewById(R.id.cancel_b);
        cancelB.setOnClickListener(this);
        editB = (Button)this.findViewById(R.id.edit_b);
        editB.setOnClickListener(this);
        breedDialog=new Dialog(this);
        breedDialog.setContentView(R.layout.dialog_breed);
        dialogBreedOkayB=(Button)breedDialog.findViewById(R.id.dialog_breed_okay_b);
        dialogBreedOkayB.setOnClickListener(this);
        breedDialogSV=(ScrollView)breedDialog.findViewById(R.id.dialog_breed_sv);

        int activityHeight = this.getResources().getDisplayMetrics().heightPixels;
        breedDialogSV.getLayoutParams().height = (int)(activityHeight * 0.70);

        breedLV=(ListView)breedDialog.findViewById(R.id.breed_lv);
        breedLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        breedLV.setOnItemClickListener(this);
        deformityDialog =new Dialog(this);
        deformityDialog.setContentView(R.layout.dialog_deformity);
        deformitySV = (ScrollView)deformityDialog.findViewById(R.id.deformity_sv);

        deformitySV.getLayoutParams().height = (int)(activityHeight * 0.70);

        deformityLV =(ListView) deformityDialog.findViewById(R.id.deformity_lv);
        deformityLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        deformityLV.setOnItemClickListener(this);
        specifyET=(EditText)deformityDialog.findViewById(R.id.specify_et);
        noDeformityCB = (CheckBox)deformityDialog.findViewById(R.id.no_deformity_cb);
        noDeformityCB.setOnCheckedChangeListener(this);
        dialogDeformityOkayB =(Button) deformityDialog.findViewById(R.id.dialog_deformity_okay_b);
        dialogDeformityOkayB.setOnClickListener(this);

        initTextInViews();
    }

    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.edit_cow, menu);
        this.menu = menu;
        initMenuText();
        return true;
    }

    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        // Handle item selection
        if(MistroActivity.Language.processLanguageMenuItemSelected(this, this, item)){
            return true;
        }
        else if(item.getItemId() == R.id.action_back_main_menu) {
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which==DialogInterface.BUTTON_POSITIVE){
                        dialog.dismiss();

                        clearEditTextDataCache();

                        Intent intent = new Intent(EditCowActivity.this, MainMenu.class);
                        intent.putExtra(MainMenu.KEY_MODE, MainMenu.MODE_ADMIN);
                        intent.putExtra(MainMenu.KEY_ADMIN_DATA, adminData);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }
                    else{
                        dialog.cancel();
                    }
                }
            };
            AlertDialog mainMenuDialog = Utils.createMainMenuDialog(this, onClickListener);
            mainMenuDialog.show();
        }
        return false;
    }

    private void cacheEditTextData(){
        if(cacheData) {
            Log.i(TAG, "Edit text data cached");
            //Incase the activity is hidden partially/fully save the data in edittexts
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_NAME, nameET.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_EAR_TAG_NUMBER, earTagNumberET.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_AGE, ageET.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_DATE_OF_BIRTH, dateOfBirthET.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_BREED, breedET.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_DEFORMITY, deformityET.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_STRAW_NUMBER, strawNumberET.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_DAM, damACTV.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_SIRE, sireACTV.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_EMBRYO_NUMBER, embryoNumberET.getText().toString());
            DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_COUNTRY_OF_ORIGIN, countryOfOriginACTV.getText().toString());
        }
        else{
            Log.i(TAG, "Edit text data NOT cached");
        }
    }

    private void restoreEditTextData(){
        Log.i(TAG, "Edit text data restored");
        //incase the activity was hidden partially for a moment, restore what the user had already entered
        nameET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_CRA_NAME, ""));
        earTagNumberET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_CRA_EAR_TAG_NUMBER, ""));
        ageET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_CRA_AGE, ""));
        dateOfBirthET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_CRA_DATE_OF_BIRTH, ""));
        breedET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_CRA_BREED, ""));
        deformityET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_CRA_DEFORMITY, ""));
        strawNumberET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_CRA_STRAW_NUMBER, ""));
        damACTV.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_CRA_DAM, ""));
        sireACTV.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_CRA_SIRE, ""));
        embryoNumberET.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_CRA_EMBRYO_NUMBER, ""));
        countryOfOriginACTV.setText(DataHandler.getSharedPreference(this, DataHandler.SP_KEY_CRA_COUNTRY_OF_ORIGIN, ""));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle bundle=this.getIntent().getExtras();
        if(bundle != null) {
            adminData=bundle.getString(MainMenu.KEY_ADMIN_DATA);
            farmer=bundle.getParcelable(Farmer.PARCELABLE_KEY);
            if(farmer!=null){
                thisCow = farmer.getCow(index);
                if(thisCow != null) {
                    Log.d(TAG, "ID for current cow is "+String.valueOf(thisCow.getId()));
                    Log.d(TAG, "Index from bundle = "+String.valueOf(index));
                    Log.d(TAG, "Number of animals from bundle = "+String.valueOf(numberOfCows));
                    nameET.setText(thisCow.getName());
                    earTagNumberET.setText(thisCow.getEarTagNumber());
                    dateOfBirthET.setText(thisCow.getDateOfBirth());
                    ageET.setText((thisCow.getAge()==-1) ? "":String.valueOf(thisCow.getAge()));
                    String[] ageTypesInEN = Locale.getArrayInLocale("age_type_array",this,Locale.LOCALE_ENGLISH);
                    for(int i = 0; i < ageTypesInEN.length; i++) {
                        if(ageTypesInEN[i].equals("Days") && thisCow.getAgeType().equals(Cow.AGE_TYPE_DAY)) {
                            ageS.setSelection(i);
                        }
                        else if(ageTypesInEN[i].equals("Months") && thisCow.getAgeType().equals(Cow.AGE_TYPE_MONTH)) {
                            ageS.setSelection(i);
                        }
                        else if(ageTypesInEN[i].equals("Years") && thisCow.getAgeType().equals(Cow.AGE_TYPE_YEAR)) {
                            ageS.setSelection(i);
                        }
                    }
                    List<String> savedBreeds=thisCow.getBreeds(this);//returns breeds in current locale
                    Log.d(TAG, "Cow breeds are "+savedBreeds);
                    String breed="";
                    for (int i=0;i<savedBreeds.size();i++) {
                        if(i==0) {
                            breed=savedBreeds.get(i);
                        }
                        else {
                            breed=breed+", "+savedBreeds.get(i);
                        }
                        Log.d(TAG, "current breed index =  "+String.valueOf(i));
                        Log.d(TAG, "saved breeds length =  "+String.valueOf(savedBreeds.size()));
                    }
                    breedET.setText(breed);
                    String[] sexInEN = Locale.getArrayInLocale("sex_array", this, Locale.LOCALE_ENGLISH);
                    for(int i = 0; i < sexInEN.length; i++) {
                        if(sexInEN[i].equals("Female") && thisCow.getSex().equals(Cow.SEX_FEMALE)) {
                            sexS.setSelection(i);
                            toggleFemaleCowViewsVisibility();
                            String[] milkingStatusInEN = Locale.getArrayInLocale("cow_status_array", this, Locale.LOCALE_ENGLISH);
                            String[] inCalfArrayInEN = Locale.getArrayInLocale("cow_in_calf_array", this, Locale.LOCALE_ENGLISH);
                            for(int j = 0; j < milkingStatusInEN.length; j++){
                                Log.d(TAG, " ********** Saved milking status = "+thisCow.getMilkingStatus());
                                if(milkingStatusInEN[j].equals(thisCow.getMilkingStatus())){
                                    milkingStatusS.setSelection(j);
                                }
                            }
                            for(int j = 0; j < inCalfArrayInEN.length; j++){
                                if(inCalfArrayInEN[j].equals(Cow.COW_IN_CALF) && thisCow.isInCalf()){
                                    inCalfStatusS.setSelection(j);
                                }
                                else if(inCalfArrayInEN[j].equals(Cow.COW_NOT_IN_CALF) && !thisCow.isInCalf()){
                                    inCalfStatusS.setSelection(j);
                                }
                            }

                        }
                        else if(sexInEN[i].equals("Male") && thisCow.getSex().equals(Cow.SEX_MALE)) {
                            sexS.setSelection(i);
                        }
                    }
                    List<String> savedDeformities=thisCow.getDeformities(this);//returns deformities in current locale
                    String deformity="";
                    for (int i=0;i<savedDeformities.size();i++) {
                        if(i==0) {
                            deformity=savedDeformities.get(i);
                        }
                        else {
                            deformity=deformity+", "+savedDeformities.get(i);
                        }
                        if(savedDeformities.get(i).equals(deformities[deformities.length-1])) {
                            deformityOSpecifyText = thisCow.getOtherDeformity();
                        }
                    }
                    deformityET.setText(deformity);

                    //if(farmer.getMode().equals(Farmer.MODE_INITIAL_REGISTRATION)) {

                        List<Cow> allCows = farmer.getCows();
                        validSires = new ArrayList<Cow>();
                        validSires.add(new Cow(false));
                        List<String> validSireNames = new ArrayList<String>();
                        validSireNames.add("");
                        int sireSelection = 0;

                        validDams = new ArrayList<Cow>();
                        validDams.add(new Cow(false));
                        List<String> validDamNames = new ArrayList<String>();
                        int damSelection = -1;
                        validDamNames.add("");
                        for(int i = 0; i < allCows.size(); i++) {
                            if(i != index) {
                                if(allCows.get(i).getEarTagNumber() != null && allCows.get(i).getEarTagNumber().length() > 0 && allCows.get(i).getSex().equals(Cow.SEX_MALE)) {
                                    validSires.add(allCows.get(i));
                                    validSireNames.add(allCows.get(i).getEarTagNumber()+" ("+allCows.get(i).getName()+")");
                                    if(thisCow.getServiceType().equals(Cow.SERVICE_TYPE_BULL)) {
                                        if(thisCow.getSire().getEarTagNumber().equals(allCows.get(i).getEarTagNumber())) {
                                            sireSelection = validSires.size() - 1;
                                        }
                                    }
                                }
                                else if(allCows.get(i).getEarTagNumber() != null && allCows.get(i).getEarTagNumber().length() > 0 && allCows.get(i).getSex().equals(Cow.SEX_FEMALE)) {
                                    validDams.add(allCows.get(i));
                                    validDamNames.add(allCows.get(i).getEarTagNumber()+" ("+allCows.get(i).getName()+")");
                                    if(thisCow.getServiceType().equals(Cow.SERVICE_TYPE_BULL) || thisCow.getServiceType().equals(Cow.SERVICE_TYPE_AI)) {
                                        if(thisCow.getDam().getEarTagNumber().equals(allCows.get(i).getEarTagNumber())) {
                                            damSelection = validDams.size() - 1;
                                        }
                                    }
                                }
                            }
                        }

                        selectedSireOwner = -1;
                        String[] sireOwners = Locale.getArrayInLocale("bull_owners", this, Locale.LOCALE_ENGLISH);
                        for(int i = 0; i < sireOwners.length; i++){
                            if(sireOwners[i].equals(thisCow.getSire().getOwnerType())){
                                selectedSireOwner = i;
                            }
                        }
                        sireOwnerNameET.setText(thisCow.getSire().getOwner());

                        String[] serviceTypesInEN = Locale.getArrayInLocale("service_types",this,Locale.LOCALE_ENGLISH);

                        ArrayAdapter<String> siresArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,validSireNames);
                        siresArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sireS.setAdapter(siresArrayAdapter);
                        sireS.setSelection(sireSelection);

                        ArrayAdapter<String> damsArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,validDamNames);
                        damsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        damS.setAdapter(damsArrayAdapter);
                        if(damSelection != -1)
                            damS.setSelection(damSelection);

                        ArrayAdapter<String> damsACTVAdapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item,validDamNames);
                        damACTV.setAdapter(damsACTVAdapter);
                        if(damSelection != -1)
                            damACTV.setText(validDamNames.get(damSelection));

                        ArrayAdapter<String> siresACTVAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, validSireNames);
                        sireACTV.setAdapter(siresACTVAdapter);
                        if(sireSelection != -1)
                            sireACTV.setText(validSireNames.get(sireSelection));

                        for(int i = 0; i < serviceTypesInEN.length; i++) {
                            if(serviceTypesInEN[i].equals("Bull") && thisCow.getServiceType().equals(Cow.SERVICE_TYPE_BULL)) {
                                serviceTypeS.setSelection(i);
                            }
                            else if(serviceTypesInEN[i].equals("Artificial Insemination") && thisCow.getServiceType().equals(Cow.SERVICE_TYPE_AI)) {
                                serviceTypeS.setSelection(i);
                                strawNumberET.setText(thisCow.getSire().getStrawNumber());
                            }
                            else if(serviceTypesInEN[i].equals("Embryo Transfer") && thisCow.getServiceType().equals(Cow.SERVICE_TYPE_ET)) {
                                serviceTypeS.setSelection(i);
                                embryoNumberET.setText(thisCow.getDam().getEmbryoNumber());
                            }
                        }
                    /*}
                    else {
                        serviceTypeTV.setVisibility(TextView.GONE);
                        serviceTypeS.setVisibility(Spinner.GONE);
                        sireTV.setVisibility(TextView.GONE);
//                        sireS.setVisibility(Spinner.GONE);
                        strawNumberTV.setVisibility(TextView.GONE);
                        strawNumberET.setVisibility(EditText.GONE);
                        damTV.setVisibility(TextView.GONE);
//                        damS.setVisibility(Spinner.GONE);
                        sireACTV.setVisibility(AutoCompleteTextView.GONE);
                        sireOwnerTV.setVisibility(TextView.GONE);
                        sireOwnerS.setVisibility(Spinner.GONE);
                        sireOwnerNameTV.setVisibility(TextView.GONE);
                        sireOwnerNameET.setVisibility(EditText.GONE);
                        damACTV.setVisibility(AutoCompleteTextView.GONE);
                        embryoNumberTV.setVisibility(TextView.GONE);
                        embryoNumberET.setVisibility(EditText.GONE);
                    }*/

                    countryOfOriginACTV.setText(thisCow.getCountryOfOrigin());
                }
                else {
                    Log.d(TAG,"Cow object is null");
                }
            }
            else {
                Log.d(TAG,"Farmer object is null");
            }

        }

        if(nameET.getText().toString().trim().length() == 0 && earTagNumberET.getText().toString().trim().length() == 0){
            //means that when the activity paused the last time it did not save data to the cow object. Try to get data from shared preferences
            restoreEditTextData();
        }
    }

    private void clearEditTextDataCache(){
        Log.i(TAG, "Edit text cache cleared");

        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_NAME, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_EAR_TAG_NUMBER, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_AGE, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_DATE_OF_BIRTH, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_BREED, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_DEFORMITY, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_STRAW_NUMBER, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_DAM, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_SIRE, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_EMBRYO_NUMBER, "");
        DataHandler.setSharedPreference(this, DataHandler.SP_KEY_CRA_COUNTRY_OF_ORIGIN, "");

        cacheData = false;
    }

    @Override
    public void initTextInViews() {
        String title = Locale.getStringInLocale("edit_cow",this);
        this.setTitle(title);

        nameETNHintTV.setText(" * " + Locale.getStringInLocale("ear_tag_no_or_name", this));
        nameTV.setText(Locale.getStringInLocale("name",this));
        earTagNumberTV.setText(Locale.getStringInLocale("ear_tag_number",this));

        ageOrDOBHintTV.setText(" * " + Locale.getStringInLocale("age_or_dob", this));

        ageTV.setText(Locale.getStringInLocale("age",this));
        int ageTypeArrayID = Locale.getArrayIDInLocale("age_type_array",this);
        if(ageTypeArrayID!=0){
            ArrayAdapter<CharSequence> ageTypeArrayAdapter=ArrayAdapter.createFromResource(this, ageTypeArrayID, android.R.layout.simple_spinner_item);
            ageTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ageS.setAdapter(ageTypeArrayAdapter);
        }
        dateOfBirthTV.setText(Locale.getStringInLocale("date_of_birth",this));
        breedTV.setText(Locale.getStringInLocale("breed",this));
        sexTV.setText(" * "+Locale.getStringInLocale("sex",this));
        int sexArrayID = Locale.getArrayIDInLocale("sex_array",this);
        if(sexArrayID!=0) {
            ArrayAdapter<CharSequence> sexArrayAdapter=ArrayAdapter.createFromResource(this, sexArrayID, android.R.layout.simple_spinner_item);
            sexArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sexS.setAdapter(sexArrayAdapter);
        }

        milkingStatusTV.setText(" * " + Locale.getStringInLocale("cow_status", this));
        inCalfStatusTV.setText(" * " + Locale.getStringInLocale("cow_in_calf", this));
        ArrayAdapter<CharSequence> milkingStatusAdapter = ArrayAdapter.createFromResource(this, Locale.getArrayIDInLocale("cow_status_array", this), android.R.layout.simple_spinner_item);
        milkingStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        milkingStatusS.setAdapter(milkingStatusAdapter);

        ArrayAdapter<CharSequence> inCalfAdapter = ArrayAdapter.createFromResource(this, Locale.getArrayIDInLocale("cow_in_calf_array", this), android.R.layout.simple_spinner_item);
        inCalfAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inCalfStatusS.setAdapter(inCalfAdapter);

        deformityTV.setText(Locale.getStringInLocale("deformity",this));
        serviceTypeTV.setText(Locale.getStringInLocale("service_type_used",this));
        int serviceTypesSireArrayID = Locale.getArrayIDInLocale("service_types",this);
        if(serviceTypesSireArrayID!=0){
            ArrayAdapter<CharSequence> serviceTypesAdapter = ArrayAdapter.createFromResource(this,serviceTypesSireArrayID,android.R.layout.simple_spinner_item);
            serviceTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            serviceTypeS.setAdapter(serviceTypesAdapter);
        }

        sireTV.setText(Locale.getStringInLocale("sire",this));
        sireOwnerTV.setText(Locale.getStringInLocale("sire_owner", this));

        int sireOwnersID = Locale.getArrayIDInLocale("bull_owners", this);
        if(sireOwnersID != 0){
            ArrayAdapter<CharSequence> sireOwnersAdapter = ArrayAdapter.createFromResource(this,sireOwnersID,android.R.layout.simple_spinner_item);
            sireOwnersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sireOwnerS.setAdapter(sireOwnersAdapter);

            if(selectedSireOwner != -1){
                sireOwnerS.setSelection(selectedSireOwner);
            }
        }

        sireOwnerNameTV.setText(Locale.getStringInLocale("name_sire_owner", this));
        sireOwnerNameET.setHint(Locale.getStringInLocale("name_of_other_farmer_or_group", this));
        damTV.setText(Locale.getStringInLocale("dam",this));
        strawNumberTV.setText(Locale.getStringInLocale("straw_number",this));
        embryoNumberTV.setText(Locale.getStringInLocale("embryo_number",this));
        countryOfOriginTV.setText(Locale.getStringInLocale("other_countries",this));
        commonCountriesTV.setText(Locale.getStringInLocale("country_of_origin",this));
        cancelB.setText(Locale.getStringInLocale("cancel",this));
        editB.setText(Locale.getStringInLocale("edit", this));

        breedDialog.setTitle(Locale.getStringInLocale("breed",this));
        breeds=Locale.getArrayInLocale("breeds_array",this);
        if(breeds==null) {
            breeds = new String[1];
            breeds[0] = "";
        }
        ArrayAdapter<String> breedArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,breeds);
        breedLV.setAdapter(breedArrayAdapter);
        dialogBreedOkayB.setText(Locale.getStringInLocale("okay",this));
        int totalBreedSVHeight = 0;
        for (int i = 0, len = breedArrayAdapter.getCount(); i < len; i++) {
            View listItem = breedArrayAdapter.getView(i, null, breedLV);
            //listItem.measure(0, 0);
            int list_child_item_height = listItem.getLayoutParams().height + breedLV.getDividerHeight();//item height
            totalBreedSVHeight += list_child_item_height; //
        }
        if(totalBreedSVHeight > 0){
            breedLV.getLayoutParams().height = totalBreedSVHeight;
            if(breedDialogSV.getLayoutParams().height > (totalBreedSVHeight + dialogBreedOkayB.getLayoutParams().height)){
                breedDialogSV.getLayoutParams().height = totalBreedSVHeight + dialogBreedOkayB.getLayoutParams().height;
            }
        }

        deformityDialog.setTitle(Locale.getStringInLocale("deformity",this));
        deformities=Locale.getArrayInLocale("deformities_array",this);
        if(deformities==null) {
            deformities = new String[1];
            deformities[0] = "";
        }
        ArrayAdapter<String> deformityArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,deformities);
        deformityLV.setAdapter(deformityArrayAdapter);
        specifyET.setHint(Locale.getStringInLocale("specify", this));
        noDeformityCB.setText(Locale.getStringInLocale("no_deformity", this));
        dialogDeformityOkayB.setText(Locale.getStringInLocale("okay",this));

        sireACTV.setHint(Locale.getStringInLocale("enter_sire_etn", this));
        damACTV.setHint(Locale.getStringInLocale("enter_dam_etn",this));

        ArrayAdapter countryArrayAdapter = ArrayAdapter.createFromResource(this,R.array.countries,android.R.layout.select_dialog_item);
        countryOfOriginACTV.setAdapter(countryArrayAdapter);
        countryOfOriginACTV.setHint(Locale.getStringInLocale("specify_other_country", this));

        int totalDeformitySVHeight = 0;
        for (int i = 0, len = deformityArrayAdapter.getCount(); i < len; i++) {
            View listItem = deformityArrayAdapter.getView(i, null, deformityLV);
            //listItem.measure(0, 0);
            int list_child_item_height = listItem.getLayoutParams().height + deformityLV.getDividerHeight();//item height
            totalDeformitySVHeight += list_child_item_height; //
        }

        Log.d(TAG, "Height of no deformity checkbox = "+String.valueOf(noDeformityCB.getLayoutParams().height));
        Log.d(TAG, "Margin top of deformity checkbox = "+String.valueOf(((ViewGroup.MarginLayoutParams)noDeformityCB.getLayoutParams()).topMargin));

        if(totalDeformitySVHeight > 0){
            deformityLV.getLayoutParams().height = totalDeformitySVHeight;
            int svChildrenHeight = totalDeformitySVHeight +
                    specifyET.getLayoutParams().height +
                    noDeformityCB.getLayoutParams().height +
                    ((ViewGroup.MarginLayoutParams)noDeformityCB.getLayoutParams()).topMargin +
                    dialogDeformityOkayB.getLayoutParams().height +
                    ((ViewGroup.MarginLayoutParams)dialogDeformityOkayB.getLayoutParams()).topMargin;

            if(deformitySV.getLayoutParams().height > svChildrenHeight){
                deformitySV.getLayoutParams().height= svChildrenHeight;
            }
        }

        ArrayAdapter<CharSequence> commonCountriesArrayAdapter = ArrayAdapter.createFromResource(this,R.array.common_countries,android.R.layout.simple_spinner_item);
        commonCountriesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        commonCountriesS.setAdapter(commonCountriesArrayAdapter);
    }

    private void initMenuText(){
        if(this.menu != null){
            MenuItem mainMenuMI = menu.findItem(R.id.action_back_main_menu);
            mainMenuMI.setTitle(Locale.getStringInLocale("back_to_main_menu", this));
        }
    }

    @Override
    public void onClick(View view) {
        if(view==cancelB) {
            Intent intent = new Intent(this, CowSelectionActivity.class);
            intent.putExtra(CowSelectionActivity.KEY_ADMIN_DATA, adminData.toString());
            startActivity(intent);
        }
        else if(view==editB) {
            if(validateInput()) {
                try{
                    cacheThisCow();

                    JSONObject cowJsonObject = thisCow.getJsonObject();
                    cowJsonObject.put("farmer_id", farmer.getId());

                    clearEditTextDataCache();

                    Log.d(TAG, "Json Object for cow = "+cowJsonObject.toString());
                    EditCowThread editCowThread = new EditCowThread();
                    editCowThread.execute(cowJsonObject.toString());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        else if(view==dateOfBirthET) {
            dateOfBirthETClicked();
        }
        else if(view==breedET) {
            breedETClicked();
        }
        else if(view==deformityET) {
            deformityETClicked();
        }
        else if(view==dialogBreedOkayB) {
            String selectedBreeds="";
            SparseBooleanArray checkedBreeds=breedLV.getCheckedItemPositions();
            for (int i=0; i<breedLV.getCount();i++)
            {
                if(checkedBreeds.get(i))
                {
                    if(!selectedBreeds.equals(""))
                    {
                        selectedBreeds=selectedBreeds+", "+breeds[i];
                    }
                    else
                    {
                        selectedBreeds=breeds[i];
                    }
                }
            }
            breedET.setText(selectedBreeds);
            breedDialog.dismiss();
        }
        else if(view==dialogDeformityOkayB) {
            String selectedDeformities="";
            SparseBooleanArray checkedDeformities=deformityLV.getCheckedItemPositions();
            for (int i=0; i<deformityLV.getCount();i++) {
                if(checkedDeformities.get(i)) {
                    if(!selectedDeformities.equals("")) {
                        selectedDeformities=selectedDeformities+", "+deformities[i];
                    }
                    else {
                        selectedDeformities=deformities[i];
                    }
                }
            }
            deformityET.setText(selectedDeformities);
            deformityDialog.dismiss();
            deformityOSpecifyText=specifyET.getText().toString();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String dateString=String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year);//TODO: this might be a bug
        dateOfBirthET.setText(dateString);
        //setAgeFromDate(dateString);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(parent==breedLV) {
            if(breedLV.isItemChecked(position)) {
                selectedBreeds++;
            }
            else {
                selectedBreeds--;
            }
            if(selectedBreeds>4) {
                breedLV.setItemChecked(position,false);
                selectedBreeds--;
                Toast.makeText(this, Locale.getStringInLocale("maximum_of_four_breeds", this), Toast.LENGTH_LONG).show();
            }
        }
        else if(parent==deformityLV) {
            if(position==deformities.length-1){ //last deformity. should be other
                if(deformityLV.isItemChecked(position)) {
                    specifyET.setVisibility(EditText.VISIBLE);
                }
                else {
                    specifyET.setVisibility(EditText.GONE);
                    specifyET.setText("");
                }
            }

            noDeformityCB.setChecked(false);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent == serviceTypeS) {
            changeServiceType();
        }
        else if(parent == commonCountriesS) {
            toggleCountryOfOriginVisibility();
        }
        else if(parent == sireOwnerS){
            toggleSireOwnerVisibility();
        }
        else if(parent == sexS){
            toggleFemaleCowViewsVisibility();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(view == dateOfBirthET && hasFocus){
            if(dateOfBirthET.getText().toString().length() ==0 ){
                dateOfBirthETClicked();
            }
        }
        else if(view == breedET && hasFocus){
            if(breedET.getText().toString().length() == 0){
                breedETClicked();
            }
        }
        else if(view == deformityET && hasFocus){
            if(deformityET.getText().toString().length() == 0){
                deformityETClicked();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.equals(noDeformityCB)){
            if(isChecked){
                deformityLV.clearChoices();
                ArrayAdapter<String> deformityArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,deformities);
                deformityLV.setAdapter(deformityArrayAdapter);
                specifyET.setVisibility(EditText.GONE);
                specifyET.setText("");

                deformityET.setHint(Locale.getStringInLocale("no_deformity", this));
            }
            else{
                deformityET.setHint("");
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(v.equals(dateOfBirthET)){
            Log.w(TAG, "About to delete date in dateOfBirthET");
            dateOfBirthET.setText("");
        }
        return false;
    }

    private void dateOfBirthETClicked() {
        Date date=null;
        if(dateOfBirthET.getText().toString().length()>0) {
            try {
                date=new SimpleDateFormat(dateFormat, java.util.Locale.ENGLISH).parse(dateOfBirthET.getText().toString());
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(date==null) {
            date=new Date();
        }
        Calendar calendar=new GregorianCalendar();
        calendar.setTime(date);
        datePickerDialog=new DatePickerDialog(this,this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        //datePickerDialog=createDialogWithoutDateField(this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void breedETClicked() {
        //uncheck everything in listview
        for (int i=0; i<breedLV.getCount();i++) {
            breedLV.setItemChecked(i,false);
        }

        String breedETString=breedET.getText().toString();
        if(!breedETString.equals(null)||!breedETString.equals(""))
        {
            String[] selectedBreeds=breedETString.split(", ");
            //for all of the breeds check if breed is in selected breeds
            for(int i=0; i<breeds.length;i++)
            {
                String currentBreed=breeds[i];
                for(int j=0; j<selectedBreeds.length;j++) {
                    if(currentBreed.equals(selectedBreeds[j])) {
                        breedLV.setItemChecked(i,true);
                        break;
                    }
                }
            }
        }
        breedDialog.show();
    }

    private void deformityETClicked() {
        //uncheck everything in listview
        for (int i=0;i<deformityLV.getCount();i++) {
            deformityLV.setItemChecked(i,false);
        }
        String deformityETString=deformityET.getText().toString();
        if(!deformityETString.equals(null)||!deformityETString.equals("")) {
            String[] selectedDeformities=deformityETString.split(", ");
            for (int i=0;i<deformities.length;i++)
            {
                String currentDeformity=deformities[i];
                for (int j=0;j<selectedDeformities.length;j++)
                {
                    if(currentDeformity.equals(selectedDeformities[j]))
                    {
                        deformityLV.setItemChecked(i,true);
                        if (i==deformities.length-1)
                        {
                            specifyET.setVisibility(EditText.VISIBLE);
                            specifyET.setText(deformityOSpecifyText);
                        }
                        break;
                    }
                }
            }
        }
        deformityDialog.show();
    }

    private void changeServiceType() {
        String[] serviceTypesInEN = Locale.getArrayInLocale("service_types", this, Locale.LOCALE_ENGLISH);
        if(serviceTypesInEN[serviceTypeS.getSelectedItemPosition()].equals("Bull")) {
            sireTV.setVisibility(TextView.VISIBLE);
            sireOwnerTV.setVisibility(TextView.VISIBLE);
            sireOwnerS.setVisibility(Spinner.VISIBLE);
            toggleSireOwnerVisibility();
//            sireS.setVisibility(Spinner.VISIBLE);
            strawNumberTV.setVisibility(TextView.GONE);
            strawNumberET.setVisibility(EditText.GONE);
            damTV.setVisibility(TextView.VISIBLE);
//            damS.setVisibility(Spinner.VISIBLE);
            damS.setVisibility(Spinner.GONE);
            damACTV.setVisibility(AutoCompleteTextView.VISIBLE);
            sireACTV.setVisibility(AutoCompleteTextView.VISIBLE);
            embryoNumberTV.setVisibility(TextView.GONE);
            embryoNumberET.setVisibility(EditText.GONE);
            commonCountriesTV.setVisibility(TextView.VISIBLE);
            commonCountriesS.setVisibility(Spinner.VISIBLE);
            toggleCountryOfOriginVisibility();
        }
        else if(serviceTypesInEN[serviceTypeS.getSelectedItemPosition()].equals("Artificial Insemination")) {
            sireTV.setVisibility(TextView.GONE);
            sireOwnerTV.setVisibility(TextView.GONE);
            sireOwnerS.setVisibility(Spinner.GONE);
            sireOwnerNameTV.setVisibility(TextView.GONE);
            sireOwnerNameET.setVisibility(EditText.GONE);
//            sireS.setVisibility(Spinner.GONE);
            strawNumberTV.setVisibility(TextView.VISIBLE);
            strawNumberET.setVisibility(EditText.VISIBLE);
            damTV.setVisibility(TextView.VISIBLE);
//            damS.setVisibility(Spinner.VISIBLE);
            damS.setVisibility(Spinner.GONE);
            damACTV.setVisibility(AutoCompleteTextView.VISIBLE);
            sireACTV.setVisibility(AutoCompleteTextView.GONE);
            embryoNumberTV.setVisibility(TextView.GONE);
            embryoNumberET.setVisibility(EditText.GONE);
            commonCountriesTV.setVisibility(TextView.GONE);
            commonCountriesS.setVisibility(Spinner.GONE);
            countryOfOriginTV.setVisibility(TextView.GONE);
            countryOfOriginACTV.setVisibility(AutoCompleteTextView.GONE);
        }
        else if(serviceTypesInEN[serviceTypeS.getSelectedItemPosition()].equals("Embryo Transfer")) {
            sireTV.setVisibility(TextView.GONE);
            sireOwnerTV.setVisibility(TextView.GONE);
            sireOwnerS.setVisibility(Spinner.GONE);
            sireOwnerNameTV.setVisibility(TextView.GONE);
            sireOwnerNameET.setVisibility(EditText.GONE);
//            sireS.setVisibility(Spinner.GONE);
            strawNumberTV.setVisibility(TextView.GONE);
            strawNumberET.setVisibility(EditText.GONE);
            damTV.setVisibility(TextView.GONE);
            damS.setVisibility(Spinner.GONE);
            damACTV.setVisibility(AutoCompleteTextView.GONE);
            sireACTV.setVisibility(AutoCompleteTextView.GONE);
            embryoNumberTV.setVisibility(TextView.VISIBLE);
            embryoNumberET.setVisibility(EditText.VISIBLE);
            commonCountriesTV.setVisibility(TextView.GONE);
            commonCountriesS.setVisibility(Spinner.GONE);
            countryOfOriginTV.setVisibility(TextView.GONE);
            countryOfOriginACTV.setVisibility(AutoCompleteTextView.GONE);
        }
    }

    private void toggleSireOwnerVisibility(){
        String[] sireOwnersInEN = Locale.getArrayInLocale("bull_owners", this, Locale.LOCALE_ENGLISH);
        if(sireOwnersInEN[sireOwnerS.getSelectedItemPosition()].equals("Own bull")){
            sireOwnerNameTV.setVisibility(TextView.GONE);
            sireOwnerNameET.setVisibility(EditText.GONE);
        }
        else{
            sireOwnerNameTV.setVisibility(TextView.VISIBLE);
            sireOwnerNameET.setVisibility(EditText.VISIBLE);
        }
    }

    private void toggleCountryOfOriginVisibility(){
        String[] commonCountries = getResources().getStringArray(R.array.common_countries);
        if(commonCountries[commonCountriesS.getSelectedItemPosition()].equals("Other")) {
            countryOfOriginACTV.setVisibility(AutoCompleteTextView.VISIBLE);
            countryOfOriginTV.setVisibility(TextView.VISIBLE);
            countryOfOriginACTV.setText("");
        }
        else {
            countryOfOriginACTV.setVisibility(AutoCompleteTextView.GONE);
            countryOfOriginTV.setVisibility(TextView.GONE);
            countryOfOriginACTV.setText(commonCountries[commonCountriesS.getSelectedItemPosition()]);
        }
    }

    private void toggleFemaleCowViewsVisibility(){
        Log.d(TAG, "Toggling female views");
        String[] sexInEN = Locale.getArrayInLocale("sex_array", this, Locale.LOCALE_ENGLISH);
        if(sexInEN[sexS.getSelectedItemPosition()].equals(Cow.SEX_FEMALE)){
            milkingStatusS.setVisibility(Spinner.VISIBLE);
            milkingStatusTV.setVisibility(TextView.VISIBLE);

            inCalfStatusS.setVisibility(Spinner.VISIBLE);
            inCalfStatusTV.setVisibility(TextView.VISIBLE);
        }
        else{
            milkingStatusS.setVisibility(Spinner.GONE);
            milkingStatusTV.setVisibility(TextView.GONE);

            inCalfStatusS.setVisibility(Spinner.GONE);
            inCalfStatusTV.setVisibility(TextView.GONE);
        }
    }

    private boolean validateInput() {
        String earTagNumberText=earTagNumberET.getText().toString();
        String nameText=nameET.getText().toString();
        if((earTagNumberText==null||earTagNumberText.equals("")) && (nameText==null||nameText.equals(""))) {
            Toast.makeText(this,Locale.getStringInLocale("enter_ear_tag_no_or_name",this),Toast.LENGTH_LONG).show();
            return false;
        }

        String[] sexInEN = Locale.getArrayInLocale("sex_array", this, Locale.LOCALE_ENGLISH);
        if(sexInEN[sexS.getSelectedItemPosition()].equals(Cow.SEX_FEMALE)){
            String[] inCalfArray = Locale.getArrayInLocale("cow_in_calf_array",this);
            String[] milkingStatusArray = Locale.getArrayInLocale("cow_status_array",this);
            if(inCalfStatusS.getSelectedItemPosition() == -1 || inCalfArray[inCalfStatusS.getSelectedItemPosition()].length() == 0){
                Toast.makeText(this, Locale.getStringInLocale("enter_in_calf_status", this), Toast.LENGTH_LONG).show();
                return false;
            }
            if(milkingStatusS.getSelectedItemPosition() == -1 || milkingStatusArray[milkingStatusS.getSelectedItemPosition()].length() == 0){
                Toast.makeText(this, Locale.getStringInLocale("enter_milk_status_of_cow", this), Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if(dateOfBirthET.getText().toString().trim().equals("") && ageET.getText().toString().trim().equals("")){
            Toast.makeText(this, Locale.getStringInLocale("enter_age_or_dob", this), Toast.LENGTH_LONG).show();
            return false;
        }
        else if (!dateOfBirthET.getText().toString().trim().equals("") && !ageET.getText().toString().trim().equals("")) {//both dob and age are set
            String[] ageTypesInEN = Locale.getArrayInLocale("age_type_array", this, Locale.LOCALE_ENGLISH);
            String ageType = ageTypesInEN[ageS.getSelectedItemPosition()];
            float unitAge = 0;
            if (ageType.equals("Years")) {
                unitAge = 31557600000L;
            } else if (ageType.equals("Months")) {
                unitAge = 2628000000L;
            } else if (ageType.equals("Days")) {
                unitAge = 86400000L;
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

            try {
                Date enteredDate = simpleDateFormat.parse(dateOfBirthET.getText().toString());
                long enteredDateMs = enteredDate.getTime();
                long ageMs = (long) (new Date().getTime() - (Integer.parseInt(ageET.getText().toString()) * unitAge));
                long msDiff = Math.abs(ageMs - enteredDateMs);
                float unitDiff = msDiff / unitAge;
                if (unitDiff > 1) {
                    Toast.makeText(this, Locale.getStringInLocale("age_diff_from_dob", this), Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(countryOfOriginACTV.getText().toString().length() > 0) {
            String[] countries = this.getResources().getStringArray(R.array.countries);
            boolean countryFound = false;
            for(int i = 0; i < countries.length; i++) {
                if(countries[i].equals(countryOfOriginACTV.getText().toString())) {
                    countryFound = true;
                    break;
                }
            }
            if(!countryFound) {
                Toast.makeText(this,Locale.getStringInLocale("country_not_found",this),Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    private void cacheThisCow() {
        clearEditTextDataCache();

        thisCow.setName(nameET.getText().toString());

        thisCow.setEarTagNumber(earTagNumberET.getText().toString());
        thisCow.setDateOfBirth(dateOfBirthET.getText().toString());
        thisCow.setAge((ageET.getText().toString() == null || ageET.getText().toString().length() == 0) ? -1 : Integer.parseInt(ageET.getText().toString()));
        String[] ageTypesInEN = Locale.getArrayInLocale("age_type_array",this,Locale.LOCALE_ENGLISH);
        if(ageTypesInEN[ageS.getSelectedItemPosition()].equals("Days")) {
            thisCow.setAgeType(Cow.AGE_TYPE_DAY);
        }
        else if(ageTypesInEN[ageS.getSelectedItemPosition()].equals("Months")) {
            thisCow.setAgeType(Cow.AGE_TYPE_MONTH);
        }
        else if(ageTypesInEN[ageS.getSelectedItemPosition()].equals("Years")) {
            thisCow.setAgeType(Cow.AGE_TYPE_YEAR);
        }
        thisCow.setDateOfBirth(dateOfBirthET.getText().toString());
        thisCow.setBreeds(breedET.getText().toString().split(", "), this, false);

        thisCow.setDeformities(deformityET.getText().toString().split(", "), this);
        thisCow.setOtherDeformity(specifyET.getText().toString());
        thisCow.setCountryOfOrigin(countryOfOriginACTV.getText().toString());
        String[] sexInEN = Locale.getArrayInLocale("sex_array",this,Locale.LOCALE_ENGLISH);
        if(sexInEN[sexS.getSelectedItemPosition()].equals("Female")) {
            thisCow.setSex(Cow.SEX_FEMALE);

            String[] milkingStatusInEN = Locale.getArrayInLocale("cow_status_array", this, Locale.LOCALE_ENGLISH);
            thisCow.setMilkingStatus(milkingStatusInEN[milkingStatusS.getSelectedItemPosition()], this);
            String[] inCalfArrayInEN = Locale.getArrayInLocale("cow_in_calf_array", this, Locale.LOCALE_ENGLISH);
            if(inCalfArrayInEN[inCalfStatusS.getSelectedItemPosition()].equals(Cow.COW_IN_CALF)){
                thisCow.setInCalf(true);
            }
            else{
                thisCow.setInCalf(false);
            }
        }
        else if(sexInEN[sexS.getSelectedItemPosition()].equals("Male")) {
            thisCow.setSex(Cow.SEX_MALE);
        }
        //if(farmer.getMode().equals(Farmer.MODE_INITIAL_REGISTRATION)) {
            String[] serviceTypesInEN = Locale.getArrayInLocale("service_types",this,Locale.LOCALE_ENGLISH);
            if(serviceTypesInEN[serviceTypeS.getSelectedItemPosition()].equals("Bull")) {
                thisCow.setServiceType(Cow.SERVICE_TYPE_BULL);

                Sire sire = new Sire();
//                sire.setName(validSires.get(sireS.getSelectedItemPosition()).getName());
//                sire.setEarTagNumber(validSires.get(sireS.getSelectedItemPosition()).getEarTagNumber());

                for(int i = 0; i < validSires.size(); i++){
                    if(sireACTV.getText().toString().equals(validSires.get(i).getEarTagNumber()+" ("+validSires.get(i).getName()+")")){
                        sire.setEarTagNumber(validSires.get(i).getEarTagNumber());
                        sire.setName(validSires.get(i).getName());
                    }
                }
                if(sire.getEarTagNumber().trim().equals("")){//if not yet set then assume the sire is not part of the herd
                    sire.setEarTagNumber(sireACTV.getText().toString());
                }

                String[] sireOwnersInEN = Locale.getArrayInLocale("bull_owners", this, Locale.LOCALE_ENGLISH);
                sire.setOwnerType(sireOwnersInEN[sireOwnerS.getSelectedItemPosition()]);
                sire.setOwner(sireOwnerNameET.getText().toString());
                thisCow.setSire(sire);

                Dam dam =new Dam();
//                dam.setName(validDams.get(damS.getSelectedItemPosition()).getName());
//                dam.setEarTagNumber(validDams.get(damS.getSelectedItemPosition()).getEarTagNumber());

                for(int i = 0; i < validDams.size(); i++ ){
                    if(damACTV.getText().toString().equals(validDams.get(i).getEarTagNumber()+" ("+validDams.get(i).getName()+")")){
                        dam.setEarTagNumber(validDams.get(i).getEarTagNumber());
                        dam.setName(validDams.get(i).getName());
                    }
                }
                if(dam.getEarTagNumber().trim().equals("")){//if not yet set then assume the dam is not part of the herd
                    dam.setEarTagNumber(damACTV.getText().toString());
                }
                thisCow.setDam(dam);
            }
            else if(serviceTypesInEN[serviceTypeS.getSelectedItemPosition()].equals("Artificial Insemination")) {
                thisCow.setServiceType(Cow.SERVICE_TYPE_AI);

                Sire sire = new Sire();
                sire.setStrawNumber(strawNumberET.getText().toString());
                thisCow.setSire(sire);

                Dam dam =new Dam();
//                dam.setName(validDams.get(damS.getSelectedItemPosition()).getName());
//                dam.setEarTagNumber(validDams.get(damS.getSelectedItemPosition()).getEarTagNumber());

                for(int i = 0; i < validDams.size(); i++ ){
                    if(damACTV.getText().toString().equals(validDams.get(i).getEarTagNumber()+" ("+validDams.get(i).getName()+")")){
                        dam.setEarTagNumber(validDams.get(i).getEarTagNumber());
                        dam.setName(validDams.get(i).getName());
                    }
                }
                if(dam.getEmbryoNumber().trim().equals("")){
                    dam.setEarTagNumber(damACTV.getText().toString());
                }
                thisCow.setDam(dam);
            }
            else if(serviceTypesInEN[serviceTypeS.getSelectedItemPosition()].equals("Embryo Transfer")) {
                thisCow.setServiceType(Cow.SERVICE_TYPE_ET);

                Dam dam =new Dam();
//                dam.setName(validDams.get(damS.getSelectedItemPosition()).getName());
//                dam.setEarTagNumber(validDams.get(damS.getSelectedItemPosition()).getEarTagNumber());

                for(int i = 0; i < validDams.size(); i++ ){
                    if(damACTV.getText().toString().equals(validDams.get(i).getEarTagNumber()+" ("+validDams.get(i).getName()+")")){
                        dam.setEarTagNumber(validDams.get(i).getEarTagNumber());
                        dam.setName(validDams.get(i).getName());
                    }
                }
                if(dam.getEmbryoNumber().trim().equals("")){
                    dam.setEarTagNumber(damACTV.getText().toString());
                }
                thisCow.setDam(dam);
            }
        //}
        farmer.setCow(thisCow,index);
    }

    private class EditCowThread extends AsyncTask<String, Integer, String>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(EditCowActivity.this, "", Locale.getStringInLocale("loading_please_wait", EditCowActivity.this), true);
        }

        @Override
        protected String doInBackground(String... params) {
            return DataHandler.sendDataToServer(EditCowActivity.this, params[0], DataHandler.ADMIN_EDIT_COW_URL, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if(result==null){
                String httpError = DataHandler.getSharedPreference(EditCowActivity.this, "http_error", "No Error thrown to application. Something must be really wrong");
                Toast.makeText(EditCowActivity.this, httpError, Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_GENERIC_FAILURE)){
                Toast.makeText(EditCowActivity.this, Locale.getStringInLocale("generic_sms_error", EditCowActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_NO_SERVICE)){
                Toast.makeText(EditCowActivity.this, Locale.getStringInLocale("no_service", EditCowActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RADIO_OFF)){
                Toast.makeText(EditCowActivity.this, Locale.getStringInLocale("radio_off", EditCowActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.SMS_ERROR_RESULT_CANCELLED)){
                Toast.makeText(EditCowActivity.this, Locale.getStringInLocale("server_not_receive_sms", EditCowActivity.this), Toast.LENGTH_LONG).show();
            }
            else if(result.equals(DataHandler.ACKNOWLEDGE_OK)){
                try{
                    Intent intent = new Intent(EditCowActivity.this, CowSelectionActivity.class);
                    intent.putExtra(CowSelectionActivity.KEY_ADMIN_DATA, adminData);//since this activity
                    startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(EditCowActivity.this, Locale.getStringInLocale("problem_connecting_to_server", EditCowActivity.this), Toast.LENGTH_LONG).show();
            }
        }
    }
}
