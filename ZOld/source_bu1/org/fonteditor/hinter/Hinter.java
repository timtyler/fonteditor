package org.fonteditor.hinter;

import org.fonteditor.elements.paths.FEPath;
import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.font.FEGlyph;
import org.fonteditor.options.display.GlyphDisplayOptions;
import org.fonteditor.sliders.SliderBase;
import org.fonteditor.sliders.SliderManagerBase;
import org.fonteditor.sliders.Sliders;
import org.fonteditor.utilities.general.Utils;

public class Hinter {
  public static void hint(FEGlyph glyph, GlyphDisplayOptions gdo) {
    if (gdo.isHint()) {
      Sliders s = glyph.getSliders();
      
      // Prevent any shifting off the top or LHS caused by the width...
      
      
      if (gdo.getPen().getBottom() > 0) {
        translate(glyph, -gdo.getLineWidthOffsetWest(), -gdo.getLineWidthOffsetNorth(), gdo);
      }

      hintBasicRight(glyph, gdo, (SliderManagerBase) s.getSliderManagerVertical());
      hintBasicBottom(glyph, gdo, (SliderManagerBase) s.getSliderManagerHorizontal());

      hintBasicLeft(glyph, gdo, (SliderManagerBase) s.getSliderManagerVertical());
      hintBasicTop(glyph, gdo, (SliderManagerBase) s.getSliderManagerHorizontal());

      Hinter.hintX(glyph, gdo, (SliderManagerBase) s.getSliderManagerVertical());
      Hinter.hintY(glyph, gdo, (SliderManagerBase) s.getSliderManagerHorizontal());
    }
  }

  private static void translate(FEGlyph glyph, int dx, int dy, GlyphDisplayOptions gdo) {
    glyph.translate(dx, dy, gdo);
  }


  protected static void hintBasicRight(FEGlyph g, GlyphDisplayOptions gdo, SliderManagerBase s_m) {
    int min_pos = s_m.getMin();
    int max_pos = s_m.getMax();
    int new_min_pos = gdo.getCoords().nearestX(min_pos, gdo.getLineWidthOffsetWest());
    g.rescaleWithFixedRight(max_pos, min_pos, new_min_pos, gdo);

    //Log.log("min_pos:" + min_pos + " new_min_pos:" + new_min_pos);
  }

  protected static void hintBasicLeft(FEGlyph g, GlyphDisplayOptions gdo, SliderManagerBase s_m) {
    int min_pos = s_m.getMin();
    int max_pos = s_m.getMax();
    int new_max_pos = gdo.getCoords().nearestX(max_pos, gdo.getLineWidthOffsetEast());
    g.rescaleWithFixedLeft(min_pos, max_pos, new_max_pos, gdo);
  }

  protected static void hintBasicBottom(FEGlyph g, GlyphDisplayOptions gdo, SliderManagerBase s_m) {
    int min_pos = s_m.getMin();
    int max_pos = s_m.getMax();
    int new_min_pos = gdo.getCoords().nearestY(min_pos, gdo.getLineWidthOffsetNorth());
    g.rescaleWithFixedBottom(max_pos, min_pos, new_min_pos, gdo);
  }

  protected static void hintBasicTop(FEGlyph g, GlyphDisplayOptions gdo, SliderManagerBase s_m) {
    int min_pos = s_m.getMin();
    int max_pos = s_m.getMax();
    int new_max_pos = gdo.getCoords().nearestY(max_pos, gdo.getLineWidthOffsetSouth());
    g.rescaleWithFixedTop(min_pos, max_pos, new_max_pos, gdo);
  }

  protected static void hintX(FEGlyph glyph, GlyphDisplayOptions gdo, SliderManagerBase s_m) {
    if (s_m.getNumber() == 0) {
      return;
    }

    PointValueGetter pvg = new PointValueGetter() {
      int getPointValue(FEPoint point) {
        return point.x;
      }
    };

    NearestValueGetter nvg = new NearestValueGetter() {
      int getNearestValue(GlyphDisplayOptions gdo, int v1, SliderBase s) {
        return gdo.getCoords().nearestX(v1, s.isHomewards() ? gdo.getLineWidthOffsetWest() : gdo.getLineWidthOffsetEast());
      }
    };

    SectionRescaler rescaler = new SectionRescaler() {
      void rescaleSection(int new_pos, FEPointList fepl, FEPoint point, int index_on_path, int min_index, int max_index) {
        int temp_x = point.x;
        int temp_y = point.y;
        fepl.scaleRangeX(min_index, index_on_path, new_pos, min_index, index_on_path);

        point.x = temp_x;
        point.y = temp_y;
        fepl.scaleRangeX(max_index, index_on_path, new_pos, index_on_path, max_index);
      }
    };

    recursivelyDivide(glyph, gdo, s_m, 0, s_m.getNumber() - 1, pvg, nvg, rescaler);
  }

