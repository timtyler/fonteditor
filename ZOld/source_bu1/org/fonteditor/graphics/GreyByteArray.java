package org.fonteditor.graphics;

public class GreyByteArray {
  private byte[] source;
  private int width;
  private int height;

  /**
   *  Copy constructor :-(
   */
  public GreyByteArray(GreyByteArray tti) {
    byte[] in_pix = tti.getArray();
    source = new byte[in_pix.length];
    width = tti.getWidth();
    height = tti.getHeight();
    
    System.arraycopy(in_pix, 0, source, 0, in_pix.length);
  }

  public GreyByteArray(byte[] a, int w, int h) {
    createImageFromArray(a, w, h);
  }

  private void createImageFromArray(byte[] a, int w, int h) {
    source = a;
    width = w;
    height = h;
  }

  int getWidth() {
    return width;
  }

  int getHeight() {
    return height;
  }

  public byte[] getArray() {
    return source;
  }

  public int getPoint(int x, int y) {
    return getArray()[x + y * width];
  }
}
