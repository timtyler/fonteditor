package org.fonteditor.gui;

import java.awt.Choice;
import java.awt.event.ItemListener;

import java.util.Enumeration;
import java.util.Vector;

public class TTChoice {
  private static final int NOT_FOUND = -99;
  private Vector vector;
  private Choice choice;

  public TTChoice(ItemListener il) {
    choice = new Choice();
    choice.addItemListener(il);
    vector = new Vector();
  }

  public void add(String s, int n) {
    choice.addItem(s);
    vector.addElement(new TTNumStr(n, s));
  }

  private void removeAll() {
    choice.removeAll();
    vector.removeAllElements();
  }

  public int stringToNumber(String s) {
    Enumeration enumeration;
    TTNumStr temp_pair;

    enumeration = vector.elements();
    while (enumeration.hasMoreElements()) {
      temp_pair = (TTNumStr) (enumeration.nextElement());
      if (temp_pair.getString() == s) {
        return temp_pair.getNumber();
      }
    }
    return NOT_FOUND; // -99 = not found...
  }

  public Choice getChoice() {
    return choice;
  }
}

//  private String numberToString(int j) {
//    Enumeration enumeration;
//    TTNumStr temp_pair;
//
//    enumeration = vector.elements();
//
//    while (enumeration.hasMoreElements()) {
//      temp_pair = (TTNumStr) (enumeration.nextElement());
//
//      if (temp_pair.getNumber() == j) {
//        return temp_pair.getString();
//      }
//    }
//
//    return ""; // null marker = not found...
//  }
//
//  public void setChoice(Choice choice) {
//    this.choice = choice;
//  }
