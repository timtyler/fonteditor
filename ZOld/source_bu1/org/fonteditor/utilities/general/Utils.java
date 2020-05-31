package org.fonteditor.utilities.general;

public class Utils {
  public static int abs(int a) {
    return (a < 0) ? -a : a;
  }

  public static int min(int a, int b) {
    return (a < b) ? a : b;
  }

  public static int min(int a, int b, int c) {
    return min(min(a, b), c);
  }

  public static float min(float a, float b) {
    return (a < b) ? a : b;
  }

  public static int max(int a, int b) {
    return (a > b) ? a : b;
  }

  public static int max(int a, int b, int c) {
    return max(max(a, b), c);
  }

  public static boolean equalsApprox(int a, int b, int tolerance) {
    if ((a - b) > tolerance) {
      return false;
    }
    
    return ((b - a) <= tolerance);
  }
}
