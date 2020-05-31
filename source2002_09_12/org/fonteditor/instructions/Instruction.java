package org.fonteditor.instructions;

import org.fonteditor.utilities.log.Log;

/**
 * Represents a single instruction
 */

public class Instruction {
  private int number;
  private String name;

  Instruction(int n, String d) {
    number = n;
    name = d;
  }

  void copy(InstructionStream is_in, InstructionStream is_out) {
    is_in = is_in;
    is_out = is_out;
    Log.log("Raw instruction error (copy)");
  }

  void execute(InstructionStream is) {
    is = is;
    Log.log("Raw instruction error (execute)");
  }

  public void save(InstructionStream is) {
    InstructionArray.savePoints(is, numberOfCoordinates());
  }

  void translate(InstructionStream is_in, int dx, int dy) {
    is_in = is_in;
    dx = dx;
    dy = dy;
  }

  public int numberOfCoordinates() {
    return 0;
  }

  public String toString() {
    return name;
  }

  public void logExecution() {
    if (InstructionArray.needsLogStatements()) {
      Log.log("Execute instruction: " + toString());
    }
  }
}