  protected static void hintY(FEGlyph glyph, GlyphDisplayOptions gdo, SliderManagerBase s_m) {
    if (s_m.getNumber() == 0) {
      return;
    }

    PointValueGetter pvg = new PointValueGetter() {
      int getPointValue(FEPoint point) {
        return point.y;
      }
    };

    NearestValueGetter nvg = new NearestValueGetter() {
      int getNearestValue(GlyphDisplayOptions gdo, int v1, SliderBase s) {
        return gdo.getCoords().nearestY(v1, s.isHomewards() ? gdo.getLineWidthOffsetSouth() : gdo.getLineWidthOffsetNorth());
      }
    };

    SectionRescaler rescaler = new SectionRescaler() {
      void rescaleSection(int new_pos, FEPointList fepl, FEPoint point, int index_on_path, int min_index, int max_index) {
        int temp_x = point.x;
        int temp_y = point.y;
        fepl.scaleRangeY(min_index, index_on_path, new_pos, min_index, index_on_path);
        point.x = temp_x;
        point.y = temp_y;
        fepl.scaleRangeY(max_index, index_on_path, new_pos, index_on_path, max_index);
      }
    };

    recursivelyDivide(glyph, gdo, s_m, 0, s_m.getNumber() - 1, pvg, nvg, rescaler);
  }

  private static void recursivelyDivide(FEGlyph glyph, GlyphDisplayOptions gdo, SliderManagerBase s_m, int min, int max, PointValueGetter pvg, NearestValueGetter nvg, SectionRescaler rescaler) {
    if (max - min < 2) {
      return;
    }

    int idx = (min + max) >> 1;
    SliderBase s = s_m.getSlider(idx);

    if (s.canMove()) {
      int old_pos = s.getPosition();
      int new_pos = nvg.getNearestValue(gdo, old_pos, s);

      int index_current = -1;

      do {
        FEPointList fepl_of_glyph = glyph.getFEPointList(gdo);
        index_current = getIndexOfNextMatchingPoint(fepl_of_glyph, index_current, old_pos, pvg);
        if (index_current >= 0) {
          FEPath path = glyph.getFEPathList(gdo).getPath(fepl_of_glyph.getPoint(index_current));
          FEPointList fepl = path.getFEPointList();
          FEPoint point = fepl_of_glyph.getPoint(index_current);
          int index_on_path = fepl.getIndexOf(point);
          if (index_on_path >= 0) {

            int min_index = getIndexOfNextSlider(fepl, index_on_path, s_m, -1, pvg);
            int max_index = getIndexOfNextSlider(fepl, index_on_path, s_m, 1, pvg);

            rescaler.rescaleSection(new_pos, fepl, point, index_on_path, min_index, max_index);
          }
        }
      } while (index_current >= 0);
    }

    recursivelyDivide(glyph, gdo, s_m, min, idx, pvg, nvg, rescaler);
    recursivelyDivide(glyph, gdo, s_m, idx, max, pvg, nvg, rescaler);
  }

  private static void rescaleSectionY(int new_pos, FEPointList fepl, FEPoint point, int index_on_path, int min_index, int max_index) {
    int temp_x = point.x;
    int temp_y = point.y;
    fepl.scaleRangeY(min_index, index_on_path, new_pos, min_index, index_on_path);
    point.x = temp_x;
    point.y = temp_y;
    fepl.scaleRangeY(max_index, index_on_path, new_pos, index_on_path, max_index);
  }

  private static int getIndexOfNextMatchingPoint(FEPointList fepl, int idx, int pos, PointValueGetter pvg) {
    int number = fepl.getNumber();

    if (idx == -1) {
      idx++;
    } else {
      for (; true;) {
        if (++idx >= number) {
          return -1;
        }

        if (pvg.getPointValue(fepl.getPoint(idx)) != pos) {
          break;
        }
      }
    }

    for (; true;) {
      if (pvg.getPointValue(fepl.getPoint(idx)) == pos) {
        return idx;
      }

      if (++idx >= number) {
        return -1;
      }
    }
  }

  private static int getIndexOfNextSlider(FEPointList fepl, int idx, SliderManagerBase s_m, int dir, PointValueGetter pvg) {
    if (Utils.abs(dir) != 1) {
      throw new RuntimeException("getIndexOfNextSlider:Looped");
    }

    int looped = idx;
    int pos = pvg.getPointValue(fepl.getPoint(looped));
    int number = fepl.getNumber();

    while (pvg.getPointValue(fepl.getPoint(idx)) == pos) {
      idx += dir;
      if (idx >= number) {
        idx = 0;
      }

      if (idx < 0) {
        idx = number - 1;
      }

      if (idx == looped) {
        return idx;
       // throw new RuntimeException("getIndexOfNextSlider:Looped"); // FIZ
      }
    }

    looped = idx;
    while (!s_m.isSliderAt(pvg.getPointValue(fepl.getPoint(idx)))) {
      idx += dir;
      if (idx >= number) {
        idx = 0;
      }

      if (idx < 0) {
        idx = number - 1;
      }

      if (idx == looped) {
        return idx;
        // throw new RuntimeException("getIndexOfNextSlider:Looped"); // FIZ
      }
    }

    return idx;
  }
}

abstract class PointValueGetter {
  abstract int getPointValue(FEPoint point);
}

abstract class NearestValueGetter {
  abstract int getNearestValue(GlyphDisplayOptions gdo, int v1, SliderBase s);
}

abstract class SectionRescaler {
  abstract void rescaleSection(int new_pos, FEPointList fepl, FEPoint point, int index_on_path, int min_index, int max_index);
}
