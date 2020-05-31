package org.fonteditor.options.pen;

import java.awt.Graphics;
import java.awt.Point;

import org.fonteditor.graphics.WideLine;
import org.fonteditor.options.coords.Coords;

public class PenRound extends Pen {
  public PenRound(int width) {
    setWidth(width);
  }


  public void drawStroke(Graphics g, Point from, Point to, Coords c) {
    WideLine.renderRound(g, from, to, getWidth(), c);
  }
  
  public void quickDrawStroke(Graphics g, Point from, Point to, Coords c) {
    WideLine.renderRound(g, from, to, getWidth(), c);
  }

    public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof PenRound)) {
      return false;
    }

    PenRound pen = (PenRound) o;

    return (pen.getWidth() == getWidth());
  }
}