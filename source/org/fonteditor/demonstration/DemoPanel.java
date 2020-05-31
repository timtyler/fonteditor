package org.fonteditor.demonstration;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
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

import org.fonteditor.FE;
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
import org.fonteditor.kerning.Kerning;
import org.fonteditor.options.coords.CoordsFactory;
import org.fonteditor.options.display.DisplayOptions;
import org.fonteditor.options.pen.Pen;
import org.fonteditor.options.pen.PenFactory;
import org.fonteditor.options.pen.PenRound;
import org.fonteditor.utilities.callback.CallBack;
import org.fonteditor.utilities.general.Forget;
import org.fonteditor.utilities.log.Log;
import org.fonteditor.utilities.resources.ImageLoader;

/**
  * Type-and-see demonstration application...
  * Inner Panel.
  */

public class DemoPanel extends Panel implements ItemListener, ActionListener {
  private TextField textfield_text;
  private TextField textfield_size;

  private Button button_update;
  private DisplayOptions gdo;
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

  private int text_size = -1;
  private ImageWrapper bg_texture;
  private AppletOffsets applet_offsets;

  public DemoPanel() {
    super();
    init();
  }

  void init() {
    setBackground(new Color(0xF8F8F8));

    textfield_text = new TextField("The quick brown fox jumped over the lazy dog");

    add(new Label("Text:", Label.RIGHT));
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
        Forget.about(o);
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

    add(new Label("Font:", Label.RIGHT));
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

    //    gdo = font_renderer.getGDO(10, 1, new PenRound(0x0), true);
    gdo = DisplayOptions.getGDOrender(10, 1, 1, new PenRound(0x0), true);
  }

  void quietlyChooseFont(String s) {
    font = FontFind.find(s, this.getGraphics(), 32, 128, false, false);
  }

  void chooseFont(String s) {
    quietlyChooseFont(s);
    refresh();
  }

  void setUpSize() {
    text_size = -1;
    int quality = quality_choice.getQualityValue();
    gdo.setCoords(CoordsFactory.makeCoords(getTextSize(), quality, quality));
  }

  int getTextSize() {
    if (text_size == -1) {
      text_size = Integer.parseInt(textfield_size.getText());
    }
    return text_size;
  }

  public void paint(Graphics g) {
    update(g);
  }

  ImageWrapper getTexture() {
    if (bg_texture == null) {
      bg_texture = ImageLoader.getImageNow("textures/texture.jpg");
    }

    return bg_texture;
  }

  public void drawBackground(Graphics g) {
    g.setColor(Color.white);
    //g.fillRect(0, 0, 99999, 99999);
    getTexture();

    int height = bg_texture.getHeight();
    int width = bg_texture.getWidth();

    int bg_offset_x = 0;
    int bg_offset_y = 0;

    if (FE.isApplet()) {
      if (applet_offsets == null) {
        applet_offsets = new AppletOffsets(this);
      }

      applet_offsets.setOffsets();

      bg_offset_x = Math.abs(applet_offsets.getHOffset()) % width;
      bg_offset_y = Math.abs(applet_offsets.getVOffset()) % height;
    }

    Dimension dim = getSize();
    int comp_width = dim.width;
    int comp_height = dim.height;

    //Log.log("comp_width:" + comp_width);
    //Log.log("width:" + width);

    int iterations_x = 1 + comp_width / width;
    int iterations_y = 1 + comp_height / height;

    int pos_x;
    int pos_y = bg_offset_y;

    //Log.log("ITX:" + iterations_x);
    //Log.log("ITY:" + iterations_y);

    for (int y = 0; y < iterations_y; y++) {
      pos_x = bg_offset_x;
      for (int x = 0; x < iterations_x; x++) {
        g.drawImage(bg_texture.getImage(), pos_x, pos_y, null);
        pos_x += width;
      }

      pos_y += height;
    }
  }

  public void update(Graphics g) {

    //Dimension d = getSize();
    //int new_x = d.width;
    //int new_y = d.height;

    //g.setColor(Color.red);

    drawBackground(g);

    String text = textfield_text.getText();
    //g.drawString(text, 10, 111);

    //int siz = Integer.parseInt(textfield_size.getText());
    //int quality = quality_choice.getQualityValue();

    setUpSize();

    if (font == null) {
      quietlyChooseFont("lucky");
    }

    drawThisString(g, text);
  }

  private boolean isTotallyBlank() {
    FontMetrics f_m = font_renderer.getFontMetrics(font, gdo);
    //Log.log("width:" + f_m.charWidth('W'));
    return (f_m.charWidth('W') <= 1);
  }

  private void drawThisString(Graphics graphics, String in_text) {
    if (isTotallyBlank()) { //text += " "; // quick hack for wrapping...
      return;
    }

    //text += " "; // quick hack for wrapping...
    FontMetrics f_m = font_renderer.getFontMetrics(font, gdo);
    int gap = f_m.getHeight();

    Dimension dim = getSize();
    int comp_width = dim.width;
    int comp_height = dim.height;

    int xi = 4;
    int x;
    int y = 75;
    int line = 0;

    String text = in_text;

    while (y < comp_height) {
      if (text.equals("")) {
        text = in_text;
      }

      x = xi;
      char last_c = '|';
      int n = findHowManyCharactersFit(comp_width, x, text);
      int n2;

      while (n == text.length() && (n > 0)) {
        text += " " + in_text;
        n = findHowManyCharactersFit(comp_width, x, text);
      }

      if (n == text.length()) {
        n2 = n;
      } else {
        n2 = FEFontRenderer.lengthBeforeLastSpace(text, n);
      }

      String line_text = text.substring(0, n2);
      text = text.substring(n2);
      text = text.trim();

      for (int i = 0; i < line_text.length(); i++) {
        char c = line_text.charAt(i);

        int kd = font_renderer.getKerningDistance(last_c, font, gdo, c, font, gdo);

        x += kd + Kerning.getKerningGap(gdo.getCoords().getAAHeight());

        last_c = c;

        CachedGlyph cached_glyph = font_renderer.getCachedGlyph(c, font, gdo);
        ImageWrapper i_w = cached_glyph.getImageWrapper();

        int offset_y = cached_glyph.getOffsetY();

        graphics.drawImage(i_w.getImage(), x, y + offset_y, null);
      }

      line++;
      y += gap;
    }
  }

  private int findHowManyCharactersFit(int comp_width, int x, String text) {
    return font_renderer.howManyCharactersFit(font, gdo, 0, text.toCharArray(), comp_width - x);
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
    //public void keyTyped(KeyEvent e) {
    //}

    public void keyReleased(KeyEvent e) {
      char k = e.getKeyChar();

      Log.log("Key:" + k);
      repaint();
    }
  }
}
