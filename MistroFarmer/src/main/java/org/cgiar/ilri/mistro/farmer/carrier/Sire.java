package org.cgiar.ilri.mistro.farmer.carrier;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jason on 8/5/13.
 */
public class Sire extends Cow implements Serializable
{
    public static final String SIRE_OWNER_OWN_BULL = "";
    private String strawNumber;
    private String owner;
    private String ownerType;

    public Sire()
    {
        super(false);
        setSex(SEX_MALE);
        strawNumber="";
        owner = "";
        ownerType = "";
    }

    public Sire(Parcel source) {
        this();
        readFromParcel(source);
    }

    public void setStrawNumber(String strawNumber)
    {
        this.strawNumber = strawNumber;
    }

    public String getStrawNumber() {
        return strawNumber;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }

    public String getOwner(){
        return this.owner;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
        dest.writeString(strawNumber);
        dest.writeString(owner);
        dest.writeString(ownerType);
    }

    @Override
    public void readFromParcel(Parcel in)
    {
        super.readFromParcel(in);
        strawNumber=in.readString();
        owner=in.readString();
        ownerType=in.readString();
    }

    public static final Creator<Sire> CREATOR=new Creator<Sire>()
    {
        @Override
        public Sire createFromParcel(Parcel source)
        {
            return new Sire(source);
        }

        @Override
        public Sire[] newArray(int size)
        {
            return new Sire[size];
        }
    };

    @Override
    public JSONObject getJsonObject()
    {
        JSONObject jsonObject=super.getJsonObject();
        try
        {
            jsonObject.put("type","sire");
            jsonObject.put("strawNumber",((strawNumber==null) ? "":strawNumber));
            jsonObject.put("owner", owner);
            jsonObject.put("ownerType", ownerType);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
