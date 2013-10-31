/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.table.TableLayout;
import java.util.Vector;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.carrier.Cow;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.carrier.MilkProduction;
import org.cgiar.ilri.mistro.farmer.ui.localization.ArrayResources;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;

/**
 *
 * @author jason
 */
public class ProductionHistoryScreen extends Form implements Screen{
    
    private final Midlet midlet;
    private final int locale;
    private final Farmer farmer;
    
    private Vector validCows;
    
   private TableLayout parentTableLayout;
    
    
    public ProductionHistoryScreen(Midlet midlet, int locale, Farmer farmer) {
        super(Locale.getStringInLocale(locale, StringResources.production_history));
        
        this.midlet = midlet;
        this.locale = locale;
        this.farmer = farmer;
        
        parentTableLayout= new TableLayout(getValidCows(), 4);
        this.setLayout(parentTableLayout);
        //cow   date    time    quantity (quantity_type)
        Cow[] cows = farmer.getCows();
        for(int i = 0; i < cows.length; i++){
            MilkProduction[] milkProduction = cows[i].getMilkProduction();
            if(milkProduction!=null){
                for(int j = 0; j < milkProduction.length; j++){
                    if(cows[i].getName()!=null && cows[i].getName().trim().length()>0){
                        Label cowL = new Label(cows[i].getEarTagNumber()+" ("+cows[i].getName()+")");
                        this.addComponent(cowL);
                    }
                    else{
                        Label cowL = new Label(cows[i].getEarTagNumber());
                        this.addComponent(cowL);
                    }
                    
                    Label dateL = new Label(milkProduction[i].getDate());
                    this.addComponent(dateL);
                    
                    Label timeL = new Label("");
                    String[] times = Locale.getStringArrayInLocale(locale, ArrayResources.milking_times);
                    String[] timesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.milking_times);
                    for(int k = 0; k < times.length; i++){
                        if(timesInEN[k].equals(milkProduction[j].getTime())){
                            timeL.setText(times[k]);
                        }
                    }
                    this.addComponent(timeL);
                    
                    String quantityType = "";
                    String[] quantityTypes = Locale.getStringArrayInLocale(locale, ArrayResources.quantity_types);
                    String[] quantityTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.quantity_types);
                    for(int k = 0; k < quantityTypes.length; k++){
                        if(quantityTypesInEN[k].equals(milkProduction[j].getQuantityType())){
                            quantityType = quantityTypes[k];
                        }
                    }
                    
                    Label quantityL = new Label(String.valueOf(milkProduction[j].getQuantity())+" ("+quantityType+")");
                    this.addComponent(quantityL);
                }
            }
            
        }
    }
    
    public void start() {
        this.show();
    }

    public void destroy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void pause() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private int getValidCows(){
        Cow[] cows = farmer.getCows();
        if(cows!=null){
            validCows = new Vector(cows.length);
        
            for(int i = 0; i < cows.length; i++){
                if(cows[i].getSex().equals(Cow.SEX_FEMALE)){
                    validCows.addElement(cows[i]);
                }
            }
            
            return validCows.size();
        }
        
        return 0;
    }
    
}
