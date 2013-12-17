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
import org.cgiar.ilri.mistro.farmer.ui.MainMenuScreen;
import org.cgiar.ilri.mistro.farmer.ui.Screen;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jason
 */
public class FertilityScreen extends Form implements Screen, ActionListener{
    
    private final Midlet midlet;
    private final int locale;
    private final Farmer farmer;
    
    private BoxLayout parentBoxLayout;
    private Command backCommand;
    
    private Button servicingB;
    private Button calvingB;
    
    public FertilityScreen(Midlet midlet, int locale, Farmer farmer) {
        super(Locale.getStringInLocale(locale, StringResources.fertility));
        
        this.midlet = midlet;
        this.locale = locale;
        this.farmer = farmer;
        
        parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)) {
                    MainMenuScreen mainMenuScreen = new MainMenuScreen(FertilityScreen.this.midlet, FertilityScreen.this.locale, FertilityScreen.this.farmer);
                    mainMenuScreen.start();
                }
            }
        });
        
        servicingB = new Button(Locale.getStringInLocale(locale, StringResources.servicing));
        servicingB.getStyle().setMargin(100, 10, 0, 0);
        servicingB.getSelectedStyle().setMargin(100, 10, 0, 0);
        setButtonStyle(servicingB);
        servicingB.addActionListener(this);
        this.addComponent(servicingB);
        
        calvingB = new Button(Locale.getStringInLocale(locale, StringResources.calving));
        calvingB.getStyle().setMargin(10, 10, 0, 0);
        calvingB.getSelectedStyle().setMargin(10, 10, 0, 0);
        setButtonStyle(calvingB);
        calvingB.addActionListener(this);
        this.addComponent(calvingB);
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
        if(evt.getComponent().equals(servicingB)){
            AddServicingScreen servicingScreen = new AddServicingScreen(midlet, locale, farmer);
            servicingScreen.start();
        }
        else if(evt.getComponent().equals(calvingB)){
            AddCalvingScreen addCalvingScreen = new AddCalvingScreen(midlet, locale, farmer);
            addCalvingScreen.start();
        }
    }
    
}
