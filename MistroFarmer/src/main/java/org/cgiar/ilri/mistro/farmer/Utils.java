package org.cgiar.ilri.mistro.farmer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

/**
 * Created by jason on 8/6/13.
 */
public class Utils
{
    public static void showSuccessfullRegistration(final Context context, String localeCode)
    {
        String title="";
        String instructions="";
        String okayText="";
        if(localeCode=="en")
        {
            title=context.getResources().getString(R.string.successful_registration_en);
            instructions=context.getResources().getString(R.string.successful_registration_instructions_en);
            okayText=context.getResources().getString(R.string.okay_en);
        }
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(instructions)
                .setCancelable(false)
                .setPositiveButton(okayText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, LandingActivity.class);
                        context.startActivity(intent);
                    }
                });
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void showGenericAlertDialog(final Context context, String title,String text, String positiveButtonText, String negativeButtonText, final Class<?> nextActivity, final Class<?> nextActivityNeg)
    {
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(text)
                .setCancelable(true)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(nextActivity!=null)
                        {
                            Intent intent = new Intent(context,nextActivity);
                            context.startActivity(intent);
                        }
                        else
                        {
                            dialog.dismiss();
                        }
                    }
                });
        if(negativeButtonText!=null)
        {
            alertDialogBuilder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if(nextActivityNeg!=null)
                    {
                        Intent intent = new Intent(context,nextActivityNeg);
                        context.startActivity(intent);
                    }
                    else
                    {
                        dialog.dismiss();
                    }
                }
            });
        }
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
    }
}
