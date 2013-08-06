package org.cgiar.ilri.mistro.farmer.backend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * Created by jason on 8/6/13.
 */
public class DataHandler
{
    public static boolean checkNetworkConnection(final Context context, String alertTitle, String alertText, String okayText)
    {
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
                    /*.setNegativeButton(cancelText, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                            Intent intent=new Intent(FarmerRegistrationActivity.this,LandingActivity.class);
                            startActivity(intent);
                        }
                    });*/
            AlertDialog alertDialog=alertDialogBuilder.create();
            alertDialog.show();
            return false;
        }
        else
        {
            return true;
        }
    }

    public static boolean sendDataToServer()
    {
        return  false;
    }
}
