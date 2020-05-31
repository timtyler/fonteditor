package org.fonteditor.editor.frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import org.fonteditor.editor.grid.CharacterGrid;
import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.font.FEGlyph;
import org.fonteditor.graphics.FontImageProcessor;
import org.fonteditor.graphics.ImageWrapper;
import org.fonteditor.options.coords.CoordsEditor;
import org.fonteditor.options.display.DisplayOptionsEditor;
import org.fonteditor.utilities.general.Forget;
import org.fonteditor.utilities.log.Log;

/**
  * Inner Panel - containing a single editable Glyph...
  */

public class GlyphInnerPanel extends Panel {
  private static final boolean DOUBLE_BUFFERING = true;

  private boolean preserve_aspect_ratio = true;

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
  private DisplayOptionsEditor gdoe;
  private GlyphOuterPanel glyph_outer_panel;
  private FEGlyph glyph;

  GlyphInnerPanel(String s, CharacterGrid character_grid, DefaultFrameDisplayOptions dfdo, GlyphOuterPanel glyph_outer_panel) {
    super();
    this.preserve_aspect_ratio = dfdo.preservesAspectRatio();
    this.character_grid = character_grid;
    this.gdoe = dfdo.getGDOE();
    this.glyph_outer_panel = glyph_outer_panel;
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

  void setPreserveAspectRatio(boolean preserve_aspect_ratio) {
    this.preserve_aspect_ratio = preserve_aspect_ratio;
  }

  boolean preservesAspectRatio() {
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

  void setGDOE(DisplayOptionsEditor gdo) {
    this.gdoe = gdo;
  }

  DisplayOptionsEditor getGDOE() {
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
        //GDO gdo = getGDOE()
        last_pointer.x = ((CoordsEditor) gdoe.getCoordsEditor()).getAAScaleFactorX() * (e.getX() - getOffsetX());
        last_pointer.y = ((CoordsEditor) gdoe.getCoordsEditor()).getAAScaleFactorY() * (e.getY() - getOffsetY());

        getGlyph().select(last_pointer, gdoe, selection);
        // if anything is selected - update point display...

        updateTextFieldsShowingSelectedCoordinates();

        setMousePressed(true);
        GlyphInnerPanel.this.repaint();
      }
    }

    public void mouseReleased(MouseEvent e) {
      Forget.about(e);

      setMousePressed(false);

      if (!getGlyph().getDragBox()) {
        getCharacterGrid().updateCharacter(getNumber());
      }

      getGlyph().release(getLastPointer(), gdoe, selection);

      //Log.log("getNumber:" + getNumber()); /// no work = 256?

      updateTextFieldsShowingSelectedCoordinates();

      // need to do ordinary repaint first...

      repaintWithAntiAliasing();
    }

    void updateTextFieldsShowingSelectedCoordinates() {
      TextField text_field_x = glyph_outer_panel.getTextfieldXRW();
      TextField text_field_y = glyph_outer_panel.getTextfieldYRW();

      if (selection.getNumberofPointsSelected() > 0) {

        int i = selection.getIndexOfFirstSelectedPoint();
        FEPointList point_list = getGlyph().getFEPointList(gdoe);
        FEPoint point = point_list.getPoint(i);
        int x = point.getX();
        int y = point.getY();

        //        CallBack callback = new CallBack() {
        //          public void callback(Object o) {
        //            FEPoint p = (FEPoint) o;
        //            x = p.getX();
        //            x = p.getY();
        //          }
        //        };
        //
        //        selection.onEachSelectedPoint(glyph.getFEPointList(gdoe), callback);

        text_field_x.setText("" + x);
        text_field_y.setText("" + y);
      } else {
        text_field_x.setText("-");
        text_field_y.setText("-");
      }
    }
  }

  void repaintWithAntiAliasing() {
    repaint_with_anti_aliasing = true;
    GlyphInnerPanel.this.repaint();
    //Log.log("repaint_with_anti_aliasing");
  }

  FEGlyph getGlyph() {
    return glyph;
  }

  class FEActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      Forget.about(e);
    }
  }

  class BMouseMotionListener extends MouseMotionAdapter {
    public void mouseDragged(MouseEvent e) {
      current_pointer.x = ((CoordsEditor) getGDOE().getCoordsEditor()).getAAScaleFactorX() * (e.getX() - getOffsetX());
      current_pointer.y = ((CoordsEditor) getGDOE().getCoordsEditor()).getAAScaleFactorY() * (e.getY() - getOffsetY());

      //FEGlyph glyph = getCharacterGrid().getFEFont().getGlyphArray().getGlyph(getNumber());
      getGlyph().drag(last_pointer, current_pointer, getGDOE(), selection);

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
