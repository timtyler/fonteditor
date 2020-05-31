package org.fonteditor.font;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.fonteditor.editor.frame.Selection;
import org.fonteditor.elements.paths.ExecutorOnFEPath;
import org.fonteditor.elements.paths.FEPath;
import org.fonteditor.elements.paths.FEPathList;
import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.graphics.WideLine;
import org.fonteditor.hinter.Expander;
import org.fonteditor.hinter.Hinter;
import org.fonteditor.hinter.Slanter;
import org.fonteditor.instructions.InstructionStream;
import org.fonteditor.options.coords.Coords;
import org.fonteditor.options.display.GlyphDisplayOptions;
import org.fonteditor.sliders.SliderBase;
import org.fonteditor.sliders.Sliders;
import org.fonteditor.springs.SpringConstants;
import org.fonteditor.springs.SpringMaker;
import org.fonteditor.springs.SpringManager;
import org.fonteditor.utilities.general.Utils;

/**
 * Represents a single glyph...
 */

public class FEGlyph implements SpringConstants {
  private int number; // number of this glyph...?
  private InstructionStream instruction_stream;
  private boolean drag_box;
  private boolean confine_drag_horizontal = false;
  private boolean confine_drag_vertical = false;
  private Point drag_start = new Point();
  private Point drag_end = new Point();
  private Sliders sliders;
  private SpringManager spring_manager;
  private GlyphDisplayOptions last_gdo;

  private static final int BORDER_X_MIN = 0x0000;
  private static final int BORDER_X_MAX = 0x8000;
  private static final int BORDER_Y_MIN = 0x0000;
  private static final int BORDER_Y_MAX = 0xFFFF;
  private static final int GRID_BORDER_X_MIN = 0x0000;
  private static final int GRID_BORDER_X_MAX = 0x7FFF;
  private static final int GRID_BORDER_Y_MIN = 0x0000;
  private static final int GRID_BORDER_Y_MAX = 0xFFFF;

  private static final boolean USE_SPRINGS = false;

  public FEGlyph(int n) {
    number = n;
    instruction_stream = new InstructionStream();
  }

  public void draw(Graphics g, GlyphDisplayOptions gdo, Selection selection) {
    g.setColor(Color.white);
    g.fillRect(0, 0, 9999, 9999);

    if (gdo.isShowGrid()) {
      drawGrid(g, gdo);
    }

    g.setColor(Color.black);

    if (gdo.isBorder()) {
      WideLine.drawRectangle(g, new Point(BORDER_X_MIN, BORDER_Y_MIN), new Point(BORDER_X_MAX, BORDER_Y_MAX), 0xE0, gdo.getCoords());
    }

    //resetRemakeFlag(); // performance hit...
    //    if (instruction_stream.isInNeedOfRemaking()) {
      last_gdo = null; // ...but why...
//    }

    makeGlyphIfNeeded(gdo); // ...but why...
    //getFEPointList(gdo); // remakeIfNecessary... doesn't work...

    if (gdo.isShowSliders()) {
      sliders.drawHorizontalSliders(g, gdo.getCoords());
      sliders.drawVerticalSliders(g, gdo.getCoords());
    }

    g.setColor(Color.black);
    FEPathList fepl = getFEPathList(gdo);

    for (int i = 0; i < fepl.getNumber(); i++) {
      fepl.getPath(i).draw(g, gdo, instruction_stream.getFEPointList(), selection, i);
    }

    g.setColor(Color.red);
    if (getDragBox()) {
      WideLine.drawRectangle(g, drag_start, drag_end, 0x180, gdo.getCoords());
    }

    if (USE_SPRINGS) {
      if (gdo.isShowSprings()) {
        spring_manager.draw(g, gdo);
      }
    }
  }

