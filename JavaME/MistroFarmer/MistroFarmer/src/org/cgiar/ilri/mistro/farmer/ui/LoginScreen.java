package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Form;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;


/**
 *
 * @author jason
 */
public class LoginScreen extends Form implements Screen{
    private final int locale;
    
    public LoginScreen(int locale) {
        super(Locale.getStringInLocale(locale, StringResources.login));
        this.locale = locale;
    }

    public void start() {
        this.show();
    }

    public void destroy() {
    }

    public void pause() {
    }
    
}
