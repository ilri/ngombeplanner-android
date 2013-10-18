package org.cgiar.ilri.mistro.farmer;

import com.sun.lwuit.Display;
import javax.microedition.midlet.*;
import org.cgiar.ilri.mistro.farmer.ui.LoginScreen;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;

/**
 * You should only call Classes in the UI package here
 * 
 * @author jason
 */
public class Midlet extends MIDlet {
    private final int locale = Locale.LOCALE_EN;
    
    public void startApp() {
        Display.init(this);
        LoginScreen loginScreen = new  LoginScreen(this, locale);
        loginScreen.start();
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
    }
    
    public void destroy(){
        this.destroyApp(true);
        this.notifyDestroyed();
    }
}
