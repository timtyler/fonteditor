import java.io.File;

import org.fonteditor.utilities.log.Log;

class ListClasses {
  private static String base = "C:\\Documents\\Java\\FontEditor\\bin\\";

  static void listClasses() {
    recursivelyListClasses(base, base);
  }

  static void recursivelyListClasses(String path, String prefix) {
    File file;
    file = new File(path);
    File[] sa = file.listFiles();
    for (int i = sa.length; --i >= 0;) {
      File f2 = sa[i];
      if (accept(f2)) {
        String name = "" + f2;
        String out_1 = name.substring(prefix.length(), name.length() - 6);
        String out_2 = StringUtil.replace(out_1, "\\", ".");

        Log.log("" + out_2);
      } else {
        if (f2.isDirectory()) {
          recursivelyListClasses("" + f2, prefix);
        }
      }
    }
  }

  static boolean accept(File path) {
    //Log.log("path.tlength()" + path.length());
    String path_name = path.toString();
    //Log.log("path_name:" + path_name);

    if (path_name.length() < 7) {
      return false;
    }

    return (".class".equals(path_name.substring((int) path_name.length() - 6)));
  }

  protected static void main(String[] args) {
    args = args;

    //Rockz.main(null);
    listClasses();
  }

  static void setBase(String base) {
    ListClasses.base = base;
  }

  static String getBase() {
    return base;
  }
}
