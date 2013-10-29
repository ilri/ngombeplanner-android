package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Component;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.list.ListCellRenderer;
import java.util.Vector;

/**
 *
 * @author jason
 */
public class MultiselectRenderer implements ListCellRenderer, ActionListener{
    
    private Vector comboBoxItems;
    private final int maximumSelection;
    
    public MultiselectRenderer(String[] items, int maximumSelection) {
        super();
        this.maximumSelection = maximumSelection;
        comboBoxItems = new Vector(items.length);
        for(int i = 0; i < items.length; i ++){
            ComboBoxItem currentBoxItem = new ComboBoxItem(items[i],i);
            //currentBoxItem.addActionListener(this);
            comboBoxItems.addElement(currentBoxItem);
            
        }
    }
    
    public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
        ComboBoxItem currentComboBoxItem = (ComboBoxItem) comboBoxItems.elementAt(index);
        if(isSelected){
            currentComboBoxItem.getStyle().setBgColor(0x2ecc71);
        }
        else {
            currentComboBoxItem.getStyle().setBgColor(0xffffff);
        }
        return currentComboBoxItem;
    }

    public Component getListFocusComponent(List list) {
        return null;
    }

    public void actionPerformed(ActionEvent evt) {
        List list = (List) evt.getComponent();
        ComboBoxItem clickedBoxItem = (ComboBoxItem) comboBoxItems.elementAt(list.getSelectedIndex());
        
        if(clickedBoxItem.isChecked()){
            //we want to uncheck the item, no need for checking for constraints
            clickedBoxItem.toggleChecked();
        }
        else{
            System.out.println("checkbox was not previously checked");
            //get the number of selected items on list
            int selectedNumber=0;
            for(int i = 0; i < comboBoxItems.size(); i++){
                ComboBoxItem currentBoxItem= (ComboBoxItem) comboBoxItems.elementAt(i);
                if(currentBoxItem.isChecked()){
                    selectedNumber++;
                }
            }
            if(maximumSelection == 0 || selectedNumber <maximumSelection){
                clickedBoxItem.toggleChecked();
            }
        }
    }
    
    public String[] getSelectedItems() {
        String[] result = null;
        int numberSelected = 0;
        for(int i = 0; i < comboBoxItems.size(); i++){
            ComboBoxItem currentItem = (ComboBoxItem)comboBoxItems.elementAt(i);
            if(currentItem.isChecked()) {
                numberSelected++;
            }
        }
        if(numberSelected > 0) {
            result = new String[numberSelected];
            int currentIndex = 0;
            for(int i = 0; i < comboBoxItems.size(); i++){
                ComboBoxItem currentItem = (ComboBoxItem)comboBoxItems.elementAt(i);
                if(currentItem.isChecked()) {
                    result[currentIndex] = currentItem.getText();
                    currentIndex++;
                }
            }
        }
        return result;
    }
}