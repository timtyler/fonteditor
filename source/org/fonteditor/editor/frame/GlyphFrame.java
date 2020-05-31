package org.fonteditor.editor.frame;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.fonteditor.utilities.general.Forget;

/**
  * A frame containing a single editable Glyph...
  */

public class GlyphFrame extends Frame {
  private Panel panel;
  private GlyphFrame myself;

  public GlyphFrame(String title, Panel app, int width, int height) {
    super(title);
    panel = app;
    myself = this;
    add("Center", panel);
    setSize(new Dimension(width, height));
    //applet.init();
    //applet.start();
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        Forget.about(e);
        getMyself().hide();
        getMyself().dispose();
      }

      public void windowDeiconified(WindowEvent e) {
        Forget.about(e);
      }

      public void windowIconified(WindowEvent e) {
        Forget.about(e);
      }
    });
    validate();
    show();
  }

  GlyphFrame getMyself() {
    return myself;
  }
  //  public void actionPerformed(ActionEvent e) {
  //     e = e;
  //    /*
  //    String arg = e.getActionCommand();
  //
  //    if (arg == QUIT) {
  //    applet.stop();
  //    applet = null;
  //    System.exit(0);
  //    }
  //    */
  //  }
}