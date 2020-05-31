package org.fonteditor;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Frame;

import org.fonteditor.editor.Editor;
import org.fonteditor.utilities.applet.TTAppletFrame;

/**
  * The main class...
  * This starts up the Font editor application...
  */

public class FE extends Applet implements FEConstants {
  private static boolean applet = true;
  private Frame frame;
  private Editor editor;

  public void init() {
    setUpUI();
  }

  public void setUpUI() {
    setLayout(new BorderLayout());
     editor = new Editor();

    add(editor, BorderLayout.CENTER);
    validate();
    repaint();
  }

  public static boolean isApplet() {
    return applet;
  }

  private static void enter() {
    FE applet = new FE();

    applet.frame = new TTAppletFrame("FE", (Applet) applet, 500, 500);
    applet.editor.setFrame(applet.frame);
    applet.frame.show();
  }

  public static void main(String[] args) {
    args = args;
    applet = false;
    FE.enter();
  }
}