  private void drawGrid(Graphics g, GlyphDisplayOptions gdo) {
    int grid_line_width = 0x40;
    int grid_line_height = 0x40;
    int pix_siz_x = (GRID_BORDER_X_MAX - GRID_BORDER_X_MIN) / gdo.getCoords().getAAWidth();
    int pix_siz_y = (GRID_BORDER_Y_MAX - GRID_BORDER_Y_MIN) / gdo.getCoords().getAAHeight();

    g.setColor(Color.lightGray);
    for (int x = GRID_BORDER_X_MIN; x <= GRID_BORDER_X_MAX; x += pix_siz_x) {
      WideLine.renderRound(g, new Point(x, GRID_BORDER_Y_MIN), new Point(x, GRID_BORDER_Y_MAX), grid_line_height, gdo.getCoords());
    }
    for (int y = GRID_BORDER_Y_MIN; y <= GRID_BORDER_Y_MAX; y += pix_siz_y) {
      WideLine.renderRound(g, new Point(GRID_BORDER_X_MIN, y), new Point(GRID_BORDER_X_MAX, y), grid_line_width, gdo.getCoords());
    }
  }

  public void invalidateGraphics(GlyphDisplayOptions gdo) {
    FEPathList fepathlist = getFEPathList(gdo);

    for (int i = 0; i < fepathlist.getNumber(); i++) {
      fepathlist.getPath(i).invalidateGraphics();
    }
  }

  private boolean isAlreadySelected(Selection selection, Point p, GlyphDisplayOptions gdo) {
    FEPointList fepointlist = getFEPointList(gdo);
    FEPathList fepathlist = getFEPathList(gdo);

    return ((gdo.isShowGrid()) && fepointlist.isAlreadySelected(selection, p, gdo.getCoords())) | fepathlist.isAlreadySelected(selection, p, gdo.getCoords());
  }

  public void select(Point p, GlyphDisplayOptions gdo, Selection selection) {
    FEPointList fepointlist = getFEPointList(gdo);
    FEPathList fepathlist = getFEPathList(gdo);

    setDragBox(false);
    confine_drag_horizontal = false;
    confine_drag_vertical = false;
    // if not clicked on existing selection clear selection...
    if (!isAlreadySelected(selection, p, gdo)) {
      //Log.log("deselectiengAll"); // 

      selection.deselectAllPoints(); // no ctrl key...
      selection.deselectAllPaths(); // no ctrl key...

      // Select points...
      Coords coords = gdo.getCoords();

      if (gdo.isShowPoints()) {
        fepointlist.select(selection, p, coords);
      }

      // If no points selected select paths...
      if (selection.getNumberofPointsSelected() == 0) {
        //Log.log("Select paths...");
        fepathlist.select(selection, p, coords);
        // If no paths selected...
        if (fepathlist.numberSelected(selection) == 0) {
          //Log.log("Select sliders");
          // Deselect all sliders...
          getSliders().getSliderManagerHorizontal().deselect();
          getSliders().getSliderManagerVertical().deselect();
          // Horizontal sliders...
          if (gdo.isShowSliders()) {
            getSliders().getSliderManagerHorizontal().select(coords.rescaleY(p.y)); //new Point(gdo.getCoords().rescaleX(p.x),
          }

          if (getSliders().getSliderManagerHorizontal().getNumberSelected() != 0) {
            selectPointsUsingHorizontalSlider(selection, gdo);
          } else {
            // Vertical sliders...
            if (gdo.isShowSliders()) {
              getSliders().getSliderManagerVertical().select(coords.rescaleX(p.x)); //, gdo.getCoords().rescaleY(p.y)
            }

            if (getSliders().getSliderManagerVertical().getNumberSelected() != 0) {
              selectPointsUsingVerticalSlider(selection, gdo);
            } else {
              // Drag box...
              setDragBox(true);
              drag_start.x = coords.rescaleX(p.x);
              drag_start.y = coords.rescaleY(p.y);
              drag_end.x = drag_start.x;
              drag_end.y = drag_start.y;
            }
          }
        }
      }
    }
    //Log.log("H:" + slider_manager_horizontal.getNumberSelected());
    //Log.log("confine_drag_horizontal:" + confine_drag_horizontal);
  }

