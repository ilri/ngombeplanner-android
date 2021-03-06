package org.cgiar.ilri.np.farmer.backend;

import android.content.Context;
import android.util.Log;
import org.cgiar.ilri.np.farmer.R;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jason on 9/5/13.
 */
public class Locale {
    private static final String TAG = "Locale";
    public static final String LOCALE_ENGLISH="en";
    public static final String LOCALE_SWAHILI="sw";
    public static final String LOCALE_LUTSOTSO="lu";
    public static final String LOCALE_NANDI="nn";
    public static final String LOCALE_KIKABRAS="kr";
    public static final String LOCALE_KIPSIGIS="kp";

    //public static final String SHARED_PREFERENCES_KEY = "locale";
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

    public static String[] getArrayInLocale(String arrayName, Context context, String localeCode) {
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

    public static String[] translateArrayToEnglish(Context context, String arrayName, String[] arrayInLocale){
        if(arrayInLocale != null){
            //Log.d(TAG, "Trying to translate array to english");
            String[] allStringsInEN = Locale.getArrayInLocale(arrayName, context, Locale.LOCALE_ENGLISH);
            String[] allStringsInLocale = Locale.getArrayInLocale(arrayName, context);

            String[] translatedArray = new String[arrayInLocale.length];
            for(int i = 0; i < arrayInLocale.length; i++){
                for(int j = 0; j < allStringsInLocale.length; j++){
                    //Log.d(TAG, "Current actual string = "+arrayInLocale[i]);
                    //Log.d(TAG, "Current string in english =  "+allStringsInEN[j]);
                    //Log.d(TAG, "Current string in locale = "+allStringsInLocale[j]);
                    if(arrayInLocale[i].equals(allStringsInLocale[j])){
                        translatedArray[i] = allStringsInEN[j];
                    }
                }
            }

            return translatedArray;
        }
        return null;
    }

    public static String[] translateArrayToLocale(Context context, String arrayName, String[] arrayInEN){
        if(arrayInEN != null){
            //Log.d(TAG, "Trying to translate array to current locale");
            String[] allStringsInEN = Locale.getArrayInLocale(arrayName, context, Locale.LOCALE_ENGLISH);
            String[] allStringsInLocale = Locale.getArrayInLocale(arrayName, context);

            String[] translatedArray = new String[arrayInEN.length];
            for(int i = 0; i < arrayInEN.length; i++){
                for(int j = 0; j < allStringsInEN.length; j++){
                    //Log.d(TAG, "Current actual string = "+arrayInEN[i]);
                    //Log.d(TAG, "Current string in english =  "+allStringsInEN[j]);
                    //Log.d(TAG, "Current string in locale = "+allStringsInLocale[j]);
                    if(arrayInEN[i].equals(allStringsInEN[j])){
                        translatedArray[i] = allStringsInLocale[j];
                    }
                }
            }

            return translatedArray;
        }
        return null;
    }

    public static String translateStringToEnglish(Context context, String arrayName, String string){
        if(string != null){
            String[] allStringsInLocale = Locale.getArrayInLocale(arrayName, context);
            String[] allStringsInEN = Locale.getArrayInLocale(arrayName, context, LOCALE_ENGLISH);

            for(int i = 0; i < allStringsInLocale.length; i++){
                if(string.equals(allStringsInLocale[i])){
                    return allStringsInEN[i];
                }
            }
        }
        return null;
    }

    public static String translateStringToLocale(Context context, String arrayName, String string){
        if(string != null){
            String[] allStringsInLocale = Locale.getArrayInLocale(arrayName, context);
            String[] allStringsInEN = Locale.getArrayInLocale(arrayName, context, LOCALE_ENGLISH);

            for(int i = 0; i < allStringsInEN.length; i++){
                if(string.equals(allStringsInEN[i])){
                    return allStringsInLocale[i];
                }
            }
        }
        return null;
    }

    public static void switchLocale(String newLocaleCode, Context context) {
        /*SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.app_name),Context.MODE_PRIVATE).edit();
        editor.putString(SHARED_PREFERENCES_KEY,newLocaleCode);
        editor.commit();*/
        DataHandler.setSharedPreference(context,DataHandler.SP_KEY_LOCALE,newLocaleCode);
    }

    public static String getLocaleCode(Context context) {
        /*SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        return sharedPreferences.getString(SHARED_PREFERENCES_KEY, LOCALE_ENGLISH);*/
        return DataHandler.getSharedPreference(context, DataHandler.SP_KEY_LOCALE, LOCALE_ENGLISH);
    }

    public static String getLocaleCode(Context context, String language){
        String code = "";
        if(language.equals(context.getString(R.string.english))) code = LOCALE_ENGLISH;
        else if(language.equals(context.getString(R.string.swahili))) code = LOCALE_SWAHILI;
        else if(language.equals(context.getString(R.string.nandi))) code = LOCALE_NANDI;
        else if(language.equals(context.getString(R.string.kipsigis))) code = LOCALE_KIPSIGIS;
        else if(language.equals(context.getString(R.string.kikabrasi))) code = LOCALE_KIKABRAS;
        else if(language.equals(context.getString(R.string.lutsotso))) code = LOCALE_LUTSOTSO;

        return code;
    }

    public static String getLanguage(Context context, String localeCode){
        String language = "";

        if(localeCode.equals(LOCALE_ENGLISH)) language = context.getString(R.string.english);
        if(localeCode.equals(LOCALE_SWAHILI)) language = context.getString(R.string.swahili);
        if(localeCode.equals(LOCALE_NANDI)) language = context.getString(R.string.nandi);
        if(localeCode.equals(LOCALE_KIPSIGIS)) language = context.getString(R.string.kipsigis);
        if(localeCode.equals(LOCALE_KIKABRAS)) language = context.getString(R.string.kikabrasi);
        if(localeCode.equals(LOCALE_LUTSOTSO)) language = context.getString(R.string.lutsotso);

        return language;
    }

    public static List<String> getAllLanguages(Context context){
        List<String> languages = new ArrayList<String>();
        languages.add(context.getString(R.string.english));
        languages.add(context.getString(R.string.swahili));
        languages.add(context.getString(R.string.lutsotso));
        languages.add(context.getString(R.string.kikabrasi));
        languages.add(context.getString(R.string.nandi));
        languages.add(context.getString(R.string.kipsigis));

        return languages;
    }
}
