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
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.table.TableLayout;
import java.util.Vector;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.carrier.Cow;
import org.cgiar.ilri.mistro.farmer.carrier.Event;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.ui.localization.ArrayResources;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;

/**
 *
 * @author jason
 */
public class PastEventsScreen extends Form implements Screen{
    
    private final Midlet midlet;
    private final int locale;
    private final Farmer farmer;

    private Vector validCows;
    //private final int parentTableRowCap=20;
    
    private TableLayout parentTableLayout;
    private Command backCommand;
    
    
    public PastEventsScreen(Midlet midlet, int locale, Farmer farmer) {
        super(Locale.getStringInLocale(locale, StringResources.past_events));
        
        this.midlet = midlet;
        this.locale = locale;
        this.farmer = farmer;
        
        //date  cow     event
        parentTableLayout = new TableLayout(100, 3);
        this.setLayout(parentTableLayout);
        
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)){
                    EventsScreen eventsScreen = new EventsScreen(PastEventsScreen.this.midlet, PastEventsScreen.this.locale, PastEventsScreen.this.farmer);
                    eventsScreen.start();
                }
            }
        });
        
        Label cowHeadingL = new Label(Locale.getStringInLocale(locale, StringResources.cow));
        cowHeadingL.getStyle().setFont(Font.create(null).createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        TableLayout.Constraint cowHeadingLC= parentTableLayout.createConstraint();
        cowHeadingLC.setWidthPercentage(30);
        this.addComponent(cowHeadingLC, cowHeadingL);
        
        Label dateHeadingL = new Label(Locale.getStringInLocale(locale, StringResources.date));
        dateHeadingL.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        TableLayout.Constraint dateHeadingLC= parentTableLayout.createConstraint();
        dateHeadingLC.setWidthPercentage(33);
        this.addComponent(dateHeadingLC, dateHeadingL);
        
        Label typeHeadingL = new Label(Locale.getStringInLocale(locale, StringResources.event));
        typeHeadingL.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        TableLayout.Constraint typeHeadingLC = parentTableLayout.createConstraint();
        typeHeadingLC.setWidthPercentage(37);
        this.addComponent(typeHeadingLC,typeHeadingL);
        
        Cow[] cows = farmer.getCows();
        for(int i = 0; i < cows.length; i++){
            Event[] cowEvents = cows[i].getEvents();
            if(cowEvents!=null){
                for(int j = 0; j < cowEvents.length; j++){
                    
                    TableLayout.Constraint cowLC= parentTableLayout.createConstraint();
                    cowLC.setWidthPercentage(30);
                    if(cows[i].getName()!=null && cows[i].getName().trim().length()>0){
                        Label cowL = new Label(cows[i].getEarTagNumber()+" ("+cows[i].getName()+")");
                        this.addComponent(cowLC, cowL);
                    }
                    else{
                        Label cowL = new Label(cows[i].getEarTagNumber());
                        this.addComponent(cowLC, cowL);
                    }
                    
                    Label dateL = new Label(cowEvents[j].getDate());
                    TableLayout.Constraint dateLC= parentTableLayout.createConstraint();
                    dateLC.setWidthPercentage(33);
                    this.addComponent(dateLC, dateL);
                    
                    String[] eventTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.cow_event_types);
                    String[] eventTypes = Locale.getStringArrayInLocale(locale, ArrayResources.cow_event_types);
                    String eventType = "";
                    for(int k = 0; k < eventTypesInEN.length; k++){
                        if(eventTypesInEN[k].equals(cowEvents[j].getType())){
                            eventType = eventTypes[k];
                        }
                    }
                    
                    Label eventTypeL = new Label(eventType);
                    TableLayout.Constraint eventTypeLC = parentTableLayout.createConstraint();
                    eventTypeLC.setWidthPercentage(37);
                    this.addComponent(eventTypeLC, eventTypeL);
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
