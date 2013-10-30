/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.utils;

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
    private static final String BASE_URL="http://192.168.2.232/~jason/MistroFarmerProject/web";
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
    
    public static void sendDataToServer(JSONObject jSONObject){
        
    }
}
