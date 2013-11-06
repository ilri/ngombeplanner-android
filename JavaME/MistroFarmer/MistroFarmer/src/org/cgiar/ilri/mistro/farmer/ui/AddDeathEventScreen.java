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
import com.sun.lwuit.TextArea;
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
public class AddDeathEventScreen extends Form implements Screen, ActionListener{

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
    private Label causeOfDeathL;
    private ComboBox causeOfDeathCB;
    private Label remarksL;
    private TextArea remarksTA;
    
    public AddDeathEventScreen(Midlet midlet, int locale, Farmer farmer) {
        super(Locale.getStringInLocale(locale, StringResources.death));
        
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
                if(evt.getCommand().equals(backCommand)){
                    EventsScreen eventsScreen = new EventsScreen(AddDeathEventScreen.this.midlet, AddDeathEventScreen.this.locale, AddDeathEventScreen.this.farmer);
                    eventsScreen.start();
                }
                else if(evt.getCommand().equals(okayCommand)){
                    if(validateInput()){
                        Cow selectedCow = (Cow)validCows.elementAt(cowCB.getSelectedIndex());
                        Date date = (Date) dateS.getValue();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        String dateString = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.MONTH)+1)+"/"+String.valueOf(calendar.get(Calendar.YEAR));
                        JSONObject jSONObject = new JSONObject();
                        try {
                            jSONObject.put("mobileNo", AddDeathEventScreen.this.farmer.getMobileNumber());
                            jSONObject.put("cowEarTagNumber", selectedCow.getEarTagNumber());
                            jSONObject.put("cowName", selectedCow.getName());
                            jSONObject.put("date", dateString);
                            jSONObject.put("eventType","Death");
                            jSONObject.put("remarks", remarksTA.getText());
                            
                            String[] causeOfDeathInEN = Locale.getStringArrayInLocale(Locale.LOCALE_EN, ArrayResources.causes_of_death);
                            jSONObject.put("causeOfDeath",causeOfDeathInEN[causeOfDeathCB.getSelectedIndex()]);
                            
                            Thread thread = new Thread(new AddDeathEventScreen.EventHandler(jSONObject));
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
        
        String[] cowNames = getValidCows();
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
        
        causeOfDeathL = new Label(Locale.getStringInLocale(locale, StringResources.cause_of_death));
        setLabelStyle(causeOfDeathL);
        this.addComponent(causeOfDeathL);
        
        causeOfDeathCB = new ComboBox(Locale.getStringArrayInLocale(locale, ArrayResources.causes_of_death));
        setComponentStyle(causeOfDeathCB, true);
        causeOfDeathCB.setRenderer(new MistroListCellRenderer(Locale.getStringArrayInLocale(locale, ArrayResources.causes_of_death)));
        this.addComponent(causeOfDeathCB);
        
        remarksL = new Label(Locale.getStringInLocale(locale, StringResources.remarks));
        setLabelStyle(remarksL);
        this.addComponent(remarksL);
        
        remarksTA = new TextArea();
        setComponentStyle(remarksTA, true);
        remarksTA.setRows(4);
        this.addComponent(remarksTA);
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

    public void actionPerformed(ActionEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private String[] getValidCows(){
        Cow[] allCows = farmer.getCows();
        validCows = new Vector(allCows.length);
        for(int i = 0; i < allCows.length; i++){
            if(allCows[i].getEarTagNumber()!=null && allCows[i].getEarTagNumber().trim().length()>0){
                validCows.addElement(allCows[i]);
            }
        }
        
        String[] cowNames = new String[validCows.size()];
        for(int i =0; i < validCows.size(); i++){
            Cow currentCow = (Cow) validCows.elementAt(i);
            if(currentCow.getName()!= null && currentCow.getName().trim().length() > 0)
                cowNames[i]=currentCow.getEarTagNumber()+" ("+currentCow.getName()+")";
            else
                cowNames[i]=currentCow.getEarTagNumber();
        }
        
        return cowNames;
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
        
        TextArea text = new TextArea();
        text.setEditable(false);
        text.setFocusable(false);
        text.getStyle().setAlignment(CENTER);
        infoDialog.addComponent(text);
        
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
        
        if(response == null){
            text.setText(Locale.getStringInLocale(locale, StringResources.problem_connecting_to_server));
            infoDialog.show(100, 100, 11, 11, true);
        }
        else if(response.equals(DataHandler.ACKNOWLEDGE_OK)){
            text.setText(Locale.getStringInLocale(locale, StringResources.information_successfully_sent_to_server));
            infoDialog.show(100, 100, 11, 11, true);
            EventsScreen eventsScreen = new EventsScreen(midlet, locale, farmer);
            eventsScreen.start();
        }
        else{
            text.setText(Locale.getStringInLocale(locale, StringResources.problem_in_data_sent_en));
            infoDialog.show(100, 100, 11, 11, true);
        }
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
