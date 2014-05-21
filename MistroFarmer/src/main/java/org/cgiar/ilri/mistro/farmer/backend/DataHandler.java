package org.cgiar.ilri.mistro.farmer.backend;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.cgiar.ilri.mistro.farmer.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.cgiar.ilri.mistro.farmer.Utils;
import org.cgiar.ilri.mistro.farmer.backend.database.DatabaseHelper;
import org.cgiar.ilri.mistro.farmer.carrier.Cow;
import org.cgiar.ilri.mistro.farmer.carrier.Dam;
import org.cgiar.ilri.mistro.farmer.carrier.Event;
import org.cgiar.ilri.mistro.farmer.carrier.EventConstraint;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.carrier.MilkProduction;
import org.cgiar.ilri.mistro.farmer.carrier.Sire;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jason on 8/6/13.
 */
public class DataHandler
{
    public static final String ACKNOWLEDGE_OK="533783";
    public static final String NO_DATA="77732";
    public static final String DATA_ERROR="934342";
    public static final String CODE_USER_NOT_AUTHENTICATED="43322";
    public static final String CODE_SIM_CARD_REGISTERED="83242";
    private static final String TAG="DataHandler";
    private static final long SMS_RESPONSE_TIMEOUT = 300000;
    private static final int HTTP_POST_TIMEOUT =20000;
    private static final int HTTP_RESPONSE_TIMEOUT =20000;
    public static final String SMS_SERVER_ADDRESS = "+254708090206";
    private static final String BASE_URL="http://azizi.ilri.cgiar.org/ngombe_planner";
    //private static final String BASE_URL="http://192.168.14.102/~jason/ngombe_planner/WebServer";
    //private static final String BASE_URL="http://172.26.23.48/~jason/ngombe_planner/WebServer";
    public static final String FARMER_REGISTRATION_URL="/php/farmer/registration.php";
    public static final String FARMER_AUTHENTICATION_URL="/php/farmer/authentication.php";
    public static final String FARMER_SIM_CARD_REGISTRATION_URL="/php/farmer/sim_card_registration.php";
    public static final String FARMER_FETCH_COW_IDENTIFIERS_URL="/php/farmer/fetch_cow_identifiers.php";
    public static final String FARMER_ADD_MILK_PRODUCTION_URL="/php/farmer/add_milk_production.php";
    public static final String FARMER_FETCH_MILK_PRODUCTION_HISTORY_URL="/php/farmer/fetch_milk_production_history.php";
    public static final String FARMER_ADD_COW_EVENT_URL="/php/farmer/add_cow_event.php";
    public static final String FARMER_FETCH_COW_EVENTS_HISTORY_URL="/php/farmer/fetch_cow_events_history.php";
    public static final String FARMER_FETCH_COW_SERVICING_EVENTS_URL="/php/farmer/fetch_servicing_events.php";
    public static final String FARMER_REGISTER_FARM_COORDS_URL="/php/farmer/register_farm_coords.php";
    public static final String FARMER_ADD_CACHED_DATA_URL="/php/farmer/add_cached_data.php";
    public static final String SP_KEY_LOCALE = "locale";
    public static final String SP_KEY_MILK_QUANTITY_TYPE = "milkQuantityTYpe";
    public static final String SP_KEY_USE_SMS_TO_SEND_DATA = "useSMSToSendData";
    public static final String SP_KEY_SMS_RESPONSE = "smsResponse";
    public static final String SP_KEY_SMS_CACHE = "smsCache";

    public static final String SP_KEY_AEA_DATE = "addEventActivityDate";
    public static final String SP_KEY_AEA_REMARKS = "addEventActivityRemarks";
    public static final String SP_KEY_AEA_STRAW_NUMBER = "addEventActivityStrawNumber";
    public static final String SP_KEY_AEA_VET_USED = "addEventActivityVetUsed";
    public static final String SP_KEY_AEA_BULL_NAME = "addEventActivityBullName";
    public static final String SP_KEY_AEA_BULL_OWNER = "addEventActivityBullOwner";
    public static final String SP_KEY_AEA_NO_SERVICING_DAYS = "addEventActivityNoServicingDays";
    public static final String SP_KEY_AMPA_DATE = "addMilkProductionActivityDate";
    public static final String SP_KEY_AMPA_QUANTITY = "addMilkProductionActivityQuantity";
    public static final String SP_KEY_CRA_NAME = "cowRegistrationActivityName";
    public static final String SP_KEY_CRA_EAR_TAG_NUMBER = "cowRegistrationActivityEarTagNumber";
    public static final String SP_KEY_CRA_AGE = "cowRegistrationActivityAge";
    public static final String SP_KEY_CRA_DATE_OF_BIRTH = "cowRegistrationActivityDateOfBirth";
    public static final String SP_KEY_CRA_BREED = "cowRegistrationActivityBreed";
    public static final String SP_KEY_CRA_DEFORMITY = "cowRegistrationActivityDeformity";
    public static final String SP_KEY_CRA_STRAW_NUMBER = "cowRegistrationActivityStrawNumber";
    public static final String SP_KEY_CRA_DAM = "cowRegistrationActivityDam";
    public static final String SP_KEY_CRA_EMBRYO_NUMBER = "cowRegistrationActivityEmbryoNumber";
    public static final String SP_KEY_CRA_COUNTRY_OF_ORIGIN = "cowRegistrationActivityCountryOfOrigin";
    public static final String SP_KEY_FRA_FULL_NAME = "farmerRegistrationActivityFullName";
    public static final String SP_KEY_FRA_EXTENSION_PERSONNEL = "farmerRegistrationActivityExtensionPersonnel";
    public static final String SP_KEY_FRA_MOBILE_NUMBER = "farmerRegistrationActivityMobileNumber";

