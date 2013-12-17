package org.cgiar.ilri.mistro.farmer.ui;

import com.sun.lwuit.Component;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.list.ListCellRenderer;

/**
 *
 * @author jason
 */
public class MistroListCellRenderer extends List implements ListCellRenderer{

    private String[] names;
    
    public MistroListCellRenderer(String[] names) {
        super();
        this.names=names;
    }
    public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
        Label label = new Label(names[index]);
        if(isSelected) {
            label.getStyle().setBgColor(0x2ecc71);
        }
        return label;
    }

    public Component getListFocusComponent(List list) {
        return this;
    }
    
}
