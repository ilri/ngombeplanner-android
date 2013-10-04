package org.cgiar.ilri.mistro.farmer.carrier;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;

/**
 * Created by jason on 8/5/13.
 */
public class Cow implements Parcelable, Serializable {
    public static final String TAG = "Cow";
    public static final String SEX_MALE = "Male";
    public static final String SEX_FEMALE = "Female";
    public static final String AGE_TYPE_DAY = "Days";
    public static final String AGE_TYPE_WEEK = "Weeks";
    public static final String AGE_TYPE_YEAR = "Years";
    public static final String MODE_ADULT_COW_REGISTRATION = "adultCowRegistration";
    public static final String MODE_BORN_CALF_REGISTRATION = "bornCalfRegistration";
    public static final String SERVICE_TYPE_BULL = "Bull";
    public static final String SERVICE_TYPE_AI = "AI";
    public static final String SERVICE_TYPE_ET = "ET";
    private String name;
    private String earTagNumber;
    private String dateOfBirth;
    private int age;
    private String ageType;
    private List<String> breeds;
    private String sex;
    private List<String> deformities;
    private Sire sire;
    private Dam dam;
    private String countryOfOrigin;
    private boolean isNotDamOrSire;
    private String mode;
    private String serviceType;
    private String otherDeformity;
    private String piggyBack;

    public Cow(boolean isNotDamOrSire) {
        name = "";
        earTagNumber = "";
        dateOfBirth = "";
        age = -1;
        ageType = "";
        this.breeds = new ArrayList<String>();
        sex = "";
        this.deformities = new ArrayList<String>();
        this.isNotDamOrSire = isNotDamOrSire;
        if (isNotDamOrSire)//LOL, brings StackOverflowError if you init sire object inside sire object
        {
            sire = new Sire();
            dam = new Dam();
        }
        mode = "";
        countryOfOrigin = "";
        serviceType = "";
        otherDeformity = "";
        piggyBack = "";
    }

    public Cow(Parcel in) {
        this(true);
        readFromParcel(in);
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEarTagNumber(String earTagNumber) {
        this.earTagNumber = earTagNumber;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAgeType(String ageType) {
        this.ageType = ageType;
    }

    public void setBreeds(String[] breeds) {
        this.breeds = new ArrayList<String>();
        for (int i = 0; i < breeds.length; i++) {
            this.breeds.add(breeds[i]);
        }
    }

    public void setPiggyBack(String piggyBack) {
        this.piggyBack = piggyBack;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setOtherDeformity(String otherDeformity) {
        this.otherDeformity = otherDeformity;
        Log.d(TAG, "other deformity set to "+otherDeformity);
    }

    public void addBreed(String breed) {
        this.breeds.add(breed);
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setDeformities(String[] deformities) {
        this.deformities = new ArrayList<String>();
        for (int i = 0; i < deformities.length; i++) {
            this.deformities.add(deformities[i]);
        }
    }

    public void addDeformity(String deformity) {
        this.deformities.add(deformity);
    }

    public void setSire(Sire sire) {
        this.sire = sire;
    }

    public void setDam(Dam dam) {
        this.dam = dam;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getName() {
        return name;
    }

    public String getMode() {
        return mode;
    }

    public String getEarTagNumber() {
        return earTagNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public int getAge() {
        return age;
    }

    public String getPiggyBack() {
        return piggyBack;
    }

    public String getAgeType() {
        return ageType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getOtherDeformity() {
        Log.d(TAG,"other deformity is = "+otherDeformity);
        return otherDeformity;
    }

    public List<String> getBreeds() {
        return breeds;
    }

    public String getSex() {
        return sex;
    }

    public List<String> getDeformities() {
        return deformities;
    }

    public Sire getSire() {
        return sire;//TODO: handle nullpointerexception
    }

    public Dam getDam() {
        if (dam == null) {
            Log.d(TAG, "dam is null");
        }
        return dam;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(earTagNumber);
        dest.writeString(dateOfBirth);
        dest.writeInt(age);
        dest.writeString(ageType);
        dest.writeStringList(breeds);
        dest.writeString(sex);
        dest.writeStringList(deformities);
        if (isNotDamOrSire) {
            dest.writeInt(1);
            dest.writeSerializable(sire);
            dest.writeSerializable(dam);
        } else {
            dest.writeInt(0);
        }

        dest.writeString(countryOfOrigin);
        dest.writeString(mode);
        dest.writeString(serviceType);
        dest.writeString(otherDeformity);
        dest.writeString(piggyBack);
    }

    public void readFromParcel(Parcel in) {
        name = in.readString();
        earTagNumber = in.readString();
        dateOfBirth = in.readString();
        age = in.readInt();
        ageType = in.readString();
        in.readStringList(breeds);
        sex = in.readString();
        in.readStringList(deformities);
        int x = in.readInt();
        if (x == 1)//isnotsireordam
        {
            this.isNotDamOrSire = true;
            sire = (Sire) in.readSerializable();
            dam = (Dam) in.readSerializable();
        } else {
            this.isNotDamOrSire = false;
        }

        countryOfOrigin = in.readString();
        mode = in.readString();
        serviceType = in.readString();
        otherDeformity = in.readString();
        piggyBack = in.readString();
    }

    public static final Creator<Cow> CREATOR = new Creator<Cow>() {
        @Override
        public Cow createFromParcel(Parcel source) {
            return new Cow(source);
        }

        @Override
        public Cow[] newArray(int size) {
            return new Cow[size];
        }
    };

    public JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", ((name == null) ? "" : name));
            jsonObject.put("earTagNumber", ((earTagNumber == null) ? "" : earTagNumber));
            jsonObject.put("dateOfBirth", ((dateOfBirth == null) ? "" : dateOfBirth));
            jsonObject.put("age", age);
            jsonObject.put("ageType", ageType);
            JSONArray breedJsonArray = new JSONArray();
            for (int i = 0; i < breeds.size(); i++) {
                breedJsonArray.put(i, breeds.get(i));
            }
            jsonObject.put("breeds", breedJsonArray);
            jsonObject.put("sex", sex);
            JSONArray deformityJsonArray = new JSONArray();
            for (int i = 0; i < deformities.size(); i++) {
                deformityJsonArray.put(i, deformities.get(i));
            }
            jsonObject.put("deformities", deformityJsonArray);
            jsonObject.put("mode", ((mode == null) ? "" : mode));
            jsonObject.put("serviceType", ((serviceType == null) ? "" : serviceType));
            jsonObject.put("otherDeformity", ((otherDeformity == null) ? "" : otherDeformity));
            jsonObject.put("countryOfOrigin", ((countryOfOrigin == null) ? "" : countryOfOrigin));
            if (isNotDamOrSire) {
                jsonObject.put("type", "cow");
                jsonObject.put("sire", ((sire == null) ? "" : sire.getJsonObject()));
                jsonObject.put("dam", ((dam == null) ? "" : dam.getJsonObject()));
            }
            if(piggyBack!=null && piggyBack.length()>0) {
                jsonObject.put("piggyBack",piggyBack);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
