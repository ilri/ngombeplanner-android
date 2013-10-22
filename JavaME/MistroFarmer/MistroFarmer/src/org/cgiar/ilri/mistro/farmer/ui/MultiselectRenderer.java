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
    
    public MultiselectRenderer(String[] items) {
        super();
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
        clickedBoxItem.toggleChecked();
    }
}