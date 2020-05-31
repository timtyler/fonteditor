package org.fonteditor.editor.grid;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.fonteditor.utilities.callback.CallBack;

/**
 * Choose quality of rendering...
 */

public class PanelQualityChoice extends Panel implements ItemListener {
  private CallBack call_back;
  private Checkbox checkbox_progressive;
  private QualityChoice choose_quality;

  public PanelQualityChoice(final CallBack call_back) {
    this.call_back = call_back;
    choose_quality = new QualityChoice(call_back, this);
    add(new Label("Quality:", Label.RIGHT));
    add(choose_quality.getChoice());
    checkbox_progressive = new Checkbox("Progressive");
    checkbox_progressive.setState(true);
    checkbox_progressive.addItemListener(this);
    add(checkbox_progressive);
    setBackground(new Color(0xD8D8D8));
    // //    character_grid.setTargets();
    validate();
    repaint();
  }

  //  private void rip() {
  //    character_grid.fontRip();
  //    character_grid.updateAll();
  //  }
  //  private void addQualityChoice() {
  //    add(new Label("Quality:", Label.RIGHT));
  //    choose_quality = new TTChoice(this);
  //    choose_quality.add("1", 1);
  //    choose_quality.add("2", 2);
  //    choose_quality.add("4", 4);
  //    choose_quality.add("8", 8);
  //    choose_quality.add("16", 16);
  //    //choose_quality.add("progressive", PROGRESSIVE);
  //
  //    choose_quality.getChoice().select("2");
  //    add(choose_quality.getChoice());
  //    //choose_quality.choice.addItemListener(this);
  //  }
  //  public static int getQualityValue() {
  //    return choose_quality.stringToNumber(choose_quality.getChoice().getSelectedItem());
  //  }
  
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

  //  public void itemStateChanged(ItemEvent e) {
  //    e = e;
  //    //Log.log("HIT!");
  //
  //    try {
  //      //if (e != null) {
  //      //rip();
  //      character_grid.clearCache();
  //      character_grid.setTargets();
  //      character_grid.resetQualities();
  //      character_grid.updateAll();
  //      //Log.log("HIT!");
  //      //}
  //      //if (choose_font.str_to_num(stateChangedString) != -99) {}
  //    } catch (java.lang.NullPointerException exc) {
  //      exc.printStackTrace();
  //      // do nothing
  //    }
  //  }
  void setCheckboxProgressive(Checkbox checkbox_progressive) {
    this.checkbox_progressive = checkbox_progressive;
  }

  Checkbox getCheckboxProgressive() {
    return checkbox_progressive;
  }

  public QualityChoice getChooseQuality() {
    return choose_quality;
  }
}