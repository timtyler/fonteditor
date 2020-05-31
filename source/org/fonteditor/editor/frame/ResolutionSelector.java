package org.fonteditor.editor.frame;

import java.awt.Button;
import java.awt.Color;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.fonteditor.options.coords.CoordsEditor;
import org.fonteditor.options.display.DisplayOptionsEditor;
import org.fonteditor.utilities.general.Forget;

/**
  * GUI component which allows the grid resolution to be selected...
  */

public class ResolutionSelector extends Panel implements ItemListener {
  private static TextField tf_x;
  private static TextField tf_y;
  private GlyphOuterPanel glyph_outer_panel;
  private Button button_set;

  public ResolutionSelector(final GlyphOuterPanel glyph_outer_panel) {
    this.glyph_outer_panel = glyph_outer_panel;
    ActionListener action_listner = new FEActionListener();

    add(new Label("X:"));
    tf_x = new TextField("12");
    add(tf_x);
    add(new Label("Y:"));
    tf_y = new TextField("20");
    add(tf_y);
    button_set = new Button("Set");
    button_set.addActionListener(action_listner);
    add(button_set);
    setBackground(new Color(0xE8E8E8));
    validate();
    repaint();
  }

  public void itemStateChanged(ItemEvent e) {
    Forget.about(e);
  }

  public static TextField getTFX() {
    return tf_x;
  }

  public static TextField getTFY() {
    return tf_y;
  }

  GlyphOuterPanel getGlyphOuterPanel() {
    return glyph_outer_panel;
  }

  Button getButtonSet() {
    return button_set;
  }

  class FEActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();

      if (source == getButtonSet()) {
        int aa_x = Integer.parseInt(getTFX().getText());
        int aa_y = Integer.parseInt(getTFY().getText());
        DisplayOptionsEditor gdoe = getGlyphOuterPanel().getGlyphInnerPanel().getGDOE();
        CoordsEditor ce = gdoe.getCoordsEditor().setAASizeEditor(aa_x, aa_y);

        gdoe.setCoords(ce);

        getGlyphOuterPanel().getGlyphInnerPanel().refresh();
      }
    }
  }
}
