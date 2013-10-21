package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.SelectionListener;
import com.sun.lwuit.layouts.BoxLayout;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.ui.localization.ArrayResources;
import org.cgiar.ilri.mistro.farmer.ui.localization.GeneralArrays;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;

/**
 *
 * @author jason
 */
public class CowRegistrationScreen extends Form implements Screen{

    private final Midlet midlet;
    private final int locale;
    private final int cowIndex;
    private final int cowNumber;
    
    private Command previousCommand;
    private Command nextCommand;
    
    private BoxLayout parentBoxLayout;
    private Label cowNameL;
    private TextField cowNameTF;
    private Label earTagNumberL;
    private TextField earTagNumberTF;
    private Label ageL;
    private TextField ageTF;
    private Label ageTypeL;
    private ComboBox ageTypeCB;
    private Label dateOfBirthL;
    private TextField dateOfBirthTF;
    private Label breedL;
    private ComboBox breedCB;
    private Label sexL;
    private ComboBox sexCB;
    private Label deformityL;
    private ComboBox deformityCB;
    private Label serviceTypeL;
    private ComboBox serviceTypeCB;
    private Label sireL;
    private ComboBox sireCB;
    private Label damL;
    private ComboBox damCB;
    private Label countryL;
    private ComboBox countryCB;
    public CowRegistrationScreen(Midlet midlet, int locale, int cowIndex, int cowNumber) {
        super(Locale.getStringInLocale(locale, StringResources.cow_registration_wth_indx)+String.valueOf(cowIndex+1));
        
        this.midlet = midlet;
        this.locale = locale;
        this.cowIndex = cowIndex;
        this.cowNumber = cowNumber;
        
        parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        previousCommand = new Command(Locale.getStringInLocale(locale, StringResources.previous));
        this.addCommand(previousCommand);
        
        if(cowIndex < (cowNumber-1))
            nextCommand = new Command(Locale.getStringInLocale(locale, StringResources.next));
        else
            nextCommand = new Command(Locale.getStringInLocale(locale, StringResources.finish));
        this.addCommand(nextCommand);
        
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {  
                if(evt.getCommand().equals(previousCommand)) {
                    if(CowRegistrationScreen.this.cowIndex>0){
                        CowRegistrationScreen previousScreen = new CowRegistrationScreen(CowRegistrationScreen.this.midlet, CowRegistrationScreen.this.locale, CowRegistrationScreen.this.cowIndex - 1, CowRegistrationScreen.this.cowNumber);
                        previousScreen.start();
                    }
                    else {
                        FarmerRegistrationScreen farmerRegistrationScreen = new FarmerRegistrationScreen(CowRegistrationScreen.this.midlet, CowRegistrationScreen.this.locale);
                        farmerRegistrationScreen.start();
                    }
                }
                else if(evt.getCommand().equals(nextCommand)) {
                    if(CowRegistrationScreen.this.cowIndex < (CowRegistrationScreen.this.cowNumber -1)) {
                        CowRegistrationScreen nextScreen = new CowRegistrationScreen(CowRegistrationScreen.this.midlet, CowRegistrationScreen.this.locale, CowRegistrationScreen.this.cowIndex + 1, CowRegistrationScreen.this.cowNumber);
                        nextScreen.start();
                    }
                }
            }
        });
        
        cowNameL = new Label(Locale.getStringInLocale(locale, StringResources.name));
        setLabelStyle(cowNameL);
        this.addComponent(cowNameL);
        
        cowNameTF = new TextField();
        setComponentStyle(cowNameTF, false);
        this.addComponent(cowNameTF);
        
        earTagNumberL = new Label(Locale.getStringInLocale(locale, StringResources.ear_tag_number));
        setLabelStyle(earTagNumberL);
        this.addComponent(earTagNumberL);
        
        earTagNumberTF = new TextField();
        setComponentStyle(earTagNumberTF, false);
        this.addComponent(earTagNumberTF);
        
        ageL = new Label(Locale.getStringInLocale(locale, StringResources.age));
        setLabelStyle(ageL);
        this.addComponent(ageL);
        
        ageTF = new TextField();
        setComponentStyle(ageTF, false);
        this.addComponent(ageTF);
        
        ageTypeL = new Label(Locale.getStringInLocale(locale, StringResources.age_type));
        setLabelStyle(ageTypeL);
        this.addComponent(ageTypeL);
        
        ageTypeCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.age_type_array));
        setComponentStyle(ageTypeCB, true);
        ageTypeCB.setRenderer(new MistroListCellRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.age_type_array)));
        this.addComponent(ageTypeCB);
        
        dateOfBirthL = new Label(Locale.getStringInLocale(locale, StringResources.date_of_birth));
        setLabelStyle(dateOfBirthL);
        this.addComponent(dateOfBirthL);
        
        dateOfBirthTF = new TextField();
        setComponentStyle(dateOfBirthTF, false);
        this.addComponent(dateOfBirthTF);
        
        breedL = new Label(Locale.getStringInLocale(locale, StringResources.breed));
        setLabelStyle(breedL);
        this.addComponent(breedL);
        
        breedCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.breeds_array));
        setComponentStyle(breedCB, true);
        breedCB.setRenderer(new MultiselectRenderer());
        this.addComponent(breedCB);
        
        sexL = new Label(Locale.getStringInLocale(locale, StringResources.sex));
        setLabelStyle(sexL);
        this.addComponent(sexL);
        
        sexCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.sex_array));
        setComponentStyle(sexCB, true);
        sexCB.setRenderer(new MistroListCellRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.sex_array)));
        this.addComponent(sexCB);
        
        deformityL = new Label(Locale.getStringInLocale(locale, StringResources.deformity));
        setLabelStyle(deformityL);
        this.addComponent(deformityL);
        
        deformityCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.deformities_array));
        setComponentStyle(deformityCB, true);
        deformityCB.setRenderer(new MultiselectRenderer());
        this.addComponent(deformityCB);
        
        serviceTypeL = new Label(Locale.getStringInLocale(locale, StringResources.service_type_used));
        setLabelStyle(serviceTypeL);
        this.addComponent(serviceTypeL);
        
        serviceTypeCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.service_types));
        setComponentStyle(serviceTypeCB, true);
        serviceTypeCB.setRenderer(new MistroListCellRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.service_types)));
        this.addComponent(serviceTypeCB);
        
        sireL = new Label(Locale.getStringInLocale(locale, StringResources.sire));
        setLabelStyle(sireL);
        this.addComponent(sireL);
        
        sireCB = new ComboBox(new String[]{" "});
        setComponentStyle(sireCB, true);
        sireCB.setRenderer(new MistroListCellRenderer(new String[]{" "}));
        this.addComponent(sireCB);
        
        damL = new Label(Locale.getStringInLocale(locale, StringResources.dam));
        setLabelStyle(damL);
        this.addComponent(damL);
        
        damCB = new ComboBox(new String[]{" "});
        setComponentStyle(damCB, true);
        damCB.setRenderer(new MistroListCellRenderer(new String[]{" "}));
        this.addComponent(damCB);
        
        countryL = new Label(Locale.getStringInLocale(locale, StringResources.country_of_origin));
        setLabelStyle(countryL);
        this.addComponent(countryL);
        
        countryCB = new ComboBox(GeneralArrays.common_countries);
        setComponentStyle(countryCB, true);
        countryCB.setRenderer(new MistroListCellRenderer(GeneralArrays.common_countries));
        this.addComponent(countryCB);
    }
    
    private void setLabelStyle(Label label){
        label.getStyle().setMargin(10, 0, 10, 0);
        label.getSelectedStyle().setMargin(10, 0, 10,0);
    }
    
    private void setComponentStyle(Component component, boolean isFocusable){
        component.getStyle().setMargin(5, 0, 0, 0);
        component.getSelectedStyle().setMargin(5, 0, 0, 0);
        if(isFocusable){
            component.getSelectedStyle().setBgColor(0x2ecc71);
        }
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
    
}
