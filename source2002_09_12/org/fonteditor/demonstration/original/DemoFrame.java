package org.fonteditor.demonstration.original;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
  * Type-and-see demonstration application... 
  * Outer frame...
  */

public class DemoFrame extends Frame {
  private Panel panel;
  private DemoFrame myself;

  public DemoFrame(String title, Panel app, int width, int height) {
    super(title);
    panel = app;
    myself = this;
    add("Center", panel);
    setSize(new Dimension(width, height));
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        e = e;
        getMyself().hide();
        getMyself().dispose();
      }

      public void windowDeiconified(WindowEvent e) {
        e = e;
      }

      public void windowIconified(WindowEvent e) {
        e = e;
      }
    });

    validate();
    show();
  }

  DemoFrame getMyself() {
    return myself;
  }
}
