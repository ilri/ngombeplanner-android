package org.cgiar.ilri.mistro.farmer.backend;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.cgiar.ilri.mistro.farmer.R;
import java.lang.reflect.Field;

/**
 * Created by jason on 9/5/13.
 */
public class Locale {
    private static final String TAG = "Locale";
    public static final String LOCALE_ENGLISH="en";
    public static final String LOCALE_SWAHILI="sw";
    public static final String SHARED_PREFERENCES_KEY = "locale";
    public static String getStringInLocale(String stringName, Context context) {
        String localeCode = getLocaleCode(context);
        String name = stringName+"_"+localeCode;
        String value = null;
        try {
            Field field = R.string.class.getDeclaredField(name);
            int id = field.getInt(field);
            if(id != 0) {
                value = context.getString(id);
            }
            else {
                Log.e(TAG,"no field in class R.string with the name "+name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String[] getArrayInLocale(String arrayName, Context context) {
        String localeCode = getLocaleCode(context);
        String name = arrayName+"_"+localeCode;
        String[] value = null;
        try {
            Field field = R.array.class.getDeclaredField(name);
            int id = field.getInt(field);
            if(id != 0) {
                value = context.getResources().getStringArray(id);
            }
            else {
                Log.e(TAG,"no field in class R.string with the name "+name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static int getArrayIDInLocale(String arrayName, Context context) {
        String localeCode = getLocaleCode(context);
        String name = arrayName+"_"+localeCode;
        int value = 0;
        try {
            Field field = R.array.class.getDeclaredField(name);
            int id = field.getInt(field);
            if(id != 0) {
                value = id;
            }
            else {
                Log.e(TAG,"no field in class R.string with the name "+name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static void switchLocale(String newLocaleCode, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.app_name),Context.MODE_PRIVATE).edit();
        editor.putString(SHARED_PREFERENCES_KEY,newLocaleCode);
        editor.commit();
    }

    public static String getLocaleCode(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        return sharedPreferences.getString(SHARED_PREFERENCES_KEY, LOCALE_ENGLISH);
    }
}
