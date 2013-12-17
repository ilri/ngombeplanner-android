package org.cgiar.ilri.mistro.farmer.carrier;

import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * Created by jason on 8/5/13.
 */
public class Sire extends Cow {
    private String strawNumber;

    public Sire()
    {
        super(false);
        setSex(SEX_MALE);
        strawNumber="";
    }

    public void setStrawNumber(String strawNumber) {
        this.strawNumber = strawNumber;
    }

    public String getStrawNumber() {
        return strawNumber;
    }
    
    public JSONObject getJsonObject() {
        JSONObject jsonObject=super.getJsonObject();
        try
        {
            jsonObject.put("type","sire");
            jsonObject.put("strawNumber",((strawNumber==null) ? "":strawNumber));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }
}