package org.fonteditor.options.pen;

import java.awt.Graphics;
import java.awt.Point;

import org.fonteditor.graphics.WideLine;
import org.fonteditor.options.coords.Coords;

public class PenSquare extends Pen {
  public PenSquare(int width) {
    setWidth(width);
  }

  public void drawStroke(Graphics g, Point from, Point to, Coords c) {
    WideLine.renderSquare(g, from, to, getWidth(), c);
  }

  public void quickDrawStroke(Graphics g, Point from, Point to, Coords c) {
    WideLine.renderSquare(g, from, to, getWidth(), c);
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof PenSquare)) {
      return false;
    }

    PenSquare pen = (PenSquare) o;

    return (pen.getWidth() == getWidth());
  }
}
