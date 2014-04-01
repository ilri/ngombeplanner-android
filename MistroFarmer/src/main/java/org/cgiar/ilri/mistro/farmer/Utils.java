package org.cgiar.ilri.mistro.farmer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import org.cgiar.ilri.mistro.farmer.backend.Locale;

/**
 * Created by jason on 8/6/13.
 */
public class Utils
{
    public static void showSuccessfullRegistration(final Context context, String localeCode)
    {
        String title= Locale.getStringInLocale("string.successful_registration",context);
        String instructions = Locale.getStringInLocale("successful_registration_instructions",context);
        String okayText = Locale.getStringInLocale("okay",context);
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(instructions)
                .setCancelable(false)
                .setPositiveButton(okayText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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
                            dialog.dismiss();
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
                        dialog.dismiss();
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

    public static AlertDialog createSMSDialog(Context context, DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(Locale.getStringInLocale("sms_charges", context));
        alertDialogBuilder
                .setMessage(Locale.getStringInLocale("incur_network_sms_charges", context))
                .setCancelable(false)
                .setPositiveButton(Locale.getStringInLocale("okay",context), onClickListener)
                .setNegativeButton(Locale.getStringInLocale("cancel",context), onClickListener);
        return alertDialogBuilder.create();
    }

    public static AlertDialog createMainMenuDialog(Context context, DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(Locale.getStringInLocale("warning", context));
        alertDialogBuilder
                .setMessage(Locale.getStringInLocale("action_will_be_canceled", context))
                .setCancelable(false)
                .setPositiveButton(Locale.getStringInLocale("okay",context), onClickListener)
                .setNegativeButton(Locale.getStringInLocale("cancel",context), onClickListener);
        return alertDialogBuilder.create();
    }
}
