package org.fonteditor.editor.frame;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.fonteditor.FE;
import org.fonteditor.FEConstants;
import org.fonteditor.editor.Fixer;
import org.fonteditor.editor.grid.CharacterGrid;
import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.font.FEGlyph;
import org.fonteditor.instructions.InstructionConstants;
import org.fonteditor.instructions.InstructionStream;
import org.fonteditor.options.display.GlyphDisplayOptionsEditor;
import org.fonteditor.springs.SpringConstants;
import org.fonteditor.utilities.callback.CallBack;
import org.fonteditor.utilities.general.Pair;

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
  private Button button_set_x;
  private Button button_set_y;
  private Button button_x_push;
  private Button button_x_pop;
  private Button button_y_push;
  private Button button_y_pop;
  private Button button_fix;
  private Button button_del;

  private TextField textfield_x_rw;
  private TextField textfield_y_rw;

  private TextField textfield_x_r;
  private TextField textfield_y_r;

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

    inner_panel = new GlyphInnerPanel(s, character_grid, dfdo, this);
    init(dfdo);
    refresh();
  }

  private void init(DefaultFrameDisplayOptions dfdo) {
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
    Panel p1e = new Panel();
    Panel p1f = new Panel();

    p1a.setBackground(new Color(0xD8D8D8));
    p1b.setBackground(new Color(0xE8E8E8));
    p1c.setBackground(new Color(0xD8D8D8));
    p1e.setBackground(new Color(0xE8E8E8));
    p1f.setBackground(new Color(0xD8D8D8));
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

    button_del = new Button("Del");
    button_del.addActionListener(getActionListenerForButtonDel());
    p1d.add(button_del);

    if (FEConstants.DEVELOPMENT_VERSION) {
      button_fix = new Button("Fix");
      button_fix.addActionListener(getActionListenerForButtonFix());
      p1d.add(button_fix);
    }

    if (FEConstants.DEVELOPMENT_VERSION) {
      button_dump = new Button("Dump");
      button_dump.addActionListener(action_listner);
      p1d.add(button_dump);
    }

    textfield_x_rw = new TextField("000");
    p1e.add(new Label("X:"));
    p1e.add(textfield_x_rw);

    button_set_x = new Button("Set");
    button_set_x.addActionListener(getActionListenerForButtonSetX());
    p1e.add(button_set_x);

    textfield_y_rw = new TextField("000");
    p1e.add(new Label("Y:"));
    p1e.add(textfield_y_rw);

    button_set_y = new Button("Set");
    button_set_y.addActionListener(getActionListenerForButtonSetY());
    p1e.add(button_set_y);

    textfield_x_r = new TextField("000");
    textfield_x_r.setEditable(false);
    p1f.add(new Label("X:"));
    p1f.add(textfield_x_r);

    button_x_push = new Button("^");
    button_x_push.addActionListener(getActionListenerForButtonXPush());
    p1f.add(button_x_push);

    button_x_pop = new Button("v");
    button_x_pop.addActionListener(getActionListenerForButtonXPop());
    p1f.add(button_x_pop);

    textfield_y_r = new TextField("000");
    textfield_y_r.setEditable(false);
    p1f.add(new Label("Y:"));
    p1f.add(textfield_y_r);

    button_y_push = new Button("^");
    button_y_push.addActionListener(getActionListenerForButtonYPush());
    p1f.add(button_y_push);

    button_y_pop = new Button("v");
    button_y_pop.addActionListener(getActionListenerForButtonYPop());
    p1f.add(button_y_pop);

    p1.setLayout(new GridLayout(8, 1, 0, 0));
    choose_width = new ChooseWidth(this);
    p1.add(choose_width);
    p1.add(resolution_selector);

    p1.add(p1a);
    p1.add(p1b);
    p1.add(p1c);
    p1.add(p1e);
    p1.add(p1f);
    p1.add(p1d);

    setLayout(new BorderLayout());
    add(p1, BorderLayout.SOUTH);
    inner_panel.init();
    add(inner_panel, BorderLayout.CENTER);

    validate();
  }

  CallBack getCallBackX() {
    return new CallBack() {
      public void callback(Object o) {
        Pair objs_point_two = (Pair) o;
        FEPoint point = (FEPoint) objs_point_two.getFirst();
        Integer v = (Integer) objs_point_two.getSecond();

        FEGlyph glyph = inner_panel.getGlyph();
        InstructionStream is = glyph.getInstructionStream();

        is.setPoint(point, v.intValue(), point.getY());
      }
    };
  }

  CallBack getCallBackY() {
    return new CallBack() {
      public void callback(Object o) {
        Pair objs_point_two = (Pair) o;
        FEPoint point = (FEPoint) objs_point_two.getFirst();
        Integer v = (Integer) objs_point_two.getSecond();

        FEGlyph glyph = inner_panel.getGlyph();
        InstructionStream is = glyph.getInstructionStream();

        is.setPoint(point, point.getX(), v.intValue());
      }
    };
  }

  ActionListener getActionListenerForButtonSetX() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent action_event) {
        action_event = action_event;
        Integer value = new Integer(Integer.parseInt(textfield_x_rw.getText()));

        Selection selection = inner_panel.getSelection();
        FEGlyph glyph = inner_panel.getGlyph();

        selection.onEachSelectedPoint(glyph.getFEPointList(inner_panel.getGDOE()), getCallBackX(), value);
        refresh();
      }
    };
  }

  ActionListener getActionListenerForButtonSetY() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent action_event) {
        action_event = action_event;
        Integer value = new Integer(Integer.parseInt(textfield_y_rw.getText()));

        Selection selection = inner_panel.getSelection();
        FEGlyph glyph = inner_panel.getGlyph();

        selection.onEachSelectedPoint(glyph.getFEPointList(inner_panel.getGDOE()), getCallBackY(), value);

        refresh();
      }
    };
  }

  ActionListener getActionListenerForButtonXPush() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent action_event) {
        action_event = action_event;
        textfield_x_rw.setText(textfield_x_r.getText());
      }
    };
  }

  ActionListener getActionListenerForButtonXPop() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent action_event) {
        action_event = action_event;
        textfield_x_r.setText(textfield_x_rw.getText());
      }
    };
  }

  ActionListener getActionListenerForButtonYPush() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent action_event) {
        action_event = action_event;
        textfield_y_rw.setText(textfield_y_r.getText());
      }
    };
  }

  ActionListener getActionListenerForButtonYPop() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent action_event) {
        action_event = action_event;
        textfield_y_r.setText(textfield_y_rw.getText());
      }
    };
  }

  ActionListener getActionListenerForButtonFix() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent action_event) {
        action_event = action_event;

        FEGlyph glyph = inner_panel.getGlyph();
        
        Fixer fixer = new Fixer(glyph);
        fixer.fix(inner_panel.getGDOE());

        refresh();
      }
    };
  }

  ActionListener getActionListenerForButtonDel() {
    return new ActionListener() {
      public void actionPerformed(ActionEvent action_event) {
        action_event = action_event;

        Selection selection = inner_panel.getSelection();
        FEGlyph glyph = inner_panel.getGlyph();
        FEPointList fepl = glyph.getFEPointList(inner_panel.getGDOE());

        selection.onEachSelectedPoint(fepl, getCallBackDel());

        selection.deselectAllPoints();
        refresh();
      }
    };
  }

  CallBack getCallBackFix() {
    return new CallBack() {
      public void callback(Object o) {
        FEPoint point = (FEPoint) o;
        FEGlyph glyph = inner_panel.getGlyph();
        InstructionStream is = glyph.getInstructionStream();

        is.setPoint(point, point.getX(), point.getY());
      }
    };
  }

  CallBack getCallBackDel() {
    return new CallBack() {
      public void callback(Object o) {
        FEPoint point = (FEPoint) o;
        FEGlyph glyph = inner_panel.getGlyph();
        InstructionStream is = glyph.getInstructionStream();

        int index_of_point = point.getInstructionPointer();
        int index_of_curve = is.getIndexOfCurveContainingPoint(point);

        switch (is.getInstructionAt(index_of_curve)) {
          case InstructionConstants.STRAIGHT_LINE :
            is.deleteInstructionsAt(index_of_curve, 3);
            break;
          case InstructionConstants.QUADRATIC_BEZIER :
            is.setInstructionAt(index_of_curve, InstructionConstants.STRAIGHT_LINE);
            is.deleteInstructionsAt(index_of_point, 2);
            break;
          case InstructionConstants.CUBIC_BEZIER :
            is.setInstructionAt(index_of_curve, InstructionConstants.QUADRATIC_BEZIER);
            is.deleteInstructionsAt(index_of_point, 2);
            break;
        }
      }
    };
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

  public TextField getTextfieldXRW() {
    return textfield_x_rw;
  }

  public TextField getTextfieldYRW() {
    return textfield_y_rw;
  }

  class FEItemListener implements ItemListener {
    public void itemStateChanged(ItemEvent e) {
      if (e != null) {
        Object o = e.getSource();
        if (o == getCheckboxFill()) { //          getFEGP().setGDOE(GlyphDisplayOptionsEditor.setFill(
          getGlyphInnerPanel().getGDOE().setFill(((Checkbox) o).getState());
          getGlyphInnerPanel().repaint();
        }

        if (o == getCheckboxOutline()) { //          getFEGP().setGDOE(GlyphDisplayOptionsEditor.setOutline(getFEGP().getGDOE(), ((Checkbox) o).getState()));
          getGlyphInnerPanel().getGDOE().getPen().setWidth(((Checkbox) o).getState() ? choose_width.getWeight() : 0);
          getGlyphInnerPanel().getFEG().getInstructionStream().setRemakeFlag(true);
          getGlyphInnerPanel().repaint();
        }

        if (o == getCheckboxPoints()) { //getFEGP().setGDOE(GlyphDisplayOptionsEditor.setShowPoints(getFEGP().getGDOE(), ((Checkbox) o).getState()));
          getGlyphInnerPanel().getGDOE().setShowPoints(((Checkbox) o).getState());
          getGlyphInnerPanel().repaint();
        }

        if (o == getCheckboxSliders()) { //getFEGP().setGDOE(GlyphDisplayOptionsEditor.setShowSliders(getFEGP().getGDOE(), ((Checkbox) o).getState()));
          getGlyphInnerPanel().getGDOE().setShowSliders(((Checkbox) o).getState());
          getGlyphInnerPanel().repaint();
        }

        if (o == getCheckboxBorder()) { //getFEGP().setGDOE(GlyphDisplayOptionsEditor.setBorder(getFEGP().getGDOE(), ((Checkbox) o).getState()));
          getGlyphInnerPanel().getGDOE().setBorder(((Checkbox) o).getState());
          getGlyphInnerPanel().repaint();
        }

        if (o == getCheckboxGrid()) { //getFEGP().setGDOE(GlyphDisplayOptionsEditor.setShowGrid(getFEGP().getGDOE(), ((Checkbox) o).getState()));
          getGlyphInnerPanel().getGDOE().setShowGrid(((Checkbox) o).getState());
          getGlyphInnerPanel().repaint();
        }

        if (o == getCheckboxSprings()) { //          getFEGP().setGDOE(GlyphDisplayOptionsEditor.setShowSprings(getFEGP().getGDOE(), ((Checkbox) o).getState()));
          getGlyphInnerPanel().getGDOE().setShowSprings(((Checkbox) o).getState());
          getGlyphInnerPanel().repaint();
        }

        if (o == getCheckboxHint()) { //          getFEGP().setGDOE((GlyphDisplayOptionsEditor) GlyphDisplayOptionsEditor.setHint(getFEGP().getGDOE(), ((Checkbox) o).getState()));
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

      if (source == button_dump) { // show instructions...
        // fegp.getFeg().getInstructionStream().dump();
        // show paths...
        inner_panel.getFEG().getInstructionStream().getFEPathList().dump();
        inner_panel.getFEG().getInstructionStream().dump();
        inner_panel.getSelection().dump();
      }
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
