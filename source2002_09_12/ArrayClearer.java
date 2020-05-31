/* ArrayClearer - Tim Tyler 2000.
 * 
 * * Doesn't work...
 */

/*
 * ToDo:
 *
 *
 */
public class ArrayClearer {
  //final static int size = 256;
  //static int[] empty_array = new int[size];

  //static {
  // empty_array = new int[size];
  // }

  /** Clears integer arrays with zeros;
   *  @param x the 1D integer array in question
   *  @author Tim Tyler tt@iname.com
   */
  private static void clear(int[] a) {
    //int l = a.length;

    //System.arraycopy(empty_array, 0, a, 0, l);
    fill(a, 0);
  }

  /** Clears integer arrays with zeros;
   *  @param x the 2D integer array in question
   *  @author Tim Tyler tt@iname.com
   */
  private static void clear(int[][] a) {
    for (int i = a.length; --i >= 0;) {
      clear(a[i]);
    }
  }

  protected static final void fill(int[] buffer, int value) {
    int size = buffer.length - 1;
    int cleared = 1;
    int index = 1;
    buffer[0] = value;

    while (cleared < size) {
      System.arraycopy(buffer, 0, buffer, index, cleared);
      size -= cleared;
      index += cleared;
      cleared <<= 1;
    }
    System.arraycopy(buffer, 0, buffer, index, size);
  }

  /** Clears integer arrays with zeros;
   *  @param x the 1D integer array in question
   *  @author Tim Tyler tt@iname.com
   */
  private static void clear(byte[] a) {
    fill(a, (byte) 0);
  }

  /** Clears integer arrays with zeros;
   *  @param x the 2D integer array in question
   *  @author Tim Tyler tt@iname.com
   */
  private static void clear(byte[][] a) {
    for (int i = a.length; --i >= 0;) {
      clear(a[i]);
    }
  }

  protected static final void fill(byte[] buffer, byte value) {
    int size = buffer.length - 1;
    int cleared = 1;
    int index = 1;
    buffer[0] = value;

    while (cleared < size) {
      System.arraycopy(buffer, 0, buffer, index, cleared);
      size -= cleared;
      index += cleared;
      cleared <<= 1;
    }
    System.arraycopy(buffer, 0, buffer, index, size);
  }
}