    public static final String CAN_SEND_USING_SMS = "canSend";
    public static final String CANNOT_SEND_USING_SMS = "cannotSend";
    public static final String SMS_DELIMITER = "#*#*";
    public static final String ACTION_SMS_SENT = "SMS_SENT_MISTRO";
    public static final String ACTION_SMS_DELIVERED = "SMS_DELIVERED_MISTRO";
    public static final String ACTION_SMS_RECEIVED = "SMS_RECEIVED_MISTRO";
    public static final String SMS_ERROR_GENERIC_FAILURE = "sms_generic_failure_error";
    public static final String SMS_ERROR_NO_SERVICE = "sms_no_service_error";
    public static final String SMS_ERROR_RADIO_OFF = "sms_radio_off_error";
    public static final String SMS_ERROR_RESULT_CANCELLED = "sms_result_cancelled_error";

    /**
     * This method checks whether the application can access the internet
     *
     * @param context   The activity/service from where you want to check for the connection
     *
     * @return  True if the application can connect to the internet and False if not
     */
    public static boolean checkNetworkConnection(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null){ //no connection
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * This method checks if an internet connection is available and asks the user whether it can use SMS instead
     * if no internet connection is found.
     * Please do not run this method in the UI thread.
     *
     * @param context   The activity/service from where you want to check for the connection
     */
    public static void requestPermissionToUseSMS(final Context context){
        if(!checkNetworkConnection(context)){
            if(getSharedPreference(context, SP_KEY_USE_SMS_TO_SEND_DATA,CANNOT_SEND_USING_SMS).equals(CANNOT_SEND_USING_SMS)){
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==DialogInterface.BUTTON_POSITIVE){
                            dialog.dismiss();
                            setSharedPreference(context, SP_KEY_USE_SMS_TO_SEND_DATA,CAN_SEND_USING_SMS);
                        }
                        else{
                            dialog.cancel();
                            setSharedPreference(context, SP_KEY_USE_SMS_TO_SEND_DATA,CANNOT_SEND_USING_SMS);
                        }
                    }
                };
                AlertDialog simCardAlertDialog = Utils.createSMSDialog(context, onClickListener);
                simCardAlertDialog.show();
            }
        }
        else{
            setSharedPreference(context, SP_KEY_USE_SMS_TO_SEND_DATA,CANNOT_SEND_USING_SMS);
        }
    }

    /**
     * This method sends the string corresponding to a jsonObject or jsonArray to the server. Note that if string is not from the specified types of
     * objects the server might not reply as expected.
     * This method should only be called from within an asynchronous thread. Refer to android.os.AsyncTask
     *
     * @param context The context from where the data is being sent
     * @param jsonString The string corresponding to either a jsonObject or a jsonArray
     * @param appendedURL The page on the server to which the data is to be sent. All pages accessible from this app are specified in DataHandler
     *                    eg DataHandler.FARMER_REGISTRATION_URL
     * @param waitForResponse Set to true if UI will be waiting for a response from the server
     *
     * @return The response from the server
     */
    public static String sendDataToServer(Context context, String jsonString, String appendedURL, boolean waitForResponse) {
        String response;
        if(checkNetworkConnection(context)){
            response = sendDataUsingHttpConnection(context, jsonString, appendedURL);
        }
        else{
            response = sendDataUsingSMS(context, jsonString, appendedURL, waitForResponse);
        }
        return response;
    }

    /**
     * This method sends data to the server using SMS
     *
     * @param context   The activity/service sending the data
     * @param jsonString    The json string to be sent
     * @param appendedURL   The page on the server to which the data is to be sent. All pages accessible from this app are specified in DataHandler
     *                      eg DataHandler.FARMER_REGISTRATION_URL
     * @param waitForResponse   Set to true if UI will be waiting for a response from the server
     *
     * @return  The response from the server
     */
    private static String sendDataUsingSMS(final Context context, String jsonString, String appendedURL, boolean waitForResponse){
        if(getSharedPreference(context, SP_KEY_USE_SMS_TO_SEND_DATA,CANNOT_SEND_USING_SMS).equals(CAN_SEND_USING_SMS)){
            //clear shared preference meant to store server's response
            setSharedPreference(context, SP_KEY_SMS_RESPONSE, "");
            setSharedPreference(context, SP_KEY_SMS_CACHE, "");

            SmsManager smsManager = SmsManager.getDefault();
            String message = appendedURL+SMS_DELIMITER+jsonString;
            ArrayList<String> multipartMessage = smsManager.divideMessage(message);
            int noOfParts = multipartMessage.size();


            MistroSMSSentReceiver mistroSMSSentReceiver = new MistroSMSSentReceiver(message, noOfParts);
            context.registerReceiver(mistroSMSSentReceiver, new IntentFilter(ACTION_SMS_SENT));
            MistroSMSDeliveredReceiver mistroSMSDeliveredReceiver = new MistroSMSDeliveredReceiver(message, noOfParts);
            context.registerReceiver(mistroSMSDeliveredReceiver, new IntentFilter(ACTION_SMS_DELIVERED));
            if(waitForResponse){//method will be waiting for a response sms from the server
                MistroSMSReceiver mistroSMSReceiver = new MistroSMSReceiver();
                IntentFilter smsReceivedIntentFilter = new IntentFilter(ACTION_SMS_RECEIVED);
                smsReceivedIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
                context.registerReceiver(mistroSMSReceiver, smsReceivedIntentFilter);
            }


            //register a new sent and delivered intent for each of the parts
            ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
            ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();
            for(int i = 0; i<noOfParts; i++){
                PendingIntent newSentPE = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_SENT), 0);
                sentPendingIntents.add(newSentPE);
                PendingIntent newDeliveredPE = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SMS_DELIVERED), 0);
                deliveredPendingIntents.add(newDeliveredPE);
            }
            smsManager.sendMultipartTextMessage(SMS_SERVER_ADDRESS, null, multipartMessage, sentPendingIntents, deliveredPendingIntents);

            long startTime = System.currentTimeMillis();
            if(waitForResponse){
                while(true){
                    long currTime = System.currentTimeMillis();
                    long timeDiff = currTime - startTime;
                    if(getSharedPreference(context, SP_KEY_SMS_RESPONSE,"").length()>0){
                        return getSharedPreference(context, SP_KEY_SMS_RESPONSE,"");
                    }
                    else if(timeDiff>SMS_RESPONSE_TIMEOUT){
                        Log.w(TAG, "SMS response timeout exceeded");
                        return null;
                    }
                }
            }
            else{
                return ACKNOWLEDGE_OK;
            }
        }
        return null;
    }

    private static String sendDataUsingHttpConnection(Context context, String jsonString, String appendedURL) {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, HTTP_POST_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, HTTP_RESPONSE_TIMEOUT);
        HttpClient httpClient=new DefaultHttpClient(httpParameters);
        HttpPost httpPost=new HttpPost(BASE_URL+appendedURL);
        try{
            List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("json", jsonString));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse httpResponse=httpClient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()==200)
            {
                HttpEntity httpEntity=httpResponse.getEntity();
                if(httpEntity!=null)
                {
                    InputStream inputStream=httpEntity.getContent();
                    String responseString=convertStreamToString(inputStream);
                    return responseString.trim();
                }
            }
            else
            {
                Log.e(TAG, "Status Code "+String.valueOf(httpResponse.getStatusLine().getStatusCode())+" passed");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            setSharedPreference(context, "http_error", e.getMessage());
        }
        if(isConnectedToServer(HTTP_POST_TIMEOUT)){
            setSharedPreference(context, "http_error", "This application was unable to reach http://azizi.ilri.cgiar.org within "+String.valueOf(HTTP_POST_TIMEOUT/1000)+" seconds. Try resetting your network connection");
        }
        return  null;
    }

    private static boolean isConnectedToServer(int timeout) {
        try{
            URL myUrl = new URL("http://azizi.ilri.cgiar.org");
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        } catch (Exception e) {
            // Handle your exceptions
            return false;
        }
    }

    private static String convertStreamToString(InputStream inputStream)
    {
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder=new StringBuilder();
        String line=null;
        try
        {
            while((line=bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line+"\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                inputStream.close();

            } catch (Exception e2)
            {
                e2.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    /**
     * This method sets a shared preference to the specified value. Note that shared preferences can only handle strings
     *
     * @param context The context from where you want to set the value
     * @param sharedPreferenceKey The key corresponding to the shared preference. All shared preferences accessible by this app are defined in
     *                            DataHandler e.g DataHandler.SP_KEY_LOCALE
     * @param value The value the sharedPreference is to be set to
     */
    public static void setSharedPreference(Context context, String sharedPreferenceKey, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.app_name),Context.MODE_PRIVATE).edit();
        editor.putString(sharedPreferenceKey,value);
        editor.commit();
        Log.d(TAG, sharedPreferenceKey+" shared preference saved as "+value);
    }

    /**
     * Gets the vaule of a shared preference accessible by the context
     *
     * @param context Context e.g activity that is requesting for the shared preference
     * @param sharedPreferenceKey The key corresponding to the shared preference. All shared preferences accessible by this app are defined in
     *                            DataHandler e.g DataHandler.SP_KEY_LOCALE
     * @param defaultValue What will be returned by this method if the sharedPreference is empty or unavailable
     *
     * @return The value of the sharedPreference or the default value specified if the sharedPreference is empty
     */
    public static String getSharedPreference(Context context, String sharedPreferenceKey, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        Log.d(TAG, "value of "+sharedPreferenceKey+" is "+sharedPreferences.getString(sharedPreferenceKey, defaultValue));
        return sharedPreferences.getString(sharedPreferenceKey, defaultValue);
    }

    /**
     * Responsible for handling errors that might occur while sms is sending
     */
    private static class MistroSMSSentReceiver extends BroadcastReceiver{

        private final int noOfParts;
        private final String originalMessage;
        private int numberSent;
        private int numberNotSent;

        public MistroSMSSentReceiver(String originalMessage, int noOfParts){
            this.noOfParts = noOfParts;
            this.originalMessage = originalMessage;

            numberSent = 0;
            numberNotSent = 0;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //check if messages sent successfully
            switch (getResultCode()){
                case Activity.RESULT_OK:
                    numberSent++;
                    //do not put anything in the sharedPreference. Silence means everyting is A-Okay
                    Log.d(TAG, "Message  "+String.valueOf(numberSent)+" of "+String.valueOf(noOfParts)+" successfully sent");
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    numberNotSent++;
                    if(getSharedPreference(context, SP_KEY_SMS_RESPONSE,"").length()==0){
                        setSharedPreference(context, SP_KEY_SMS_RESPONSE, SMS_ERROR_GENERIC_FAILURE);
                    }
                    Log.e(TAG, "Message "+String.valueOf(numberNotSent)+" of "+String.valueOf(noOfParts)+" not sent due to GENERIC_FAILURE error");
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    numberNotSent++;
                    if(getSharedPreference(context, SP_KEY_SMS_RESPONSE,"").length()==0){
                        setSharedPreference(context, SP_KEY_SMS_RESPONSE, SMS_ERROR_NO_SERVICE);
                    }
                    Log.e(TAG, "Message "+String.valueOf(numberNotSent)+" of "+String.valueOf(noOfParts)+" not sent due to NO_SERVICE error");
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    numberNotSent++;
                    if(getSharedPreference(context, SP_KEY_SMS_RESPONSE,"").length()==0){
                        setSharedPreference(context, SP_KEY_SMS_RESPONSE, SMS_ERROR_GENERIC_FAILURE);
                    }
                    Log.e(TAG, "Message "+String.valueOf(numberNotSent)+" of "+String.valueOf(noOfParts)+" not sent due to NULL_PDU error");
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    numberNotSent++;
                    if(getSharedPreference(context, SP_KEY_SMS_RESPONSE,"").length()==0){
                        setSharedPreference(context, SP_KEY_SMS_RESPONSE, SMS_ERROR_RADIO_OFF);
                    }
                    Log.e(TAG, "Message "+String.valueOf(numberNotSent)+" of "+String.valueOf(noOfParts)+" not sent due to RADIO_OFF error");
                    break;
            }
        }
    }

    /**
     * Handles errors that might occur when SMS has been sent and not yet received on the other side
     */
    private static class MistroSMSDeliveredReceiver extends BroadcastReceiver{

        private final String originalMessage;
        private final int noOfParts;
        private int noDelivered;
        private int noNotDelivered;

        public MistroSMSDeliveredReceiver(String originalMessage, int noOfParts){
            this.originalMessage = originalMessage;
            this.noOfParts = noOfParts;

            noDelivered = 0;
            noNotDelivered = 0;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    noDelivered++;
                    Log.d(TAG, "Message  "+String.valueOf(noDelivered)+" of "+String.valueOf(noOfParts)+" successfully sent");
                    //do not insert anything into sharedPreferences, silence means everything is okay
                    break;
                case Activity.RESULT_CANCELED:
                    noNotDelivered++;
                    if(getSharedPreference(context, SP_KEY_SMS_RESPONSE,"").length()==0){
                        setSharedPreference(context, SP_KEY_SMS_RESPONSE, SMS_ERROR_RESULT_CANCELLED);
                    }
                    Log.e(TAG, "Message "+String.valueOf(noNotDelivered)+" of "+String.valueOf(noOfParts)+" not sent due to RADIO_OFF error");
                    break;
            }
        }
    }

    /**
     * This method saves farmer details into a SQLite database.
     * Run this method in a thread running asynchronously to the UI thread.
     *
     * @param context The activity/service from where you want to save the farmer details
     * @param farmerData Data for the farmer represented as a JSONObject
     */
    public static void saveFarmerData(Context context, JSONObject farmerData){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase writableDB = databaseHelper.getWritableDatabase();


        //insert farmer data
        if(writableDB.isOpen()){
            try{
                //remove all the data associated to farmer
                databaseHelper.runTruncateQuery(writableDB, databaseHelper.TABLE_FARMER);
                databaseHelper.runTruncateQuery(writableDB, databaseHelper.TABLE_COW);
                databaseHelper.runTruncateQuery(writableDB, databaseHelper.TABLE_MILK_PRODUCTION);
                databaseHelper.runTruncateQuery(writableDB, databaseHelper.TABLE_EVENT);

                String[] columns = new String[]{"id","name","mobile_no","location_county","location_district","gps_longitude", "gps_latitude", "date_added", "sim_card_sn"};
                String[] columnValues = new String[columns.length];

                columnValues[0] = farmerData.getString("id");
                columnValues[1] = farmerData.getString("name");
                columnValues[2] = farmerData.getString("mobile_no");
                columnValues[3] = farmerData.getString("location_county");
                columnValues[4] = farmerData.getString("location_district");
                columnValues[5] = farmerData.getString("gps_longitude");
                columnValues[6] = farmerData.getString("gps_latitude");
                columnValues[7] = farmerData.getString("date_added");
                columnValues[8] = farmerData.getString("sim_card_sn");

                databaseHelper.runInsertQuery(databaseHelper.TABLE_FARMER, columns, columnValues, 0, writableDB);

                //insert cow data
                JSONArray cowData = farmerData.getJSONArray("cows");
                for(int i = 0; i < cowData.length(); i++){
                    JSONObject currCow = cowData.getJSONObject(i);
                    columns = new String[]{"id", "farmer_id", "name", "ear_tag_number", "date_of_birth", "age", "age_type", "sex", "sire_id", "dam_id", "date_added", "service_type", "country_id", "bull_owner", "owner_name"};
                    columnValues = new String[columns.length];

                    columnValues[0] = currCow.getString("id");
                    columnValues[1] = currCow.getString("farmer_id");
                    columnValues[2] = currCow.getString("name");
                    columnValues[3] = currCow.getString("ear_tag_number");
                    columnValues[4] = currCow.getString("date_of_birth");
                    columnValues[5] = currCow.getString("age");
                    columnValues[6] = currCow.getString("age_type");
                    columnValues[7] = currCow.getString("sex");
                    columnValues[8] = currCow.getString("sire_id");
                    columnValues[9] = currCow.getString("dam_id");
                    columnValues[10] = currCow.getString("date_added");
                    columnValues[11] = currCow.getString("service_type");
                    columnValues[12] = currCow.getString("country_id");
                    columnValues[13] = currCow.getString("bull_owner");
                    columnValues[14] = currCow.getString("owner_name");

                    databaseHelper.runInsertQuery(databaseHelper.TABLE_COW, columns, columnValues, 0, writableDB);

                    JSONArray cowEvents = currCow.getJSONArray("events");
                    for(int j = 0; j < cowEvents.length(); j++){
                        JSONObject currEvent = cowEvents.getJSONObject(j);

                        columns = new String[]{"id", "cow_id", "event_name", "remarks", "event_date", "birth_type", "parent_cow_event", "bull_id", "servicing_days", "cod", "no_of_live_births", "saved_on_server", "date_added"};
                        columnValues = new String[columns.length];

                        columnValues[0] = currEvent.getString("id");
                        columnValues[1] = currEvent.getString("cow_id");
                        columnValues[2] = currEvent.getString("event_name");
                        columnValues[3] = currEvent.getString("remarks");
                        columnValues[4] = currEvent.getString("event_date");
                        columnValues[5] = currEvent.getString("birth_type");
                        columnValues[6] = currEvent.getString("parent_cow_event");
                        columnValues[7] = currEvent.getString("bull_id");
                        columnValues[8] = currEvent.getString("servicing_days");
                        columnValues[9] = currEvent.getString("cause_of_death");
                        columnValues[10] = currEvent.getString("no_of_live_births");
                        columnValues[11] = "1";
                        columnValues[12] = currEvent.getString("date_added");

                        databaseHelper.runInsertQuery(databaseHelper.TABLE_EVENT, columns, columnValues, 0, writableDB);
                    }

                    JSONArray cowMilkProduction = currCow.getJSONArray("milk_production");
                    for(int j = 0; j < cowMilkProduction.length(); j++){
                        JSONObject currMProduction = cowMilkProduction.getJSONObject(j);
                        //(id INTEGER PRIMARY KEY, cow_id INTEGER, time TEXT, quantity INTEGER, date_added TEXT, date TEXT, quantity_type TEXT)");
                        columns = new String[]{"id", "cow_id", "time", "quantity", "date_added", "date", "quantity_type"};

                        columnValues = new String[columns.length];

                        columnValues[0] = currMProduction.getString("id");
                        columnValues[1] = currMProduction.getString("cow_id");
                        columnValues[2] = currMProduction.getString("time");
                        columnValues[3] = currMProduction.getString("quantity");
                        columnValues[4] = currMProduction.getString("date_added");
                        columnValues[5] = currMProduction.getString("date");
                        columnValues[6] = currMProduction.getString("quantity_type");

                        databaseHelper.runInsertQuery(databaseHelper.TABLE_MILK_PRODUCTION, columns, columnValues, 0, writableDB);

                    }
                }

                JSONArray eventsConstraints = farmerData.getJSONArray("event_constraints");
                for(int i = 0; i < eventsConstraints.length(); i++){
                    JSONObject currConstraint = eventsConstraints.getJSONObject(i);

                    //id INTEGER PRIMARY KEY, event TEXT, time INTEGER, time_units TEXT
                    columns = new String[]{"id", "event", "time", "time_units"};
                    columnValues = new String[columns.length];

                    columnValues[0] = currConstraint.getString("id");
                    columnValues[1] = currConstraint.getString("event");
                    columnValues[2] = currConstraint.getString("time");
                    columnValues[3] = currConstraint.getString("time_units");

                    databaseHelper.runInsertQuery(databaseHelper.TABLE_EVENTS_CONSTRAINTS, columns, columnValues, 0, writableDB);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            Log.e(TAG, "Writable database did not open. Was unable to save cow data into the SQLite DB");
        }

        //close the database
        writableDB.close();
        databaseHelper.close();
    }

    /**
     * This method gets cached farmer data from the SQLite database
     * @param context The activity/service from where you want to get the farmer data
     *
     * @return Returns null if something goes wrong or a farmer object if successful
     */
    public static Farmer getFarmerData(Context context){
        Farmer farmer = null;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase readableDB = databaseHelper.getReadableDatabase();

        //fetch farmer data
        TelephonyManager telephonyManager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String simCardSN = telephonyManager.getSimSerialNumber();

        String[] columns = new String[]{"id","name","mobile_no","gps_longitude", "gps_latitude", "sim_card_sn"};
        String selection  = "sim_card_sn='"+simCardSN+"'";
        String[][] farmerResult = databaseHelper.runSelectQuery(readableDB,databaseHelper.TABLE_FARMER, columns, selection, null, null, null, null, null);
        if(farmerResult.length == 1){//only one farmer should have this sim card sn

            String farmerID = farmerResult[0][0];
            farmer = new Farmer();
            farmer.setFullName(farmerResult[0][1]);
            farmer.setMobileNumber(farmerResult[0][2]);
            farmer.setLongitude(farmerResult[0][3]);
            farmer.setLatitude(farmerResult[0][4]);
            farmer.setSimCardSN(farmerResult[0][5]);

            //fetch cow data
            columns = new String[]{"id", "name", "ear_tag_number", "date_of_birth", "age", "age_type", "sex", "sire_id", "dam_id", "date_added", "service_type", "country_id", "bull_owner", "owner_name"};
            selection = "farmer_id="+farmerID;

            String[][] cowResult = databaseHelper.runSelectQuery(readableDB, databaseHelper.TABLE_COW, columns, selection, null, null, null, null, null);
            if(cowResult.length > 0){
                for(int cowIndex = 0 ; cowIndex < cowResult.length; cowIndex++){
                    Cow currCow = new Cow(true);

                    String cowID = cowResult[cowIndex][0];
                    currCow.setName(cowResult[cowIndex][1]);
                    currCow.setEarTagNumber(cowResult[cowIndex][2]);
                    Log.d(TAG, "Current cow's name and eartag number are "+cowResult[cowIndex][1]+ " " + cowResult[cowIndex][2]);
                    currCow.setDateOfBirth(cowResult[cowIndex][3]); //TODO: not sure if this will work
                    if(cowResult[cowIndex][4].length() > 0){
                        currCow.setAge(Integer.parseInt(cowResult[cowIndex][4]));
                    }
                    currCow.setDateAdded(cowResult[cowIndex][9]);
                    currCow.setAgeType(cowResult[cowIndex][5]);
                    currCow.setSex(cowResult[cowIndex][6]);
                    currCow.setServiceType(cowResult[cowIndex][10]);

                    //set sire
                    if(cowResult[cowIndex][7].length() > 0){
                        columns = new String[]{"id", "name", "ear_tag_number", "date_of_birth", "age", "age_type", "sex", "sire_id", "dam_id", "date_added", "service_type", "country_id", "bull_owner", "owner_name"};
                        selection = "id="+cowResult[cowIndex][7];
                        String[][] sireRes = databaseHelper.runSelectQuery(readableDB, databaseHelper.TABLE_COW, columns, selection, null, null, null, null, null);
                        if(sireRes.length == 1){
                            Sire sire = new Sire();
                            sire.setName(sireRes[cowIndex][1]);
                            sire.setEarTagNumber(sireRes[cowIndex][2]);
                            sire.setDateAdded(sireRes[cowIndex][9]);

                            currCow.setSire(sire);
                        }
                        else{
                            Log.w(TAG, "No sire fetched for current cow");
                            Log.w(TAG, " cow's id = "+cowResult[cowIndex][0]);
                            Log.w(TAG, " sire's id = "+cowResult[cowIndex][7]);
                        }
                    }

                    //set dam
                    if(cowResult[cowIndex][8].length() > 0){
                        columns = new String[]{"id", "name", "ear_tag_number", "date_of_birth", "age", "age_type", "sex", "sire_id", "dam_id", "date_added", "service_type", "country_id", "bull_owner", "owner_name"};
                        selection = "id="+cowResult[cowIndex][7];
                        String[][] damRes = databaseHelper.runSelectQuery(readableDB, databaseHelper.TABLE_COW, columns, selection, null, null, null, null, null);
                        if(damRes.length == 1){
                            Dam dam = new Dam();
                            dam.setName(damRes[cowIndex][1]);
                            dam.setEarTagNumber(damRes[cowIndex][2]);
                            dam.setDateAdded(damRes[cowIndex][9]);

                            currCow.setDam(dam);
                        }
                        else{
                            Log.w(TAG, "No dam fetched for current cow");
                            Log.w(TAG, " cow's id = "+cowResult[cowIndex][0]);
                            Log.w(TAG, " dam's id = "+cowResult[cowIndex][8]);
                        }
                    }

                    //fetch cow events
                    columns = new String[] {"id", "cow_id", "event_name", "remarks", "event_date", "birth_type", "parent_cow_event", "bull_id", "servicing_days", "cod", "no_of_live_births", "saved_on_server", "date_added"};
                    selection = "cow_id="+cowID;
                    String[][] eventResult = databaseHelper.runSelectQuery(readableDB, databaseHelper.TABLE_EVENT, columns, selection, null, null, null, null, null);
                    for(int eventIndex = 0; eventIndex < eventResult.length; eventIndex++){
                        Event currEvent = new Event();

                        currEvent.setId(Integer.parseInt(eventResult[eventIndex][0]));
                        currEvent.setType(eventResult[eventIndex][2]);
                        currEvent.setRemarks(eventResult[eventIndex][3]);
                        currEvent.setEventDate(eventResult[eventIndex][4]);
                        currEvent.setBirthType(eventResult[eventIndex][5]);
                        if(eventResult[eventIndex][6].length() > 0)
                            currEvent.setParentCowEventID(Integer.parseInt(eventResult[eventIndex][6]));
                        if(eventResult[eventIndex][7].length() > 0)
                            currEvent.setBullID(Integer.parseInt(eventResult[eventIndex][7]));
                        if(eventResult[eventIndex][8].length() > 0)
                            currEvent.setServicingDays(Integer.parseInt(eventResult[eventIndex][8]));
                        currEvent.setCod(eventResult[eventIndex][9]);
                        if(eventResult[eventIndex][10].length() > 0)
                            currEvent.setNoOfLiveBirths(Integer.parseInt(eventResult[eventIndex][10]));
                        if(eventResult[eventIndex][11].equals("1")){
                            currEvent.setSavedOnServer(true);
                        }
                        else{
                            currEvent.setSavedOnServer(false);
                        }
                        currEvent.setDateAdded(eventResult[eventIndex][12]);

                        currCow.addEvent(currEvent);
                    }

                    //(id INTEGER PRIMARY KEY, cow_id INTEGER, time TEXT, quantity INTEGER, date_added TEXT, date TEXT, quantity_type TEXT)");
                    columns = new String[] {"id", "cow_id", "time", "quantity", "date_added", "date", "quantity_type"};
                    selection = "cow_id="+cowID;
                    String[][] mpResult = databaseHelper.runSelectQuery(readableDB, databaseHelper.TABLE_MILK_PRODUCTION, columns, selection, null, null, null, null, null);
                    for(int mpIndex = 0; mpIndex < mpResult.length; mpIndex++){
                        MilkProduction currMP = new MilkProduction();

                        currMP.setId(Integer.parseInt(mpResult[mpIndex][0]));
                        currMP.setTime(mpResult[mpIndex][2]);
                        currMP.setQuantity(Integer.parseInt(mpResult[mpIndex][3]));
                        currMP.setDateAdded(mpResult[mpIndex][4]);
                        currMP.setDate(mpResult[mpIndex][5]);
                        currMP.setQuantityType(mpResult[mpIndex][6]);

                        currCow.addMilkProduction(currMP);
                    }
                    farmer.addCow(currCow);
                }
            }
            else{
                Log.w(TAG, "No cows fetched from database");
                Log.w(TAG, " farmer's id = "+farmerID);
                Log.w(TAG, " farmer's name = "+farmer.getFullName());
            }
        }
        else{
            Log.e(TAG, "Unable to get cached farmer data. Might be because no farmer in has the provided simCardSN or more than one do");
            Log.e(TAG, " SimcardSN = "+simCardSN);
            Log.e(TAG, " Number of fetched farmers = "+String.valueOf(farmerResult.length));
        }

        return farmer;
    }

    public static List<EventConstraint> getEventConstraints(Context context){
        List<EventConstraint> result = new ArrayList<EventConstraint>();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase readableDB = databaseHelper.getReadableDatabase();

        String[] columns = new String[]{"id", "event", "time", "time_units"};
        String[][] constraintResult = databaseHelper.runSelectQuery(readableDB,databaseHelper.TABLE_EVENTS_CONSTRAINTS, columns, null, null, null, null, null, null);
        if(constraintResult.length > 0){
            for(int i = 0; i < constraintResult.length; i++){
                String[] currConstraint = constraintResult[i];
                result.add(new EventConstraint(Integer.parseInt(currConstraint[0]), currConstraint[1], Integer.parseInt(currConstraint[2]), currConstraint[3]));
            }
        }

        return result;
    }

    /**
     * This method caches data that would have been sent to the server. Note that this method has an almost identical arguement
     * signature similar to the sendDataToServer method.
     *
     * @param context   The activity/service from where you want to save cache the request
     * @param jsonString    The valid json string containing data for the request as you would have sent it in a normal request
     * @param appendedURL   The URI for the module on the server which you want the request to go to (eventually) e.g FARMER_ADD_COW_EVENT_URL
     */
    public static final boolean cacheRequest(Context context, String jsonString, String appendedURL){
        //TODO: do stuff
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase writableDB = databaseHelper.getWritableDatabase();
        if(writableDB.isOpen()){
            String[] columns = new String[]{"url", "json"};
            String[] values = new String[columns.length];
            values[0] = appendedURL;
            values[1] = jsonString;

            databaseHelper.runInsertQuery(databaseHelper.TABLE_CACHED_REQUESTS, columns, values, -1, writableDB);
            return true;
        }
        else{
            Log.e(TAG, "Writable database did not open. Was unable to cache request in the SQLite DB. Choosing to send the data to the server instead");
            sendDataToServer(context, jsonString, appendedURL, false);
        }
        return false;
    }

    /**
     * This method sends cached data to the server
     *
     * @param waitForResponse If set to true, this method will wait for the response from the server and returns it to the caller
     * @param context   The activity/service from where you want to send the data to the server
     */
    public static final String sendCachedRequests(Context context, boolean waitForResponse){
        Log.d(TAG, "Trying to send cached data to server");
        //public static String sendDataToServer(Context context, String jsonString, String appendedURL, boolean waitForResponse) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase writableDB = databaseHelper.getWritableDatabase();
        if(writableDB.isOpen()){
            String[] columns = new String[]{"id","url", "json"};
            String[][] result = databaseHelper.runSelectQuery(writableDB, databaseHelper.TABLE_CACHED_REQUESTS, columns, null, null, null, null, null, null);
            List<String> ids = new ArrayList<String>();
            if(result != null){
                try{
                    JSONArray requests = new JSONArray();
                    for(int requestIndex = 0; requestIndex < result.length; requestIndex++){
                        JSONObject currRequest = new JSONObject();
                        ids.add(result[requestIndex][0]);
                        String currRequestURL = result[requestIndex][1];
                        JSONObject currRequestData = new JSONObject(result[requestIndex][2]);
                        currRequest.put("requestURL", currRequestURL);
                        currRequest.put("requestData", currRequestData);
                        requests.put(currRequest);
                    }
                    TelephonyManager telephonyManager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                    String simCardSN = telephonyManager.getSimSerialNumber();
                    if(simCardSN!=null){
                        JSONObject finalRequest = new JSONObject();
                        finalRequest.put("simCardSN", simCardSN);
                        finalRequest.put("pastRequests", requests);
                        if(requests.length() > 0){
                            Log.d(TAG, "Sending the following cached data "+finalRequest.toString());
                            String response = sendDataToServer(context, finalRequest.toString(), FARMER_ADD_CACHED_DATA_URL, waitForResponse);
                            if(response != null && !response.equals(CODE_USER_NOT_AUTHENTICATED)){
                                //delete the saved data from cache
                                databaseHelper.runTruncateQuery(writableDB, databaseHelper.TABLE_CACHED_REQUESTS);
                                /*String[] idsArray = new String[ids.size()];
                                idsArray = ids.toArray(idsArray);
                                databaseHelper.runDeleteQuery(writableDB, databaseHelper.TABLE_CACHED_REQUESTS, "id", idsArray);*/
                                Log.d(TAG, "Deleted cached requests from SQLite database");
                            }
                            return response;
                        }
                        else {
                            Log.d(TAG, "No Cached data in database");
                            return  NO_DATA;
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                Log.w(TAG, "Did not get any cached requests in the SQLite database, exiting");
            }
        }
        else{
            Log.e(TAG, "Readable database did not open. Was una");
        }
        return null;
    }

    /**
     * This broadcast receiver is responsible for watching for SMSs coming for the server and concatenating SMS fragments in SMSs that are
     * longer than the standard SMS length
     */
    private static class MistroSMSReceiver extends BroadcastReceiver{

        private final SmsManager smsManager;

        public MistroSMSReceiver(){
            smsManager = SmsManager.getDefault();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle bundle = intent.getExtras();
            try{
                if(bundle != null){
                    final Object[] pdusObject = (Object[])bundle.get("pdus");
                    for(int i = 0; i < pdusObject.length; i++){
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObject[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        String message = currentMessage.getDisplayMessageBody();

                        if(phoneNumber.equals(SMS_SERVER_ADDRESS)){
                            Log.d(TAG, "SMS received from server");
                            if(getSharedPreference(context, SP_KEY_SMS_RESPONSE,"").length()==0){
                                if(isExpectedString(message)){
                                    Log.d(TAG, "SMS is valid json saved to shared preferences");
                                    setSharedPreference(context, SP_KEY_SMS_RESPONSE, message);
                                }
                                else{
                                    String cachedMessage = getSharedPreference(context, SP_KEY_SMS_CACHE, "");
                                    cachedMessage = cachedMessage + message;
                                    if(isExpectedString(cachedMessage)){
                                        Log.d(TAG, "cached message + SMS is valid json saved to shared preferences");
                                        setSharedPreference(context, SP_KEY_SMS_RESPONSE, cachedMessage);
                                    }
                                    else{
                                        Log.d(TAG, "cached message + SMS is invalid json, cached in shared preference and waiting for other messages");
                                        setSharedPreference(context, SP_KEY_SMS_CACHE, cachedMessage);
                                    }
                                }
                            }
                        }
                        else{
                            Log.w(TAG, "Message received from "+phoneNumber+" which is not the servers address ("+SMS_SERVER_ADDRESS+")");
                        }
                    }
                }
                else{
                    Log.w(TAG, "Bundle containing sms contains null. Skipping this one");
                }
            }
            catch (Exception e){
                Log.e(TAG, "Exception thrown while trying to receive message"+e);
            }
        }

        private boolean isExpectedString(String test) {
            boolean valid = false;

            try{
                new JSONObject(test);
                valid = true;
            }
            catch (JSONException e){
                valid = false;
            }

            if(valid==false){
                try {
                    new JSONArray(test);
                    valid = true;
                }
                catch(JSONException ex) {
                    valid = false;
                }
            }

            if(valid==false && test!=null){
                if(test.equals(ACKNOWLEDGE_OK)) valid = true;
                else if(test.equals(DATA_ERROR)) valid = true;
                else if(test.equals(NO_DATA)) valid = true;
                else if(test.equals(CODE_SIM_CARD_REGISTERED)) valid = true;
                else if(test.equals(CODE_SIM_CARD_REGISTERED)) valid = true;
            }

            return valid;
        }
    }
}
