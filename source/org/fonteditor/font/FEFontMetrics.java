package org.fonteditor.font;
import java.awt.FontMetrics;

import org.fonteditor.FEFontRenderer;
import org.fonteditor.cache.CachedGlyph;
import org.fonteditor.kerning.Kerning;
import org.fonteditor.options.display.DisplayOptions;

public class FEFontMetrics extends FontMetrics {
  private FEFont font;
  private FEFontRenderer renderer;
  private DisplayOptions display_options;

  private int max_width = -1;
  private int max_descent = -1;

  private static char baseline_character = 'o';
  private static char ascent_character = 'H';
  private static char descent_character = 'g';

  public FEFontMetrics(FEFont font, FEFontRenderer renderer, DisplayOptions display_options) {
    super(null);
    this.font = font;
    this.renderer = renderer;
    this.display_options = display_options;
  }

  public int getAscent() {
    return getCachedGlyph(ascent_character).getHeight();
  }

  public int getDescent() {
    CachedGlyph cached_glyph_g = getCachedGlyph(descent_character);
    CachedGlyph cached_glyph_h = getCachedGlyph(ascent_character);
    int bottom_of_g = cached_glyph_g.getHeight() + cached_glyph_g.getOffsetY();
    int bottom_of_h = cached_glyph_h.getHeight() + cached_glyph_h.getOffsetY();
    
    return bottom_of_g - bottom_of_h;
  }

  public int getLeading() {
    return getAscent() / 4 + 1;
  }

  // this could do with being cached...
  public int getMaxDescent() {
    int baseline = getAscentPlusDescent(baseline_character);
    if (max_descent == -1) {
      for (int i = 32; i < 127; i++) {
        int descent = getAscentPlusDescent((char) i) - baseline;
        max_descent = Math.max(max_descent, descent);
      }
    }
    
    return max_descent;
  }

  /** Returns the distance from the bottom of the character to the maximum ascent point. */
  private int getAscentPlusDescent(char ch) {
    CachedGlyph glyph = getCachedGlyph(ch);
    return glyph.getOffsetY() + glyph.getHeight();
  }

  public int getMaxAdvance() {
    if (max_width == -1) {
      for (int i = 0; i < 255; i++) {
        max_width = Math.max(max_width, getCachedGlyph((char) i).getWidth());
      }
    }
    
    return max_width;
  }

  public int charWidth(char ch) {
    CachedGlyph glyph = getCachedGlyph(ch);
    return glyph.getWidth();
  }

  public int charsWidth(char[] chars, int start, int length) {
    char bar = '|';
    char previous = bar;
    int width = 0;
    for (int i = start; i < length; i++) {
      char ch = chars[i];
      int kd = renderer.getKerningDistance(previous, font, display_options, ch, font, display_options);
      width += kd + Kerning.getKerningGap(display_options.getCoords().getAAHeight());

      previous = ch;
    }
    
    // A bit of a crude hack to add the width of the last character...
    width += renderer.getKerningDistance(previous, font, display_options, bar, font, display_options);

    return width;
  }

  private CachedGlyph getCachedGlyph(char ch) {
    return renderer.getCachedGlyph(ch, font, display_options);
  }
}
