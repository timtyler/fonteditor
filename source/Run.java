import java.applet.Applet;
import java.awt.BorderLayout;

import org.fonteditor.FE;
//import org.fonteditor.utilities.general.ListClasses;

/**
 * Runs everything - works as an applet - or as an application...
 */

public final class Run extends Applet {
  public void init() {
   setLayout(new BorderLayout());

    FE fe = new FE();
    fe.init();
    add(fe, BorderLayout.CENTER);

    validate();
    //show();
    repaint();
  }

  public static void main(String[] args) {
    //Log.log("-------Run-------");
    //ListClasses.main(args);
    
    //FontTest.main(args);
    FE.main(args);
  }
}