  private void selectPointsUsingHorizontalSlider(Selection selection, GlyphDisplayOptions gdo) {
    FEPointList fepointlist = getFEPointList(gdo);

    for (int i = 0; i < sliders.getSliderManagerHorizontal().getNumber(); i++) {
      SliderBase slider = getSliders().getSliderManagerHorizontal().getSlider(i);

      if (slider.areAnySelected()) {
        fepointlist.selectInBox(selection, 0, slider.getPosition() - 1, 0xFFFF, slider.getPosition() + 1);
      }
    }
    confine_drag_vertical = true;
  }

  private void selectPointsUsingVerticalSlider(Selection selection, GlyphDisplayOptions gdo) {
    FEPointList fepointlist = getFEPointList(gdo);

    for (int i = 0; i < sliders.getSliderManagerVertical().getNumber(); i++) {
      SliderBase slider = getSliders().getSliderManagerVertical().getSlider(i);

      if (slider.areAnySelected()) {
        fepointlist.selectInBox(selection, slider.getPosition() - 1, 0, slider.getPosition() + 1, 0xFFFF);
      }
    }
    confine_drag_horizontal = true;
  }

  public void drag(Point p1, Point p2, GlyphDisplayOptions gdo, Selection selection) {
    FEPointList fepointlist = getFEPointList(gdo);
    FEPathList fepathlist = getFEPathList(gdo);

    // Points...
    if (confine_drag_vertical) {
      p2.x = p1.x;
      //Log.log("CONFINE_V");
    }
    if (confine_drag_horizontal) {
      p2.y = p1.y;
      //Log.log("CONFINE_H");
    }
    // recode!
    for (int i = 0; i < fepointlist.getNumber(); i++) {
      FEPoint pt = fepointlist.getPoint(i);

      if (selection.isPointSelected(i)) {
        instruction_stream.dragPoint(p1, p2, pt.getInstructionPointer(), gdo);
      }
    }

    // Paths...
    // recode!
    for (int i = 0; i < fepathlist.getNumber(); i++) {
      if (selection.isPathSelected(i)) { //fepathlist.getPath(i).areAnySelected(selection)) {
        instruction_stream.dragStroke(p1, p2, fepathlist.getPath(i).getInstructionPointer(), gdo);
      }
    }

    if (getDragBox()) {
      drag_end.x = gdo.getCoords().rescaleX(p2.x);
      drag_end.y = gdo.getCoords().rescaleY(p2.y);
    }
    
    resetRemakeFlag();
  }

  public void resetRemakeFlag() {
    instruction_stream.setRemakeFlag(true);
  }

  public void resetLastGDO() {
    last_gdo = null;
  }

  //  public void remakeIfNecessary(GlyphDisplayOptions gdo) {
  //    if (instruction_stream.isInNeedOfRemaking()) {
  //      last_gdo = null;
  //    }
  //
  //    makeGlyphIfNeeded(gdo);
  //  }

  public void release(Point p, GlyphDisplayOptions gdo, Selection selection) {
    p = p;
    if (getDragBox()) {
      FEPointList fepointlist = getFEPointList(gdo);
      FEPathList fepathlist = getFEPathList(gdo);
      int min_x = Utils.min(drag_start.x, drag_end.x);
      int min_y = Utils.min(drag_start.y, drag_end.y);
      int max_x = Utils.max(drag_start.x, drag_end.x);
      int max_y = Utils.max(drag_start.y, drag_end.y);

      //      Log.log("drag:min_x:" + min_x + " min_y:" + min_y + " max_x:" + max_x + " max_y:" + max_y);

      setDragBox(false);
      fepointlist.selectInBox(selection, min_x, min_y, max_x, max_y);
      fepathlist.selectInBox(selection, min_x, min_y, max_x, max_y, gdo.getCoords());
      // count points on selected paths..

      //      selectionfepointlist.countSelectedPoints(selection);

      //Log.log("fepointlist:N:" + fepointlist.getNumberOfPointsSelected());
    }
  }

