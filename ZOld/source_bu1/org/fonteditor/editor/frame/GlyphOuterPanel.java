package org.fonteditor.editor.frame;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.fonteditor.FE;
import org.fonteditor.editor.grid.CharacterGrid;
import org.fonteditor.options.display.GlyphDisplayOptionsEditor;
import org.fonteditor.springs.SpringConstants;

/**
  * Outer Panel - containing a single editable Glyph...
  */

public class GlyphOuterPanel extends Panel implements SpringConstants {
  private FE fe;
  private Frame frame;
  private String edit_character;
  private int number;
  private CharacterGrid character_grid;
  private GlyphInnerPanel inner_panel;
  private Checkbox checkbox_outline;
  private Checkbox checkbox_fill;
  private Checkbox checkbox_points;
  private Checkbox checkbox_sliders;
  private Checkbox checkbox_grid;
  private Checkbox checkbox_hint;
  private Checkbox checkbox_border;
  private Checkbox checkbox_springs;
  private Checkbox checkbox_par;
  private Button button_makedefault;
  private Button button_dump;
  private ResolutionSelector resolution_selector;
  private ActionListener action_listner;
  
private ChooseWidth choose_width;

  public GlyphOuterPanel(String s, CharacterGrid character_grid, DefaultFrameDisplayOptions dfdo) {
    super();
    this.character_grid = character_grid;
    edit_character = s;
    this.fe = fe;
    number = (int) s.charAt(0);
    resolution_selector = new ResolutionSelector(this);

    inner_panel = new GlyphInnerPanel(s, character_grid, dfdo);
    init(dfdo);
    refresh();
  }

  private void init(DefaultFrameDisplayOptions dfdo) {
    //this.addMouseListener(new BMouseListener());
    //this.addMouseMotionListener(new BMouseMotionListener());
    //this.addKeyListener(new BKeyListener()); 
    
    setBackground(Color.white);
    ItemListener item_listner = new FEItemListener();
    ActionListener action_listner = new FEActionListener();

    validate();
    repaint();
    Panel p1 = new Panel();
    Panel p1a = new Panel();
    Panel p1b = new Panel();
    Panel p1c = new Panel();
    Panel p1d = new Panel();

    p1a.setBackground(new Color(0xD8D8D8));
    p1b.setBackground(new Color(0xE8E8E8));
    p1c.setBackground(new Color(0xD8D8D8));
    p1d.setBackground(new Color(0xE8E8E8));
    GlyphDisplayOptionsEditor gdo = dfdo.getGDOE();

    checkbox_fill = new Checkbox("Fill");
    checkbox_fill.addItemListener(item_listner);
    checkbox_fill.setState(gdo.isFill());
    p1a.add(checkbox_fill);
    
    checkbox_hint = new Checkbox("Hint");
    checkbox_hint.addItemListener(item_listner);
    checkbox_hint.setState(gdo.isHint());
    p1a.add(checkbox_hint);
    
    if (USE_SPRINGS) {
      checkbox_springs = new Checkbox("Springs");
      checkbox_springs.addItemListener(item_listner);
      checkbox_springs.setState(gdo.isShowSprings());

      p1a.add(checkbox_springs);
    }
    
    checkbox_points = new Checkbox("Points");
    checkbox_points.addItemListener(item_listner);
    checkbox_points.setState(gdo.isShowPoints());
    p1b.add(checkbox_points);
    
    checkbox_sliders = new Checkbox("Edges");
    checkbox_sliders.addItemListener(item_listner);
    checkbox_sliders.setState(gdo.isShowSliders());
    p1b.add(checkbox_sliders);
    
    checkbox_grid = new Checkbox("Grid");
    checkbox_grid.addItemListener(item_listner);
    checkbox_grid.setState(gdo.isShowGrid());
    p1b.add(checkbox_grid);
    
    checkbox_border = new Checkbox("Border");
    checkbox_border.addItemListener(item_listner);
    checkbox_border.setState(gdo.isBorder());
    p1b.add(checkbox_border);
    
    checkbox_par = new Checkbox("Preserve Aspect Ratio");
    checkbox_par.addItemListener(item_listner);
    checkbox_par.setState(dfdo.preservesAspectRatio());
    p1c.add(checkbox_par);
    
    button_makedefault = new Button("Make options default");
    button_makedefault.addActionListener(action_listner);
    p1d.add(button_makedefault);
    
    button_dump = new Button("Dump");
    button_dump.addActionListener(action_listner);
    p1d.add(button_dump);
    
    p1.setLayout(new GridLayout(6, 1, 0, 0));
    choose_width = new ChooseWidth(this);
    p1.add(choose_width);
    p1.add(resolution_selector);
    
    p1.add(p1a);
    p1.add(p1b);
    p1.add(p1c);
    p1.add(p1d);
    
    setLayout(new BorderLayout());
    add(p1, BorderLayout.SOUTH);
    inner_panel.init();
    add(inner_panel, BorderLayout.CENTER);
    
    validate();
  }

  void refresh() {
    inner_panel.refresh();
  }

  public void setFrame(Frame frame) {
    this.frame = frame;
  }

  public Frame getFrame() {
    return frame;
  }

  Checkbox getCheckboxOutline() {
    return checkbox_outline;
  }

  Checkbox getCheckboxFill() {
    return checkbox_fill;
  }

  public GlyphInnerPanel getGlyphInnerPanel() {
    return inner_panel;
  }

  Checkbox getCheckboxPoints() {
    return checkbox_points;
  }

  Checkbox getCheckboxSliders() {
    return checkbox_sliders;
  }

