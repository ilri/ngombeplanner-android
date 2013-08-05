package org.cgiar.ilri.mistro.farmer.carrier;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jason on 8/5/13.
 */
public class Farmer implements Parcelable, Serializable
{
    public static final String KEY="farmer";
    private String fullName;
    private String extensionPersonnel;
    private String mobileNumber;
    private List<Cow> cows;

    public Farmer()
    {
        this.cows=new ArrayList<Cow>();
    }

    public Farmer(Parcel source)
    {
        readFromParcel(source);
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public void setExtensionPersonnel(String extensionPersonnel)
    {
        this.extensionPersonnel = extensionPersonnel;
    }

    public void setMobileNumber(String mobileNumber)
    {
        this.mobileNumber = mobileNumber;
    }

    public void setCows(List<Cow> cows)
    {
        this.cows = cows;
    }

    public void addCow(Cow cow)
    {
        this.cows.add(cow);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(fullName);
        dest.writeString(extensionPersonnel);
        dest.writeString(mobileNumber);
        dest.writeTypedList(cows);
    }

    public void readFromParcel(Parcel in)
    {
        this.fullName=in.readString();
        this.extensionPersonnel=in.readString();
        this.mobileNumber=in.readString();
        in.readTypedList(cows,Cow.CREATOR);
    }

    public static final Creator<Farmer> CREATOR=new Creator<Farmer>()
    {
        @Override
        public Farmer createFromParcel(Parcel source)
        {
            return new Farmer(source);
        }

        @Override
        public Farmer[] newArray(int size)
        {
            return new Farmer[size];
        }
    };
}
