/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.spinner.Spinner;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import org.cgiar.ilri.mistro.farmer.Midlet;
import org.cgiar.ilri.mistro.farmer.carrier.Cow;
import org.cgiar.ilri.mistro.farmer.carrier.Farmer;
import org.cgiar.ilri.mistro.farmer.ui.localization.ArrayResources;
import org.cgiar.ilri.mistro.farmer.ui.localization.Locale;
import org.cgiar.ilri.mistro.farmer.ui.localization.StringResources;
import org.cgiar.ilri.mistro.farmer.utils.DataHandler;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 *
 * @author jason
 */
public class ServicingScreen extends Form implements Screen, ActionListener{
    private final Midlet midlet;
    private final int locale;
    private final Farmer farmer;
    
    private Vector validCows;
    
    private BoxLayout parentBoxLayout;
    private Command backCommand;
    private Command okayCommand;
    
    private Label cowL;
    private ComboBox cowCB;
    private Label dateL;
    private Spinner dateS;
    private Label servicingTypeL;
    private ComboBox servicingTypeCB;
    private Label vetUsedL;
    private TextField vetUsedTF;
    private Label bullNameL;
    private TextField bullNameTF;
    private Label earTagNumberL;
    private TextField earTagNumberTF;
    private Label strawNumberL;
    private TextField strawNumberTF;
    
