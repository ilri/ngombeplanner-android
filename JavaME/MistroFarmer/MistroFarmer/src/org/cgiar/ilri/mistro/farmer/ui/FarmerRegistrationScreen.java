package org.cgiar.ilri.mistro.farmer.ui;


import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import org.cgiar.ilri.mistro.farmer.Midlet;
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
public class FarmerRegistrationScreen extends Form implements Screen{
    
    private final Midlet midlet;
    private final int locale;
    
    private BoxLayout parentBoxLayout;
    private Command backCommand;
    private Command nextCommand;
    private TextField fullNameTF;
    private Label fullNameL;
    private TextField mobileNoTF;
    private Label mobileNoL;
    private TextField ePersonnelTV;
    private Label ePersonnelL;

    public FarmerRegistrationScreen(Midlet midlet, int locale) {
        super(Locale.getStringInLocale(locale, StringResources.register));
        this.locale = locale;
        this.midlet = midlet;
        
        this.parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        
        nextCommand = new Command(Locale.getStringInLocale(locale, StringResources.next));
        this.addCommand(nextCommand);
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)){
                    LoginScreen loginScreen = new LoginScreen(FarmerRegistrationScreen.this.midlet, FarmerRegistrationScreen.this.locale);
                    loginScreen.start();
                }
                else if(evt.getCommand().equals(nextCommand)) {
                }
            }
        });
        
        fullNameL = new Label(Locale.getStringInLocale(locale, StringResources.full_name));
        fullNameL.getStyle().setMargin(10, 0, 10, 0);
        fullNameL.getSelectedStyle().setMargin(10, 0, 10,0);
        this.addComponent(fullNameL);
        
        fullNameTF = new TextField();
        fullNameTF.getStyle().setMargin(5, 0, 0, 0);
        fullNameTF.getSelectedStyle().setMargin(5, 0, 0, 0);
        this.addComponent(fullNameTF);
        
        mobileNoL = new Label(Locale.getStringInLocale(locale, StringResources.mobile_number));
        mobileNoL.getStyle().setMargin(10, 0, 10, 0);
        mobileNoL.getSelectedStyle().setMargin(10, 0, 10, 0);
        this.addComponent(mobileNoL);
        
        mobileNoTF = new TextField();
        mobileNoTF.getStyle().setMargin(5, 0, 0, 0);
        mobileNoTF.getSelectedStyle().setMargin(5, 0, 0, 0);
        this.addComponent(mobileNoTF);
        
        ePersonnelL = new Label(Locale.getStringInLocale(locale, StringResources.extension_p));
        ePersonnelL.getStyle().setMargin(10, 0, 10, 0);
        ePersonnelL.getSelectedStyle().setMargin(10, 0, 10, 0);
        this.addComponent(ePersonnelL);
        
        ePersonnelTV = new TextField();
        ePersonnelTV.getStyle().setMargin(5, 0, 0, 0);
        ePersonnelTV.getSelectedStyle().setMargin(5, 0, 0, 0);
        this.addComponent(ePersonnelTV);
    }
    
    public void start() {
        this.show();
    }

    public void destroy() {
    }

    public void pause() {
    }
    
}
