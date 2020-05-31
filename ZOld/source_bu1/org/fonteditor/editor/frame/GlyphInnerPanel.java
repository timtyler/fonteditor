package org.fonteditor.editor.frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import org.fonteditor.editor.grid.CharacterGrid;
import org.fonteditor.font.FEGlyph;
import org.fonteditor.graphics.FontImageProcessor;
import org.fonteditor.graphics.ImageWrapper;
import org.fonteditor.options.display.GlyphDisplayOptionsEditor;
import org.fonteditor.options.coords.CoordsEditor;
import org.fonteditor.utilities.log.Log;

/**
  * Inner Panel - containing a single editable Glyph...
  */

public class GlyphInnerPanel extends Panel {
  private static final boolean DOUBLE_BUFFERING = true;

  private static boolean preserve_aspect_ratio = true;

  private CharacterGrid character_grid;
  private Selection selection;

  private int offset_x = 0;
  private int offset_y = 0;

  private boolean repaint_with_anti_aliasing = true;

  private Point last_pointer = new Point();
  private Point current_pointer = new Point();
  private boolean mouse_pressed;

  private ImageWrapper big_image = new ImageWrapper();

  private Graphics offscreen_graphics;

  private boolean draw_it;

  private String edit_character;
  private char number;
  private GlyphDisplayOptionsEditor gdoe;
  private FEGlyph glyph;

  GlyphInnerPanel(String s, CharacterGrid character_grid, DefaultFrameDisplayOptions dfdo) {
    super();
    this.preserve_aspect_ratio = dfdo.preservesAspectRatio();
    this.character_grid = character_grid;
    this.gdoe = dfdo.getGDOE();
    edit_character = s;
    number = s.charAt(0);
    glyph = character_grid.getFEFont().getGlyphArray().getGlyph(number);
    glyph.resetRemakeFlag();
    selection = new Selection();
  }

  void init() {
    this.addMouseListener(new BMouseListener());
    this.addMouseMotionListener(new BMouseMotionListener());
    this.addKeyListener(new BKeyListener());

    setBackground(Color.white);

    validate();
    repaint();
  }

  public void paint(Graphics g) {
    update(g);
  }

  public void update(Graphics g) {
    //Log.log("update called");

    Dimension d = getSize();

    int new_x = d.width;
    int new_y = d.height;

    if (preserve_aspect_ratio) {
      if ((new_x << 1) >= new_y) {
        new_x = new_y >> 1;
      }

      if (new_y >= (new_x << 1)) {
        new_y = new_x << 1;
      }
    }

    if (repaint_with_anti_aliasing) {
       gdoe.antiAliasing(true);
    }

    if ((new_x != gdoe.getCoordsEditor().getWidth()) || (new_y != gdoe.getCoordsEditor().getHeight())) {
      if (preserve_aspect_ratio) {
        offset_x = (d.width - new_x) >> 1;
        offset_y = (d.height - new_y) >> 1;

        g.setColor(Color.white);
        g.fillRect(0, 0, 99999, 99999); // overkill...
      } else {
        offset_x = 0;
        offset_y = 0;
      }

      gdoe.getCoordsEditor().setWidth(new_x);
      gdoe.getCoordsEditor().setHeight(new_y);

      glyph.invalidateGraphics(gdoe);

      draw_it = ((gdoe.getCoordsEditor().getXScale() > 0) && (gdoe.getCoordsEditor().getYScale() > 0));
      if (draw_it) {
        if (DOUBLE_BUFFERING || ((((CoordsEditor) gdoe.getCoordsEditor()).getLogAAScaleFactorX() | ((CoordsEditor) gdoe.getCoordsEditor()).getLogAAScaleFactorY()) != 0)) {

          Image image = createImage(gdoe.getCoordsEditor().getXScale(), gdoe.getCoordsEditor().getYScale());

          big_image.setImage(image);
        }
      }
    }

    if (draw_it) {
      if ((((CoordsEditor) gdoe.getCoordsEditor()).getLogAAScaleFactorX() | ((CoordsEditor) gdoe.getCoordsEditor()).getLogAAScaleFactorY()) == 0) {
        if (DOUBLE_BUFFERING) {
          offscreen_graphics = big_image.getGraphics();

          glyph.draw(offscreen_graphics, gdoe, selection);
          g.drawImage(big_image.getImage(), offset_x, offset_y, null);
          //Log.log("g.drawImage(big_image");
        } else {
          glyph.draw(g, gdoe, selection);
          //Log.log("feg.draw(g, gdo);");
        }
      } else {
        offscreen_graphics = big_image.getGraphics();

        glyph.draw(offscreen_graphics, gdoe, selection);
        big_image.freshImage();

        ImageWrapper ti = FontImageProcessor.scaleDown(big_image, ((CoordsEditor) gdoe.getCoordsEditor()).getLogAAScaleFactorX(), ((CoordsEditor) gdoe.getCoordsEditor()).getLogAAScaleFactorY());
        g.drawImage(ti.getImage(), offset_x, offset_y, null);
        //Log.log("drawImage(ti.i...");

        // this seems unnecessary...?
      }

      //if (repaint_with_anti_aliasing) {
        gdoe.antiAliasing(false);
        repaint_with_anti_aliasing = false;
      //}
    } else {
      refresh();
    }
  }
  

