/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.CheckBox;
import com.sun.lwuit.Component;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.FocusListener;

/**
 *
 * @author jason
 */
public class ComboBoxItem extends CheckBox{
    private boolean checked;
    private final int index;

    public ComboBoxItem(String name, int index) {
        super(name);
        this.index = index;
        this.checked = false;
    }
    
    public boolean isChecked(){
        return checked;
    }
    
    public void toggleChecked(){
        this.checked = !checked;
        this.setSelected(checked);
    }
    
    public void setChecked(){
        this.checked = true;
        this.setSelected(true);
    }
}

