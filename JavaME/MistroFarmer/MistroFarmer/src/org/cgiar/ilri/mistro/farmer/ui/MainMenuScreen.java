/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Form;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;

/**
 *
 * @author jason
 */
public class MainMenuScreen extends Form implements Screen, ActionListener{

    private final int locale;
    private final Midlet midlet;
    private final Farmer farmer;
    
    private Command backCommand;
    private Button milkProductionB;
    private Button fertilityB;
    private Button eventsB;
    private BoxLayout parentBoxLayout;
    
    public MainMenuScreen(Midlet midlet, int locale, Farmer farmer) {
        super(Locale.getStringInLocale(locale, StringResources.main_menu));
        
        this.locale = locale;
        this.midlet = midlet;
        this.farmer = farmer;
        
        this.parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)) {
                    LoginScreen loginScreen = new LoginScreen(MainMenuScreen.this.midlet, MainMenuScreen.this.locale);
                    loginScreen.start();
                }
            }
        });
        
        milkProductionB = new Button(Locale.getStringInLocale(locale, StringResources.milk_production));
        milkProductionB.getStyle().setMargin(80, 10, 0, 0);
        milkProductionB.getSelectedStyle().setMargin(80, 10, 0, 0);
        setButtonStyle(milkProductionB);
        milkProductionB.addActionListener(this);
        this.addComponent(milkProductionB);
        
        fertilityB = new Button(Locale.getStringInLocale(locale, StringResources.fertility));
        fertilityB.getStyle().setMargin(10, 10, 0, 0);
        fertilityB.getSelectedStyle().setMargin(10, 10, 0, 0);
        setButtonStyle(fertilityB);
        fertilityB.addActionListener(this);
        this.addComponent(fertilityB);
        
        eventsB = new Button(Locale.getStringInLocale(locale, StringResources.other_events));
        eventsB.getStyle().setMargin(10, 10, 0, 0);
        eventsB.getSelectedStyle().setMargin(10, 10, 0, 0);
        setButtonStyle(eventsB);
        eventsB.addActionListener(this);
        this.addComponent(eventsB);
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
        
    }

    public void pause() {
        
    }

    public void actionPerformed(ActionEvent evt) {
        if(evt.getComponent().equals(milkProductionB)){
            MilkProductionScreen milkProductionScreen = new MilkProductionScreen(midlet, locale,farmer);
            milkProductionScreen.start();
        }
        else if(evt.getComponent().equals(fertilityB)){
            FertilityScreen fertilityScreen = new FertilityScreen(midlet, locale, farmer);
            fertilityScreen.start();
        }
        else if(evt.getComponent().equals(eventsB)){
            EventsScreen eventsScreen = new EventsScreen(midlet, locale, farmer);
            eventsScreen.start();
        }
    }
    
}
