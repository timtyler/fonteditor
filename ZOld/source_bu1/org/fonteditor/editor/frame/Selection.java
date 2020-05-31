package org.fonteditor.editor.frame;

import org.fonteditor.utilities.log.Log;

/**
  * Keeps track of which elements are selected...
  */

public class Selection {
  private boolean[] points = null;
  private boolean[] paths = null;
  // private boolean[] sliders = null;

  //  private Hashtable points_hashtable = new Hashtable();
  //  private Hashtable paths_hashtable = new Hashtable();
  //  private Hashtable sliders_hashtable = new Hashtable();

  // ---- Points ----

  public void ensurePoints(int number) {
    if ((points == null) || (points.length != number)) {
      points = new boolean[number];
    }
  }

  public boolean isPointSelected(int index) {
    return (points == null) ? false : points[index];
  }

  public void selectPoint(int index) {
    points[index] = true;
  }

  public void deselectPoint(int index) {
    points[index] = false;
  }

//  public void selectAllPoints() {
//    for (int index = points.length; --index >= 0;) {
//      selectPoint(index);
//    }
//  }

  public void deselectAllPoints() {
    if (points != null) {
      for (int index = points.length; --index >= 0;) {
        deselectPoint(index);
      }
    }
  }

  public int getNumberofPointsSelected() {
    if (points == null) {
      return 0;
    }

    int count = 0;
    for (int index = points.length; --index >= 0;) {
      if (points[index] == true) {
        count++;
      }
    }

    return count;
  }

  // ---- Paths ----

  public void ensurePaths(int number) {
    if ((paths == null) || (paths.length != number)) {
      paths = new boolean[number];
    }
  }

  public boolean isPathSelected(int index) {
    return (paths == null) ? false : paths[index];
  }

  public void selectPath(int index) {
    paths[index] = true;
  }

  public void deselectPath(int index) {
    paths[index] = false;
  }

//  public void selectAllPaths() {
//    for (int index = paths.length; --index >= 0;) {
//      selectPath(index);
//    }
//  }

  public void deselectAllPaths() {
    if (paths != null) {
      for (int index = paths.length; --index >= 0;) {
        deselectPath(index);
      }
    }
  }

  public void dump() {
    for (int i = 0; i < points.length; i++) {
      Log.log("Selected:" + i + " - " + isPointSelected(i));
    }
  }

//  public int getNumberofPathsSelected() {
//    if (paths == null) {
//      return 0;
//    }
//
//    int count = 0;
//    for (int index = paths.length; --index >= 0;) {
//      if (paths[index] == true) {
//        count++;
//      }
//    }
//
//    return count;
//  }
}