  public void makeGlyphFromInstructionStream(GlyphDisplayOptions gdo) {
    instruction_stream.resetPointAndPathLists();
    //    Log.log("makeGlyphFromInstructionStream");
    sliders = null;
    instruction_stream.executeToMakeGlyph(false);
    expand(gdo);
    slant(gdo);
    hint(gdo);
    instruction_stream.setRemakeFlag(false);
    getSliders();
  }

  private void hint(GlyphDisplayOptions gdo) {
    SpringMaker sm = new SpringMaker(getFEPathList(gdo), getFEPointList(gdo));
    spring_manager = sm.makeSprings(); // needed by the hinter...
    Hinter.hint(this, gdo);
  }
  
  private void slant(GlyphDisplayOptions gdo) {
    Slanter.slant(this, gdo);
  }

  private void expand(GlyphDisplayOptions gdo) {
    Expander.expand(this, gdo);
  }

  public void translate(int dx, int dy, GlyphDisplayOptions gdo) {
    FEPathList fepathlist = getFEPathList(gdo);

    //Log.log("instruction_stream:" + instruction_stream);
    //Log.log("instruction_stream.fepathlist:" + instruction_stream.fepathlist);
    fepathlist.executeOnEachPath(new ExecutorOnFEPath() {
      public void execute(FEPath p, Object o) {
        Point r = (Point) o;
        FEPointList fepl = p.getFEPointList();

        fepl.translate(r.x, r.y);
      }
    }, new Point(dx, dy));
    //instruction_stream.fepointlist.translate(dx, dy);
    //getSliders().translate(dx, dy);
  }

  public void rescaleWithFixedBottom(int fixed, int o, int n, GlyphDisplayOptions gdo) {
    getFEPointList(gdo).rescaleWithFixedBottom(fixed, o, n);
  }

  public void rescaleWithFixedTop(int fixed, int o, int n, GlyphDisplayOptions gdo) {
    getFEPointList(gdo).rescaleWithFixedTop(fixed, o, n);
  }

  public void rescaleWithFixedLeft(int fixed, int o, int n, GlyphDisplayOptions gdo) {
    getFEPointList(gdo).rescaleWithFixedLeft(fixed, o, n);
  }

  public void rescaleWithFixedRight(int fixed, int o, int n, GlyphDisplayOptions gdo) {
    getFEPointList(gdo).rescaleWithFixedRight(fixed, o, n);
  }

  public Sliders getSliders() {
    return (sliders == null) ? getNewSliders() : sliders;
  }

  public Sliders getNewSliders() {
    sliders = new Sliders(this);

    return sliders;
  }

  public int getMinX(GlyphDisplayOptions gdo) {
    FEPointList fepointlist = getFEPointList(gdo);

    return fepointlist.getMinX();
  }

  public int getMinY(GlyphDisplayOptions gdo) {
    FEPointList fepointlist = getFEPointList(gdo);

    return fepointlist.getMinY();
  }

  public int getMaxX(GlyphDisplayOptions gdo) {
    FEPointList fepointlist = getFEPointList(gdo);

    return fepointlist.getMaxX();
  }

  public int getMaxY(GlyphDisplayOptions gdo) {
    FEPointList fepointlist = getFEPointList(gdo);

    return fepointlist.getMaxY();
  }

  void setInstructionStream(InstructionStream instruction_stream) {
    this.instruction_stream = instruction_stream;
  }

  public FEPathList getFEPathList(GlyphDisplayOptions gdo) {
    makeGlyphIfNeeded(gdo);

    return instruction_stream.getFEPathList();
  }

  public FEPointList getFEPointList(GlyphDisplayOptions gdo) {
    makeGlyphIfNeeded(gdo);

    return instruction_stream.getFEPointList();
  }

  public void makeGlyphIfNeeded(GlyphDisplayOptions gdo) {
    if (!gdo.equals(last_gdo)) {
      try {
      last_gdo = (GlyphDisplayOptions) gdo.clone();
      } catch (CloneNotSupportedException e) { e = e; }
      makeGlyphFromInstructionStream(gdo);
    }
  }

  public boolean hasX(int x, GlyphDisplayOptions gdo) {
    FEPointList point_list = getFEPointList(gdo);

    return point_list.hasX(x);
  }

