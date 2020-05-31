package org.fonteditor.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.fonteditor.utilities.callback.CallBack;

/**
 * GUI control - used to choose fonts...
 */

public class SelectorExpand extends TTChoice {
  private CallBack call_back;

  public SelectorExpand(final CallBack call_back, ItemListener il) {
    super(il);
    this.call_back = call_back;

    add("0.3", 0x180);
    add("0.4", 0x1C0);
    add("0.5", 0x200);
    add("0.55", 0x240);
    add("0.6", 0x280);
    add("0.65", 0x2C0);
    add("0.75", 0x300);
    add("0.8125", 0x340);
    add("0.875", 0x380);
    add("0.9375", 0x3C0);
    add("1.0", 0x400);
    add("1.0625", 0x440);
    add("1.125", 0x480);
    add("1.1875", 0x4C0);
    add("1.25", 0x500);
    add("1.3125", 0x540);
    add("1.375", 0x580);
    add("1.4375", 0x5C0);
    add("1.5", 0x600);
    add("1.625", 0x680);
    add("1.75", 0x700);
    add("1.875", 0x780);
    add("2.0", 0x800);

    getChoice().select("1.0");
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

 public int getExpand() {
   return stringToNumber(getChoice().getSelectedItem());
 }
}
