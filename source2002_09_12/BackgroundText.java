

/*
 * Outline text...
 * Min max height 4 text...
 * Anti-aliased text...
 * Calms down too soon...
 * No bg image...
 * Looks like original...
 * Granularity...
 */

public class BackgroundText {
//  private int width;
//  private int height;
//  private int size;
//
//  private int big_width;
//  private int big_height;
//  private int big_size;
//
//  private int min_x;
//  private int max_x;
//  private int min_y;
//  private int max_y;
//  private int multiplier = 2;
//  private int radius = 4 * multiplier;
//
//  byte[] buffer;
//  private int[] grabbed_pix;
//
//  //BackgroundText(Component c, int w, int h, String text, String font_name, int font_style, int font_size) {
//  void setup(Component c, int w, int h, String text, String font_name, int font_style, int font_size, int font_pos_x, int font_pos_y) {
//
//    width = w;
//    height = h;
//    size = width * height;
//    //Log.log("size:" + size);
//
//    big_width = width * multiplier;
//    big_height = height * multiplier;
//    big_size = big_width * big_height;
//    //Log.log("big_size:" + big_size);
//
//    Font f = new Font(font_name, font_style, font_size * multiplier);
//
//    Image temp_im = c.createImage(big_width, big_height);
//
//    ImageWrapper temp_ttim = new ImageWrapper(temp_im);
//
//    Graphics temp_g = temp_ttim.getGraphics();
//    temp_g.setFont(f);
//    temp_g.setColor(Color.black);
//    //temp_g.setColor(Color.gray);
//    temp_g.fillRect(0, 0, 9999, 9999);
//    temp_g.setColor(Color.white);
//    temp_g.drawString(text, font_pos_x * multiplier, font_pos_y * multiplier);
//
//    ImageWrapper grown = grow(temp_ttim, (multiplier * 4) / 2);
//    ImageWrapper outlined = outline(grown, (multiplier * 9) / 2);
//
//    //TTImage outlined = temp_ttim;
//    //TTImage outlined = grown;
//
//    //Log.log("W:" + temp_ttim.width);
//
//    //TTImage scaled_image = temp_ttim;
//    ImageWrapper scaled_image = ImageProcessor.scale(outlined, 1024 / multiplier, 1024 / multiplier);
//
//    //Log.log("scaled_imageW:" + scaled_image.width);
//
//    //ImageWrapper scaled_image = temp_ttim;
//
//    // scale image...
//
//    grabbed_pix = scaled_image.getArray();
//
//    //Log.log("size:" + size);
//    //Log.log("grabbed_pix:" + grabbed_pix.length);
//
//    buffer = new byte[size];
//
//    for (int i = 0; i < size; i++) {
//      buffer[i] = (byte) (grabbed_pix[i] & 0xFF);
//    }
//
//    //Log.log("size:" + size);
//
//    findBounds();
//  }
//
//  private ImageWrapper outline(ImageWrapper tti, int r) {
//    grabbed_pix = tti.getArray();
//
//    int[] out = new int[grabbed_pix.length];
//    //ArrayClearer.fill(grabbed_pix,0x000000);
//
//    for (int x = 0; x < tti.width; x++) {
//      for (int y = 0; y < tti.height; y++) {
//
//        int c = grabbed_pix[x + tti.width * y];
//
//        out : if ((c & 0xFF) != 0) {
//          for (int x2 = -r; x2 < r; x2++) {
//            for (int y2 = -r; y2 < r; y2++) {
//              if ((x2 * x2 + y2 * y2) <= r * r) {
//                if ((safeGrabbedRead(x + x2, y + y2) & 0xFF) == 0) {
//                  out[x + y * tti.width] = c;
//                  break out;
//                }
//              }
//            }
//          }
//        }
//      }
//    }
//
//    return new ImageWrapper(out, tti.width, tti.height);
//  }
//
//  private ImageWrapper grow(ImageWrapper tti, int r) {
//    grabbed_pix = tti.getArray();
//
//    int[] out = new int[grabbed_pix.length];
//    //ArrayClearer.fill(grabbed_pix,0x000000);
//
//    for (int x = 0; x < tti.width; x++) {
//      for (int y = 0; y < tti.height; y++) {
//
//        int c = grabbed_pix[x + tti.width * y];
//
//        out : if ((c & 0xFF) == 0) {
//          for (int x2 = -r; x2 < r; x2++) {
//            for (int y2 = -r; y2 < r; y2++) {
//              if ((x2 * x2 + y2 * y2) <= r * r) {
//                if ((safeGrabbedRead(x + x2, y + y2) & 0xFF) != 0) {
//                  out[x + y * tti.width] = 0xFFFFFFFF;
//                  break out;
//                }
//              }
//            }
//          }
//        } else {
//          out[x + y * tti.width] = 0xFFFFFFFF;
//        }
//      }
//    }
//
//    return new ImageWrapper(out, tti.width, tti.height);
//  }
//
//  private void refreshBaseImage(byte dst[]) {
//    for (int j = min_y; j < max_y; j++) {
//      for (int i = min_x; i < max_x; i++) {
//        int b = read(i, j);
//        if (b != 0) {
//          dst[j * width + i] |= b; // add background text...
//        }
//      }
//    }
//  }
//
//  private byte read(int x, int y) {
//    return buffer[x + y * width];
//  }
//
//  private int safeGrabbedRead(int x, int y) {
//    if ((x < 0) || (y < 0) || (x >= big_width) || (y >= big_height)) {
//      return 0xFF000000;
//    }
//
//    return grabbed_pix[x + y * big_width];
//  }
//
//  private void findBounds() {
//    min_x = 0;
//    while (columnIsBlank(min_x)) {
//      min_x++;
//    }
//
//    max_x = width - 1;
//    while (columnIsBlank(max_x)) {
//      max_x--;
//    }
//
//    min_y = 0;
//    while (rowIsBlank(min_y)) {
//      min_y++;
//    }
//
//    max_y = height - 1;
//    while (rowIsBlank(max_y)) {
//      max_y--;
//    }
//
//  }
//
//  private boolean rowIsBlank(int y) {
//    for (int x = 0; x < width - 1; x++) {
//      if ((read(x, y) & 0xFF) != 0) {
//        return false;
//      }
//    }
//
//    return true;
//  }
//
//  private boolean columnIsBlank(int x) {
//    for (int y = 0; y < height - 1; y++) {
//      if ((read(x, y) & 0xFF) != 0) {
//        return false;
//      }
//    }
//
//    return true;
//  }
}
