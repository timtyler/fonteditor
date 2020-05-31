package org.fonteditor.utilities.applet;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.fonteditor.utilities.general.Forget;

public class TTAppletFrame extends Frame implements ActionListener {
  private static Applet applet;
  private static TTAppletFrame myself;

  public TTAppletFrame(String title, Applet app, int width, int height) {
    super(title);
    applet = app;
    myself = this;

    //    MenuBar menubar = new MenuBar();
    //    Menu file = new Menu("File",true);
    //        
    //    menubar.add(file);
    //        
    //    file.add(QUIT);
    //        
    //    setMenuBar(menubar);
    //        
    //    file.addActionListener(this);

    add("Center", applet);
    setSize(new Dimension(width, height));
    show();
    applet.init();
    applet.start();
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        Forget.about(e);
        System.exit(0);
      }

      public void windowDeiconified(WindowEvent e) {
        Forget.about(e);
      }

      public void windowIconified(WindowEvent e) {
        Forget.about(e);
      }
    });
  }

  public void actionPerformed(ActionEvent e) {
    Forget.about(e);
  }
}