package org.fonteditor.cache;

import java.util.Hashtable;

import org.fonteditor.font.FEFont;
import org.fonteditor.options.display.GlyphDisplayOptions;

/**
  * Manages the Font Cache...
  */

public class FEFontCache {
  private Hashtable hashtable = new Hashtable();

  public CachedGlyph getCachedGlyph(FEFont fefont, GlyphDisplayOptions gdo, char c) {
    FEDisplayOptionsCache display_cache = (FEDisplayOptionsCache) hashtable.get((Object) fefont);

    if (display_cache == null) {
      display_cache = new FEDisplayOptionsCache(fefont);
      hashtable.put((Object) fefont, (Object) display_cache);
    }
    return display_cache.getCachedGlyph(gdo, c);
  }

   public void remove(FEFont font) {
    if (hashtable != null) {
      if (font != null) {
        hashtable.remove((Object) font);
        // System.gc();
      }
    }
  }

   public void remove(FEFont font, GlyphDisplayOptions gdo, char c) {
    if (hashtable != null) {
      FEDisplayOptionsCache display_cache = (FEDisplayOptionsCache) hashtable.get((Object) font);

      display_cache.remove(gdo, c);
    }
  }

  public void removeAll() {
    hashtable.clear();
    // System.gc();
  }
}