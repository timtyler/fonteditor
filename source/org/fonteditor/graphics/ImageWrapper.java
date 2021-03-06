package org.fonteditor.graphics;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

import org.fonteditor.utilities.general.Forget;
import org.fonteditor.utilities.log.Log;

// Don't try writing to JPEGs.  It will have no effect...

/**
 * This is a thin wrapper over a Java image.
 * The image is nurmally backed by a pixel array -
 * for use with MemoryImageSource.
 */

public class ImageWrapper implements Cloneable {
  private Image image;
  private int[] source;
  private int width;
  private int height;
  private boolean get_fresh = false;
  private static Toolkit toolkit;

  static {
    toolkit = Toolkit.getDefaultToolkit();
  }

  public Object clone() {
    int[] source_old = getArray();
    int[] source_new = new int[getArray().length];

    System.arraycopy(source_old, 0, source_new, 0, source_old.length);
    return new ImageWrapper(source_new, getWidth(), getHeight());
  }

  /** Constructor from existing image...
   */
  public ImageWrapper(Image image) {
    setImage(image);
  }

  public ImageWrapper() {
    image = null;
    width = -1;
    height = -1;
    source = null;
  }

  public ImageWrapper(int[] a, int w, int h, boolean x) {
    Forget.about(x);
    image = toolkit.createImage(new MemoryImageSource(w, h, a, 0, w));
  }

  public ImageWrapper(Component component, int w, int h) {
    setImage(component.createImage(w, h));
  }
//  public ImageWrapper(int w, int h) {
//    setImage(toolkit.createImage(w, h));
//  }

  public ImageWrapper(int[] a, int w, int h) {
    createImageFromArray(a, w, h);
  }

  private void createImageFromArray(int[] a, int w, int h) {
    source = a;
    width = w;
    height = h;
    image = toolkit.createImage(new MemoryImageSource(w, h, ColorModel.getRGBdefault(), a, 0, w));
  }

  public void setWidthAndHeight() {
    width = image.getWidth(null);
    height = image.getHeight(null);
  }
  
  // used for JPEGs...
  public void setImage(Image image) {
    this.image = image;
    
    if (image != null) {
      setWidthAndHeight();
    }
    
    source = null;
    get_fresh = true;
  }

  public Image getImage() {
    return image;
  }

  public Graphics getGraphics() {
    return image.getGraphics();
  }

  public void freshImage() {
    get_fresh = true;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
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
      PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, source, 0, width);

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

//  /**
//   *  Copy constructor :-(
//   */
//  public ImageWrapper(ImageWrapper tti) {
//    width = tti.getWidth();
//    height = tti.getHeight();
//    int[] in_pix = tti.getArray();
//    int[] out_pix = new int[in_pix.length];
//
//    for (int i = 0; i < width; i++) {
//      for (int j = 0; j < height; j++) {
//        out_pix[i + width * j] = in_pix[i + width * j];
//      }
//    }
//    createImageFromArray(out_pix, width, height);
//  }
