package org.fonteditor;

import java.awt.Component;
import java.awt.FontMetrics;

import org.fonteditor.cache.CachedGlyph;
import org.fonteditor.cache.FEFontCache;
import org.fonteditor.cache.FEGlyphCache;
import org.fonteditor.font.FEFont;
import org.fonteditor.font.FEFontMetrics;
import org.fonteditor.kerning.Kerning;
import org.fonteditor.kerning.KerningPreprocessor;
import org.fonteditor.options.display.DisplayOptions;

/**
 * Main class allowing public access to the renderer...
 * Handles a single FontCache...
 * Is responsible for obtaining a reference to a visible Component - so image creation can work...
 */

// Currently needs a visible component instance - so it can create Image objects...
// Totally pointless - and indeed counter-productive - under Java 2 of course...
// ...but *essential* under Java 1.1 :-(

public class FEFontRenderer {
  private FEFontCache fefontcache = new FEFontCache(); // could be global...

  public FEFontRenderer(Component component) {
    FEGlyphCache.setComponent(component);
  }

  // Optimise? - cache common kerning distances for speed...
  // ...using a "map" based on the "key" of c1 x c2...?
  public int getKerningDistance(char c1, FEFont font1, DisplayOptions gdo1, char c2, FEFont font2, DisplayOptions gdo2) {

    //final Coords coords = gdo1.getCoords();
    //int spacing = 2;
    //coords.scaleY(font1.getHintingCues().getBottomLetterO() >> 3);

    c1 = KerningPreprocessor.process(c1);
    c2 = KerningPreprocessor.process(c2);

    CachedGlyph cached_glyph1 = fefontcache.getCachedGlyph(font1, gdo1, c1);
    CachedGlyph cached_glyph2 = fefontcache.getCachedGlyph(font2, gdo2, c2);

    return Kerning.getKerningDistance(cached_glyph1, cached_glyph2);
  }

  public void remove(FEFont font) {
    fefontcache.remove(font);
  }

  public FEFontCache getFEFontCache() {
    return fefontcache;
  }

  public CachedGlyph getCachedGlyph(char c, FEFont font, DisplayOptions gdo) {
    return fefontcache.getCachedGlyph(font, gdo, c);
  }

  // Optimise: this is currently an *unnecessarily* expensive operation...

  //  public int howManyCharactersFit(FEFont font, DisplayOptions display_options, String text, int width) {
  //    int number = 1;
  //    int size = 0;
  //    char[] data = text.toCharArray();
  //    int data_length = data.length;
  //
  //    do {
  //      if (number >= data_length) {
  //        return data_length;
  //      }
  //
  //      FontMetrics f_m = getFontMetrics(font, display_options);
  //      size = f_m.charsWidth(data, 0, number);
  //      number++;
  //    } while (size < width);
  //
  //    return number - 2;
  //  }

  public int howManyCharactersFit(FEFont font, DisplayOptions display_options, int start, char[] chars, int width) {
    int number = start;
    int chars_length = chars.length;

    char bar = '|';
    char previous = bar;

    int cwidth = 0;
    while (cwidth < width) {
      //for (int i = 0; i < length; i++) {
      char ch = chars[number];
      int kd = getKerningDistance(previous, font, display_options, ch, font, display_options);
      cwidth += kd + Kerning.getKerningGap(display_options.getCoords().getAAHeight());

      previous = ch;

      if (++number >= chars_length) {
        return number;
      }
    }

    return number - 1;
  }

  public FontMetrics getFontMetrics(FEFont font, DisplayOptions display_options) {
    return new FEFontMetrics(font, this, display_options);
  }

  /**
   * Given a string and the length of a prefix, return the index of the last
   * space...
   */
  public static int lengthBeforeLastSpace(String text, int n) {
    int i = n - 1;

    if (i < 0) {
      return n;
    }

    while (text.charAt(i) != ' ') {
      if (--i < 0) {
        return n;
      }
    }

    return i;
  }
}

//  public init(Component component) {
//    FEGlyphCache.setComponent(component);
//    initialised = true;
//  }
