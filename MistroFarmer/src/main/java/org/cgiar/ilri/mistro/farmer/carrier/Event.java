package org.cgiar.ilri.mistro.farmer.carrier;

/**
 * Created by jrogena on 09/04/14.
 */
public class Event {
    private static final String TAG = "Event";
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
