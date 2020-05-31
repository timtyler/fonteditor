package org.fonteditor.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.fonteditor.utilities.callback.CallBack;

/**
 * GUI control - used to choose fonts...
 */

public class SelectorFont extends TTChoice {
  private CallBack call_back;

  public SelectorFont(final CallBack call_back, ItemListener il) {
    super(il);
    this.call_back = call_back;
    String[] sa = FontList.getInternalFonts();

    for (int i = 0; i < sa.length; i++) {
      add(sa[i], i);
    }

    sa = FontList.getSystemFonts();
    for (int i = 0; i < sa.length; i++) {
      add(sa[i], i);
    }

    getChoice().select("lucky");
  }

  public void itemStateChanged(ItemEvent e) {
    String state_changed_string = null;

    try {
      if (e != null) {
        state_changed_string = (String) (e.getItem());
        call_back.callback(state_changed_string);
      }
    } catch (java.lang.NullPointerException npe) {
      npe.printStackTrace();
    }
  }
 
  public String getSelectedItem() {
    return getChoice().getSelectedItem();
  }
}
