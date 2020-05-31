package org.fonteditor;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Frame;

import org.fonteditor.demonstration.DemoPanel;
import org.fonteditor.editor.Editor;
import org.fonteditor.utilities.applet.TTAppletFrame;
import org.fonteditor.utilities.general.Forget;

/**
  * The main class...
  * This starts up the Font editor application...
  */

public class FE extends Applet implements FEConstants {
  private static boolean applet = true;
  private Frame frame;
  private Editor editor;
  private DemoPanel demo_panel;

  public void init() {
    setUpUI();
  }

  public void setUpUI() {
    setLayout(new BorderLayout());
    if (EDITOR) {
      editor = new Editor();
      add(editor, BorderLayout.CENTER);
    } else {
      demo_panel = new DemoPanel();
      add(demo_panel, BorderLayout.CENTER);
    }

    validate();
    repaint();
  }

  public static boolean isApplet() {
    return applet;
  }

  private static void enter() {
    FE applet = new FE();

    applet.frame = new TTAppletFrame("FE", (Applet) applet, 500, 500);

    if (EDITOR) {
      applet.editor.setFrame(applet.frame);
    }

    applet.frame.show();
  }

  public static void main(String[] args) {
    Forget.about(args);
    applet = false;
    FE.enter();
  }
}
