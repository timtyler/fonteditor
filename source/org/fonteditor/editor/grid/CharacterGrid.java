package org.fonteditor.editor.grid;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import org.fonteditor.FEFontRenderer;
import org.fonteditor.cache.CachedGlyph;
import org.fonteditor.editor.Fixer;
import org.fonteditor.editor.frame.DefaultFrameDisplayOptions;
import org.fonteditor.editor.frame.GlyphFrame;
import org.fonteditor.editor.frame.GlyphOuterPanel;
import org.fonteditor.font.FEFont;
import org.fonteditor.font.FEGlyph;
import org.fonteditor.font.FontFind;
import org.fonteditor.graphics.ImageWrapper;
import org.fonteditor.gui.PanelFontSelector;
import org.fonteditor.options.coords.Coords;
import org.fonteditor.options.display.DisplayOptions;
import org.fonteditor.options.display.DisplayOptionsEditor;
import org.fonteditor.options.pen.Pen;
import org.fonteditor.options.pen.PenRound;
import org.fonteditor.utilities.claim.Claim;
import org.fonteditor.utilities.general.Forget;

/**
  * Represents a grid of (editable) glyphs...
  */

public class CharacterGrid extends Canvas {
  private char char_min = 32;
  private char char_max = 128;

  private char redraw_min_start = char_min;
  private char redraw_min = 0;
  private char redraw_max = 0;

  private int columns = 16;
  private int rows = 6;

  private Point last_pointer = new Point();
  private Point current_pointer = new Point();

  private boolean mouse_pressed;

  private boolean first_time = true;

  private int c_max = 256;

  private CharacterInGrid[] ciga = new CharacterInGrid[c_max];

  private int log_aa_scale_factor_x;
  private int log_aa_scale_factor_y;

  private int aa_scale_factor_x;
  private int aa_scale_factor_y;

  private FEFont font;

  private int last_width;
  private int last_height;

  private FEFontRenderer font_renderer;
  private DefaultFrameDisplayOptions default_frame_display_options = new DefaultFrameDisplayOptions();
  private PanelFontSelector fsl;
  private PanelQualityChoice pqc;
  private DisplayOptions gdo;

  public CharacterGrid() {
    this.addMouseListener(new BMouseListener());
    this.addMouseMotionListener(new BMouseMotionListener());
    this.addKeyListener(new BKeyListener());
    setBackground(Color.white);
    antiAliasingOff();

    for (int i = 0; i < c_max; i++) {
      ciga[i] = new CharacterInGrid();
    }

    markAllForUpdate();
    font_renderer = new FEFontRenderer(this);
    gdo = DisplayOptions.getGDOrender(2, 1, 1, new PenRound(0x0), true);
    validate();
    repaint();
  }

  private void markAllForUpdate() {
    redraw_min = char_min;
    redraw_min_start = redraw_min;
    redraw_max = char_max;
  }

  public void setTargets() {
    int v = pqc.getChooseQuality().getQualityValue();

    for (int i = char_min; i < char_max; i++) {
      ciga[i].setTarget(v);
    }
  }

  public void resetQualities() {
    for (int i = char_min; i < char_max; i++) {
      //redraw_min; i < redraw_max; i++) {
      ciga[i].resetQuality(0);
    }
  }

  public void updateAll() {
    markAllForUpdate();
    resetQualities();
    repaint();
  }

  public void paint(Graphics g) {
    markAllForUpdate();
    update(g);
  }

