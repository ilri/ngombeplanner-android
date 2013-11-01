/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Command;
import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
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
    private Command backCommand;
    
    
    public ProductionHistoryScreen(Midlet midlet, int locale, Farmer farmer) {
        super(Locale.getStringInLocale(locale, StringResources.production_history));
        
        this.midlet = midlet;
        this.locale = locale;
        this.farmer = farmer;
        
        parentTableLayout= new TableLayout(getValidCows(), 4);
        this.setLayout(parentTableLayout);
        
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)) {
                    MilkProductionScreen milkProductionScreen = new MilkProductionScreen(ProductionHistoryScreen.this.midlet, ProductionHistoryScreen.this.locale, ProductionHistoryScreen.this.farmer);
                    milkProductionScreen.start();
                }
            }
        });
        
        //cow   date    time    quantity (quantity_type)
        
        //cow   date    time    quantity (quantity_type)
        Label cowHeadingL = new Label(Locale.getStringInLocale(locale, StringResources.cow));
        cowHeadingL.getStyle().setFont(Font.create(null).createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        TableLayout.Constraint cowHeadingLC= parentTableLayout.createConstraint();
        cowHeadingLC.setWidthPercentage(35);
        this.addComponent(cowHeadingLC, cowHeadingL);
        
        Label dateHeadingL = new Label(Locale.getStringInLocale(locale, StringResources.date));
        dateHeadingL.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        TableLayout.Constraint dateHeadingLC= parentTableLayout.createConstraint();
        dateHeadingLC.setWidthPercentage(30);
        this.addComponent(dateHeadingLC, dateHeadingL);
        
        Label timeHeadingL = new Label(Locale.getStringInLocale(locale, StringResources.time));
        timeHeadingL.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        TableLayout.Constraint timeHeadingLC = parentTableLayout.createConstraint();
        timeHeadingLC.setWidthPercentage(25);
        this.addComponent(timeHeadingLC,timeHeadingL);
        
        Label quantityHeadingL = new Label(Locale.getStringInLocale(locale, StringResources.quantity));
        quantityHeadingL.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        TableLayout.Constraint quantityHeadingLC = parentTableLayout.createConstraint();
        quantityHeadingLC.setWidthPercentage(15);
        this.addComponent(quantityHeadingLC, quantityHeadingL);
        
        Cow[] cows = farmer.getCows();
        for(int i = 0; i < cows.length; i++){
            MilkProduction[] milkProduction = cows[i].getMilkProduction();
            if(milkProduction!=null){
                System.out.println("milk production for cow "+i+" is not null");
                for(int j = 0; j < milkProduction.length; j++){
                    System.out.println("milk production = "+j);
                    TableLayout.Constraint cowLC= parentTableLayout.createConstraint();
                    cowLC.setWidthPercentage(35);
                    if(cows[i].getName()!=null && cows[i].getName().trim().length()>0){
                        Label cowL = new Label(cows[i].getEarTagNumber()+" ("+cows[i].getName()+")");
                        this.addComponent(cowLC, cowL);
                    }
                    else{
                        Label cowL = new Label(cows[i].getEarTagNumber());
                        this.addComponent(cowLC, cowL);
                    }
                    
                    Label dateL = new Label(milkProduction[j].getDate());
                    TableLayout.Constraint dateLC= parentTableLayout.createConstraint();
                    dateLC.setWidthPercentage(30);
                    this.addComponent(dateLC, dateL);
                    
                    Label timeL = new Label("");
                    TableLayout.Constraint timeLC = parentTableLayout.createConstraint();
                    timeLC.setWidthPercentage(25);
                    String[] times = Locale.getStringArrayInLocale(locale, ArrayResources.milking_times);
                    String[] timesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.milking_times);
                    for(int k = 0; k < times.length; k++){
                        if(timesInEN[k].equals(milkProduction[j].getTime())){
                            timeL.setText(times[k]);
                        }
                    }
                    this.addComponent(timeLC, timeL);
                    
                    String quantityType = "";
                    String[] quantityTypes = Locale.getStringArrayInLocale(locale, ArrayResources.quantity_types);
                    String[] quantityTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.quantity_types);
                    for(int k = 0; k < quantityTypes.length; k++){
                        if(quantityTypesInEN[k].equals(milkProduction[j].getQuantityType())){
                            quantityType = quantityTypes[k];
                        }
                    }
                    
                    Label quantityL = new Label(String.valueOf(milkProduction[j].getQuantity())+" ("+quantityType+")");
                    TableLayout.Constraint quantityLC = parentTableLayout.createConstraint();
                    quantityLC.setWidthPercentage(15);
                    this.addComponent(quantityLC, quantityL);
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
