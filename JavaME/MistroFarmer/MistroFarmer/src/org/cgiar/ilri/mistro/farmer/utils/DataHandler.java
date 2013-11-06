/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import org.json.me.JSONObject;

/**
 *
 * @author jason
 */
public class DataHandler {
    public static final String ACKNOWLEDGE_OK="533783";
    public static final String NO_DATA="77732";
    public static final String DATA_ERROR="934342";
    public static final String CODE_USER_NOT_AUTHENTICATED="43322";
    public static final String CODE_SIM_CARD_REGISTERED="83242";
    private static final String TAG="DataHandler";
    private static final int HTTP_POST_TIMEOUT =20000;
    private static final int HTTP_RESPONSE_TIMEOUT =20000;
    private static final String BASE_URL="http://127.0.0.1/~jason/MistroFarmerProject/web";
    //private static final String BASE_URL="http://hpc.ilri.cgiar.org/~jrogena/mistro_web";
    public static final String FARMER_REGISTRATION_URL="/php/farmer/registration.php";
    public static final String FARMER_AUTHENTICATION_URL="/php/farmer/authentication.php";
    public static final String FARMER_SIM_CARD_REGISTRATION_URL="/php/farmer/sim_card_registration.php";
    public static final String FARMER_FETCH_COW_IDENTIFIERS_URL="/php/farmer/fetch_cow_identifiers.php";
    public static final String FARMER_ADD_MILK_PRODUCTION_URL="/php/farmer/add_milk_production.php";
    public static final String FARMER_FETCH_MILK_PRODUCTION_HISTORY_URL="/php/farmer/fetch_milk_production_history.php";
    public static final String FARMER_ADD_COW_EVENT_URL="/php/farmer/add_cow_event.php";
    public static final String FARMER_FETCH_COW_EVENTS_HISTORY_URL="/php/farmer/fetch_cow_events_history.php";
    public static final String FARMER_FETCH_COW_SERVICING_EVENTS_URL="/php/farmer/fetch_servicing_events.php";
    public static final String SP_KEY_LOCALE = "locale";
    public static final String SP_KEY_MILK_QUANTITY_TYPE = "milkQuantityTYpe";
    
    public static String sendDataToServer(JSONObject jSONObject, String appendedURL){
        HttpConnection hc = null;
        OutputStream out = null;
        InputStream in = null;
        try
        {
            hc = (HttpConnection) Connector.open(BASE_URL+appendedURL);
            hc.setRequestMethod(HttpConnection.POST);
            hc.setRequestProperty("User-Agent","Profile/MIDP-1.0 Confirguration/CLDC-1.0");
            hc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String outData = "json="+jSONObject.toString(); 
            out = hc.openOutputStream();
            out.write(outData.getBytes());
            //out.flush();
            /*in = hc.openInputStream();
            int length = (int) hc.getLength();
            byte[] inData = new byte[length];
            in.read(inData);
            String response = new String(inData); //response can either contain the name of the xml to be fetched or the errors
            */
            StringBuffer stringBuffer = new StringBuffer();
            in = hc.openDataInputStream();
            int chr;
            while ((chr = in.read()) != -1)
            {
                stringBuffer.append((char) chr);
            }
            String response=stringBuffer.toString();
            return response;

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if(in!= null)
            {
                try
                {
                    in.close();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
            if(out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
            if(hc != null)
            {
                try
                {
                    hc.close();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
}
