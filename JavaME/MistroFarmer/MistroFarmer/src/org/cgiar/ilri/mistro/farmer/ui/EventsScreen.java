/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Button;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.ui.localization.ArrayResources;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;

/**
 *
 * @author jason
 */
public class EventsScreen extends Form implements Screen, ActionListener{
    
    private final Midlet midlet;
    private final int locale;
    private final Farmer farmer;
    
    private BoxLayout parentBoxLayout;
    private Command backCommand;
    
    private Button addEventButton;
    private Button pastEventsButton;
    
    public EventsScreen(Midlet midlet, int locale, Farmer farmer) {
        super(Locale.getStringInLocale(locale, StringResources.other_events));
        
        this.midlet = midlet;
        this.locale = locale;
        this.farmer = farmer;
        
        parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)){
                    MainMenuScreen mainMenuScreen = new MainMenuScreen(EventsScreen.this.midlet, EventsScreen.this.locale, EventsScreen.this.farmer);
                    mainMenuScreen.start();
                }
            }
        });
        
        
        addEventButton = new Button(Locale.getStringInLocale(locale, StringResources.add_an_event));
        addEventButton.getStyle().setMargin(100, 10, 0, 0);
        addEventButton.getSelectedStyle().setMargin(100, 10, 0, 0);
        setButtonStyle(addEventButton);
        addEventButton.addActionListener(this);
        this.addComponent(addEventButton);
        
        pastEventsButton = new Button(Locale.getStringInLocale(locale, StringResources.past_events));
        pastEventsButton.getStyle().setMargin(10, 10, 0, 0);
        pastEventsButton.getSelectedStyle().setMargin(10, 10, 0, 0);
        setButtonStyle(pastEventsButton);
        pastEventsButton.addActionListener(this);
        this.addComponent(pastEventsButton);
    }

    private void setButtonStyle(Button button){
        button.getStyle().setAlignment(Component.CENTER);
        button.getSelectedStyle().setAlignment(Component.CENTER);
        button.getSelectedStyle().setBgColor(0x2ecc71);
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

    public void actionPerformed(ActionEvent evt) {
        if(evt.getComponent().equals(addEventButton)){
            final Dialog infoDialog = new Dialog();
            infoDialog.setDialogType(Dialog.TYPE_INFO);
            final Command backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
            final Command nextCommand = new Command(Locale.getStringInLocale(locale, StringResources.next));
            infoDialog.addCommand(backCommand);
            infoDialog.addCommand(nextCommand);
            
            Label eventTypeL = new Label(Locale.getStringInLocale(locale, StringResources.event_type));
            eventTypeL.getStyle().setMargin(10, 0, 10, 0);
            eventTypeL.getStyle().setAlignment(Label.CENTER);
            eventTypeL.getSelectedStyle().setMargin(10, 0, 10,0);
            infoDialog.addComponent(eventTypeL);
            
            final ComboBox eventTypeCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.generic_cow_event_types));
            eventTypeCB.getStyle().setMargin(5, 0, 0, 0);
            eventTypeCB.getStyle().setAlignment(ComboBox.CENTER);
            eventTypeCB.getSelectedStyle().setMargin(5, 0, 0, 0);
            eventTypeCB.getSelectedStyle().setBgColor(0x2ecc71);
            eventTypeCB.setRenderer(new MistroListCellRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.generic_cow_event_types)));
            infoDialog.addComponent(eventTypeCB);
            
            infoDialog.addCommandListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    if(evt.getCommand().equals(backCommand)){
                        infoDialog.dispose();
                    }
                    else if(evt.getCommand().equals(nextCommand)){
                        String[] eventTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.generic_cow_event_types);
                        infoDialog.dispose();
                        if(eventTypesInEN[eventTypeCB.getSelectedIndex()].equals("Death")){
                            AddDeathEventScreen addDeathEventScreen = new AddDeathEventScreen(midlet, locale, farmer);
                            addDeathEventScreen.start();
                        }
                        else if(eventTypesInEN[eventTypeCB.getSelectedIndex()].equals("Acquisition")){
                            AddAcquisitionEventScreen acquisitionEventScreen = new AddAcquisitionEventScreen(midlet, locale, farmer);
                            acquisitionEventScreen.start();
                        }
                        else{
                            AddGenericEventScreen addEventScreen = new AddGenericEventScreen(midlet, locale, farmer, eventTypesInEN[eventTypeCB.getSelectedIndex()]);
                            addEventScreen.start();
                        }
                    }
                }
            });
            
            infoDialog.show(100, 100, 11, 11, true);
        }
        else if(evt.getComponent().equals(pastEventsButton)){

        }
    }
    
}