  Checkbox getCheckboxGrid() {
    return checkbox_grid;
  }

  Checkbox getCheckboxHint() {
    return checkbox_hint;
  }

  Checkbox getCheckboxBorder() {
    return checkbox_border;
  }

  Checkbox getCheckboxSprings() {
    return checkbox_springs;
  }

  Checkbox getCheckboxPar() {
    return checkbox_par;
  }

  CharacterGrid getCharacterGrid() {
    return character_grid;
  }

  class FEItemListener implements ItemListener {
    public void itemStateChanged(ItemEvent e) {
      if (e != null) {
        Object o = e.getSource();

        if (o == getCheckboxFill()) {
//          getFEGP().setGDOE(GlyphDisplayOptionsEditor.setFill(
          getGlyphInnerPanel().getGDOE().setFill(((Checkbox) o).getState());
          getGlyphInnerPanel().repaint();
        }

        if (o == getCheckboxOutline()) {
//          getFEGP().setGDOE(GlyphDisplayOptionsEditor.setOutline(getFEGP().getGDOE(), ((Checkbox) o).getState()));
          getGlyphInnerPanel().getGDOE().getPen().setWidth(((Checkbox) o).getState() ? choose_width.getWeight() : 0);
          getGlyphInnerPanel().getFEG().getInstructionStream().setRemakeFlag(true);
          getGlyphInnerPanel().repaint();
        }

        if (o == getCheckboxPoints()) {
          //getFEGP().setGDOE(GlyphDisplayOptionsEditor.setShowPoints(getFEGP().getGDOE(), ((Checkbox) o).getState()));
          getGlyphInnerPanel().getGDOE().setShowPoints(((Checkbox) o).getState());
          getGlyphInnerPanel().repaint();
        }

        if (o == getCheckboxSliders()) {
          //getFEGP().setGDOE(GlyphDisplayOptionsEditor.setShowSliders(getFEGP().getGDOE(), ((Checkbox) o).getState()));
          getGlyphInnerPanel().getGDOE().setShowSliders(((Checkbox) o).getState());
          getGlyphInnerPanel().repaint();
        }

        if (o == getCheckboxBorder()) {
          //getFEGP().setGDOE(GlyphDisplayOptionsEditor.setBorder(getFEGP().getGDOE(), ((Checkbox) o).getState()));
           getGlyphInnerPanel().getGDOE().setBorder(((Checkbox) o).getState());
         getGlyphInnerPanel().repaint();
        }

        if (o == getCheckboxGrid()) {
          //getFEGP().setGDOE(GlyphDisplayOptionsEditor.setShowGrid(getFEGP().getGDOE(), ((Checkbox) o).getState()));
          getGlyphInnerPanel().getGDOE().setShowGrid(((Checkbox) o).getState());
          getGlyphInnerPanel().repaint();
        }

        if (o == getCheckboxSprings()) {
//          getFEGP().setGDOE(GlyphDisplayOptionsEditor.setShowSprings(getFEGP().getGDOE(), ((Checkbox) o).getState()));
          getGlyphInnerPanel().getGDOE().setShowSprings(((Checkbox) o).getState());
         getGlyphInnerPanel().repaint();
        }

        if (o == getCheckboxHint()) {
//          getFEGP().setGDOE((GlyphDisplayOptionsEditor) GlyphDisplayOptionsEditor.setHint(getFEGP().getGDOE(), ((Checkbox) o).getState()));
          getGlyphInnerPanel().getGDOE().setHint(((Checkbox) o).getState());
         getGlyphInnerPanel().getFEG().getInstructionStream().setRemakeFlag(true);
          getGlyphInnerPanel().repaint();
        }

        if (o == getCheckboxPar()) {
          getGlyphInnerPanel().setPreserveAspectRatio(((Checkbox) o).getState());
          refresh();
        }
      }
    }
  }

  class FEActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      Object source = e.getSource();

      if (source == button_makedefault) {
        getCharacterGrid().makeDefault(getGlyphInnerPanel().getGDOE(), getFrame().getWidth(), getFrame().getHeight(), getGlyphInnerPanel().preservesAspectRatio());
      }

      if (source == button_dump) {
        // show instructions...
        // fegp.getFeg().getInstructionStream().dump();
        // show paths...
        inner_panel.getFEG().getInstructionStream().getFEPathList().dump();
        inner_panel.getSelection().dump();
      }
    }
  }

//  class BMouseListener extends MouseAdapter {
//    public void mousePressed(MouseEvent e) {
//      e = e;
//    }
//
//    public void mouseReleased(MouseEvent e) {
//      e = e;
//    }
//  }
//
//  class BMouseMotionListener extends MouseMotionAdapter {
//    public void mouseDragged(MouseEvent e) {
//      e = e;
//    }
//  }

//  // Inner class -- for key presses...
//  class BKeyListener extends KeyAdapter {
//    // The user pressed a key corresponding to an ASCII value...
//    public void keyTyped(KeyEvent e) {
//      char k = e.getKeyChar();
//
//      if (k == 'd') {
//        Log.log("Delete point");
//        GlyphOuterPanel.this.repaint();
//      } else if (k == 's') {
//        Log.log("Make sraight line");
//        GlyphOuterPanel.this.repaint();
//      } else if (k == 'b') {
//        Log.log("Make bezier");
//        GlyphOuterPanel.this.repaint();
//      } else if (k == 'a') {
//        Log.log("Add point");
//        GlyphOuterPanel.this.repaint();
//      }
//    }
//  }

}
