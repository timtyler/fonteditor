package org.fonteditor.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.fonteditor.utilities.callback.CallBack;

/**
 * GUI control - used to choose fonts...
 */

public class SelectorWeight extends TTChoice {
  private CallBack call_back;

  public SelectorWeight(final CallBack call_back, ItemListener il) {
    super(il);
    this.call_back = call_back;

    add("0", 0);
    add("1", 1);
    add("2", 2);
    add("3", 3);
    add("4", 4);
    add("5", 5);
    add("6", 6);
    add("7", 7);
    add("8", 8);
    add("9", 9);

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

 public int getWeight() {
   return stringToNumber(getChoice().getSelectedItem()) * 0x100;
 }
}
