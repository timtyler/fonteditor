package org.fonteditor.sliders;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.fonteditor.elements.paths.ExecutorOnFEPath;
import org.fonteditor.elements.paths.FEPath;
import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.font.FEGlyph;

import org.fonteditor.graphics.WideLine;

import org.fonteditor.instructions.InstructionStream;

import org.fonteditor.options.coords.Coords;

import org.fonteditor.utilities.general.Utils;

public class Sliders implements SliderConstants {
  private SliderManagerBase slider_manager_vertical;
  private SliderManagerBase slider_manager_horizontal;

  public Sliders(FEPath p) {
    resetSliders();
    setUpSliders(p);
  }

  public Sliders(FEGlyph glyph) {
    resetSliders();
    setUpSliders(glyph.getInstructionStream());
  }

  void resetSliders() {
    slider_manager_vertical = new SliderManagerBase();
    slider_manager_horizontal = new SliderManagerBase();
  }

  void setUpSliders(InstructionStream instruction_stream) {
    // On each path...
    instruction_stream.getFEPathList().executeOnEachPath(new ExecutorOnFEPath() {
      public void execute(FEPath p, Object o) {
        o = o;
        setUpSlidersInternal(p);
      }
    }, null);
    
    sortAndDedupeSliders();
  }

  void setUpSliders(FEPath p) {
    setUpSlidersInternal(p);
    sortAndDedupeSliders();
  }

  void setUpSlidersInternal(FEPath p) {
    // On each path...
    // Go through all the points
    // boolean direction = p.getDirection();// set back to null at the bottom if you use this line...
    FEPointList fepl = p.getFEPointList();

    if (fepl.getNumber() > 0) {
      FEPoint current_point;
      FEPoint next_point;
      //      FEPoint[] array = fepl.getPoints();
      int increasing;

      increasing = (fepl.getPoint(fepl.getNumber() - 2).x < fepl.getPoint(fepl.getNumber() - 1).x) ? 1 : -1;
      current_point = fepl.getPoint(fepl.getNumber() - 1);
      for (int i = 0; i < fepl.getNumber(); i++) {
        int cpx = current_point.x;

        next_point = fepl.getPoint(i);
        if (Utils.equalsApprox(current_point.x, next_point.x, TOLERANCE)) {
          boolean dir = current_point.y > next_point.y;

          slider_manager_vertical.add(new SliderBase(VERTICAL, SliderBase.PARALLEL, dir, Utils.abs(next_point.y - current_point.y), current_point));
        } else {
          if (increasing > 0) {
            if (cpx > next_point.x) {
              slider_manager_vertical.add(new SliderBase(VERTICAL, SliderBase.INFLECTION, true, Utils.abs(next_point.y - current_point.y), current_point));
        }
        // else {
          //  }
          }

          if (increasing < 0) {
            if (cpx < next_point.x) {
              slider_manager_vertical.add(new SliderBase(VERTICAL, SliderBase.INFLECTION, true, Utils.abs(next_point.y - current_point.y), current_point));
        }
        // else {
          //  }
          }

          if (cpx > next_point.x) {
            increasing = -1;
          }

          if (cpx < next_point.x) {
            increasing = 1;
          }
        }
        current_point = next_point;
      }

      increasing = (fepl.getPoint(fepl.getNumber() - 2).y < fepl.getPoint(fepl.getNumber() - 1).y) ? 1 : -1;
      current_point = fepl.getPoint(fepl.getNumber() - 1);

      for (int i = 0; i < fepl.getNumber(); i++) {
        int cpy = current_point.y;

        next_point = fepl.getPoint(i);
        if (Utils.equalsApprox(current_point.y, next_point.y, TOLERANCE)) {
          boolean dir = current_point.x > next_point.x;

          slider_manager_horizontal.add(new SliderBase(HORIZONTAL, SliderBase.PARALLEL, dir, Utils.abs(next_point.x - current_point.x), current_point));
        } else {
          if (increasing > 0) {
            if (cpy > next_point.y) {
              slider_manager_horizontal.add(new SliderBase(HORIZONTAL, SliderBase.INFLECTION, true, Utils.abs(next_point.x - current_point.x), current_point));
        }
        // else {
          //  }
          }

          if (increasing < 0) {
            if (cpy < next_point.y) {
              slider_manager_horizontal.add(new SliderBase(HORIZONTAL, SliderBase.INFLECTION, true, Utils.abs(next_point.x - current_point.x), current_point));
        }
        // else {
          //  }
          }

          if (cpy > next_point.y) {
            increasing = -1;
          }

          if (cpy < next_point.y) {
            increasing = 1;
          }
        }
        current_point = next_point;
      }

      // identified points of inflection... ;-)
     }
  }

