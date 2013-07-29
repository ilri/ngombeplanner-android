package org.cgiar.ilri.mistro.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class CowRegistrationActivity extends SherlockActivity implements View.OnClickListener
{
    public static final String KEY_MODE="mode";
    public static final String KEY_INDEX="index";
    public static final String KEY_NUMBER_OF_COWS="numberOfCows";
    public static final int MODE_COW=0;
    public static final int MODE_SIRE=1;
    public static final int MODE_DAM=2;

    private int mode;
    private int index;
    private int numberOfCows;
    private String localeCode;

    private TextView nameTV;
    private TextView earTagNumberTV;
    private TextView ageTV;
    private TextView dateOfBirthTV;
    private TextView breedTV;
    private TextView sexTV;
    private Spinner sexS;
    private TextView deformityTV;
    private TextView sireTV;
    private TextView damTV;
    private Button previousButton;
    private Button nextButton;
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
            if(mode==MODE_COW)
            {
                index=bundle.getInt(KEY_INDEX);
                numberOfCows=bundle.getInt(KEY_NUMBER_OF_COWS);
            }
        }
        else
        {
            Toast.makeText(this,"Bundle is null, going rogue!",Toast.LENGTH_LONG).show();
        }

        //init child views
        nameTV=(TextView)this.findViewById(R.id.name_tv);
        earTagNumberTV=(TextView)this.findViewById(R.id.ear_tag_number_tv);
        ageTV=(TextView)this.findViewById(R.id.age_tv);
        dateOfBirthTV=(TextView)this.findViewById(R.id.date_of_birth_tv);
        breedTV=(TextView)this.findViewById(R.id.breed_tv);
        sexTV=(TextView)this.findViewById(R.id.sex_tv);
        sexS=(Spinner)this.findViewById(R.id.sex_s);
        if(mode==MODE_DAM||mode==MODE_SIRE)//TODO: remember to auto set sex in datastructure if dam or sire
        {
            sexTV.setVisibility(TextView.GONE);
            sexS.setVisibility(Spinner.GONE);
        }
        deformityTV=(TextView)this.findViewById(R.id.deformity_tv);
        sireTV=(TextView)this.findViewById(R.id.sire_tv);
        damTV=(TextView)this.findViewById(R.id.dam_tv);
        previousButton=(Button)this.findViewById(R.id.previous_button);
        previousButton.setOnClickListener(this);
        if(mode==MODE_DAM||mode==MODE_SIRE||index==0)
        {
            previousButton.setVisibility(Button.INVISIBLE);
        }
        nextButton=(Button)this.findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);

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
            dateOfBirthTV.setText(R.string.date_of_birth_en);
            breedTV.setText(R.string.breed_en);
            sexTV.setText(R.string.sex_en);
            ArrayAdapter<CharSequence> arrayAdapter=ArrayAdapter.createFromResource(this, R.array.sex_array_en, android.R.layout.simple_spinner_item);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sexS.setAdapter(arrayAdapter);
            deformityTV.setText(R.string.deformity_en);
            sireTV.setText(R.string.sire_en);
            damTV.setText(R.string.dam_en);
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
        }//TODO: implement for sire and dam


    }
}
