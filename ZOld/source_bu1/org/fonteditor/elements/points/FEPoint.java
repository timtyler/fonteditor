package org.fonteditor.elements.points;

import java.awt.Point;

import org.fonteditor.instructions.InstructionStream;
import org.fonteditor.utilities.general.Utils;

public class FEPoint extends Point {
  private int instruction_pointer; // for use keeping track of things in the editor...

  // int index; //  for use keeping track of things in overall point list...?
  public FEPoint(int x, int y) {
    this.x = x;
    this.y = y;
  }

  //  // copy contructor - kill this...
  //  public FEPoint(FEPoint p) {
  //    this.x = p.x;
  //    this.y = p.y;
  //    this.selected = p.selected;
  //    this.instruction_pointer = p.instruction_pointer;
  //  }
  
  public FEPoint(InstructionStream is) {
    instruction_pointer = is.getInstructionPointer();
    this.x = -1;
    this.y = -1;
  }

  public int squaredDistanceFrom(Point p) {
    int dx = (p.x - x) >> 1;
    int dy = (p.y - y) >> 1;

    return dx * dx + dy * dy;
  }

  public int quickDistanceFrom(Point p) {
    return (Utils.abs(p.x - x) + Utils.abs(p.y - y)) >> 1;
  }

  public boolean equals(Object o) {
    FEPoint fep = (FEPoint) o;

    return ((fep.x == x) && (fep.y == y));
  }

  public int hashCode() {
    return super.hashCode();
  }

  public void setInstructionPointer(int instruction_pointer) {
    this.instruction_pointer = instruction_pointer;
  }

  public int getInstructionPointer() {
    return instruction_pointer;
  }
}
