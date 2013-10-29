package org.cgiar.ilri.mistro.farmer.carrier;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * Created by jason on 8/5/13.
 */
public class Farmer {
    public static final String MODE_INITIAL_REGISTRATION = "initialRegistration";
    public static final String MODE_NEW_COW_REGISTRATION = "newCowRegistration";
    private String fullName;
    private String extensionPersonnel;
    private String mobileNumber;
    private Cow[] cows;
    private String longitude;
    private String latitude;
    private String simCardSN;
    private String mode;

    public Farmer()
    {
        fullName="";
        extensionPersonnel="";
        mobileNumber="";
        longitude="";
        latitude="";
        simCardSN ="";
        mode = "";
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setExtensionPersonnel(String extensionPersonnel) {
        this.extensionPersonnel = extensionPersonnel;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public int getCowNumber() {
        if(cows!=null){
            return cows.length;
        }
        else {
            return 0;
        }
    }

    public String getMode() {
        return this.mode;
    }

    public void setCowNumber(int number) {
        this.cows = new Cow[number];
        for (int i=0;i<number;i++) {
            cows[i] = new Cow(true);
        }
    }

    public void setCows(Cow[] cows) {
        this.cows = cows;
    }
    public void setCow(Cow cow, int index) {
        if(index<cows.length) {
            cows[index] = cow;
        }
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setSimCardSN(String simCardSN) {
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

    public Cow[] getCows() {
        return cows;
    }

    public Cow getCow(int index)
    {
        if(index<cows.length)
            return cows[index];
        else
            return null;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getSimCardSN() {
        return simCardSN;
    }

    public JSONObject getJsonObject()
    {
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("fullName",((fullName==null) ? "":fullName));
            jsonObject.put("extensionPersonnel",((extensionPersonnel==null) ? "":extensionPersonnel));
            jsonObject.put("mobileNumber",((mobileNumber==null) ? "":mobileNumber));
            JSONArray cowsJsonArray=new JSONArray();
            for (int i=0;i<cows.length;i++)
            {
                cowsJsonArray.put(i,cows[i].getJsonObject());
            }
            jsonObject.put("cows",cowsJsonArray);
            jsonObject.put("longitude",((longitude==null) ? "":longitude));
            jsonObject.put("latitude",((latitude==null) ? "":latitude));
            jsonObject.put("simCardSN",((simCardSN ==null) ? "": simCardSN));
            jsonObject.put("mode",((mode ==null) ? "": mode));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return  jsonObject;
    }
}