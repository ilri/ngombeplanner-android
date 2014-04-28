package org.cgiar.ilri.mistro.farmer.carrier;

/**
 * Created by jason on 4/28/14.
 */
public class MilkProduction {
    private static final String TAG = "MilkProduction";
    public static final String TIME_MORNING = "Morning";
    public static final String TIME_AFTERNOON = "Afternoon";
    public static final String TIME_EVENING = "Evening";
    public static final String TIME_COMBINED = "Combined";
    public static final String QUANTITY_TYPE_LITRES = "Litres";
    public static final String QUANTITY_TYPE_KGS = "KGs";
    private int id;
    private String time;
    private int quantity;
    private String dateAdded;
    private String date;
    private String quantityType;


    public MilkProduction(){
        id = -1;
        time = "";
        quantity = -1;
        dateAdded = "";
        date = "";
        quantityType = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQuantityType() {
        return quantityType;
    }

    public void setQuantityType(String quantityType) {
        this.quantityType = quantityType;
    }
}
