package org.fonteditor;

import java.awt.Component;

import org.fonteditor.cache.CachedGlyph;
import org.fonteditor.cache.FEFontCache;
import org.fonteditor.cache.FEGlyphCache;
import org.fonteditor.font.FEFont;
import org.fonteditor.options.pen.Pen;
import org.fonteditor.options.display.GlyphDisplayOptions;
import org.fonteditor.utilities.general.Range;

/**
 * Main class allowing public access to the renderer... 
 */

public class FEFontRenderer {
  private FEFontCache fefontcache = new FEFontCache();
  private GlyphDisplayOptions gdo;

  public FEFontRenderer(Component component) {
    FEGlyphCache.setComponent(component);
  }

  // cache these... using a "map"...?
  public int getKerningDistance(char c1, FEFont font1, GlyphDisplayOptions gdo1, char c2, FEFont font2, GlyphDisplayOptions gdo2) {
    // A bit of a hack, really ;-)
    // May cause havoc if these characters aren't available...
    c1 = process(c1);
    c2 = process(c2);
    CachedGlyph cached_glyph1 = fefontcache.getCachedGlyph(font1, gdo1, c1);
    CachedGlyph cached_glyph2 = fefontcache.getCachedGlyph(font2, gdo2, c2);

    return getKerningDistance(cached_glyph1, cached_glyph2);
  }

  private int getKerningDistance(CachedGlyph cached_glyph1, CachedGlyph cached_glyph2) {
    int[] rhs_kerning_1 = cached_glyph1.getKerning().getKerningOffsetsRHS();
    int[] lhs_kerning_2 = cached_glyph2.getKerning().getKerningOffsetsLHS();
    int min_y1 = cached_glyph1.getOffsetY();
    int min_y2 = cached_glyph2.getOffsetY();
    int max_y1 = min_y1 + cached_glyph1.getHeight();
    int max_y2 = min_y2 + cached_glyph2.getHeight();
    Range r1 = new Range(min_y1, max_y1);
    Range r2 = new Range(min_y2, max_y2);
    int largest_offset = 1;

    if (r1.doesOverlap(r2)) {
      Range r3 = r1.overlap(r2);
      int end = r3.getMin();

      for (int y = r3.getMax(); --y >= end;) {
        int current_offset = rhs_kerning_1[y - min_y1] - lhs_kerning_2[y - min_y2];

        if (current_offset > largest_offset) {
          largest_offset = current_offset;
        }
      }
    }
    return largest_offset + 1;
  }

  public void remove(FEFont font) {
    fefontcache.remove(font);
  }

  public FEFontCache getFEFontCache() {
    return fefontcache;
  }

  public GlyphDisplayOptions getGDO(int siz, int quality, Pen pen, boolean filled) {
    if ((gdo == null) || (siz != gdo.getCoords().getAAHeight()) || ((siz * quality) != gdo.getCoords().getHeight())) {
      gdo = GlyphDisplayOptions.getGDOrender(siz, quality, quality, pen, filled);
    }
    
    return gdo;
  }

//  public ImageWrapperTranslated getCharacterImage(char c, FEFont font, GlyphDisplayOptions gdo) {
//    return fefontcache.getCachedGlyph(font, gdo, c).getImageWrapperTranslated();
//  }

  public CachedGlyph getCachedGlyph(char c, FEFont font, GlyphDisplayOptions gdo) {
    return fefontcache.getCachedGlyph(font, gdo, c);
  }

  char process(char c) {
    switch (c) {

      case ' ' :
        return 'H';

      case '\'' :
      case '`' :
      case '.' :
      case ',' :
      case ';' :
      case ':' :
        return '!';

      case '-' :
        return 'o';
    }
    return c;
  }
}