  public void update(Graphics g) {
    if (first_time) {
      first_time = false;
      fontLoad(fsl.getFontSelector().getChoice().getSelectedItem(), g);
    }

    Dimension d = getSize();

    if ((d.width != last_width) || (d.height != last_height)) {
      last_width = d.width;
      last_height = d.height;
      font_renderer.getFEFontCache().removeAll();
      markAllForUpdate();
      resetQualities();
    }

    if (redraw_min < redraw_max) {
      CharacterInGrid cig = ciga[redraw_min];
      boolean progressive = pqc.getCheckboxProgressive().getState();

      if (!cig.metTarget()) {
        if (progressive) {
          cig.incrementQuality();
        } else {
          cig.finishQuality();
        }
      }

      int x = redraw_min % columns;
      int y = (redraw_min / columns) - 2;

      int dx = last_width / columns - 3;
      int dy = last_height / rows - 3;

      g.setColor(Color.white);
      g.fillRect((last_width * x) / columns + 1, ((last_height * (y)) / rows) + 1, dx + 2, dy + 2);

      int quality;
      quality = cig.getQuality();
      int siz = Math.min(dx << 1, dy);

      Coords c = new Coords((siz >> 1) * quality, siz * quality, siz >> 1, siz);
      gdo.setCoords(c);

      CachedGlyph cached_glyph = font_renderer.getCachedGlyph(redraw_min, font, gdo);
      Claim.claim(cached_glyph != null, "A problem with a cached_glyph being null");
      ImageWrapper i_w = cached_glyph.getImageWrapper();

      int offset_y = cached_glyph.getOffsetY();

      g.drawImage(i_w.getImage(), (last_width * x) / columns + 2, offset_y + ((last_height * (y)) / rows) + 2, null);

      repaint();
      redraw_min++;
    } else {
      if (needToDoMore()) {
        //Log.log("moreNeedsDoing");
        redraw_min = redraw_min_start; /// hmm...
        repaint();
      }

      g.setColor(Color.black);

      for (int x = 1; x < columns; x++) {
        //Log.log("columns" + columns);
        g.drawLine((last_width * x) / columns, 0, (last_width * x) / columns, 1999);
      }

      for (int y = 1; y < rows; y++) {
        //Log.log("rows" + rows);
        g.drawLine(0, (last_height * y) / rows, 1999, (last_height * y) / rows);
      }
    }
  }

  private boolean needToDoMore() {
    for (int i = char_min; i < char_max; i++) {
      if (!ciga[i].metTarget()) {
        return true;
      }
    }
    
    return false;
  }

  public void clearCache() {
    font_renderer.remove(font);
  }

  public void fontLoad(String s, Graphics g) {
    clearCache();

    boolean bold = fsl.getCheckboxBold().getState();
    boolean italic = fsl.getCheckboxItalic().getState();

    font = FontFind.find(s, g, char_min, char_max, bold, italic);
  }

  public void setFsl(PanelFontSelector fsl) {
    this.fsl = fsl;
  }

  private void antiAliasingOff() {
    setAntiAliasing(0, 0);
  }

  private void setAntiAliasing(int aax, int aay) {
    log_aa_scale_factor_x = aax;
    log_aa_scale_factor_y = aay;
    aa_scale_factor_x = 1 << aax;
    aa_scale_factor_y = 1 << aay;
  }

  void newFrame(String s) {
    GlyphOuterPanel applet2 = new GlyphOuterPanel(s, this, default_frame_display_options);

    applet2.setFrame(new GlyphFrame("Edit character - " + s, (Panel) applet2, default_frame_display_options.getWidth(), default_frame_display_options.getHeight()));
    applet2.getFrame().show();
  }

  private void repaintCharacter(char number) {
    if (redraw_min == redraw_max) {
      redraw_min = Character.MAX_VALUE;
      redraw_max = Character.MIN_VALUE;
    }

    if (redraw_min > number) {
      redraw_min = number;
    }

    if (redraw_max <= number) {
      redraw_max = (char) (number + 1);
    }

    redraw_min_start = redraw_min;

    ciga[number].resetQuality(0);
    repaint();
  }

  // *** TOTAL OVERKILL CURRENTLY ***
  // OPTIMISE
  private void removeCharacterFromCache(DisplayOptions gdo, char c) {
    font_renderer.getFEFontCache().remove(font, gdo, c);
  }

  public void removeFontFromCache() {
    font_renderer.getFEFontCache().remove(font);
  }

  public void updateCharacter(char c) {
    removeCharacterFromCache(gdo, c);
    repaintCharacter(c);
  }

