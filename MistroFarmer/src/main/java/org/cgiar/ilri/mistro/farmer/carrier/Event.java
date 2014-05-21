package org.cgiar.ilri.mistro.farmer.carrier;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jrogena on 09/04/14.
 */
public class Event {
    private static final String TAG = "Event";
    private static final String DATE_FORMAT = "YYYY-MM-dd HH:mm:ss";
    private int id;
    private String eventDate;
    private String birthType;
    private int parentCowEventID;
    private int bullID;
    private int servicingDays;
    private String cod;
    private int noOfLiveBirths;
    private String type;
    private String remarks;
    private boolean savedOnServer;
    private String dateAdded;

    public Event(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType(){
        return this.type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getRemarks(){
        return this.remarks;
    }

    public void setRemarks(String remarks){
        this.remarks = remarks;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setDateAdded(String dateAdded){
        this.dateAdded = dateAdded;
    }

    public String getDateAdded(){
        return  dateAdded;
    }

    public long getDateAddedMillisecods(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        long result = 0;

        try {
            Date dateAdded = dateFormat.parse(this.dateAdded);
            result = dateAdded.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Event date added in milliseconds = "+String.valueOf(result));

        return result;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getBirthType() {
        return birthType;
    }

    public void setBirthType(String birthType) {
        this.birthType = birthType;
    }

    public int getParentCowEventID() {
        return parentCowEventID;
    }

    public void setParentCowEventID(int parentCowEventID) {
        this.parentCowEventID = parentCowEventID;
    }

    public int getBullID() {
        return bullID;
    }

    public void setBullID(int bullID) {
        this.bullID = bullID;
    }

    public int getServicingDays() {
        return servicingDays;
    }

    public void setServicingDays(int servicingDays) {
        this.servicingDays = servicingDays;
    }

    public boolean isServicingEvent(){
        if(type.equals("Artificial Insemination") || type.equals("Bull Servicing")){
            return true;
        }
        return false;
    }

    public void setSavedOnServer(boolean savedOnServer){
        this.savedOnServer = savedOnServer;
    }

    public boolean getSavedOnServer(){
        return savedOnServer;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public int getNoOfLiveBirths() {
        return noOfLiveBirths;
    }

    public void setNoOfLiveBirths(int noOfLiveBirths) {
        this.noOfLiveBirths = noOfLiveBirths;
    }
}
