package org.fonteditor.cache;

import java.util.Hashtable;

import org.fonteditor.font.FEFont;

import org.fonteditor.options.display.DisplayOptions;

import org.fonteditor.utilities.claim.Claim;

/**
  * Caches fonts for a specified set of Display Options...
  */

public class FEDisplayOptionsCache {
  private FEFont fefont;
  private Hashtable hashtable = new Hashtable();

  public FEDisplayOptionsCache(FEFont fefont) {
    this.fefont = fefont;
  }

  public FEGlyphCache getFEGlyphCache(DisplayOptions gdo) {
    return (FEGlyphCache) hashtable.get((Object) gdo);
  }

  CachedGlyph getCachedGlyph(DisplayOptions gdo, char c) {
    FEGlyphCache glyph_cache = getFEGlyphCache(gdo);

    if (glyph_cache == null) {
      glyph_cache = new FEGlyphCache(fefont); // fefont, gdo

      Object key = null;
      try {
        key = (Object) gdo.clone();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }

      hashtable.put(key, (Object) glyph_cache);
    }

    return glyph_cache.getCachedGlyph(c, fefont, gdo);
  }

  void remove(DisplayOptions gdo, char c) {
    FEGlyphCache fegc = getFEGlyphCache(gdo);

    Claim.claim(fegc != null, "Oh dear in remove(), fegc = null");
    fegc.remove(c);
  }
}
