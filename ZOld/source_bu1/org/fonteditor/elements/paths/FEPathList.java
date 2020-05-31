package org.fonteditor.elements.paths;

import java.awt.Point;

import org.fonteditor.editor.frame.Selection;
import org.fonteditor.elements.points.FEPoint;

import org.fonteditor.options.coords.Coords;

import org.fonteditor.utilities.callback.CallBack;
import org.fonteditor.utilities.callback.CallBackWithReturn;
import org.fonteditor.utilities.log.Log;

/**
 * A list of paths.
 * Together these part compose a glyph.
 */

public class FEPathList {
  private FEPath[] new_array;
  private static final int INCREMENT = 8;
  private int number = 0;
  private int number_of_paths = 0;
  private FEPath[] paths = new FEPath[number_of_paths];

  public boolean isAlreadySelected(Selection selection, Point p, Coords c) {
    for (int i = number; --i >= 0;) {
      if (paths[i].isInsidePolygon(p, c)) {
        return selection.isPathSelected(i);
      }
    }

    return false;
  }

  public int numberSelected(Selection selection) {
    int count = 0;

    for (int i = 0; i < number; i++) {
      if (selection.isPathSelected(i)) {
        count++;
      }
    }
    return count;
  }

  public void select(Selection selection, Point p, Coords c) {
    for (int i = number; --i >= 0;) {
      if (paths[i].isInsidePolygon(p, c)) {
        select(selection, number, i);

        break;
      }
    }
  }

  public void select(Selection selection, int max, int i) {
    selection.ensurePaths(max);
    selection.selectPath(i);
  }

  public void deselect(Selection selection) {
    for (int i = number; --i >= 0;) {
      selection.deselectPath(i);
    }
  }

  public void selectInBox(Selection selection, int min_x, int min_y, int max_x, int max_y, Coords c) {
    for (int i = number; --i >= 0;) {
      if (paths[i].isInsideBox(min_x, min_y, max_x, max_y, c)) {
        selection.selectPath(i);
      }
    }
  }

  public FEPath add(FEPath fep) {
    if (number >= number_of_paths) {
      makeMore();
    }
    paths[number] = fep;
    return paths[number++];
  }

  public void executeOnEachPath(ExecutorOnFEPath e, Object o) {
    for (int i = 0; i < number; i++) {
      e.execute(paths[i], o);
    }
  }

  public void executeOnEachPath(CallBack e) {
    for (int i = 0; i < number; i++) {
      e.callback(paths[i]);
    }
  }

  public Object executeOnEachPath(CallBackWithReturn e) {
    for (int i = 0; i < number; i++) {
      Object o = e.callback(paths[i]);
      if (o != null) {
        return o;
      }
    }

    return null;
  }

  private void makeMore() {
    new_array = new FEPath[number_of_paths + INCREMENT];
    System.arraycopy(paths, 0, new_array, 0, number_of_paths);
    paths = new_array;
    number_of_paths += INCREMENT;
  }

  public FEPath getPath(FEPoint p) {
    for (int i = number; --i >= 0;) {
      FEPath fep = paths[i];

      if (fep.contains(p)) {
        return fep;
      }
    }
    return null;
  }

  public void dump() {
    Log.log("Number of paths:" + number);
    for (int i = number; --i >= 0;) {
      FEPath fep = paths[i];

      fep.dump();
    }
    Log.log("--- END ---");
  }

  public int getNumber() {
    return number;
  }

  public void setPaths(FEPath[] paths) {
    this.paths = paths;
  }

  public FEPath getPath(int i) {
    return paths[i];
  }
}