  void sortAndDedupeSliders() {
    // sort...
    slider_manager_vertical.sort();
    slider_manager_horizontal.sort();
    // dedupe...
    slider_manager_vertical.dedupe();
    slider_manager_horizontal.dedupe();
  }

  // draw sliders...
  public void drawHorizontalSliders(Graphics g, Coords c) {
    if (slider_manager_horizontal != null) {
      for (int i = 0; i < slider_manager_horizontal.getNumber(); i++) {
        SliderBase slider = slider_manager_horizontal.getSlider(i);
        boolean direction = slider.isHomewards();
        int type = slider.getType();
        boolean parallel = (type == SliderBase.PARALLEL);

        g.setColor(parallel ? Color.red : Color.blue);
        WideLine.renderRound(g, new Point(0, slider.getPosition()), new Point(0xFFFF, slider.getPosition()), 0x50, c);
        if (parallel) {
          if (direction) {
            WideLine.renderRound(g, new Point(0x7FFF - HEAD_SIZE, slider.getPosition() - HEAD_SIZE), new Point(0x7FFF, slider.getPosition()), 0x50, c);
            WideLine.renderRound(g, new Point(0x7FFF - HEAD_SIZE, slider.getPosition() + HEAD_SIZE), new Point(0x7FFF, slider.getPosition()), 0x50, c);
          } else {
            WideLine.renderRound(g, new Point(HEAD_SIZE, slider.getPosition() - HEAD_SIZE), new Point(0, slider.getPosition()), 0x50, c);
            WideLine.renderRound(g, new Point(HEAD_SIZE, slider.getPosition() + HEAD_SIZE), new Point(0, slider.getPosition()), 0x50, c);
          }
        }
      }
    }
  }

  public void drawVerticalSliders(Graphics g, Coords c) {
    if (slider_manager_vertical != null) {
      for (int i = 0; i < slider_manager_vertical.getNumber(); i++) {
        SliderBase slider = slider_manager_vertical.getSlider(i);
        boolean direction = slider.isHomewards();
        int type = slider.getType();
        boolean parallel = (type == SliderBase.PARALLEL);

        g.setColor(parallel ? Color.red : Color.blue);
        WideLine.renderRound(g, new Point(slider.getPosition(), 0), new Point(slider.getPosition(), 0xFFFF), 0x50, c);
        if (parallel) {
          if (direction) {
            WideLine.renderRound(g, new Point(slider.getPosition() - HEAD_SIZE, 0xFFFF - HEAD_SIZE), new Point(slider.getPosition(), 0xFFFF), 0x50, c);
            WideLine.renderRound(g, new Point(slider.getPosition() + HEAD_SIZE, 0xFFFF - HEAD_SIZE), new Point(slider.getPosition(), 0xFFFF), 0x50, c);
          } else {
            WideLine.renderRound(g, new Point(slider.getPosition() - HEAD_SIZE, HEAD_SIZE), new Point(slider.getPosition(), 0), 0x50, c);
            WideLine.renderRound(g, new Point(slider.getPosition() + HEAD_SIZE, HEAD_SIZE), new Point(slider.getPosition(), 0), 0x50, c);
          }
        }
      }
    }
  }

  public SliderManagerBase getSliderManagerVertical() {
    return slider_manager_vertical;
  }

  public SliderManagerBase getSliderManagerHorizontal() {
    return slider_manager_horizontal;
  }
}

/*
  public void translate(int dx, int dy) {
    slider_manager_vertical.translate(dx);
    slider_manager_horizontal.translate(dy);
  }

  public void rescaleRangeX(int min, int centre, int max, int new_centre) {
    slider_manager_vertical.rescaleRange(min, centre, max, new_centre);
  }

  public void rescaleRangeY(int min, int centre, int max, int new_centre) {
    slider_manager_horizontal.rescaleRange(min, centre, max, new_centre);
  }

  public void rescaleWithFixedLeft(int fixed, int o, int n) {
    slider_manager_vertical.rescaleWithFixedLeftOrTop(fixed, o, n);
  }

  public void rescaleWithFixedRight(int fixed, int o, int n) {
    slider_manager_vertical.rescaleWithFixedRightOrBottom(fixed, o, n);
  }

  public void rescaleWithFixedTop(int fixed, int o, int n) {
    slider_manager_horizontal.rescaleWithFixedLeftOrTop(fixed, o, n);
  }

  public void rescaleWithFixedBottom(int fixed, int o, int n) {
    slider_manager_horizontal.rescaleWithFixedRightOrBottom(fixed, o, n);
  }
  */

  //  public void setSliderManagerVertical(SliderManagerBase slider_manager_vertical) {
  //    this.slider_manager_vertical = slider_manager_vertical;
  //  }

  //  public void setSliderManagerHorizontal(SliderManagerBase slider_manager_horizontal) {
  //    this.slider_manager_horizontal = slider_manager_horizontal;
  //  }
