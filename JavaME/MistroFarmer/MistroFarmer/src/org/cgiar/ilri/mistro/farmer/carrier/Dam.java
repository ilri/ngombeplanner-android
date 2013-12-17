package org.cgiar.ilri.mistro.farmer.carrier;

import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * Created by jason on 8/5/13.
 */
public class Dam extends Cow {
    private String embryoNumber;

    public Dam() {
        super(false);
        setSex(SEX_FEMALE);
        embryoNumber="";
    }

    public void setEmbryoNumber(String embryoNumber) {
        this.embryoNumber = embryoNumber;
    }

    public String getEmbryoNumber() {
        return embryoNumber;
    }
    
   
    public JSONObject getJsonObject() {
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