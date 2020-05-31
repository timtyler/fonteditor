package org.fonteditor.graphics;

/**
 * Convert between image formats...
 */

public class GraphicsConverter {
  public static GreyByteArray convert(ImageWrapper iw) {
    int width = iw.getWidth();
    int height = iw.getHeight();
    int[] source = iw.getArray();
    int length = source.length;

    byte[] ba = new byte[source.length];

    for (int i = length; --i >= 0;) {
      ba[i] = (byte) source[i];
    }

    return new GreyByteArray(ba, width, height);
  }
  
  public static ImageWrapper convert(GreyByteArray gba) {
    int width = gba.getWidth();
    int height = gba.getHeight();
    byte[] source = gba.getArray();
    int length = source.length;

    int[] ia = new int[source.length];

    for (int i = length; --i >= 0;) {
      int temp = source[i] & 0xFF;
      ia[i] = (temp == 0) ? 0xFF000000 : (temp | (temp << 8)  | (temp << 16)) ;
    }

    return new ImageWrapper(ia, width, height);
  }
  
    public static ImageWrapperTranslated convert(GreyByteArrayTranslated gbat) {
      ImageWrapper ia = convert(gbat.getGreyByteArray());
      return new ImageWrapperTranslated(ia, gbat.getOffsetY());
    }
    
    public static GreyByteArrayTranslated convert(ImageWrapperTranslated iwt) {
      GreyByteArray gba = convert(iwt.getImageWrapper());
      return new GreyByteArrayTranslated(gba, iwt.getOffsetY());
    }
}
