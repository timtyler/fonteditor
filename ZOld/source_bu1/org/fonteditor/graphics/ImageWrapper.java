package org.fonteditor.graphics;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

import org.fonteditor.utilities.log.Log;

// Don't try writing to JPEGs.  It will have no effect...

public class ImageWrapper {
  private Image i;
  private int[] source;
  private int width;
  private int height;
  private boolean get_fresh = false;
  private static Toolkit toolkit;

  static {
    toolkit = Toolkit.getDefaultToolkit();
  }

  /**
   *  Copy constructor :-(
   */
  public ImageWrapper(ImageWrapper tti) {
    width = tti.getWidth();
    height = tti.getHeight();
    int[] in_pix = tti.getArray();
    int[] out_pix = new int[in_pix.length];

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        out_pix[i + width * j] = in_pix[i + width * j];
      }
    }
    createImageFromArray(out_pix, width, height);
  }

  /** Constructor from existing image...
   */
  public ImageWrapper(Image image) {
    setImage(image);
  }

  public ImageWrapper() {
    i = null;
    width = -1;
    height = -1;
    source = null;
  }

  public ImageWrapper(int[] a, int w, int h, boolean x) {
    x = x;
    i = toolkit.createImage(new MemoryImageSource(w, h, a, 0, w));
  }

  public ImageWrapper(Component component, int w, int h) {
    setImage(component.createImage(w, h));
  }

  public ImageWrapper(int[] a, int w, int h) {
    createImageFromArray(a, w, h);
  }

  private void createImageFromArray(int[] a, int w, int h) {
    source = a;
    width = w;
    height = h;
    i = toolkit.createImage(new MemoryImageSource(w, h, ColorModel.getRGBdefault(), a, 0, w));
  }

  // used for JPEGs...
  public void setImage(Image image) {
    i = image;
    
    if (i != null) {
      width = image.getWidth(null);
      height = image.getHeight(null);
    }
    
    source = null;
    get_fresh = true;
  }

  public Image getImage() {
    return i;
  }

  public Graphics getGraphics() {
    return i.getGraphics();
  }

  public void freshImage() {
    get_fresh = true;
  }

  int getWidth() {
    return width;
  }

  int getHeight() {
    return height;
  }

  public int[] getArray() {
    if ((source == null) || (source.length < 1)) {
      width = getWidth();
      height = getHeight();
      source = new int[width * height];
      //debug("Making new image:" + (width * height));
    }
    
    if (get_fresh) {
      get_fresh = false;
      PixelGrabber pg = new PixelGrabber(i, 0, 0, width, height, source, 0, width);

      try {
        pg.grabPixels();
      } catch (InterruptedException e) {
        Log.log(e.toString());
      }
    }
    
    return source;
  }

  public int getPoint(int x, int y) {
    return getArray()[x + y * width];
  }
}
