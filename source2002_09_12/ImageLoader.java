/**
 * A class containing static methods which load images -
 * including images within jar files...
 * <p>
 * @author Tim Tyler
 * @version 1.11
 **/

import java.applet.Applet;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.fonteditor.graphics.ImageWrapper;

public class ImageLoader {
  private static ImageLoader image_loader;
  private static Toolkit toolkit;

  /**
   * ImageLoader, constructor.
   * <p>
   * The constructor is private.
   * There should not be any instances of this
   * class created outside the private one used
   * in the class.
   **/
  private ImageLoader() {
  }

  /**
   * static initialization.
   * <p>
   * Create an instance of this class for later use...
   **/
  static {
    image_loader = new ImageLoader();
  }

  /**
   * Get an image.
   * <p>
   * Loads a specified image, either from the currect directory,
   * Or from inside the relevant jar file, whichever is appropriate.
   **/
  protected static ImageWrapper getImage(String name) {
    InputStream in;
    ImageWrapper image;
    //boolean ispng;

    //if (!Rockz.directory_separator.equals("/")) {
    //name = StringParser.searchAndReplace(name, "/", Rockz.directory_separator);
    //}

    // Log.log("Loading: " + name + ".");

    byte[] byte_array;
    int byte_array_size;

    toolkit = Toolkit.getDefaultToolkit();
    //ispng = (name.indexOf(".png") > 0);

    try {
      //Log.log("Starting to load: " + name);

      in = image_loader.getClass().getResourceAsStream(name);

      //in = ImageLoader.class.getResourceAsStream(name);
      if (in == null) {
        throw new RuntimeException("Problem locating image file: " + name);
      }

      // Thanks to Karl Schmidt for the followig code...
      ByteArrayOutputStream bytes;

      bytes = new ByteArrayOutputStream();
      byte_array_size = 1024; // choose a size...
      byte_array = new byte[byte_array_size];

      int rb;

      while ((rb = in.read(byte_array, 0, byte_array_size)) > -1) {
        bytes.write(byte_array, 0, rb);
      }

      bytes.close();

      byte_array = bytes.toByteArray();

      image = new ImageWrapper(toolkit.createImage(byte_array)); // JPEG...

      // !?!?!?!?
      // do { } while(!toolkit.prepareImage(image.i, -1, -1, null));
      // Log.log("JPEG->NULL?:" + image.i);

      in.close();

      //Log.log("Finished loading: " + name + ".");

      return image;
    } catch (IOException e) {
      e.printStackTrace();
    }

    //Log.log("BAD EXIT: "+ name);

    return null;
  }

  /** 
   * If you want to wait for your images to load, you should
   * seriously consider using the ImageLoadingManager class...
   */
  protected static ImageWrapper getImageNow(String name) {
    ImageWrapper temp_image = getImage(name);
    do {
    } while (!toolkit.prepareImage(temp_image.getImage(), -1, -1, null));
    // Log.log("Finished loading: " + name + ".");
    return temp_image;
  }

  /** 
   * If you want to wait for your images to load, you should
   * seriously consider using the ImageLoadingManager class...
   */
  protected static ImageWrapper getRelativeImageNow(Applet a, String name) {
    URL url = null;
    try {
      url = new URL(a.getDocumentBase(), name);
    } catch (MalformedURLException e) {
      e = e;
    }

    ImageWrapper temp_image = new ImageWrapper(a.getImage(url));
    do {
    } while (!toolkit.prepareImage(temp_image.getImage(), -1, -1, null));
    // Log.log("Finished loading: " + name + ".");
    return temp_image;
  }

  /**
    * Returns the Toolkit used...
    * <p>
    * Allow access to the toolkit used (i.e. access from outside this class)...
    **/
  protected static Toolkit getToolkit() {
    return toolkit;
  }
}
