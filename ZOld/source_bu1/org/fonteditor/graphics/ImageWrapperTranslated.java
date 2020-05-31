package org.fonteditor.graphics;

import java.awt.Graphics;
public class ImageWrapperTranslated {
  private ImageWrapper image_wrapper;
  private int offset_y;

  public ImageWrapperTranslated(ImageWrapper image_wrapper, int y) {
    this.image_wrapper = image_wrapper;
    this.offset_y = y;
  }

  public ImageWrapper getImageWrapper() {
    return image_wrapper;
  }

  public Graphics getGraphics() {
    return image_wrapper.getGraphics();
  }

  public int getOffsetY() {
    return offset_y;
  }
}