  public void fix() {
    for (int i = char_min; i < char_max; i++) {
      FEGlyph glyph = font.getGlyphArray().getGlyph(i);

      Fixer fixer = new Fixer(glyph);
      fixer.fix(default_frame_display_options.getGDOE());
    }
  }

  public FEFont getFEFont() {
    return font;
  }

  public void makeDefault(DisplayOptionsEditor gdoe, int width, int height, boolean preserve_aspect_ratio) {
    default_frame_display_options.setGDOE(gdoe);
    default_frame_display_options.setWidth(width);
    default_frame_display_options.setHeight(height);
    default_frame_display_options.setPreserveAspectRatio(preserve_aspect_ratio);
  }

  void setAaScaleFactorX(int aa_scale_factor_x) {
    this.aa_scale_factor_x = aa_scale_factor_x;
  }

  int getAaScaleFactorX() {
    return aa_scale_factor_x;
  }

  int getAaScaleFactorY() {
    return aa_scale_factor_y;
  }

  int getLastWidth() {
    return last_width;
  }

  int getLastHeight() {
    return last_height;
  }

  int getColumns() {
    return columns;
  }

  int getRows() {
    return rows;
  }

  Point getLastPointer() {
    return last_pointer;
  }

  void setMousePressed(boolean mouse_pressed) {
    this.mouse_pressed = mouse_pressed;
  }

  boolean isMousePressed() {
    return mouse_pressed;
  }

  void setCurrentPointer(Point current_pointer) {
    this.current_pointer = current_pointer;
  }

  Point getCurrentPointer() {
    return current_pointer;
  }

  public void chooseFont(String s) {
    fontLoad(s, this.getGraphics());
    clearCacheAndUpdateAll();
  }

  public void setWeight(int weight) {
    gdo.getPen().setWidth(weight);
    clearCacheAndUpdateAll();
  }

  //  public void setOutline(boolean outline) {
  //    gdo.setOutline(outline);
  //    clearCacheAndUpdateAll();
  //  }

  public void setFilled(boolean filled) {
    gdo.setFill(filled);
    clearCacheAndUpdateAll();
  }

  public void setPen(Pen pen) {
    gdo.setPen(pen);
    clearCacheAndUpdateAll();
  }

  void clearCacheAndUpdateAll() {
    clearCache();
    updateAll();
  }

  public void setPQC(PanelQualityChoice pqc) {
    this.pqc = pqc;
  }

  public PanelQualityChoice getPQC() {
    return pqc;
  }

  void setGDO(DisplayOptions gdo) {
    this.gdo = gdo;
  }

  public DisplayOptions getGDO() {
    return gdo;
  }

  class BMouseListener extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
      if (!isMousePressed()) {
        setMousePressed(true);
        getLastPointer().x = getAaScaleFactorX() * e.getX();
        getLastPointer().y = getAaScaleFactorY() * e.getY();
        CharacterGrid.this.repaint();
      }
    }

    public void mouseReleased(MouseEvent e) {
      if (isMousePressed()) {
        setMousePressed(false);
        getLastPointer().x = getAaScaleFactorX() * e.getX();
        getLastPointer().y = getAaScaleFactorY() * e.getY();
        setMousePressed(true);
        int x = e.getX() * getColumns() / getLastWidth();
        int y = e.getY() * getRows() / getLastHeight();

        newFrame("" + (char) (x + (y + 2) * 16));
        CharacterGrid.this.repaint();
      }
    }
  }

  class BMouseMotionListener extends MouseMotionAdapter {
    public void mouseDragged(MouseEvent e) {
      getCurrentPointer().x = getAaScaleFactorX() * e.getX();
      getCurrentPointer().y = getAaScaleFactorY() * e.getY();
      getLastPointer().x = getCurrentPointer().x;
      getLastPointer().y = getCurrentPointer().y;
      CharacterGrid.this.repaint();
    }
  }

  class BKeyListener extends KeyAdapter {
    public void keyTyped(KeyEvent e) {
      Forget.about(e);
    }
  }

}
