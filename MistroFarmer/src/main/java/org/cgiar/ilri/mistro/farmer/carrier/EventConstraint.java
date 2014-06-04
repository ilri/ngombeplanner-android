package org.cgiar.ilri.mistro.farmer.carrier;

import android.util.Log;

import java.util.Date;

/**
 * Created by jrogena on 21/05/14.
 */
public class EventConstraint {
    private final String TAG = "EventConstraint";
    private final String UNIT_DAY = "Days";
    private final String UNIT_MONTH = "Months";
    private final String UNIT_YEAR = "Years";

    public static final String CONSTRAINT_MATURITY = "Maturity";
    public static final String CONSTRAINT_BIRTH_TO_LACTATION = "MaxTimeBirthLactation";
    public static final String CONSTRAINT_MILKING = "Milking";
    public static final String CONSTRAINT_CALVING = "Calving";
    public static final String CONSTRAINT_MILK_FLACTUATION = "DeltaMilk";
    public static final String CONSTRAINT_MILK_MAX_SITTING = "MaxMilkSitting";
    public static final String CONSTRAINT_MILK_MAX_COMBINED = "MaxMilkCombined";

    private int id;
    private String event;
    private int time;
    private String timeUnits;

    public EventConstraint(int id, String event, int time, String timeUnits) {
        this.id = id;
        this.event = event;
        this.time = time;
        this.timeUnits = timeUnits;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTimeUnits() {
        return timeUnits;
    }

    public void setTimeUnits(String timeUnits) {
        this.timeUnits = timeUnits;
    }

    public long getTimeMilliseconds(){
        long result = 0l;

        long timeUnits = 0l;
        if(this.timeUnits.equals(UNIT_DAY)) timeUnits = 86400000l;
        else if(this.timeUnits.equals(UNIT_MONTH)) timeUnits = 86400000l * 30;//get number of milliseconds in a month
        else if(this.timeUnits.equals(UNIT_YEAR)) timeUnits = 86400000l * 365;//get number of milliseconds in a year

        result = timeUnits * this.time;

        Log.d(TAG, "Event constraint Time milliseconds = "+String.valueOf(result));

        return result;
    }

    public int getValue(){
        return this.time;
    }

    public String getUnits(){
        return this.timeUnits;
    }
}
