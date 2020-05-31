package org.fonteditor.demonstration;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.fonteditor.FEFontRenderer;
import org.fonteditor.cache.CachedGlyph;
import org.fonteditor.editor.grid.QualityChoice;
import org.fonteditor.font.FEFont;
import org.fonteditor.font.FontFind;
import org.fonteditor.graphics.ImageWrapper;
import org.fonteditor.gui.SelectorExpand;
import org.fonteditor.gui.SelectorFont;
import org.fonteditor.gui.SelectorPen;
import org.fonteditor.gui.SelectorSlant;
import org.fonteditor.gui.SelectorWeight;
import org.fonteditor.options.coords.CoordsFactory;
import org.fonteditor.options.display.GlyphDisplayOptions;
import org.fonteditor.options.pen.Pen;
import org.fonteditor.options.pen.PenFactory;
import org.fonteditor.options.pen.PenRound;
import org.fonteditor.utilities.callback.CallBack;
import org.fonteditor.utilities.log.Log;

/**
  * Type-and-see demonstration application...
  * Inner Panel.
  */

public class DemoPanel extends Panel implements ItemListener, ActionListener {
  private TextField textfield_text;
  private TextField textfield_size;

  private Button button_update;
  private GlyphDisplayOptions gdo;
  private FEFontRenderer font_renderer = new FEFontRenderer(this);
  private FEFont font = null;
  private QualityChoice quality_choice;

  private SelectorFont selector_font;
  private SelectorWeight selector_weight;
  private SelectorPen selector_pen;

  private CallBack callback_font;
  private CallBack callback_weight;
  private CallBack callback_slant;
  private CallBack callback_expand;

  private CallBack callback_pen;
  private CallBack callback_quality;

  private Checkbox checkbox_filled;
  private Checkbox checkbox_outline;

  private SelectorExpand selector_expand;
  private SelectorSlant selector_slant;

  public DemoPanel() {
    super();
    init();
  }

  void init() {
    setBackground(Color.white);
    textfield_text = new TextField("The quick brown fox jumped over the lazy dog");
    add(textfield_text);

    callback_font = new CallBack() {
      public void callback(Object o) {
        chooseFont((String) o);
        refresh();
      }
    };

    callback_weight = new CallBack() {
      public void callback(Object o) {
        gdo.getPen().setWidth(Integer.parseInt((String) o));
        refresh();
      }
    };

    callback_slant = new CallBack() {
      public void callback(Object o) {
        gdo.setSlant(Integer.parseInt((String) o));
        refresh();
      }
    };

    callback_expand = new CallBack() {
      public void callback(Object o) {
        gdo.setExpand(Integer.parseInt((String) o));
        refresh();
      }
    };

    callback_pen = new CallBack() {
      public void callback(Object o) {
        gdo.setPen((Pen) o);
        refresh();
      }
    };

    callback_quality = new CallBack() {
      public void callback(Object o) {
        o = o;
        refresh();
      }
    };

    quality_choice = new QualityChoice(callback_quality, this);

    add(new Label("Quality:", Label.RIGHT));
    add(quality_choice.getChoice());

    textfield_text.addKeyListener(new BKeyListener());
    add(new Label("Size:", Label.RIGHT));
    textfield_size = new TextField("44");
    add(textfield_size);

    button_update = new Button("Update");
    button_update.addActionListener(this);
    add(button_update);

    selector_font = new SelectorFont(callback_font, this);
    add(selector_font.getChoice());

    add(new Label("Weight:", Label.RIGHT));
    selector_weight = new SelectorWeight(callback_weight, this);
    add(selector_weight.getChoice());

    add(new Label("Slant:", Label.RIGHT));
    selector_slant = new SelectorSlant(callback_slant, this);
    add(selector_slant.getChoice());

    add(new Label("Expand:", Label.RIGHT));
    selector_expand = new SelectorExpand(callback_expand, this);
    add(selector_expand.getChoice());

    add(new Label("Pen:", Label.RIGHT));
    selector_pen = new SelectorPen(callback_pen, this);
    add(selector_pen.getChoice());

    checkbox_filled = new Checkbox("Filled", true);
    checkbox_filled.addItemListener(this);
    add(checkbox_filled);

    checkbox_outline = new Checkbox("Outline", true);
    checkbox_outline.addItemListener(this);
    add(checkbox_outline);

    validate();
    repaint();

    gdo = font_renderer.getGDO(10, 1, new PenRound(0x0), true);
  }

  void quietlyChooseFont(String s) {
    font = FontFind.find(s, this.getGraphics(), 32, 128, false, false);
  }

  void chooseFont(String s) {
    quietlyChooseFont(s);
    refresh();
  }

  void setUpSize() {
    int siz = Integer.parseInt(textfield_size.getText());
    int quality = quality_choice.getQualityValue();
    gdo.setCoords(CoordsFactory.makeCoords(siz, quality, quality));
  }

  public void paint(Graphics g) {
    update(g);
  }

  public void update(Graphics g) {
    //Dimension d = getSize();
    //int new_x = d.width;
    //int new_y = d.height;

    g.setColor(Color.white);
    g.fillRect(0, 0, 99999, 99999);
    //g.setColor(Color.red);

    String text = textfield_text.getText();
    //g.drawString(text, 10, 111);

    //int siz = Integer.parseInt(textfield_size.getText());
    //int quality = quality_choice.getQualityValue();

    setUpSize();

    if (font == null) {
      quietlyChooseFont("lucky");
    }

    char last_c = '@';
    int x = 4;

    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);

      int kd = font_renderer.getKerningDistance(last_c, font, gdo, c, font, gdo);

      x += kd + 2;

      last_c = c;

      CachedGlyph cached_glyph = font_renderer.getCachedGlyph(c, font, gdo);
      ImageWrapper i_w = cached_glyph.getImageWrapper();

      int offset_y = cached_glyph.getOffsetY();

      g.drawImage(i_w.getImage(), x, 75 + offset_y, null);
    }
  }

  public void itemStateChanged(ItemEvent e) {
    Object source = e.getSource();

    if (source == selector_font.getChoice()) {
      chooseFont(selector_font.getSelectedItem());
    }

    if (source == selector_weight.getChoice()) {
      callback_weight.callback("" + getWeight());
    }

    if (source == selector_slant.getChoice()) {
      callback_slant.callback("" + getSlant());
    }

    if (source == selector_expand.getChoice()) {
      callback_expand.callback("" + getExpand());
    }

    if (source == quality_choice.getChoice()) {
      refresh();
    }

    if (source == selector_pen.getChoice()) {
      callback_pen.callback(PenFactory.newPen(selector_pen.getSelectedItem(), getWeight()));
    }

    if (source == checkbox_filled) {
      gdo.setFill(checkbox_filled.getState());
      refresh();
    }

    if (source == checkbox_outline) {
      callback_weight.callback("" + (checkbox_outline.getState() ? getWeight() : 0));
      refresh();
    }
  }

  void refresh() {
    gdo.refresh();
    repaint();
  }

  int getWeight() {
    return selector_weight.getWeight();
  }

  int getExpand() {
    return selector_expand.getExpand();
  }

  int getSlant() {
    return selector_slant.getSlant();
  }

  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();

    if (source == button_update) {
      refresh();
    }
  }

  class BKeyListener extends KeyAdapter {
    // The user pressed a key corresponding to an ASCII value...
    public void keyTyped(KeyEvent e) {
      char k = e.getKeyChar();

      Log.log("Key:" + k);
      repaint();
    }

    public void keyReleased(KeyEvent e) {
      keyTyped(e);
    }
  }
}