    public ServicingScreen(Midlet midlet, int locale, Farmer farmer) {
        super(Locale.getStringInLocale(locale, StringResources.servicing));
        
        this.midlet = midlet;
        this.locale = locale;
        this.farmer = farmer;
        
        parentBoxLayout = new BoxLayout(BoxLayout.Y_AXIS);
        this.setLayout(parentBoxLayout);
        
        backCommand = new Command(Locale.getStringInLocale(locale, StringResources.back));
        this.addCommand(backCommand);
        
        okayCommand = new Command(Locale.getStringInLocale(locale, StringResources.okay));
        this.addCommand(okayCommand);
        
        this.addCommandListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if(evt.getCommand().equals(backCommand)) {
                    FertilityScreen fertilityScreen = new FertilityScreen(ServicingScreen.this.midlet, ServicingScreen.this.locale, ServicingScreen.this.farmer);
                    fertilityScreen.start();
                }
                else if(evt.getCommand().equals(okayCommand)){
                    if(validateInput()){
                        JSONObject jSONObject = new JSONObject();
                        try {
                            Cow selectedCow = (Cow)validCows.elementAt(cowCB.getSelectedIndex());
                            String[] sevicingTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.main_service_types);
                            jSONObject.put("mobileNo", ServicingScreen.this.farmer.getMobileNumber());
                            jSONObject.put("cowEarTagNumber", selectedCow.getEarTagNumber());
                            jSONObject.put("cowName", selectedCow.getName());
                            Date date = (Date) dateS.getValue();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            String dateString = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.MONTH)+1)+"/"+String.valueOf(calendar.get(Calendar.YEAR));
                            jSONObject.put("date", dateString);//TODO:implement date spinner
                            jSONObject.put("eventType", sevicingTypesInEN[servicingTypeCB.getSelectedIndex()]);
                            jSONObject.put("strawNumber", strawNumberTF.getText());
                            jSONObject.put("vetUsed", vetUsedTF.getText());
                            jSONObject.put("bullName", bullNameTF.getText());
                            jSONObject.put("bullEarTagNo", earTagNumberTF.getText());
                            Thread thread = new Thread(new EventHandler(jSONObject));
                            thread.run();
                        } 
                        catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                        
                    }
                }
            }
        });
        
        cowL = new Label(Locale.getStringInLocale(locale, StringResources.cow));
        setLabelStyle(cowL);
        this.addComponent(cowL);
        
        String [] cowNames = getValidCows();
        cowCB = new ComboBox(cowNames);
        setComponentStyle(cowCB, true);
        cowCB.setRenderer(new MistroListCellRenderer(cowNames));
        this.addComponent(cowCB);
        
        dateL = new Label(Locale.getStringInLocale(locale, StringResources.date));
        setLabelStyle(dateL);
        this.addComponent(dateL);
        
        dateS = Spinner.createDate(System.currentTimeMillis() - (31536000730l*50), System.currentTimeMillis(), System.currentTimeMillis(), '/', Spinner.DATE_FORMAT_DD_MM_YYYY);
        setComponentStyle(dateS, true);
        dateS.getSelectedStyle().setFgColor(0x2ecc71);
        this.addComponent(dateS);
        
        servicingTypeL = new Label(Locale.getStringInLocale(locale, StringResources.service_type_used));
        setLabelStyle(servicingTypeL);
        this.addComponent(servicingTypeL);
        
        String[] mainServiceTypes = Locale.getStringArrayInLocale(locale, ArrayResources.main_service_types);
        servicingTypeCB = new ComboBox(mainServiceTypes);
        setComponentStyle(servicingTypeCB, true);
        servicingTypeCB.setRenderer(new MistroListCellRenderer(mainServiceTypes));
        servicingTypeCB.addActionListener(this);
        this.addComponent(servicingTypeCB);
        
        vetUsedL = new Label(Locale.getStringInLocale(locale, StringResources.vet_used));
        setLabelStyle(vetUsedL);
        this.addComponent(vetUsedL);
        
        vetUsedTF = new TextField();
        setComponentStyle(vetUsedTF, false);
        this.addComponent(vetUsedTF);
        
        bullNameL = new Label(Locale.getStringInLocale(locale, StringResources.bull_name));
        setLabelStyle(bullNameL);
        this.addComponent(bullNameL);
        
        bullNameTF = new TextField();
        setComponentStyle(bullNameTF, false);
        this.addComponent(bullNameTF);
        
        earTagNumberL = new Label(Locale.getStringInLocale(locale, StringResources.ear_tag_number));
        setLabelStyle(earTagNumberL);
        this.addComponent(earTagNumberL);
        
        earTagNumberTF = new TextField();
        setComponentStyle(earTagNumberTF, false);
        this.addComponent(earTagNumberTF);
        
        strawNumberL = new Label(Locale.getStringInLocale(locale, StringResources.straw_number));
        setLabelStyle(strawNumberL);
        this.addComponent(strawNumberL);
        
        strawNumberTF = new TextField();
        setComponentStyle(strawNumberTF, false);
        this.addComponent(strawNumberTF);
        
        serviceTypeSelected();
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
    
    private String[] getValidCows(){
        Cow[] allCows = farmer.getCows();
        validCows = new Vector(allCows.length);
        if(allCows!=null){
            for(int i = 0; i < allCows.length; i++){
                if(allCows[i].getSex().equals(Cow.SEX_FEMALE) && allCows[i].getEarTagNumber()!=null && allCows[i].getEarTagNumber().trim().length()>0){
                    validCows.addElement(allCows[i]);
                }
            }
            
            String[] cowNames = new String[validCows.size()];
            for(int i = 0 ; i < validCows.size(); i++){
                Cow currentCow = (Cow) validCows.elementAt(i);
                if(currentCow.getName()!=null && currentCow.getName().trim().length() > 0){
                    cowNames[i] = currentCow.getEarTagNumber()+" ("+currentCow.getName()+")";
                }
                else{
                    cowNames[i] = currentCow.getEarTagNumber();
                }
            }
            return cowNames;
        }
        String[] placibo = {" "};
        return placibo;
    }

    public void actionPerformed(ActionEvent evt) {
        if(evt.getComponent().equals(servicingTypeCB)){
            serviceTypeSelected();
        }
    }
    
    private void serviceTypeSelected(){
        String[] serviceTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.main_service_types);
        if(serviceTypesInEN[servicingTypeCB.getSelectedIndex()].equals(Cow.SERVICE_TYPE_AI)){
            setLabelFocusable(strawNumberL, true);
            setComponentFocusable(strawNumberTF, true);
            setLabelFocusable(vetUsedL,true);
            setComponentFocusable(vetUsedTF, true);
        }
        else{
            setLabelFocusable(strawNumberL, false);
            setComponentFocusable(strawNumberTF, false);
            setLabelFocusable(vetUsedL,false);
            setComponentFocusable(vetUsedTF, false);
        }
    }
    
    private void setLabelFocusable(Label label, boolean focusable){
        if(focusable){
            label.getStyle().setFgColor(0x000000);
        }
        else{
            label.getStyle().setFgColor(0xC0C0C0);
        }
    }
    
    private void setComponentFocusable(Component component, boolean focusable){
        if(focusable){
            component.getStyle().setBgColor(0xFFFFFF);
            component.setFocusable(true);
        }
        else{
            component.getStyle().setBgColor(0xC0C0C0);
            component.setFocusable(false);
        }
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
        
        String[] serviceTypesInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.main_service_types);
        if(serviceTypesInEN[servicingTypeCB.getSelectedIndex()].equals(Cow.SERVICE_TYPE_AI)){
            if((earTagNumberTF.getText()==null || earTagNumberTF.getText().trim().length() == 0) && (strawNumberTF.getText()==null || strawNumberTF.getText().trim().length() == 0)){
                text.setText(Locale.getStringInLocale(locale, StringResources.enter_bull_etn_or_straw_no));
                infoDialog.show(100, 100, 11, 11, true);
                return false;
            }
        }
        else if(serviceTypesInEN[servicingTypeCB.getSelectedIndex()].equals("Bull Servicing")){
            if(earTagNumberTF.getText()==null || earTagNumberTF.getText().trim().length() == 0){
                text.setText(Locale.getStringInLocale(locale, StringResources.enter_ear_tag_number));
                infoDialog.show(100, 100, 11, 11, true);
                earTagNumberTF.requestFocus();
                return false;
            }
        }
        if(validateDate()!=null){
            text.setText(validateDate());
            dateS.requestFocus();
            infoDialog.show(100, 100, 11, 11, true);
            return false;
        }
        return true;
    }
    
    private String validateDate(){
        Date dateSelected  = (Date) dateS.getValue();
        long currentTime = System.currentTimeMillis();
        
        long timeDiffDays = (currentTime - dateSelected.getTime())/86400000;
        
        if(timeDiffDays > 30){
            return Locale.getStringInLocale(locale, StringResources.milk_data_too_old);
        }
        else if(timeDiffDays < 0){
            return Locale.getStringInLocale(locale, StringResources.date_in_future);
        }
        
        return null;
    }
    
    private void reactToServerResponse(String response){
        
    }
    
    private class EventHandler implements Runnable{
        
        private JSONObject jSONObject;

        public EventHandler(JSONObject jSONObject) {
            this.jSONObject = jSONObject;
        }

        public void run() {
            String response = DataHandler.sendDataToServer(jSONObject, DataHandler.FARMER_ADD_COW_EVENT_URL);
            reactToServerResponse(response);
        }
        
    }
}
