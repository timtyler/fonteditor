package org.fonteditor;
/**
 * Class in the root - referenced when finding resources there...
 */

public final class RootClass {
  private static volatile RootClass root_class;

  /**
   * RootClass, constructor.
   * <p>
   * The constructor is private.
   * There should not be any instances of this
   * class created outside the private one used
   * in the class.
   **/
  private RootClass() {
  }

  // lazy initialization
  public static synchronized RootClass getRootClass() {
    if (root_class == null) {
      root_class = new RootClass();
      root_class.thisIsASingleton();
    }
    return root_class;
  }

  void thisIsASingleton() {
  }
}
