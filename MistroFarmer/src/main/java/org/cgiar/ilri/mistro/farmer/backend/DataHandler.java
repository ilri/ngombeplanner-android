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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    //private static final String BASE_URL="http://192.168.2.232/~jason/MistroFarmerProject/web";
    //private static final String BASE_URL="http://10.0.2.2/~jason/MistroFarmerProject/web";
    public static final String SMS_SERVER_ADDRESS = "+254723572302";
    private static final String BASE_URL="http://hpc.ilri.cgiar.org/~jrogena/mistro_web";
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
    public static final String SP_KEY_LOCALE = "locale";
    public static final String SP_KEY_MILK_QUANTITY_TYPE = "milkQuantityTYpe";
    public static final String SP_KEY_USE_SMS_TO_SEND_DATA = "useSMSToSendData";
    public static final String SP_KEY_SMS_RESPONSE = "smsResponse";
    public static final String SP_KEY_SMS_CACHE = "smsCache";
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
     *
     * @param context
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
            response = sendDataUsingHttpConnection(jsonString, appendedURL);
        }
        else{
            response = sendDataUsingSMS(context, jsonString, appendedURL, waitForResponse);
        }
        return response;
    }

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

    private static String sendDataUsingHttpConnection(String jsonString, String appendedURL) {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, HTTP_POST_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, HTTP_RESPONSE_TIMEOUT);
        HttpClient httpClient=new DefaultHttpClient(httpParameters);
        HttpPost httpPost=new HttpPost(BASE_URL+appendedURL);
        try
        {
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
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return  null;
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
                                if(isJSONStringValid(message)){
                                    Log.d(TAG, "SMS is valid json saved to shared preferences");
                                    setSharedPreference(context, SP_KEY_SMS_RESPONSE, message);
                                }
                                else{
                                    String cachedMessage = getSharedPreference(context, SP_KEY_SMS_CACHE, "");
                                    cachedMessage = cachedMessage + message;
                                    if(isJSONStringValid(cachedMessage)){
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

        private boolean isJSONStringValid(String test) {
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

            return valid;
        }
    }
}
