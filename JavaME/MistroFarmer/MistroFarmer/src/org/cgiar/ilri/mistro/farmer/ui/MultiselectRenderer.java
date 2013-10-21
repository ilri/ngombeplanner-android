package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.CheckBox;
import com.sun.lwuit.Component;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.list.ListCellRenderer;

/**
 *
 * @author jason
 */
public class MultiselectRenderer extends CheckBox implements ListCellRenderer{
    
    public MultiselectRenderer() {
        super();
    }
    
    public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
        this.setText(""+value);
        this.setFocus(isSelected);
        this.setSelected(isSelected);
        return this;
    }

    public Component getListFocusComponent(List list) {
        return this;
    }
    
}