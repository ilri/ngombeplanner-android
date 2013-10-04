package org.cgiar.ilri.mistro.farmer.carrier;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jason on 8/5/13.
 */
public class Dam extends Cow implements Serializable
{
    private String embryoNumber;

    public Dam()
    {
        super(false);
        setSex(SEX_FEMALE);
        embryoNumber="";
    }

    public Dam(Parcel source)
    {
        this();
        readFromParcel(source);
    }

    public void setEmbryoNumber(String embryoNumber)
    {
        this.embryoNumber = embryoNumber;
    }

    public String getEmbryoNumber() {
        return embryoNumber;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeString(embryoNumber);
    }


    @Override
    public void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);
        embryoNumber=in.readString();
    }

    public static final Creator<Dam> CREATOR = new Creator<Dam>()
    {
        @Override
        public Dam createFromParcel(Parcel source)
        {
            return new Dam(source);
        }

        @Override
        public Dam[] newArray(int size)
        {
            return new Dam[size];
        }
    };

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject jsonObject=super.getJsonObject();
        try
        {
            jsonObject.put("type","dam");
            jsonObject.put("embryoNumber",((embryoNumber==null) ? "":embryoNumber));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
