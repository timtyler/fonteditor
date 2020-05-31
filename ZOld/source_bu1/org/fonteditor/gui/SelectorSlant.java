package org.fonteditor.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.fonteditor.utilities.callback.CallBack;

/**
 * GUI control - used to choose fonts...
 */

public class SelectorSlant extends TTChoice {
  private CallBack call_back;

  public SelectorSlant(final CallBack call_back, ItemListener il) {
    super(il);
    this.call_back = call_back;

    add("0", 0x00);
    add("1", 0x10);
    add("2", 0x20);
    add("3", 0x30);
    add("4", 0x40);
    add("5", 0x50);
    add("6", 0x60);
    add("7", 0x70);
    add("8", 0x80);
    add("9", 0x90);

    getChoice().select("0");
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

 public int getSlant() {
   return stringToNumber(getChoice().getSelectedItem());
 }
}
