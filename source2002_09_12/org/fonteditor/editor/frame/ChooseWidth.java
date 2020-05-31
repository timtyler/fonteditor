package org.fonteditor.editor.frame;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.fonteditor.gui.SelectorPen;
import org.fonteditor.options.pen.Pen;
import org.fonteditor.options.pen.PenFactory;
import org.fonteditor.utilities.callback.CallBack;

/**
  * GUI component to choose the width of the lines the font is rendered with...
  */

public class ChooseWidth extends Panel {
  private static TextField tf_width;
  private GlyphOuterPanel glyph_outer_panel;
  private SelectorPen selector_pen;
  private Checkbox checkbox_outline;
  private Button button_set;
  private CallBack callback_pen;

  public ChooseWidth(final GlyphOuterPanel glyph_outer_panel) {
    this.glyph_outer_panel = glyph_outer_panel;
    ItemListener item_listner = new FEItemListener();
    ItemListener item_listner2 = new FEItemListener2();
    ActionListener action_listner = new FEActionListener();

    checkbox_outline = new Checkbox("Outline");
    checkbox_outline.addItemListener(item_listner);
    checkbox_outline.setState(true);
    add(checkbox_outline);

    add(new Label("Width:"));

    tf_width = new TextField("800");
    add(tf_width);

    button_set = new Button("Set");
    button_set.addActionListener(action_listner);
    add(button_set);

    callback_pen = new CallBack() {
      public void callback(Object o) {
        glyph_outer_panel.getGlyphInnerPanel().getGDOE().setPen((Pen) o);
      }
    };

    add(new Label("Pen:", Label.RIGHT));
    selector_pen = new SelectorPen(callback_pen, item_listner2);
    add(selector_pen.getChoice());

    setBackground(new Color(0xD8D8D8));
    validate();
    repaint();
  }

  void setUpWidth() {
    glyph_outer_panel.getGlyphInnerPanel().getGDOE().getPen().setWidth(getWeight());
    glyph_outer_panel.getGlyphInnerPanel().getFEG().resetRemakeFlag();
    callRepaint();
  }

  void callRepaint() {
    glyph_outer_panel.getGlyphInnerPanel().repaint();
  }

  int getWeight() {
    if (!checkbox_outline.getState()) {
      return 0;
    }
    
    return Integer.parseInt(tf_width.getText());
  }

  public Checkbox getCheckboxOutline() {
    return checkbox_outline;
  }

  Button getButtonSet() {
    return button_set;
  }

  GlyphOuterPanel getGlyphOuterPanel() {
    return glyph_outer_panel;
  }

  class FEItemListener implements ItemListener {
    public void itemStateChanged(ItemEvent e) {
      if (e != null) {
        Object o = e.getSource();

        if (o == getCheckboxOutline()) {
          setUpWidth();
          getGlyphOuterPanel().getGlyphInnerPanel().getFEG().resetRemakeFlag();
          getGlyphOuterPanel().getGlyphInnerPanel().repaint();
        }
      }
    }
  }

  class FEItemListener2 implements ItemListener {
    public void itemStateChanged(ItemEvent e) {
      if (e != null) {
        Object source = e.getSource();

        if (source == selector_pen.getChoice()) {
          callback_pen.callback(PenFactory.newPen(selector_pen.getSelectedItem(), getWeight()));
          callRepaint();
        }
      }
    }
  }

  class FEActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      // String arg = e.getActionCommand();
      Object source = e.getSource();

      if (source == getButtonSet()) {
        setUpWidth();
      }
    }
  }
}
