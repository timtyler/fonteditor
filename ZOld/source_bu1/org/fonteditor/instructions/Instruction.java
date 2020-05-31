package org.fonteditor.instructions;

import org.fonteditor.utilities.log.Log;

/**
 * Represents a single instruction
 */

public class Instruction { //implements InstructionConstants {  
//   private final static int INSTRUCTION_MASK = 0x00;
//  private final static int ERROR = INSTRUCTION_MASK | 0;
//  private final static int GLYPH_NUMBER = INSTRUCTION_MASK | 1;
//  private final static int GLYPH_NEXT = INSTRUCTION_MASK | 2;
//  private final static int STRAIGHT_LINE = INSTRUCTION_MASK | 3;
//  private final static int QUADRATIC_BEZIER = INSTRUCTION_MASK | 4;
//  private final static int CUBIC_BEZIER = INSTRUCTION_MASK | 5;
//  private final static int END_GLYPH = INSTRUCTION_MASK | 6;
//  private final static int END_FONT = INSTRUCTION_MASK | 7;
//  private final static int CLOSE_PATH = INSTRUCTION_MASK | 8;
//  private final static int MOVE_TO = INSTRUCTION_MASK | 9;
//  private final static int MAX_NUMBER = 12;

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

  public String getName() {
    return name;
  }

  public void logExecution() {
    if (InstructionArray.needsLogStatements()) {
      Log.log("Execute instruction: " + getName());
    }
  }
}
