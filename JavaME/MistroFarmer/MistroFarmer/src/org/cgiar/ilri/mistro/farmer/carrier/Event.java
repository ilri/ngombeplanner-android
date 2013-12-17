/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.carrier;

import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 *
 * @author jason
 */
public class Event {
    private String type;
    private String date;

    public Event(JSONObject jSONObject) {
        try {
            date = jSONObject.getString("event_date");
            type = jSONObject.getString("event_type");
        } 
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    public String getType(){
        return type;
    }
    
    public String getDate(){
        return date;
    }
}
