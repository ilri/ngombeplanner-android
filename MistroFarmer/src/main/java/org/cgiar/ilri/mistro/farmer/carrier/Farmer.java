package org.cgiar.ilri.mistro.farmer.carrier;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.cgiar.ilri.mistro.farmer.backend.DataHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jason on 8/5/13.
 */
public class Farmer implements Parcelable, Serializable
{
    public static final String TAG="Farmer";
    public static final String PARCELABLE_KEY="farmer";
    public static final String MODE_INITIAL_REGISTRATION = "initialRegistration";
    public static final String MODE_NEW_COW_REGISTRATION = "newCowRegistration";
    public static final String MODE_EDIT_FARMER = "editFarmer";
    public static final String MODE_EDIT_COW = "editCow";
    private int id;
    private String fullName;
    private String extensionPersonnel;
    private String mobileNumber;
    private List<Cow> cows;
    private String longitude;
    private String latitude;
    private String simCardSN;
    private String mode;
    private String preferredLocale;
    private boolean isInFarm;
    private String site;
    private boolean isActive;

    public Farmer()
    {
        fullName="";
        extensionPersonnel="";
        mobileNumber="";
        this.cows=new ArrayList<Cow>();
        longitude="";
        latitude="";
        simCardSN ="";
        mode = "";
        preferredLocale = "";
        isInFarm = false;
        id = -1;
        site = "";
        isActive = true;
    }

    public Farmer(Parcel source)
    {
        this();
        readFromParcel(source);
    }

    public Farmer(JSONObject farmerJsonObject){
        try{
            Log.d(TAG, "Json for this farmer = "+farmerJsonObject.toString());
            id = farmerJsonObject.getInt("id");
            fullName = farmerJsonObject.getString("name");
            this.extensionPersonnel = (DataHandler.isNull(farmerJsonObject.getString("extension_personnel"))) ? "" : farmerJsonObject.getString("extension_personnel");
            mobileNumber = farmerJsonObject.getString("mobile_no");
            longitude = farmerJsonObject.getString("gps_longitude");
            latitude = farmerJsonObject.getString("gps_latitude");
            simCardSN = farmerJsonObject.getString("sim_card_sn");
            preferredLocale = farmerJsonObject.getString("pref_locale");
            site = (DataHandler.isNull(farmerJsonObject.getString("location_district"))) ? "" : farmerJsonObject.getString("location_district");
            if(farmerJsonObject.getInt("is_active") == 1){
                isActive = true;
            }
            else{
                isActive = false;
            }
            isInFarm = false;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getId(){
        return id;
    }

    public void setCows(JSONArray cowArray){
        cows = new ArrayList<Cow>(cowArray.length());
        for(int i = 0; i < cowArray.length(); i++){
            Cow currCow = new Cow(cowArray, i);
            cows.add(currCow);
        }
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setExtensionPersonnel(String extensionPersonnel)
    {
        this.extensionPersonnel = extensionPersonnel;
    }

    public void setMobileNumber(String mobileNumber)
    {
        this.mobileNumber = mobileNumber;
    }

    public int getCowNumber()
    {
        return cows.size();
    }

    public String getMode() {
        return this.mode;
    }

    /**
     * This methods inits the number of cows the farmer has.
     * Be warned that calling this function when this farmer object
     * already has cows will invalidate the previous cows.
     *
     * @param number    The number of cows
     */
    public void setCowNumber(int number)
    {
        this.cows=new ArrayList<Cow>();
        for (int i=0;i<number;i++)
        {
            cows.add(new Cow(true));
        }
    }

    public void setCows(List<Cow> cows)
    {
        this.cows = cows;
    }
    public void setCow(Cow cow, int index)
    {
        if(index<cows.size())
        {
            cows.set(index,cow);
        }
        else
        {
            Log.e(TAG,"Trying to add cow in index greater than size of Cow list");
        }
    }

    public String getPreferredLocale() {
        return preferredLocale;
    }

    public void setPreferredLocale(String preferredLocale) {
        this.preferredLocale = preferredLocale;
    }

    public void addCow(Cow cow)
    {
        this.cows.add(cow);
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public void setSimCardSN(String simCardSN)
    {
        this.simCardSN = simCardSN;
    }

    public String getFullName() {
        return fullName;
    }

    public String getExtensionPersonnel() {
        return extensionPersonnel;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public List<Cow> getCows() {
        return cows;
    }

    public boolean isInFarm(){
        return isInFarm;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public List<Cow> getCows(String sex){
        List<Cow> newCowList = new ArrayList<Cow>();

        //get all cows with the specific sex
        for(int i = 0; i < cows.size(); i++){
            if(cows.get(i).getSex().equals(sex))
                newCowList.add(cows.get(i));
        }
        return newCowList;
    }

    public Cow getCow(int index)
    {
        if(index<cows.size())
            return cows.get(index);
        else
            return null;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getSimCardSN()
    {
        return simCardSN;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(fullName);
        dest.writeString(extensionPersonnel);
        dest.writeString(mobileNumber);
        dest.writeTypedList(cows);
        dest.writeString(longitude);
        dest.writeString(latitude);
        dest.writeString(simCardSN);
        dest.writeString(mode);
        if(isInFarm) dest.writeInt(1);
        else dest.writeInt(0);
        dest.writeString(preferredLocale);
        dest.writeString(site);
        if(isActive) dest.writeInt(1);
        else dest.writeInt(0);
    }

    public void readFromParcel(Parcel in)
    {
        this.id = in.readInt();
        this.fullName=in.readString();
        this.extensionPersonnel=in.readString();
        this.mobileNumber=in.readString();
        in.readTypedList(cows,Cow.CREATOR);
        this.longitude=in.readString();
        this.latitude=in.readString();
        this.simCardSN =in.readString();
        this.mode = in.readString();

        int isInFarm = in.readInt();
        if(isInFarm == 1) this.isInFarm = true;
        else this.isInFarm = false;
        this.preferredLocale = in.readString();
        this.site = in.readString();
        int isActive = in.readInt();
        if(isActive == 1 ) this.isActive = true;
        else this.isActive = false;
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

    public JSONObject getJsonObject()
    {
        JSONObject jsonObject=new JSONObject();
        try
        {
            jsonObject.put("id", id);
            jsonObject.put("fullName",((fullName==null) ? "":fullName));
            jsonObject.put("extensionPersonnel",((extensionPersonnel==null) ? "":extensionPersonnel));
            jsonObject.put("mobileNumber",((mobileNumber==null) ? "":mobileNumber));
            JSONArray cowsJsonArray=new JSONArray();
            for (int i=0;i<cows.size();i++)
            {
                cowsJsonArray.put(i,cows.get(i).getJsonObject());
            }
            jsonObject.put("cows",cowsJsonArray);
            jsonObject.put("longitude",((longitude==null) ? "":longitude));
            jsonObject.put("latitude",((latitude==null) ? "":latitude));
            jsonObject.put("simCardSN",((simCardSN ==null) ? "": simCardSN));
            jsonObject.put("mode",((mode ==null) ? "": mode));
            jsonObject.put("preferredLocale", preferredLocale);
            jsonObject.put("site", site);
            int isActive = 1;
            if(this.isActive == false) isActive = 0;
            jsonObject.put("isActive", isActive);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return  jsonObject;
    }
}
