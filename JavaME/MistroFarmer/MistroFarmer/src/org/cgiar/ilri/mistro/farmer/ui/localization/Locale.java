/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.ui.localization;

/**
 *
 * @author jason
 */
public class Locale {
    public static final int LOCALE_NO = 2;
    public static final int LOCALE_EN = 0;
    public static final int LOCALE_SW = 1;
    
    public static String getStringInLocale(int locale,String[] array){
        if(locale < array.length) {
            return array[locale];
        }
        else{
            return null;
        }
    }
    
    public static String[] getStringArrayInLocale(int locale,String[][] array){
        if(locale < array.length) {
            return array[locale];
        }
        else {
            return null;
        }
    }
}
