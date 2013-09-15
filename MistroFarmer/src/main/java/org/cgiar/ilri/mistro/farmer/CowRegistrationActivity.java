package org.cgiar.ilri.mistro.farmer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CowRegistrationActivity extends SherlockActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, ListView.OnItemClickListener,  Spinner.OnItemSelectedListener
{
    public static final String TAG="CowRegistrationActivity";
    public static final String KEY_INDEX="index";
    public static final String KEY_NUMBER_OF_COWS="numberOfCows";
    private final String dateFormat="dd/MM/yyyy";
    private TextView nameTV;
    private EditText nameET;
    private TextView earTagNumberTV;
    private EditText earTagNumberET;
    private TextView ageTV;
    private Spinner ageS;
    private EditText ageET;
    private TextView dateOfBirthTV;
    private EditText dateOfBirthET;
    private TextView breedTV;
    private EditText breedET;
    private TextView sexTV;
    private Spinner sexS;
    private TextView deformityTV;
    private EditText deformityET;
    private TextView serviceTypeTV;
    private Spinner serviceTypeS;
    private TextView sireTV;
    private Spinner sireS;
    private TextView strawNumberTV;
    private EditText strawNumberET;
    private TextView damTV;
    private Spinner damS;
    private TextView embryoNumberTV;
    private EditText embryoNumberET;
    private TextView countryOfOriginTV;
    private AutoCompleteTextView countryOfOriginACTV;
    private Button previousButton;
    private Button nextButton;
    private DatePickerDialog datePickerDialog;
    private Dialog breedDialog;
    private ScrollView breedDialogSV;
    private ListView breedLV;
    private Button dialogBreedOkayB;
    private Dialog deformityDialog;
    private ListView deformityLV;
    private EditText specifyET;
    private Button dialogDeformityOkayB;

    private int index;
    private int numberOfCows;
    private int selectedBreeds;
    private String[] breeds;
    private String[] deformities;
    private String deformityOSpecifyText;
    private Cow thisCow;
    private Farmer farmer;
    private List<Cow> validSires;
    private List<Cow> validDams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_registration);

        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null) {
            index=bundle.getInt(KEY_INDEX);
            numberOfCows=bundle.getInt(KEY_NUMBER_OF_COWS);
        }
        selectedBreeds = 0;

        //init views
        nameTV=(TextView)this.findViewById(R.id.name_tv);
        nameET=(EditText)this.findViewById(R.id.name_et);
        earTagNumberTV=(TextView)this.findViewById(R.id.ear_tag_number_tv);
        earTagNumberET=(EditText)this.findViewById(R.id.ear_tag_number_et);
        ageTV=(TextView)this.findViewById(R.id.age_tv);
        ageS=(Spinner)this.findViewById(R.id.age_s);
        ageET=(EditText)this.findViewById(R.id.age_et);
        dateOfBirthTV=(TextView)this.findViewById(R.id.date_of_birth_tv);
        dateOfBirthET=(EditText)this.findViewById(R.id.date_of_birth_et);
        //dateOfBirthET.setOnFocusChangeListener(this);
        dateOfBirthET.setOnClickListener(this);
        breedTV=(TextView)this.findViewById(R.id.breed_tv);
        breedET=(EditText)this.findViewById(R.id.breed_et);
        //breedET.setOnFocusChangeListener(this);
        breedET.setOnClickListener(this);
        sexTV=(TextView)this.findViewById(R.id.sex_tv);
        sexS=(Spinner)this.findViewById(R.id.sex_s);
        deformityTV=(TextView)this.findViewById(R.id.deformity_tv);
        deformityET=(EditText)this.findViewById(R.id.deformity_et);
        //deformityET.setOnFocusChangeListener(this);
        deformityET.setOnClickListener(this);
        serviceTypeTV = (TextView)this.findViewById(R.id.service_type_tv);
        serviceTypeS = (Spinner)this.findViewById(R.id.service_type_s);
        serviceTypeS.setOnItemSelectedListener(this);
        sireTV = (TextView)this.findViewById(R.id.sire_tv);
        sireS = (Spinner)this.findViewById(R.id.sire_s);
        strawNumberTV = (TextView)this.findViewById(R.id.straw_number_tv);
        strawNumberET = (EditText)this.findViewById(R.id.straw_number_et);
        damTV = (TextView)this.findViewById(R.id.dam_tv);
        damS = (Spinner)this.findViewById(R.id.dam_s);
        embryoNumberTV = (TextView)this.findViewById(R.id.embryo_number_tv);
        embryoNumberET = (EditText)this.findViewById(R.id.embryo_number_et);
        countryOfOriginTV = (TextView)this.findViewById(R.id.country_of_origin_tv);
        countryOfOriginACTV = (AutoCompleteTextView)this.findViewById(R.id.country_of_origin_actv);
        previousButton = (Button)this.findViewById(R.id.previous_button);
        previousButton.setOnClickListener(this);
        nextButton = (Button)this.findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);
        breedDialog=new Dialog(this);
        breedDialog.setContentView(R.layout.dialog_breed);
        dialogBreedOkayB=(Button)breedDialog.findViewById(R.id.dialog_breed_okay_b);
        dialogBreedOkayB.setOnClickListener(this);
        breedDialogSV=(ScrollView)breedDialog.findViewById(R.id.dialog_breed_sv);
        breedLV=(ListView)breedDialog.findViewById(R.id.breed_lv);
        breedLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        breedLV.setOnItemClickListener(this);
        deformityDialog =new Dialog(this);
        deformityDialog.setContentView(R.layout.dialog_deformity);
        deformityLV =(ListView) deformityDialog.findViewById(R.id.deformity_lv);
        deformityLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        deformityLV.setOnItemClickListener(this);
        specifyET=(EditText)deformityDialog.findViewById(R.id.specify_et);
        dialogDeformityOkayB =(Button) deformityDialog.findViewById(R.id.dialog_deformity_okay_b);
        dialogDeformityOkayB.setOnClickListener(this);

        initTextInViews();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.cow_registration, menu);
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
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle bundle=this.getIntent().getExtras();
        if(bundle != null) {
            farmer=bundle.getParcelable(Farmer.PARCELABLE_KEY);
            if(farmer!=null){
                if(farmer.getMode().equals(Farmer.MODE_NEW_COW_REGISTRATION) || index == 0) {
                    previousButton.setVisibility(Button.INVISIBLE);
                }
                thisCow = farmer.getCow(index);
                if(thisCow != null) {
                    nameET.setText(thisCow.getName());
                    earTagNumberET.setText(thisCow.getEarTagNumber());
                    dateOfBirthET.setText(thisCow.getDateOfBirth());
                    ageET.setText((thisCow.getAge()==-1) ? "":String.valueOf(thisCow.getAge()));
                    String[] ageTypesInEN = Locale.getArrayInLocale("age_type_array",this,Locale.LOCALE_ENGLISH);
                    for(int i = 0; i < ageTypesInEN.length; i++) {
                        if(ageTypesInEN[i].equals("Days") && thisCow.getAgeType().equals(Cow.AGE_TYPE_DAY)) {
                            ageS.setSelection(i);
                        }
                        else if(ageTypesInEN[i].equals("Weeks") && thisCow.getAgeType().equals(Cow.AGE_TYPE_WEEK)) {
                            ageS.setSelection(i);
                        }
                        else if(ageTypesInEN[i].equals("Years") && thisCow.getAgeType().equals(Cow.AGE_TYPE_YEAR)) {
                            ageS.setSelection(i);
                        }
                    }
                    List<String> savedBreeds=thisCow.getBreeds();
                    String breed="";
                    for (int i=0;i<savedBreeds.size();i++) {
                        if(i==0) {
                            breed=savedBreeds.get(i);
                        }
                        else {
                            breed=breed+", "+savedBreeds.get(i);
                        }
                    }
                    breedET.setText(breed);
                    String[] sexInEN = Locale.getArrayInLocale("sex_array", this, Locale.LOCALE_ENGLISH);
                    for(int i = 0; i < sexInEN.length; i++) {
                        if(sexInEN[i].equals("Female") && thisCow.getSex().equals(Cow.SEX_FEMALE)) {
                            sexS.setSelection(i);
                        }
                        else if(sexInEN[i].equals("Male") && thisCow.getSex().equals(Cow.SEX_MALE)) {
                            sexS.setSelection(i);
                        }
                    }
                    List<String> savedDeformities=thisCow.getDeformities();
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

                    List<Cow> allCows = farmer.getCows();
                    validSires = new ArrayList<Cow>();
                    validSires.add(new Cow(false));
                    List<String> validSireNames = new ArrayList<String>();
                    validSireNames.add("");
                    int sireSelection = 0;

                    validDams = new ArrayList<Cow>();
                    validDams.add(new Cow(false));
                    List<String> validDamNames = new ArrayList<String>();
                    int damSelection = 0;
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
                    ArrayAdapter<String> siresArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,validSireNames);
                    siresArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sireS.setAdapter(siresArrayAdapter);
                    sireS.setSelection(sireSelection);

                    ArrayAdapter<String> damsArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,validDamNames);
                    damsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    damS.setAdapter(damsArrayAdapter);
                    damS.setSelection(damSelection);

                    countryOfOriginACTV.setText(thisCow.getCountryOfOrigin());
                    String[] serviceTypesInEN = Locale.getArrayInLocale("service_types",this,Locale.LOCALE_ENGLISH);
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

                    if(thisCow.getMode().equals(Cow.MODE_BORN_CALF_REGISTRATION)) {
                        this.setTitle(Locale.getStringInLocale("calf_registration",this));
                        setAgeFromDate(thisCow.getDateOfBirth());
                        thisCow.setAge(Integer.parseInt(ageET.getText().toString()));
                        if(ageTypesInEN[ageS.getSelectedItemPosition()].equals("Days")) {
                            thisCow.setAgeType(Cow.AGE_TYPE_DAY);
                        }
                        else if(ageTypesInEN[ageS.getSelectedItemPosition()].equals("Weeks")) {
                            thisCow.setAgeType(Cow.AGE_TYPE_WEEK);
                        }
                        else if(ageTypesInEN[ageS.getSelectedItemPosition()].equals("Years")) {
                            thisCow.setAgeType(Cow.AGE_TYPE_YEAR);
                        }
                    }
                }
                else {
                    Log.d(TAG,"Cow object is null");
                }
            }
            else {
                Log.d(TAG,"Farmer object is null");
            }

        }
    }

    private void initTextInViews() {
        String title = Locale.getStringInLocale("cow_registration",this)+" "+String.valueOf(index+1);
        this.setTitle(title);
        nameTV.setText(Locale.getStringInLocale("name",this));
        earTagNumberTV.setText(Locale.getStringInLocale("ear_tag_number",this));
        ageTV.setText(Locale.getStringInLocale("age",this));
        int ageTypeArrayID = Locale.getArrayIDInLocale("age_type_array",this);
        if(ageTypeArrayID!=0){
            ArrayAdapter<CharSequence> ageTypeArrayAdapter=ArrayAdapter.createFromResource(this, ageTypeArrayID, android.R.layout.simple_spinner_item);
            ageTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ageS.setAdapter(ageTypeArrayAdapter);
        }
        dateOfBirthTV.setText(Locale.getStringInLocale("date_of_birth",this));
        breedTV.setText(Locale.getStringInLocale("breed",this));
        sexTV.setText(Locale.getStringInLocale("sex",this));
        int sexArrayID = Locale.getArrayIDInLocale("sex_array",this);
        if(sexArrayID!=0) {
            ArrayAdapter<CharSequence> sexArrayAdapter=ArrayAdapter.createFromResource(this, sexArrayID, android.R.layout.simple_spinner_item);
            sexArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sexS.setAdapter(sexArrayAdapter);
        }
        deformityTV.setText(Locale.getStringInLocale("deformity",this));
        serviceTypeTV.setText(Locale.getStringInLocale("service_type_used",this));
        int serviceTypesSireArrayID = Locale.getArrayIDInLocale("service_types",this);
        if(serviceTypesSireArrayID!=0){
            ArrayAdapter<CharSequence> serviceTypesAdapter = ArrayAdapter.createFromResource(this,serviceTypesSireArrayID,android.R.layout.simple_spinner_item);
            serviceTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            serviceTypeS.setAdapter(serviceTypesAdapter);
        }
        sireTV.setText(Locale.getStringInLocale("sire",this));
        damTV.setText(Locale.getStringInLocale("dam",this));
        strawNumberTV.setText(Locale.getStringInLocale("straw_number",this));
        embryoNumberTV.setText(Locale.getStringInLocale("embryo_number",this));
        countryOfOriginTV.setText(Locale.getStringInLocale("country_of_origin",this));
        previousButton.setText(Locale.getStringInLocale("previous",this));
        if(index == (numberOfCows -1)) {
            nextButton.setText(Locale.getStringInLocale("finish",this));
        }
        else {
            nextButton.setText(Locale.getStringInLocale("next",this));
        }

        breedDialog.setTitle(Locale.getStringInLocale("breed",this));
        breeds=Locale.getArrayInLocale("breeds_array",this);
        if(breeds==null) {
            breeds = new String[1];
            breeds[0] = "";
        }
        ArrayAdapter<String> breedArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,breeds);
        breedLV.setAdapter(breedArrayAdapter);
        dialogBreedOkayB.setText(Locale.getStringInLocale("okay",this));

        deformityDialog.setTitle(Locale.getStringInLocale("deformity",this));
        deformities=Locale.getArrayInLocale("deformities_array",this);
        if(deformities==null) {
            deformities = new String[1];
            deformities[0] = "";
        }
        ArrayAdapter<String> deformityArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,deformities);
        deformityLV.setAdapter(deformityArrayAdapter);
        specifyET.setHint(Locale.getStringInLocale("specify",this));
        dialogDeformityOkayB.setText(Locale.getStringInLocale("okay",this));
    }

    @Override
    public void onClick(View view) {
        if(view==previousButton) {
            cacheThisCow();
            Intent intent=new Intent(CowRegistrationActivity.this, CowRegistrationActivity.class);
            intent.putExtra(KEY_INDEX,index-1);
            intent.putExtra(KEY_NUMBER_OF_COWS,numberOfCows);
            Bundle bundle=new Bundle();
            bundle.putParcelable(Farmer.PARCELABLE_KEY,farmer);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else if(view==nextButton) {
            if(validateInput()) {
                cacheThisCow();
                Bundle bundle=new Bundle();
                bundle.putParcelable(Farmer.PARCELABLE_KEY,farmer);
                if(index == (numberOfCows-1))//last cow
                {
                    Log.d(TAG, farmer.getJsonObject().toString());
                    //TODO: send to server
                    if (DataHandler.checkNetworkConnection(this, null)) {
                        sendDataToServer(farmer.getJsonObject());
                    }
                }
                else
                {
                    Intent intent=new Intent(CowRegistrationActivity.this, CowRegistrationActivity.class);
                    intent.putExtra(KEY_INDEX,index+1);
                    intent.putExtra(KEY_NUMBER_OF_COWS,numberOfCows);
                    intent.putExtras(bundle);
                    startActivity(intent);
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String dateString=String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year);//TODO: this might be a bug
        dateOfBirthET.setText(dateString);
        setAgeFromDate(dateString);
    }

    private void setAgeFromDate(String dateString) {
        Log.d(TAG, "date entered : " + dateString);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(dateFormat);
        Date enteredDate=new Date();
        try
        {
            enteredDate=simpleDateFormat.parse(dateString);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        Date today=new Date();
        Log.d(TAG,"today's date : "+today.toString());
        long millisecondDifference=today.getTime()-enteredDate.getTime();
        Log.d(TAG,"millisecond difference : "+String.valueOf(millisecondDifference));
        String[] ageTypesInEN = Locale.getArrayInLocale("age_type_array",this,Locale.LOCALE_ENGLISH);
        if(millisecondDifference>0&&millisecondDifference<604800000L)//less than one week
        {
            int days=(int)(millisecondDifference/86400000L);
            ageET.setText(String.valueOf(days));
            for(int i = 0; i < ageTypesInEN.length; i++) {
                if(ageTypesInEN[i].equals("Day")) {
                    ageS.setSelection(i);
                }
            }
        }
        else if(millisecondDifference>=604800000L&&millisecondDifference<31557600000L)//less than a year
        {
            int weeks=(int)(millisecondDifference/604800000L);
            ageET.setText(String.valueOf(weeks));
            for(int i = 0; i < ageTypesInEN.length; i++) {
                if(ageTypesInEN[i].equals("Weeks")) {
                    ageS.setSelection(i);
                }
            }
        }
        else if(millisecondDifference>=31557600000L)//a year or greater
        {
            int years=(int)(millisecondDifference/31557600000L);
            ageET.setText(String.valueOf(years));
            for(int i = 0; i < ageTypesInEN.length; i++) {
                if(ageTypesInEN[i].equals("Years")) {
                    ageS.setSelection(i);
                }
            }
        }
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
                Toast.makeText(this,Locale.getStringInLocale("maximum_of_four_breeds",this),Toast.LENGTH_LONG).show();
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
        }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent == serviceTypeS) {
            changeServiceType();
        }
    }

    private void changeServiceType() {
        String[] serviceTypesInEN = Locale.getArrayInLocale("service_types", this, Locale.LOCALE_ENGLISH);
        if(serviceTypesInEN[serviceTypeS.getSelectedItemPosition()].equals("Bull")) {
            sireTV.setVisibility(TextView.VISIBLE);
            sireS.setVisibility(Spinner.VISIBLE);
            strawNumberTV.setVisibility(TextView.GONE);
            strawNumberET.setVisibility(EditText.GONE);
            damTV.setVisibility(TextView.VISIBLE);
            damS.setVisibility(Spinner.VISIBLE);
            embryoNumberTV.setVisibility(TextView.GONE);
            embryoNumberET.setVisibility(EditText.GONE);
        }
        else if(serviceTypesInEN[serviceTypeS.getSelectedItemPosition()].equals("Artificial Insemination")) {
            sireTV.setVisibility(TextView.GONE);
            sireS.setVisibility(Spinner.GONE);
            strawNumberTV.setVisibility(TextView.VISIBLE);
            strawNumberET.setVisibility(EditText.VISIBLE);
            damTV.setVisibility(TextView.VISIBLE);
            damS.setVisibility(Spinner.VISIBLE);
            embryoNumberTV.setVisibility(TextView.GONE);
            embryoNumberET.setVisibility(EditText.GONE);
        }
        else if(serviceTypesInEN[serviceTypeS.getSelectedItemPosition()].equals("Embryo Transfer")) {
            sireTV.setVisibility(TextView.GONE);
            sireS.setVisibility(Spinner.GONE);
            strawNumberTV.setVisibility(TextView.GONE);
            strawNumberET.setVisibility(EditText.GONE);
            damTV.setVisibility(TextView.GONE);
            damS.setVisibility(Spinner.GONE);
            embryoNumberTV.setVisibility(TextView.VISIBLE);
            embryoNumberET.setVisibility(EditText.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private boolean validateInput() {
        String earTagNumberText=earTagNumberET.getText().toString();
        String nameText=nameET.getText().toString();
        if(earTagNumberText==null||earTagNumberText.equals("")) {
            Toast.makeText(this,Locale.getStringInLocale("enter_ear_tag_number",this),Toast.LENGTH_LONG).show();
            return false;
        }
        //TODO: validate country of origin
        return true;
    }

    private void cacheThisCow() {
        if(thisCow==null) {
            thisCow=new Cow(true);
        }
        thisCow.setName(nameET.getText().toString());
        if(farmer.getMode().equals(Farmer.MODE_INITIAL_REGISTRATION)) {
            thisCow.setMode(Cow.MODE_ADULT_COW_REGISTRATION);
        }
        thisCow.setEarTagNumber(earTagNumberET.getText().toString());
        thisCow.setDateOfBirth(dateOfBirthET.getText().toString());
        thisCow.setAge((ageET.getText().toString() == null || ageET.getText().toString().length() == 0) ? -1 : Integer.parseInt(ageET.getText().toString()));
        String[] ageTypesInEN = Locale.getArrayInLocale("age_type_array",this,Locale.LOCALE_ENGLISH);
        if(ageTypesInEN[ageS.getSelectedItemPosition()].equals("Days")) {
            thisCow.setAgeType(Cow.AGE_TYPE_DAY);
        }
        else if(ageTypesInEN[ageS.getSelectedItemPosition()].equals("Weeks")) {
            thisCow.setAgeType(Cow.AGE_TYPE_WEEK);
        }
        else if(ageTypesInEN[ageS.getSelectedItemPosition()].equals("Years")) {
            thisCow.setAgeType(Cow.AGE_TYPE_YEAR);
        }
        thisCow.setDateOfBirth(dateOfBirthET.getText().toString());
        thisCow.setBreeds(breedET.getText().toString().split(", "));
        thisCow.setDeformities(deformityET.getText().toString().split(", "));
        String[] selectedDeformities = deformityET.getText().toString().split(", ");
        thisCow.setOtherDeformity(specifyET.getText().toString());
        thisCow.setCountryOfOrigin(countryOfOriginACTV.getText().toString());
        String[] sexInEN = Locale.getArrayInLocale("sex_array",this,Locale.LOCALE_ENGLISH);
        if(sexInEN[sexS.getSelectedItemPosition()].equals("Female")) {
            thisCow.setSex(Cow.SEX_FEMALE);
        }
        else if(sexInEN[sexS.getSelectedItemPosition()].equals("Male")) {
            thisCow.setSex(Cow.SEX_MALE);
        }
        String[] serviceTypesInEN = Locale.getArrayInLocale("service_types",this,Locale.LOCALE_ENGLISH);
        if(serviceTypesInEN[serviceTypeS.getSelectedItemPosition()].equals("Bull")) {
            thisCow.setServiceType(Cow.SERVICE_TYPE_BULL);

            Sire sire = new Sire();
            sire.setName(validSires.get(sireS.getSelectedItemPosition()).getName());
            sire.setEarTagNumber(validSires.get(sireS.getSelectedItemPosition()).getEarTagNumber());
            thisCow.setSire(sire);

            Dam dam =new Dam();
            dam.setName(validDams.get(damS.getSelectedItemPosition()).getName());
            dam.setEarTagNumber(validDams.get(damS.getSelectedItemPosition()).getEarTagNumber());
            thisCow.setDam(dam);
        }
        else if(serviceTypesInEN[serviceTypeS.getSelectedItemPosition()].equals("Artificial Insemination")) {
            thisCow.setServiceType(Cow.SERVICE_TYPE_AI);

            Sire sire = new Sire();
            sire.setStrawNumber(strawNumberET.getText().toString());
            thisCow.setSire(sire);

            Dam dam =new Dam();
            dam.setName(validDams.get(damS.getSelectedItemPosition()).getName());
            dam.setEarTagNumber(validDams.get(damS.getSelectedItemPosition()).getEarTagNumber());
            thisCow.setDam(dam);
        }
        else if(serviceTypesInEN[serviceTypeS.getSelectedItemPosition()].equals("Embryo Transfer")) {
            thisCow.setServiceType(Cow.SERVICE_TYPE_ET);

            Dam dam = new Dam();
            dam.setEmbryoNumber(embryoNumberET.getText().toString());
            thisCow.setDam(dam);
        }
        farmer.setCow(thisCow,index);
    }

    private void sendDataToServer(JSONObject jsonObject)
    {
        ServerRegistrationThread serverRegistrationThread=new ServerRegistrationThread();
        serverRegistrationThread.execute(jsonObject);
    }

    private class ServerRegistrationThread extends AsyncTask<JSONObject,Integer,Boolean> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(CowRegistrationActivity.this, "",Locale.getStringInLocale("loading_please_wait",CowRegistrationActivity.this), true);
        }

        @Override
        protected Boolean doInBackground(JSONObject... params) {
            Log.d(TAG,"sending registration data to server");
            String responseString=DataHandler.sendDataToServer(params[0].toString(), DataHandler.FARMER_REGISTRATION_URL);
            if(responseString!=null && responseString.equals(DataHandler.ACKNOWLEDGE_OK)) {
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if(result) {
                Log.d(TAG,"data successfully sent to server");
                if(farmer.getMode().equals(Farmer.MODE_INITIAL_REGISTRATION)) {
                    Utils.showSuccessfullRegistration(CowRegistrationActivity.this,null);
                }
                else {
                    Toast.makeText(CowRegistrationActivity.this, Locale.getStringInLocale("event_successfully_recorded",CowRegistrationActivity.this), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CowRegistrationActivity.this, EventsActivity.class);
                    startActivity(intent);
                }
            }
            else {
                Toast.makeText(CowRegistrationActivity.this,Locale.getStringInLocale("problem_connecting_to_server",CowRegistrationActivity.this),Toast.LENGTH_LONG).show();
            }
        }
    }
}