  public boolean hasY(int y, GlyphDisplayOptions gdo) {
    FEPointList point_list = getFEPointList(gdo);

    return point_list.hasY(y);
  }

  public InstructionStream getInstructionStream() {
    return instruction_stream;
  }

  public int getNumber() {
    return number;
  }

  private void setDragBox(boolean drag_box) {
    this.drag_box = drag_box;
  }

  public boolean getDragBox() {
    return drag_box;
  }
}

//  public void setRemakeFlag(boolean f) {
//    instruction_stream.setRemakeFlag(f);
//    last_gdo = null;
//  }

//  /**
//   * Method rescaleWithFixedLeft.
//   * @param i
//   * @param max_x
//   * @param f
//   * @param gdo
//   */
//  //  public void rescaleWithFixedLeft(int i, int o, int n, GlyphDisplayOptions gdo) {
//  //  FEPointList point_list = getFEPointList(gdo);
//
//  // point_list.rescaleWithFixedLeft(i, o, n);
//  //}
//
//  /**
//   * Method rescaleWithFixedTop.
//   * @param i
//   * @param max_y
//   * @param f
//   * @param gdo
//   */
//  //  public void rescaleWithFixedTop(int i, int o, int n, GlyphDisplayOptions gdo) {
//  //   FEPointList point_list = getFEPointList(gdo);
//
//  //  point_list.rescaleWithFixedTop(i, o, n);
//  //}

//  /**
//   * Method getMinX.
//   * @return int
//   */
//  public int getMinX(GlyphDisplayOptions gdo) {
//    FEPointList point_list = getFEPointList(gdo);
//    return point_list.getMinX();
//  }
//
//  /**
//   * Method getMinY.
//   * @return int
//   */
//  public int getMinY(GlyphDisplayOptions gdo) {
//    FEPointList point_list = getFEPointList(gdo);
//    return point_list.getMinY();
//  }
//
//  /**
//   * Method getMaxX.
//   * @return int
//   */
//  public int getMaxX(GlyphDisplayOptions gdo) {
//    FEPointList point_list = getFEPointList(gdo);
//    return point_list.getMaxX();
//  }
//
//  /**
//   * Method getMaxY.
//   * @return int
//   */
//  public int getMaxY(GlyphDisplayOptions gdo) {
//    FEPointList point_list = getFEPointList(gdo);
//    return point_list.getMaxY();
//  }
//
//  /**
//   * Method rescaleWithFixedRight.
//   * @param i
//   * @param max_x
//   * @param f
//   * @param gdo
//   */
//  public void rescaleWithFixedRight(int i, int o, int n, GlyphDisplayOptions gdo) {
//    FEPointList point_list = getFEPointList(gdo);
//    point_list.rescaleWithFixedRight(i, o, n);
//  }

//  void rescaleRangeX(int min, int centre, int max, int new_centre) {
//    instruction_stream.fepathlist.executeOnEachPath(new ExecutorOnFEPath() {
//      void execute(FEPath p, Object o) {
//        Rectangle r = (Rectangle) o;
//        FEPointList fepl = p.getFEPointList();
//        fepl.rescaleRangeX(r.x, r.y, r.width, r.height);
//      }
//    }, new Rectangle(min, centre, max, new_centre));
//    //instruction_stream.fepointlist.rescaleRangeX(min, centre, max, new_centre);
//    sliders.rescaleRangeX(min, centre, max, new_centre);
//  }
//
//  void rescaleRangeY(int min, int centre, int max, int new_centre) {
//    instruction_stream.fepathlist.executeOnEachPath(new ExecutorOnFEPath() {
//      void execute(FEPath p, Object o) {
//        Rectangle r = (Rectangle) o;
//        FEPointList fepl = p.getFEPointList();
//        fepl.rescaleRangeY(r.x, r.y, r.width, r.height);
//      }
//    }, new Rectangle(min, centre, max, new_centre));
//    //instruction_stream.fepointlist.rescaleRangeY(min, centre, max, new_centre);
//    sliders.rescaleRangeY(min, centre, max, new_centre);
//  }
//

