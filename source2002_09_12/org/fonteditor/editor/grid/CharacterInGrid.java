package org.fonteditor.editor.grid;

/**
 * A Glyph in the Grid.
 * Keeps track of how well it is currently rendered...
 */

public class CharacterInGrid {
  private int quality = 0;
  private int target = 1;

  void resetQuality(int quality) {
    this.quality = quality;
  }

  void incrementQuality() {
    if (!metTarget()) {
      switch (quality) {

        //       case 8 :
        //          quality = 16;
        //          break;
        //        case 4 :
        //          quality = 8;
        //          break;
        //        case 2 :
        //          quality = 4;
        //          break;
        //        case 1 :
        //          quality = 2;
        //          break;
        case 0 :
          quality = 1;
          break;

        default :
          quality = target;
          //Log.log("setquality:" + quality);

          break;
      }
    }
  }

  boolean metTarget() {
    return (quality == target);
  }

  void setTarget(int target) {
    // Log.log("setTarget:" + target);
    this.target = target;
  }

  int getQuality() {
    return quality;
  }

  void finishQuality() {
    quality = target;
  }
}
