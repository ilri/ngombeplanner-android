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
public class MilkProductionScreen extends Form implements Screen, ActionListener{

    private final int locale;
    private final Midlet midlet;
    private final Farmer farmer;
    
    private BoxLayout parentBoxLayout;
    private Command backCommand;
    
    private Button addMilkProductionB;
    private Button pastMilkProdutionB;
    
    public MilkProductionScreen(Midlet midlet, int locale, Farmer farmer) {
        super(Locale.getStringInLocale(locale, StringResources.milk_production));
        this.midlet = midlet;
        this.locale = locale;
        this.farmer = farmer;
        
        this.parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)) {
                    MainMenuScreen mainMenuScreen = new MainMenuScreen(MilkProductionScreen.this.midlet, MilkProductionScreen.this.locale, MilkProductionScreen.this.farmer);
                    mainMenuScreen.start();
                }
            }
        });
        
        parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        addMilkProductionB = new Button(Locale.getStringInLocale(locale, StringResources.add_production));
        addMilkProductionB.getStyle().setMargin(100, 10, 0, 0);
        addMilkProductionB.getSelectedStyle().setMargin(100, 10, 0, 0);
        setButtonStyle(addMilkProductionB);
        addMilkProductionB.addActionListener(this);
        this.addComponent(addMilkProductionB);
        
        pastMilkProdutionB = new Button(Locale.getStringInLocale(locale, StringResources.production_history));
        pastMilkProdutionB.getStyle().setMargin(10, 10, 0, 0);
        pastMilkProdutionB.getSelectedStyle().setMargin(10, 10, 0, 0);
        setButtonStyle(pastMilkProdutionB);
        pastMilkProdutionB.addActionListener(this);
        this.addComponent(pastMilkProdutionB);
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
        if(evt.getComponent().equals(addMilkProductionB)){
            AddMilkProductionScreen addMilkProductionScreen = new AddMilkProductionScreen(midlet, locale, farmer);
            addMilkProductionScreen.show();
        }
    }
    
}
