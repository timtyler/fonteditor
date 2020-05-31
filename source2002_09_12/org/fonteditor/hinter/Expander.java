package org.fonteditor.hinter;

import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.font.FEGlyph;
import org.fonteditor.options.display.GlyphDisplayOptions;

public class Expander {
  public static void expand(FEGlyph glyph, GlyphDisplayOptions gdo) {
    int expand_factor = gdo.getExpand();
    if (expand_factor > 0) {
      FEPointList fepl_of_glyph = glyph.getFEPointList(gdo);
      for (int index = fepl_of_glyph.getNumber(); --index >= 0;) {
        FEPoint point = fepl_of_glyph.getPoint(index);
        point.setX((point.getX() * expand_factor) >> 10);
      }
    }
  }
}
