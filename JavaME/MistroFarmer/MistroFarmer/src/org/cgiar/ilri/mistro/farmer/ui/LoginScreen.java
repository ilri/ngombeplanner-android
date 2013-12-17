package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;
import org.cgiar.ilri.mistro.farmer.utils.DataHandler;
import org.json.me.JSONException;
import org.json.me.JSONObject;


/**
 *
 * @author jason
 */
public class LoginScreen extends Form implements Screen, ActionListener{
    private final int locale;
    
    private BoxLayout parentBoxLayout;
    private Button loginButton;
    private Button registerButton;
    private Command exitCommand;
    private final Midlet midlet;
    
    public LoginScreen(Midlet midlet,int locale) {
        super();
        this.midlet = midlet;
        this.locale = locale;
        
        //init all layout components
        this.parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        exitCommand = new Command(Locale.getStringInLocale(locale, StringResources.exit));
        this.addCommand(exitCommand);
        this.addCommandListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(exitCommand)){
                    destroy();
                }
            }
        });
        
        loginButton = new Button(Locale.getStringInLocale(locale, StringResources.login));
        loginButton.getStyle().setAlignment(Component.CENTER);
        loginButton.getStyle().setMargin(100, 10, 0, 0);
        loginButton.getSelectedStyle().setAlignment(Component.CENTER);
        loginButton.getSelectedStyle().setMargin(100, 10, 0, 0);
        loginButton.getSelectedStyle().setBgColor(0x2ecc71);
        loginButton.addActionListener(this);
        this.addComponent(loginButton);
        
        registerButton = new Button(Locale.getStringInLocale(locale, StringResources.register));
        registerButton.getStyle().setAlignment(Component.CENTER);
        registerButton.getStyle().setMargin(10, 10, 0, 0);
        registerButton.getSelectedStyle().setAlignment(Component.CENTER);
        registerButton.getSelectedStyle().setMargin(10, 10, 0, 0);
        registerButton.getSelectedStyle().setBgColor(0x2ecc71);
        registerButton.addActionListener(this);
        this.addComponent(registerButton);
    }

    public void start() {
        this.show();
    }

    public void destroy() {
        midlet.destroy();
    }

    public void pause() {
    }

    public void actionPerformed(ActionEvent event) {
        if(event.getComponent().equals(registerButton)){
            FarmerRegistrationScreen farmerRegistrationScreen = new FarmerRegistrationScreen(midlet, locale, null);
            farmerRegistrationScreen.start();
        }
        else if(event.getComponent().equals(loginButton)){
            final Dialog infoDialog = new Dialog(Locale.getStringInLocale(locale, StringResources.login));
            infoDialog.setDialogType(Dialog.TYPE_INFO);
            
            final Command cancelCommand = new Command(Locale.getStringInLocale(locale, StringResources.cancel));
            infoDialog.addCommand(cancelCommand);
            
            final Command loginCommand = new Command(Locale.getStringInLocale(locale, StringResources.login));
            infoDialog.addCommand(loginCommand);
            
            Label mobileNumberL = new Label(Locale.getStringInLocale(locale, StringResources.mobile_number));
            mobileNumberL.getStyle().setMargin(10, 0, 10, 0);
            mobileNumberL.getSelectedStyle().setMargin(10, 0, 10, 0);
            infoDialog.addComponent(mobileNumberL);
            
            final TextField mobileNumberTF = new TextField();
            mobileNumberTF.getStyle().setMargin(5, 0, 0, 0);
            mobileNumberTF.getSelectedStyle().setMargin(5, 0, 0, 0);
            mobileNumberTF.setConstraint(TextField.NUMERIC);
            mobileNumberTF.setInputModeOrder(new String[] {"123"});
            infoDialog.addComponent(mobileNumberTF);
            
            infoDialog.addCommandListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if(evt.getCommand().equals(cancelCommand)){
                        infoDialog.dispose();
                    }
                    else if(evt.getCommand().equals(loginCommand)){
                        if(mobileNumberTF.getText()!=null || mobileNumberTF.getText().trim().length()>0){
                            String mobileNumber = mobileNumberTF.getText();
                            Thread thread = new Thread(new LoginHandler(mobileNumber, infoDialog));
                            thread.run();
                        }
                    }
                }
            });
            
            infoDialog.show(100, 100, 11, 11, true);
        }
    }
    
    private void actOnServerResponse(String response){
        if(response == null){
            System.err.println("no response from server");
        }
        else if(response.equals(DataHandler.CODE_USER_NOT_AUTHENTICATED)) {
            System.err.println("user not authenticated");
        }
        else{
            try {
                JSONObject farmerJSONObject = new JSONObject(response);
                Farmer farmer = new Farmer(farmerJSONObject);
                
                MainMenuScreen mainMenuScreen = new MainMenuScreen(midlet, locale, farmer);
                mainMenuScreen.start();
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private class LoginHandler implements Runnable{
        
        String mobileNumber;
        Dialog parentDialog;

        public LoginHandler(String mobileNumber, Dialog parentDialog) {
            this.mobileNumber = mobileNumber;
            this.parentDialog = parentDialog;
        }
        

        public void run() {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("mobileNumber", mobileNumber);
                String response = DataHandler.sendDataToServer(jSONObject, DataHandler.FARMER_AUTHENTICATION_URL);
                parentDialog.dispose();
                actOnServerResponse(response);
            } 
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
}
