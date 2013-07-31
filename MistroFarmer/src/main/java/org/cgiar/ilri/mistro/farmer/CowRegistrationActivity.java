package org.cgiar.ilri.mistro.farmer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CowRegistrationActivity extends SherlockActivity implements View.OnClickListener,TextWatcher,AdapterView.OnItemSelectedListener,View.OnFocusChangeListener,DatePickerDialog.OnDateSetListener
{
    public static final String KEY_MODE="mode";
    public static final String KEY_INDEX="index";
    public static final String KEY_NUMBER_OF_COWS="numberOfCows";
    public static final int MODE_COW=0;
    public static final int MODE_SIRE=1;
    public static final int MODE_DAM=2;
    private final String dateFormat="MM/yyyy";

    private int mode;
    private int index;
    private int numberOfCows;
    private String localeCode;

    private TextView nameTV;
    private TextView earTagNumberTV;
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
    private TextView sireTV;
    private EditText sireET;
    private TextView damTV;
    private EditText damET;
    private Button previousButton;
    private Button nextButton;
    private boolean monitorAgeChange=true;
    private DatePickerDialog datePickerDialog;
    private Dialog breedDialog;
    private ScrollView breedDialogSV;
    private ListView breedLV;
    private Button dialogBreedOkayB;
    private String[] breeds;
    private Dialog deformityDialog;
    private ListView deformityLV;
    private Button dialogDeformityOkayB;
    private String[] deformities;
    private TextView serviceTypeTV;
    private Spinner serviceTypeS;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_registration);

        localeCode="en";//TODO:get locale from sharedPreferences

        Bundle bundle=this.getIntent().getExtras();
        if(bundle!=null)
        {
            mode=bundle.getInt(KEY_MODE);
            index=bundle.getInt(KEY_INDEX);
            numberOfCows=bundle.getInt(KEY_NUMBER_OF_COWS);
        }
        else
        {
            Toast.makeText(this,"Bundle is null, going rogue!",Toast.LENGTH_LONG).show();
        }

        //init child views
        nameTV=(TextView)this.findViewById(R.id.name_tv);
        earTagNumberTV=(TextView)this.findViewById(R.id.ear_tag_number_tv);
        ageTV=(TextView)this.findViewById(R.id.age_tv);
        ageS=(Spinner)this.findViewById(R.id.age_s);
        ageS.setOnItemSelectedListener(this);
        ageET=(EditText)this.findViewById(R.id.age_et);
        ageET.addTextChangedListener(this);
        dateOfBirthTV=(TextView)this.findViewById(R.id.date_of_birth_tv);
        dateOfBirthET=(EditText)this.findViewById(R.id.date_of_birth_et);
        dateOfBirthET.setOnFocusChangeListener(this);
        dateOfBirthET.setOnClickListener(this);
        breedTV=(TextView)this.findViewById(R.id.breed_tv);
        breedET=(EditText)this.findViewById(R.id.breed_et);
        breedET.setOnFocusChangeListener(this);
        breedET.setOnClickListener(this);
        sexTV=(TextView)this.findViewById(R.id.sex_tv);
        sexS=(Spinner)this.findViewById(R.id.sex_s);
        deformityTV=(TextView)this.findViewById(R.id.deformity_tv);
        deformityET=(EditText)this.findViewById(R.id.deformity_et);
        deformityET.setOnFocusChangeListener(this);
        deformityET.setOnClickListener(this);
        sireTV=(TextView)this.findViewById(R.id.sire_tv);
        sireET=(EditText)this.findViewById(R.id.sire_et);
        sireET.setOnFocusChangeListener(this);
        sireET.setOnClickListener(this);
        damTV=(TextView)this.findViewById(R.id.dam_tv);
        damET=(EditText)this.findViewById(R.id.dam_et);
        damET.setOnFocusChangeListener(this);
        damET.setOnClickListener(this);
        serviceTypeTV=(TextView)this.findViewById(R.id.service_type_tv);
        serviceTypeS=(Spinner)this.findViewById(R.id.service_type_s);
        previousButton=(Button)this.findViewById(R.id.previous_button);
        previousButton.setOnClickListener(this);
        if(mode==MODE_DAM||mode==MODE_SIRE||index==0)
        {
            previousButton.setVisibility(Button.INVISIBLE);
        }
        nextButton=(Button)this.findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);
        breedDialog=new Dialog(this);
        breedDialog.setContentView(R.layout.dialog_breed);
        dialogBreedOkayB=(Button)breedDialog.findViewById(R.id.dialog_breed_okay_b);
        dialogBreedOkayB.setOnClickListener(this);
        /*WindowManager.LayoutParams dialogLayoutParams=new WindowManager.LayoutParams();
        dialogLayoutParams.copyFrom(breedDialog.getWindow().getAttributes());
        dialogLayoutParams.width=WindowManager.LayoutParams.MATCH_PARENT;
        dialogLayoutParams.height=WindowManager.LayoutParams.MATCH_PARENT;
        breedDialog.getWindow().setAttributes(dialogLayoutParams);*/
        breedDialogSV=(ScrollView)breedDialog.findViewById(R.id.dialog_breed_sv);
        breedLV=(ListView)breedDialog.findViewById(R.id.breed_lv);
        breedLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        deformityDialog =new Dialog(this);
        deformityDialog.setContentView(R.layout.dialog_deformity);
        deformityLV =(ListView) deformityDialog.findViewById(R.id.deformity_lv);
        deformityLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        dialogDeformityOkayB =(Button) deformityDialog.findViewById(R.id.dialog_deformity_okay_b);
        if(mode==MODE_DAM||mode==MODE_SIRE)//TODO: remember to auto set sex in datastructure if dam or sire
        {
            sexTV.setVisibility(TextView.GONE);
            sexS.setVisibility(Spinner.GONE);
            damTV.setVisibility(TextView.GONE);
            damET.setVisibility(EditText.GONE);
            sireTV.setVisibility(TextView.GONE);
            sireET.setVisibility(EditText.GONE);
        }
        if(mode==MODE_SIRE)
        {
            serviceTypeTV.setVisibility(TextView.VISIBLE);
            serviceTypeS.setVisibility(Spinner.VISIBLE);
        }

        //init text in child views
        initTextInViews(localeCode);
    }

    private void initTextInViews(String localeCode)
    {
        if(localeCode.equals("en"))
        {
            if(mode==MODE_COW)
            {
                String title=getResources().getString(R.string.cow_registration_en)+" "+String.valueOf(index+1);
                setTitle(title);
            }
            else if(mode==MODE_SIRE)
            {
                setTitle(R.string.sire_registration_en);
            }
            else if(mode==MODE_DAM)
            {
                setTitle(R.string.dam_registration_en);
            }
            nameTV.setText(R.string.name_en);
            earTagNumberTV.setText(R.string.ear_tag_number_en);
            ageTV.setText(R.string.age_en);
            ArrayAdapter<CharSequence> arrayAdapter2=ArrayAdapter.createFromResource(this, R.array.age_type_array_en, android.R.layout.simple_spinner_item);
            arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ageS.setAdapter(arrayAdapter2);
            dateOfBirthTV.setText(R.string.date_of_birth_en);
            breedTV.setText(R.string.breed_en);
            sexTV.setText(R.string.sex_en);
            ArrayAdapter<CharSequence> arrayAdapter=ArrayAdapter.createFromResource(this, R.array.sex_array_en, android.R.layout.simple_spinner_item);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sexS.setAdapter(arrayAdapter);
            deformityTV.setText(R.string.deformity_en);
            sireTV.setText(R.string.sire_en);
            damTV.setText(R.string.dam_en);
            serviceTypeTV.setText(R.string.service_type_used_en);
            ArrayAdapter<CharSequence> serviceTypesAdapter=ArrayAdapter.createFromResource(this,R.array.service_types_array_en,android.R.layout.simple_spinner_item);
            serviceTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            serviceTypeS.setAdapter(serviceTypesAdapter);
            previousButton.setText(R.string.previous_en);
            if(mode==MODE_SIRE||mode==MODE_DAM)
            {
                nextButton.setText(R.string.okay_en);
            }
            else
            {
                if(index==numberOfCows-1)//last cow
                {
                    nextButton.setText(R.string.finish_en);
                }
                else
                {
                    nextButton.setText(R.string.next_en);
                }
            }
            breedDialog.setTitle(R.string.breed_en);
            breeds=getResources().getStringArray(R.array.breeds_array_en);
            ArrayAdapter<String> breedArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,breeds);
            breedLV.setAdapter(breedArrayAdapter);
            dialogBreedOkayB.setText(R.string.okay_en);
            deformityDialog.setTitle(R.string.deformity_en);
            deformities=getResources().getStringArray(R.array.deformities_array_en);
            ArrayAdapter<String> deformityArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,deformities);
            deformityLV.setAdapter(deformityArrayAdapter);
            dialogDeformityOkayB.setText(R.string.okay_en);
            dialogDeformityOkayB.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view)
    {
        if(view==previousButton)
        {
            Intent intent=new Intent(CowRegistrationActivity.this, CowRegistrationActivity.class);
            intent.putExtra(KEY_MODE,MODE_COW);
            intent.putExtra(KEY_INDEX,index-1);
            intent.putExtra(KEY_NUMBER_OF_COWS,numberOfCows);
            startActivity(intent);
        }
        else if(view==nextButton)
        {
            if(mode==MODE_COW)
            {
                if(index==numberOfCows-1)//last cow
                {
                    Intent intent=new Intent(CowRegistrationActivity.this, LandingActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent=new Intent(CowRegistrationActivity.this, CowRegistrationActivity.class);
                    intent.putExtra(KEY_MODE,MODE_COW);
                    intent.putExtra(KEY_INDEX,index+1);
                    intent.putExtra(KEY_NUMBER_OF_COWS,numberOfCows);
                    startActivity(intent);
                }
            }
            else//go back to cow
            {
                Intent intent=new Intent(CowRegistrationActivity.this, CowRegistrationActivity.class);
                intent.putExtra(KEY_MODE,MODE_COW);
                intent.putExtra(KEY_INDEX,index);
                intent.putExtra(KEY_NUMBER_OF_COWS,numberOfCows);
                startActivity(intent);
            }
        }
        else if(view==dialogBreedOkayB)
        {
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
        else if(view==dialogDeformityOkayB)
        {
            String selectedDeformities="";
            SparseBooleanArray checkedDeformities=deformityLV.getCheckedItemPositions();
            for (int i=0; i<deformityLV.getCount();i++)
            {
                if(checkedDeformities.get(i))
                {
                    if(!selectedDeformities.equals(""))
                    {
                        selectedDeformities=selectedDeformities+", "+deformities[i];
                    }
                    else
                    {
                        selectedDeformities=deformities[i];
                    }
                }
            }
            deformityET.setText(selectedDeformities);
            deformityDialog.dismiss();
        }
        else if(view==dateOfBirthET)
        {
            dateOfBirthETClicked();
        }
        else if(view==breedET)
        {
            breedETClicked();
        }
        else if(view==deformityET)
        {
            deformityETClicked();
        }
        else if(view==sireET)
        {
            sireETClicked();
        }
        else if(view==damET)
        {
            damETClicked();
        }
    }

    private void dateOfBirthETClicked()
    {
        Date date=null;
        if(dateOfBirthET.getText().toString().length()>0)
        {
            try
            {
                date=new SimpleDateFormat(dateFormat, Locale.ENGLISH).parse(dateOfBirthET.getText().toString());
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        if(date==null)
        {
            date=new Date();
        }
        Calendar calendar=new GregorianCalendar();
        calendar.setTime(date);
        datePickerDialog=new DatePickerDialog(this,this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        //datePickerDialog=createDialogWithoutDateField(this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void breedETClicked()
    {
        breedDialog.show();
    }

    private void deformityETClicked()
    {
        deformityDialog.show();
    }

    private void sireETClicked()
    {
        Intent intent=new Intent(CowRegistrationActivity.this, CowRegistrationActivity.class);
        intent.putExtra(KEY_MODE,MODE_SIRE);
        intent.putExtra(KEY_INDEX,index);
        intent.putExtra(KEY_NUMBER_OF_COWS,numberOfCows);
        startActivity(intent);
    }

    private void damETClicked()
    {
        Intent intent=new Intent(CowRegistrationActivity.this, CowRegistrationActivity.class);
        intent.putExtra(KEY_MODE,MODE_DAM);
        intent.putExtra(KEY_INDEX,index);
        intent.putExtra(KEY_NUMBER_OF_COWS,numberOfCows);
        startActivity(intent);
    }


    private String getDateFromAge()
    {
        if(ageET.getText().toString().length()>0)
        {
            int age=Integer.valueOf(ageET.getText().toString());
            long milliseconds=0;
            if(ageS.getSelectedItemPosition()==0)//days
            {
                milliseconds=age*86400000L;
            }
            else if(ageS.getSelectedItemPosition()==1)//weeks
            {
                milliseconds=age*604800000L;
            }
            else if(ageS.getSelectedItemPosition()==2)//years
            {
                milliseconds=age*31557600000L;
            }
            long nowMilliseconds=new Date().getTime();
            long pastDateMilliseconds=nowMilliseconds-milliseconds;
            Date pastDate=new Date(pastDateMilliseconds);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat(dateFormat);
            return simpleDateFormat.format(pastDate);
        }
        return null;
    }

    private void setAgeFromDate(String dateString)
    {
        monitorAgeChange=false;
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
        long millisecondDifference=today.getTime()-enteredDate.getTime();
        if(millisecondDifference>0&&millisecondDifference<604800000L)//less than one week
        {
            int days=(int)(millisecondDifference/86400000L);
            ageET.setText(String.valueOf(days));
            ageS.setSelection(0);
        }
        else if(millisecondDifference>=604800000L&&millisecondDifference<31557600000L)//less than a year
        {
            int weeks=(int)(millisecondDifference/604800000L);
            ageET.setText(String.valueOf(weeks));
            ageS.setSelection(1);
        }
        else if(millisecondDifference>=31557600000L)//a year or greater
        {
            int years=(int)(millisecondDifference/31557600000L);
            ageET.setText(String.valueOf(years));
            ageS.setSelection(2);
        }
        monitorAgeChange=true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable)
    {
        if(monitorAgeChange)
        {
            dateOfBirthET.setText(getDateFromAge());
        }
    }
    private DatePickerDialog createDialogWithoutDateField(DatePickerDialog.OnDateSetListener dateSetListener, int cyear, int cmonth, int cday){

    DatePickerDialog dpd = new DatePickerDialog(this, dateSetListener,cyear,cmonth, cday);
    try
    {
        Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
        for (Field datePickerDialogField : datePickerDialogFields)
        {
            if (datePickerDialogField.getName().equals("mDatePicker"))
            {
                datePickerDialogField.setAccessible(true);
                DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                Field datePickerFields[] = datePickerDialogField.getType().getDeclaredFields();
                for (Field datePickerField : datePickerFields)
                {
                    if ("mDayPicker".equals(datePickerField.getName()))
                    {
                        datePickerField.setAccessible(true);
                        Object dayPicker = new Object();
                        dayPicker = datePickerField.get(datePicker);
                        ((View) dayPicker).setVisibility(View.GONE);
                    }
                }
            }

        }
    }catch(Exception ex){
    }
    return dpd;

}

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if(parent==ageS)
        {
            if(monitorAgeChange)
            {
                dateOfBirthET.setText(getDateFromAge());
            }

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    @Override
    public void onFocusChange(View view, boolean hasFocus)
    {
        if(view==dateOfBirthET&&hasFocus)
        {
            dateOfBirthETClicked();
        }
        else if(view==breedET&&hasFocus)
        {
            breedETClicked();
        }
        else if(view==deformityET&&hasFocus)
        {
            deformityETClicked();
        }
        else if(view==sireET)
        {
            sireETClicked();
        }
        else if(view==damET)
        {
            damETClicked();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        String dateString=String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year);//TODO: this might be a bug
        dateOfBirthET.setText(dateString);
        setAgeFromDate(dateString);
    }


}
