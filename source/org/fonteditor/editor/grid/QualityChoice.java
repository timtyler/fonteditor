package org.fonteditor.editor.grid;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.fonteditor.gui.TTChoice;
import org.fonteditor.utilities.callback.CallBack;

/**
 * Choose quality of rendering...
 */

public class QualityChoice extends TTChoice implements ItemListener {
  private CallBack call_back;

  public QualityChoice(final CallBack call_back, ItemListener il) {
    super(il);
    this.call_back = call_back;
    add("1x1", 1);
    add("2x2", 2);
    add("3x3", 3);
    add("4x4", 4);
    add("5x5", 5);
    add("6x6", 6);
    add("7x7", 7);
    add("8x8", 8);
    add("9x9", 9);
    add("10x10", 10);
    add("11x11", 11);
    add("12x12", 12);
    add("13x13", 13);
    add("14x14", 14);
    add("15x15", 15);
    add("16x16", 16);
    getChoice().select("2x2");
  }

  public int getQualityValue() {
    return stringToNumber(getChoice().getSelectedItem());
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
}
