package org.fonteditor.gui;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.fonteditor.options.pen.PenFactory;
import org.fonteditor.utilities.callback.CallBack;

public class PanelFontSelector extends Panel implements ItemListener {
  private SelectorFont selector_font;
  private SelectorWeight selector_weight;
  private SelectorPen selector_pen;

  private CallBack call_back_font;
  private CallBack call_back_weight;
  private CallBack call_back_pen;
  private CallBack call_back_checkbox_outline;
  private CallBack call_back_checkbox_filled;

  private Checkbox checkbox_filled;
  private Checkbox checkbox_outline;

  private Checkbox checkbox_bold;
  private Checkbox checkbox_italic;

  public PanelFontSelector(final CallBack call_back_font, final CallBack call_back_weight, final CallBack call_back_checkbox_outline, final CallBack call_back_checkbox_filled, final CallBack call_back_pen, final boolean styles) {
    this.call_back_font = call_back_font;
    this.call_back_weight = call_back_weight;
    this.call_back_checkbox_outline = call_back_checkbox_outline;
    this.call_back_checkbox_filled = call_back_checkbox_filled;
    this.call_back_pen = call_back_pen;

    //add(new Label("Font:", Label.RIGHT));
    selector_font = new SelectorFont(call_back_font, this);
    add(selector_font.getChoice());

    add(new Label("Weight:", Label.RIGHT));
    selector_weight = new SelectorWeight(call_back_weight, this);
    add(selector_weight.getChoice());

    add(new Label("Pen:", Label.RIGHT));
    selector_pen = new SelectorPen(call_back_pen, this);
    add(selector_pen.getChoice());

    checkbox_bold = new Checkbox("Bold");
    checkbox_bold.addItemListener(this);

    if (styles) {
      add(checkbox_bold);
    }

    checkbox_bold.setState(true);

    checkbox_italic = new Checkbox("Italic");
    checkbox_italic.addItemListener(this);

    if (styles) {
      add(checkbox_italic);
    }

    checkbox_filled = new Checkbox("Filled", true);
    checkbox_filled.addItemListener(this);
    add(checkbox_filled);

    checkbox_outline = new Checkbox("Outline", true);
    checkbox_outline.addItemListener(this);
    add(checkbox_outline);

    //add(new Label("Size:"));
    //tf_size = new TextField("256");
    //add(tf_size);

    setBackground(new Color(0xE8E8E8));
    validate();
    repaint();
  }

  public void itemStateChanged(ItemEvent e) {
    Object source = e.getSource();
    if (source == selector_font.getChoice()) {
      call_back_font.callback(selector_font.getSelectedItem());
    }

    if (source == selector_weight.getChoice()) {
      call_back_weight.callback("" + getWeight());
    }

    if (source == selector_pen.getChoice()) {
      call_back_pen.callback(PenFactory.newPen(selector_pen.getSelectedItem(), getWeight()));
    }

    if (source == checkbox_outline) {
      call_back_checkbox_outline.callback((Checkbox) source);
    }

    if (source == checkbox_filled) {
      call_back_checkbox_filled.callback((Checkbox) source);
    }
  }

  int getWeight() {
    return selector_weight.getWeight();
  }

  int getWeight(String s) {
    return Integer.parseInt(s) * 0x100;
  }

  public Checkbox getCheckboxBold() {
    return checkbox_bold;
  }

  public Checkbox getCheckboxItalic() {
    return checkbox_italic;
  }

  public SelectorFont getFontSelector() {
    return selector_font;
  }

  public void setSelectorWeight(SelectorWeight selector_weight) {
    this.selector_weight = selector_weight;
  }

  public SelectorWeight getSelectorWeight() {
    return selector_weight;
  }
}
