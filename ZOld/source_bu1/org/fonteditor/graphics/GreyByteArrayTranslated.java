package org.fonteditor.graphics;

public class GreyByteArrayTranslated {
  private GreyByteArray grey_byte_array;
  private int offset_y;

  public GreyByteArrayTranslated(GreyByteArray grey_byte_array, int offset_y) {
    this.grey_byte_array = grey_byte_array;
    this.offset_y = offset_y;
  }

  public GreyByteArray getGreyByteArray() {
    return grey_byte_array;
  }

  public int getOffsetY() {
    return offset_y;
  }
}