  void refresh() {
    gdoe.refresh();
    repaint();
  }

  public FEGlyph getFEG() {
    return glyph;
  }

  Point getLastPointer() {
    return last_pointer;
  }

  void setMousePressed(boolean mouse_pressed) {
    this.mouse_pressed = mouse_pressed;
  }

  static void setPreserveAspectRatio(boolean preserve_aspect_ratio) {
    GlyphInnerPanel.preserve_aspect_ratio = preserve_aspect_ratio;
  }

  static boolean preservesAspectRatio() {
    return preserve_aspect_ratio;
  }

  CharacterGrid getCharacterGrid() {
    return character_grid;
  }

  int getOffsetX() {
    return offset_x;
  }

  int getOffsetY() {
    return offset_y;
  }

  void setGDOE(GlyphDisplayOptionsEditor gdo) {
    this.gdoe = gdo;
  }

  GlyphDisplayOptionsEditor getGDOE() {
    return gdoe;
  }

  char getNumber() {
    return number;
  }

  public Selection getSelection() {
    return selection;
  }

   class BMouseListener extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
      if (!mouse_pressed) {
        last_pointer.x = ((CoordsEditor) getGDOE().getCoordsEditor()).getAAScaleFactorX() * (e.getX() - getOffsetX());
        last_pointer.y = ((CoordsEditor) getGDOE().getCoordsEditor()).getAAScaleFactorY() * (e.getY() - getOffsetY());

        glyph.select(last_pointer, getGDOE(), selection);
        setMousePressed(true);
        GlyphInnerPanel.this.repaint();
        //Log.log("Press");
      }
    }

    public void mouseReleased(MouseEvent e) {
      e = e;

      setMousePressed(false);

      if (!glyph.getDragBox()) {
        getCharacterGrid().updateCharacter(getNumber());
      }

      glyph.release(getLastPointer(), getGDOE(), selection);

      //Log.log("getNumber:" + getNumber()); /// no work = 256?

      repaintWithAntiAliasing();
    }
  }

  void repaintWithAntiAliasing() {
    repaint_with_anti_aliasing = true;
    GlyphInnerPanel.this.repaint();
    //Log.log("repaint_with_anti_aliasing");
  }

  class FEActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      e = e;
    }
  }

  class BMouseMotionListener extends MouseMotionAdapter {
    public void mouseDragged(MouseEvent e) {
      current_pointer.x = ((CoordsEditor) getGDOE().getCoordsEditor()).getAAScaleFactorX() * (e.getX() - getOffsetX());
      current_pointer.y = ((CoordsEditor) getGDOE().getCoordsEditor()).getAAScaleFactorY() * (e.getY() - getOffsetY());

      //FEGlyph glyph = getCharacterGrid().getFEFont().getGlyphArray().getGlyph(getNumber());
      glyph.drag(last_pointer, current_pointer, getGDOE(), selection);

      last_pointer.x = current_pointer.x;
      last_pointer.y = current_pointer.y;

      GlyphInnerPanel.this.repaint();
    }
  }

   class BKeyListener extends KeyAdapter {
    // The user pressed a key corresponding to an ASCII value.
    public void keyTyped(KeyEvent e) {
      char k = e.getKeyChar();
      if (k == 'd') {
        Log.log("Delete point");
        GlyphInnerPanel.this.repaint();
      } else if (k == 's') {
        Log.log("Make sraight line");
        GlyphInnerPanel.this.repaint();
      } else if (k == 'b') {
        Log.log("Make bezier");
        GlyphInnerPanel.this.repaint();
      } else if (k == 'a') {
        Log.log("Add point");
        GlyphInnerPanel.this.repaint();
      }
    }
  }
}
