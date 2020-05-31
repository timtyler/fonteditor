package org.fonteditor.editor.frame;

import org.fonteditor.options.display.GlyphDisplayOptionsEditor;

/**
  * Keeps track of the default display options for a framed Glyph...
  */

public class DefaultFrameDisplayOptions {
  private int width;
  private int height;
  private boolean preserve_aspect_ratio;
  private GlyphDisplayOptionsEditor gdoe;

  public DefaultFrameDisplayOptions() {
    width = 360;
    height = 780;
    setGDOE(new GlyphDisplayOptionsEditor());
    preserve_aspect_ratio = true;
  }

  public void setPreserveAspectRatio(boolean preserve_aspect_ratio) {
    this.preserve_aspect_ratio = preserve_aspect_ratio;
  }

  public boolean preservesAspectRatio() {
    return preserve_aspect_ratio;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getHeight() {
    return height;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getWidth() {
    return width;
  }

  public void setGDOE(GlyphDisplayOptionsEditor gdo) {
    this.gdoe = gdo;
  }

  public GlyphDisplayOptionsEditor getGDOE() {
    return gdoe;
  }
}
