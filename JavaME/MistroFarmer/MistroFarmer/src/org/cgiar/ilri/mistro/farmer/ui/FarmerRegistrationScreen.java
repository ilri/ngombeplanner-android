package org.cgiar.ilri.mistro.farmer.ui;


import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
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
    private Label cowNumberL;
    private TextField cowNumberTF;
    private Farmer farmer;

    public FarmerRegistrationScreen(Midlet midlet, int locale, Farmer farmer) {
        super(Locale.getStringInLocale(locale, StringResources.register));
        this.locale = locale;
        this.midlet = midlet;
        if(farmer!=null){
            this.farmer = farmer;
        }
        else {
            this.farmer = new Farmer();
        }
        
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
                    String noOfCowsString = cowNumberTF.getText();
                    if(validateInput()){
                        saveFarmerDetails();
                        if(noOfCowsString!= null && !noOfCowsString.equals("")) {
                            CowRegistrationScreen firstCowScreen = new CowRegistrationScreen(FarmerRegistrationScreen.this.midlet, FarmerRegistrationScreen.this.locale, FarmerRegistrationScreen.this.farmer, 0, Integer.parseInt(noOfCowsString));
                            firstCowScreen.start();
                        }
                        else {
                            //TODO: send farmer details to server
                        }
                    }
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
        mobileNoTF.setConstraint(TextField.NUMERIC);
        mobileNoTF.setInputModeOrder(new String[] {"123"});
        this.addComponent(mobileNoTF);
        
        ePersonnelL = new Label(Locale.getStringInLocale(locale, StringResources.extension_p));
        ePersonnelL.getStyle().setMargin(10, 0, 10, 0);
        ePersonnelL.getSelectedStyle().setMargin(10, 0, 10, 0);
        this.addComponent(ePersonnelL);
        
        ePersonnelTV = new TextField();
        ePersonnelTV.getStyle().setMargin(5, 0, 0, 0);
        ePersonnelTV.getSelectedStyle().setMargin(5, 0, 0, 0);
        this.addComponent(ePersonnelTV);
        
        cowNumberL = new Label(Locale.getStringInLocale(locale, StringResources.number_of_cows));
        cowNumberL.getStyle().setMargin(10, 0, 10, 0);
        cowNumberL.getSelectedStyle().setMargin(10, 0, 10, 0);
        this.addComponent(cowNumberL);
        
        cowNumberTF = new TextField();
        cowNumberTF.getStyle().setMargin(5, 0, 0, 0);
        cowNumberTF.getSelectedStyle().setMargin(5, 0, 0, 0);
        cowNumberTF.setConstraint(TextField.NUMERIC);
        cowNumberTF.setInputModeOrder(new String[] {"123"});
        this.addComponent(cowNumberTF);
        
        restoreFarmerDetails();
    }
    
    private boolean validateInput(){
        final Dialog infoDialog = new Dialog();
        infoDialog.setDialogType(Dialog.TYPE_INFO);
        final Command backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        infoDialog.addCommand(backCommand);
        infoDialog.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)){
                    infoDialog.dispose();
                }
            }
        });
        
        Label text = new Label();
        text.getStyle().setAlignment(CENTER);
        infoDialog.addComponent(text);
        if(fullNameTF.getText()== null || fullNameTF.getText().trim().length()==0){
            fullNameTF.requestFocus();
            text.setText(Locale.getStringInLocale(locale, StringResources.enter_your_name));
            infoDialog.show(100, 100, 11, 11, true);
            return false;
        }
        if(mobileNoTF.getText()== null || mobileNoTF.getText().trim().length()==0){
            mobileNoTF.requestFocus();
            text.setText(Locale.getStringInLocale(locale, StringResources.enter_your_mobile_no));
            infoDialog.show(100, 100, 11, 11, true);
            return false;
        }
        return true;
    }
    
    private void saveFarmerDetails(){
        farmer.setMode(Farmer.MODE_INITIAL_REGISTRATION);
        farmer.setFullName(fullNameTF.getText());
        farmer.setMobileNumber(mobileNoTF.getText());
        farmer.setExtensionPersonnel(ePersonnelTV.getText());
        if(cowNumberTF.getText()!=null && cowNumberTF.getText().trim().length() > 0){
            farmer.setCowNumber(Integer.parseInt(cowNumberTF.getText().trim()));
        }
    }
    
    private void restoreFarmerDetails(){
        if(farmer!=null){
            fullNameTF.setText(farmer.getFullName());
            mobileNoTF.setText(farmer.getMobileNumber());
            ePersonnelTV.setText(farmer.getExtensionPersonnel());
            cowNumberTF.setText(String.valueOf(farmer.getCowNumber()));
        }
    }
    
    public void start() {
        this.show();
    }

    public void destroy() {
    }

    public void pause() {
    }
    
}
