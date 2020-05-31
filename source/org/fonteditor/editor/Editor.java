package org.fonteditor.editor;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;

import org.fonteditor.FE;
import org.fonteditor.FEConstants;
import org.fonteditor.demonstration.DemoFrame;
import org.fonteditor.demonstration.DemoPanel;
import org.fonteditor.editor.grid.CharacterGrid;
import org.fonteditor.editor.grid.PanelQualityChoice;
import org.fonteditor.font.FontSave;
import org.fonteditor.gui.PanelFontSelector;
import org.fonteditor.options.pen.Pen;
import org.fonteditor.utilities.callback.CallBack;
import org.fonteditor.utilities.general.Forget;
import org.fonteditor.utilities.log.Log;

/**
  * Runs the main "Font Editor" component...
  */

public class Editor extends Panel implements FEConstants {
  private CharacterGrid character_grid;
  private Button button_save;
  private Button button_resize;
  private Button button_fix;
  private Button button_demo;
  private Frame frame;
  private PanelFontSelector panel_font_selector;

  public void setFrame(Frame frame) {
    this.frame = frame;
  }
  public Editor() {
    ActionListener action_listner = new FEActionListener();

    setLayout(new BorderLayout());
    Panel p = new Panel();
    int add = FE.isApplet() ? 0 : 1;

    p.setLayout(new GridLayout(add + 2, 0, 0, 0));
    Panel p2 = new Panel();

    p2.setBackground(new Color(0xE8E8E8));
    button_resize = new Button("Resize");
    button_resize.addActionListener(action_listner);
    button_save = new Button("Save");
    button_save.addActionListener(action_listner);

    button_fix = new Button("Fix");
    button_fix.addActionListener(getActionListnerFix());

    button_demo = new Button("Demo");
    button_demo.addActionListener(action_listner);

    character_grid = new CharacterGrid();
    
    p2.add(button_resize);
    p2.add(button_demo);
    p2.add(button_save);
    
    if (FEConstants.DEVELOPMENT_VERSION) {
      p2.add(button_fix);
    }

    CallBack callback_font = new CallBack() {
      public void callback(Object o) {
        character_grid.chooseFont((String) o);
      }
    };

    CallBack callback_weight = new CallBack() {
      public void callback(Object o) {
        character_grid.setWeight(Integer.parseInt((String) o));
      }
    };

    CallBack callback_outline = new CallBack() {
      public void callback(Object o) {
        character_grid.setWeight(((Checkbox) o).getState() ? panel_font_selector.getSelectorWeight().getWeight() : 0);
      }
    };

    CallBack callback_filled = new CallBack() {
      public void callback(Object o) {
        character_grid.setFilled(((Checkbox) o).getState());
      }
    };

    CallBack callback_pen = new CallBack() {
      public void callback(Object o) {
        character_grid.setPen((Pen) o);
      }
    };

    CallBack callback_choice = new CallBack() {
      public void callback(Object o) {
        Forget.about(o);
        character_grid.clearCache();
        character_grid.setTargets();
        character_grid.resetQualities();
        character_grid.updateAll();
      }
    };

    panel_font_selector = new PanelFontSelector(callback_font, callback_weight, callback_outline, callback_filled, callback_pen, JAVA_2);

    character_grid.setFsl(panel_font_selector);
    p.add(panel_font_selector);

    PanelQualityChoice pqc = new PanelQualityChoice(callback_choice);

    p.add(pqc);

    character_grid.setPQC(pqc);

    character_grid.setTargets();

    if (!FE.isApplet()) {
      p.add(p2);
    }

    add(p, BorderLayout.SOUTH);
    add(character_grid, BorderLayout.CENTER);
    validate();
    repaint();
  }

  void newFrame(String s) {
    DemoPanel panel = new DemoPanel();
    DemoFrame frame = new DemoFrame(s, (Panel) panel, 880, 200);

    frame.show();
  }

  public Frame getFrame() {
    return frame;
  }

  Button getButtonSave() {
    return button_save;
  }

  Button getButtonResize() {
    return button_resize;
  }

  Button getButtonDemo() {
    return button_demo;
  }

  CharacterGrid getCharacterGrid() {
    return character_grid;
  }

  ActionListener getActionListnerFix() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Forget.about(e);
        character_grid.fix();
      }
    };
  }

  /** 
   * ActionListener class
   */
  class FEActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();

      if (source == getButtonSave()) {
        Log.log("getFrame():" + getFrame());
        FileDialog fd = new FileDialog(getFrame(), "Save objects as", FileDialog.SAVE);

        fd.setFilenameFilter(new FilenameFilter() {
          public boolean accept(File dir, String name) {
            Forget.about(dir);
            //Log.log("save:" + name);
            return !(name.endsWith(".cff"));
          }
        });

        fd.show();
        String file_name = fd.getFile();
        String dir_name = fd.getDirectory();
        String path_name = dir_name + file_name;

        if (!file_name.equals("")) {
          Log.log("In save!" + path_name);
          FontSave.save(getCharacterGrid().getFEFont(), path_name);
          Log.log("Out save!" + path_name);
        }
      }

      // what does this do?

      if (source == getButtonResize()) {
        getCharacterGrid().getFEFont().scaleRipped();
        getCharacterGrid().removeFontFromCache();
      }

      if (source == getButtonDemo()) {
        newFrame("Demonstration");
      }
    }
  }

  /** 
   * KeyListener class for key presses
   */
  class BKeyListener extends KeyAdapter {
    // The user pressed a key corresponding to an ASCII value.
    public void keyTyped(KeyEvent e) {
      Forget.about(e);
    }
  }
}
