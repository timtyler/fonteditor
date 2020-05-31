package org.fonteditor.graphics;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.fonteditor.options.coords.Coords;
import org.fonteditor.options.pen.Pen;
public class WideLine {

  // dodgy approach... ;-)
  public static void renderUneven(Graphics g, Point from, Point to, int width, Coords c) {
    int x1 = from.x;
    int y1 = from.y;
    int x2 = to.x;
    int y2 = to.y;
    int dx = (x2 - x1);
    int dy = (y2 - y1);
    int mag = (int) Math.sqrt(((dx * dx) >>> 2) + ((dy * dy) >>> 2));

    mag = mag << 1;
    if (mag < 1) {
      mag = 1;
    }

    int rav_x = (dy * width) / mag;
    int rav_y = (dx * width) / mag;
    Polygon temp_polygon = new Polygon();

    temp_polygon.addPoint(c.scaleX(x1 + rav_x), c.scaleY(y1 - rav_y));
    temp_polygon.addPoint(c.scaleX(x1 - rav_x), c.scaleY(y1 + rav_y));
    temp_polygon.addPoint(c.scaleX(x2 - rav_x), c.scaleY(y2 + rav_y));
    temp_polygon.addPoint(c.scaleX(x2 + rav_x), c.scaleY(y2 - rav_y));
    g.fillPolygon(temp_polygon);
  }

  public static void renderSquare(Graphics g, Point from, Point to, int width, Coords c) {
    int x1 = from.x;
    int x2 = to.x;
    int dx = (x2 - x1);

    if (dx < 0) {
      renderSquare(g, to, from, width, c);
    }

    int y1 = from.y;
    int y2 = to.y;
    int dy = (y2 - y1);

    Polygon temp_polygon = new Polygon();

    if (dy > 0) {
      /*     45
       *    /  6
       * 3  /
       * 21
       */
      temp_polygon.addPoint(c.scaleX(x1 + width), c.scaleY(y1 - width));
      temp_polygon.addPoint(c.scaleX(x1 - width), c.scaleY(y1 - width));
      temp_polygon.addPoint(c.scaleX(x1 - width), c.scaleY(y1 + width));
      temp_polygon.addPoint(c.scaleX(x2 - width), c.scaleY(y2 + width));
      temp_polygon.addPoint(c.scaleX(x2 + width), c.scaleY(y2 + width));
      temp_polygon.addPoint(c.scaleX(x2 + width), c.scaleY(y2 - width));
    } else {
      /* 23 
       * 1  \
       *    \ 4
       *     56
       */
      temp_polygon.addPoint(c.scaleX(x1 - width), c.scaleY(y1 - width));
      temp_polygon.addPoint(c.scaleX(x1 - width), c.scaleY(y1 + width));
      temp_polygon.addPoint(c.scaleX(x1 + width), c.scaleY(y1 + width));
      temp_polygon.addPoint(c.scaleX(x2 + width), c.scaleY(y2 + width));
      temp_polygon.addPoint(c.scaleX(x2 + width), c.scaleY(y2 - width));
      temp_polygon.addPoint(c.scaleX(x2 - width), c.scaleY(y2 - width));
    }
    
    g.fillPolygon(temp_polygon);
  }

  // takes internal points...
  public static void renderRound(Graphics g, Point from, Point to, int width, Coords c) {
    int x1 = from.x;
    int y1 = from.y;
    int x2 = to.x;
    int y2 = to.y;

    g.fillOval(c.scaleX(x1 - width), c.scaleY(y1 - width), c.scaleX(width + width), c.scaleY(width + width));
    g.fillOval(c.scaleX(x2 - width), c.scaleY(y2 - width), c.scaleX(width + width), c.scaleY(width + width));
    renderUneven(g, from, to, width, c);
  }

  public static void drawPolygon(Graphics g, Polygon p, Pen pen, Coords c) {
    Point from = new Point();
    Point to = new Point();

    for (int i = 1; i < p.npoints; i++) {
      from.x = p.xpoints[i - 1];
      from.y = p.ypoints[i - 1];
      to.x = p.xpoints[i];
      to.y = p.ypoints[i];
      pen.drawStroke(g, from, to, c);
    }

    from.x = p.xpoints[0];
    from.y = p.ypoints[0];
    to.x = p.xpoints[p.npoints - 1];
    to.y = p.ypoints[p.npoints - 1];
    pen.drawStroke(g, from, to, c);
  }

  public static void drawRectangle(Graphics g, Point p1, Point p2, int width, Coords c) {
    Point from = new Point();
    Point to = new Point();

    from.x = p1.x;
    from.y = p1.y;
    to.x = p1.x;
    to.y = p2.y;
    renderRound(g, from, to, width, c);

    from.x = p1.x;
    from.y = p2.y;
    to.x = p2.x;
    to.y = p2.y;
    renderRound(g, from, to, width, c);

    from.x = p2.x;
    from.y = p2.y;
    to.x = p2.x;
    to.y = p1.y;
    renderRound(g, from, to, width, c);

    from.x = p2.x;
    from.y = p1.y;
    to.x = p1.x;
    to.y = p1.y;
    renderRound(g, from, to, width, c);
  }
}

/*
public static void renderSquare(Graphics g, Point from, Point to, int width) {
   int x1 = from.x;
   int y1 = from.y;
   int x2 = to.x;
   int y2 = to.y;
   int dx = (x1 - x2);
   int dy = (y1 - y2);
  
   int mag = (int)Math.sqrt(((dx * dx)) + ((dy * dy)));
   // mag = mag ;
  
   int rav_x = -((dy * width) / mag);
   int rav_y = (dx * width) / mag;
  
   Polygon temp_polygon = new Polygon();
   temp_polygon.addPoint(x1 + rav_x, y1 + rav_y);
   temp_polygon.addPoint(x1 - rav_x, y1 - rav_y);
   temp_polygon.addPoint(x2 - rav_x, y2 - rav_y);
   temp_polygon.addPoint(x2 + rav_x, y2 + rav_y);
  
   g.fillPolygon(temp_polygon);
}
  
  
public static void renderRound(Graphics g, Point from, Point to, int width) {
   int x1 = from.x;
   int y1 = from.y;
   int x2 = to.x;
   int y2 = to.y;
  
   g.fillOval(x1 - width, y1 - width, width + width, width + width);
   g.fillOval(x2 - width, y2 - width, width + width, width + width);
  
   renderSquare(g, from, to, width);
}
*/
