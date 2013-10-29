package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Form;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;


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
            MainMenuScreen mainMenuScreen = new MainMenuScreen(midlet, locale);
            mainMenuScreen.start();
        }
    }
    
}