/*
public void rescaleWithFixedLeft(SliderManagerBase s_m, int fixed, int o, int n, GlyphDisplayOptions gdo) {
  FEPathList fepathlist = getFEPathList(gdo);

  fepathlist.executeOnEachPath(new ExecutorOnFEPath() {
    public void execute(FEPath p, Object o) {
      Rectangle r = (Rectangle) o;
      FEPointList fepl = p.getFEPointList();

      fepl.rescaleWithFixedLeft(r.x, r.y, r.width);
    }
  }, new Rectangle(fixed, o, n, 0));
  //instruction_stream.fepointlist.rescaleWithFixedLeft(fixed, o, n);
  s_m.rescaleWithFixedLeftOrTop(fixed, o, n);
}

public void rescaleWithFixedRight(SliderManagerBase s_m, int fixed, int o, int n, GlyphDisplayOptions gdo) {
  FEPathList fepathlist = getFEPathList(gdo);

  fepathlist.executeOnEachPath(new ExecutorOnFEPath() {
    public void execute(FEPath p, Object o) {
      Rectangle r = (Rectangle) o;
      FEPointList fepl = p.getFEPointList();

      fepl.rescaleWithFixedRight(r.x, r.y, r.width);
    }
  }, new Rectangle(fixed, o, n, 0));
  //instruction_stream.fepointlist.rescaleWithFixedRight(fixed, o, n);
  s_m.rescaleWithFixedRightOrBottom(fixed, o, n);
}

public void rescaleWithFixedTop(SliderManagerBase s_m, int fixed, int o, int n, GlyphDisplayOptions gdo) {
  FEPathList fepathlist = getFEPathList(gdo);

  fepathlist.executeOnEachPath(new ExecutorOnFEPath() {
    public void execute(FEPath p, Object o) {
      Rectangle r = (Rectangle) o;
      FEPointList fepl = p.getFEPointList();

      fepl.rescaleWithFixedTop(r.x, r.y, r.width);
    }
  }, new Rectangle(fixed, o, n, 0));
  //instruction_stream.fepointlist.rescaleWithFixedTop(fixed, o, n);
  s_m.rescaleWithFixedLeftOrTop(fixed, o, n);
}

public void rescaleWithFixedBottom(SliderManagerBase s_m, int fixed, int o, int n, GlyphDisplayOptions gdo) {
  FEPathList fepathlist = getFEPathList(gdo);

  fepathlist.executeOnEachPath(new ExecutorOnFEPath() {
    public void execute(FEPath p, Object o) {
      Rectangle r = (Rectangle) o;
      FEPointList fepl = p.getFEPointList();

      fepl.rescaleWithFixedBottom(r.x, r.y, r.width);
    }
  }, new Rectangle(fixed, o, n, 0));
  //instruction_stream.fepointlist.rescaleWithFixedTop(fixed, o, n);
  s_m.rescaleWithFixedRightOrBottom(fixed, o, n);
}
*/

/*
private void reselect(FEPathList fepathlist, FEPathList old_fepathlist, FEPointList fepointlist, FEPointList old_fepointlist) {
  //Log.log(fepathlist.getNumber() + " - " + old_fepathlist.getNumber());
  for (int i = 0; i < Utils.min(fepathlist.getNumber(), old_fepathlist.getNumber()); i++) {
    if (old_fepathlist.getPath(i).areAnySelected()) {
      fepathlist.select(i);
    }
  }
  
  for (int i = 0; i < Utils.min(fepointlist.getNumber(), old_fepointlist.getNumber()); i++) {
    if (old_fepointlist.getPoint(i).areAnySelected()) {
      fepointlist.select(i);
    }
  }
}
*/

//  private void deselectAll(GlyphDisplayOptions gdo) {
//    FEPointList fepointlist = getFEPointList(gdo);
//    FEPathList fepathlist = getFEPathList(gdo);
//
//    fepointlist.deselect();
//    fepathlist.deselect();
//  }
