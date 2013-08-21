package org.cgiar.ilri.mistro.farmer.backend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
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
    private static final int HTTP_POST_TIMEOUT =20000;
    private static final int HTTP_RESPONSE_TIMEOUT =20000;
    //private static final String BASE_URL="http://192.168.2.232/mistro";//"http://10.0.2.2/";//TODO: configure baseURL
    private static final String BASE_URL="http://192.168.2.232/~jason/mistro";
    public static final String FARMER_REGISTRATION_URL="/farmer/registration.php";
    public static final String FARMER_AUTHENTICATION_URL="/farmer/authentication.php";
    public static final String FARMER_SIM_CARD_REGISTRATION_URL="/farmer/simCardRegistration.php";
    public static final String FARMER_FETCH_COW_IDENTIFIERS_URL="/farmer/fetchCowIdentifiers.php";
    public static final String FARMER_ADD_MILK_PRODUCTION_URL="/farmer/addMilkProduction.php";
    public static final String FARMER_FETCH_MILK_PRODUCTION_HISTORY_URL="/farmer/fetchMilkProductionHistory.php";
    public static boolean checkNetworkConnection(final Context context, String localeCode)
    {
        String alertTitle="";
        String alertText="";
        String okayText="";
        if(localeCode.equals("en"))
        {
            alertTitle=context.getResources().getString(R.string.enable_network_en);
            alertText=context.getResources().getString(R.string.reason_for_enabling_network_en);
            okayText=context.getResources().getString(R.string.okay_en);
        }
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null)//no network connection
        {
            AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(alertTitle);
            alertDialogBuilder
                    .setMessage(alertText)
                    .setCancelable(false)
                    .setPositiveButton(okayText, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent intent=new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            context.startActivity(intent);
                        }
                    });

            AlertDialog alertDialog=alertDialogBuilder.create();
            alertDialog.show();
            return false;
        }
        else
        {
            return true;
        }
    }

    public static String sendDataToServer(String jsonString, String appendedURL)
    {
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
}
