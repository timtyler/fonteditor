package org.fonteditor.font;

import java.io.DataOutputStream;
import java.io.FileOutputStream;

import org.fonteditor.instructions.InstructionConstants;
import org.fonteditor.instructions.InstructionStream;
import org.fonteditor.utilities.log.Log;

public class FontSave implements InstructionConstants {
  private static DataOutputStream out;
  private static FileOutputStream os;

  public static void save(FEFont font, String filename) {
    boolean first_glyph = true;

    try {
      os = new FileOutputStream(filename);
      out = new DataOutputStream(os);
      int min = font.getMin();
      int max = font.getMax();
      GlyphArray fega = font.getGlyphArray();

      Log.log("min:" + min + " - max:" + max);
      for (int i = min; i < max; i++) {
        if (first_glyph) {
          first_glyph = false;
          writeOut((byte) GLYPH_NUMBER);
          writeOut((byte) i);
        } else {
          writeOut((byte) GLYPH_NEXT);
        }
        
        FEGlyph glyph = fega.getGlyph(i);
        InstructionStream is = glyph.getInstructionStream();

        is.output();
      }
      
      writeOut((byte) END_FONT);
      out.flush();
      out.close();
      os.flush();
      os.close();
    } catch (Exception e) {
      Log.log("TT_Error (save):");
      e.printStackTrace();
    }
  }

  public static void writeOut(byte b) {
    try {
      out.writeByte(b);
    } catch (Exception e) {
      System.out.println("TT_Error (writeOut):");
      e.printStackTrace();
    }
  }

  static void setOut(DataOutputStream out) {
    FontSave.out = out;
  }

  static DataOutputStream getOut() {
    return out;
  }

  static void setOs(FileOutputStream os) {
    FontSave.os = os;
  }

  static FileOutputStream getOs() {
    return os;
  }
}
