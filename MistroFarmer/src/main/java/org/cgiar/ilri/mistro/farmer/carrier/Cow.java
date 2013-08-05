package org.cgiar.ilri.mistro.farmer.carrier;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jason on 8/5/13.
 */
public class Cow implements Parcelable, Serializable
{
    public static final int SEX_MALE=0;
    public static final int SEX_FEMALE=1;
    private String name;
    private String earTagNumber;
    private String dateOfBirth;
    private List<String> breeds;
    private int sex;
    private List<String> deformities;
    private Sire sire;
    private Dam dam;
    private String countryOfOrigin;

    public Cow()
    {
        this.breeds=new ArrayList<String>();
        this.deformities=new ArrayList<String>();
    }

    public Cow(Parcel in)
    {
        readFromParcel(in);
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setEarTagNumber(String earTagNumber)
    {
        this.earTagNumber = earTagNumber;
    }

    public void setDateOfBirth(String dateOfBirth)
    {
        this.dateOfBirth = dateOfBirth;
    }

    public void setBreeds(String[] breeds)
    {
        this.breeds=new ArrayList<String>();
        for (int i=0; i<breeds.length; i++)
        {
            this.breeds.add(breeds[i]);
        }
    }

    public void addBreed(String breed)
    {
        this.breeds.add(breed);
    }

    public void setSex(int sex)
    {
        this.sex = sex;
    }

    public void setDeformities(String[] deformities)
    {
        this.deformities=new ArrayList<String>();
        for (int i=0; i<deformities.length;i++)
        {
            this.deformities.add(deformities[i]);
        }
    }

    public void addDeformity(String deformity)
    {
        this.deformities.add(deformity);
    }

    public void setSire(Sire sire)
    {
        this.sire = sire;
    }

    public void setDam(Dam dam)
    {
        this.dam = dam;
    }

    public void setCountryOfOrigin(String countryOfOrigin)
    {
        this.countryOfOrigin = countryOfOrigin;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeString(earTagNumber);
        dest.writeString(dateOfBirth);
        dest.writeStringList(breeds);
        dest.writeInt(sex);
        dest.writeStringList(deformities);
        dest.writeSerializable(sire);
        dest.writeSerializable(dam);
        dest.writeString(countryOfOrigin);
    }

    public void readFromParcel(Parcel in)
    {
        name=in.readString();
        earTagNumber=in.readString();
        dateOfBirth=in.readString();
        in.readStringList(breeds);
        sex=in.readInt();
        in.readStringList(deformities);
        sire=(Sire)in.readSerializable();
        dam=(Dam)in.readSerializable();
        countryOfOrigin=in.readString();
    }

    public static final Creator<Cow> CREATOR = new Creator<Cow>()
    {
        @Override
        public Cow createFromParcel(Parcel source)
        {
            return new Cow(source);
        }

        @Override
        public Cow[] newArray(int size)
        {
            return new Cow[size];
        }
    };
}
