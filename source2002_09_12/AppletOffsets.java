import java.applet.Applet;
import java.awt.Component;
import java.awt.Point;

import netscape.javascript.JSObject;
import org.fonteditor.utilities.log.Log;

public class AppletOffsets {
  private Component apref;
  private String apname;

  private static boolean is_ie;
  private static boolean is_nav;
  private static boolean is_sun;
  private static boolean is_sun_in_ie;

  private int v_offset;
  private int h_offset;

  private JSObject js_w_object;

  static {
    is_ie = false;
    is_nav = false;
    is_sun = false;
    is_sun_in_ie = false;

    String s = System.getProperty("java.vendor");

    // Log.log(s);

    if (s.indexOf("Microsoft") != -1) {
      is_ie = true;
    } else {
      if (s.indexOf("Netscape") != -1) {
        is_nav = true;
      } else {
        if (s.indexOf("Sun") != -1) {
          is_sun = true;
        } else {
          is_sun = true;
        }
      }
    }
  }

  public AppletOffsets(Component component) { //, String name) {
    apname = "";
    apref = component;
    try {
      apname = ((Applet) component).getParameter("name");
    } catch (Exception e) {
      Log.log("App problem");
      //    e.printStackTrace();
    }

    try {
      js_w_object = JSObject.getWindow((Applet) apref);
    } catch (Exception e) {
      Log.log("JS problem");
      //    e.printStackTrace();
    }

    if (is_sun) {
      Log.log("Check for Sun in IE");

      try {
        js_w_object.eval("window.statusbar.visible");
      } catch (Exception e) {
        Log.log("Must be Sun in IE");
        is_sun_in_ie = true;
        is_ie = false;
        is_nav = false;
        is_sun = false;
      }
    }
  }

  /*
  static boolean classExists(String class_name) {
     try {
        Class a_class = Class.forName(class_name); 
     }
        catch (Exception e) {
           return false;
        }
  
     return true;
  }
  */

  void setOffsets() {
    if (is_sun_in_ie) {
      setOffsetSuninIE(8, 6);
    }

    if (is_ie) {
      setOffsetIE();
    }

    if (is_nav) {
      setOffsetNav(8, -6);
    }

    if (is_sun) {
      //Log.log("Moz");
      setOffsetNav(6, -2);
    }
  }

  private void setOffsetIE() {
    if (js_w_object == null) {
      return;
    }

    JSObject jsobject1 = null;
    JSObject jsobject2 = null;
    try {
      jsobject1 = (JSObject) js_w_object.getMember("document");
    } catch (ClassCastException _ex) {
      Log.log("Assigning doc failed");
    }

    try {
      jsobject2 = (JSObject) jsobject1.getMember(apname);
    } catch (ClassCastException e) {
      Log.log("Assigning applet name failed");
      jsobject2 = null;
      e.printStackTrace();
      return;
    }

    Point p = new Point();

    addOffsetIE(jsobject2, p);

    JSObject jso = jsobject2;
    JSObject jsol;

    //int cnt = 0;
    try {
      do {
        jsol = jso;

        jso = (JSObject) jsol.getMember("parentElement");
        String s = (String) jso.getMember("tagName");
        //Log.log("XX->" + s);

        if ((s.equals("TABLE")) || (s.equals("TBODY"))) {
          //if (contains2(jso,jsol)) {

          addOffsetIE(jso, p);
          //Log.log("TABLE");
        }
        //Log.log("C:" + cnt++ + " - " + p.x + " " + p.y);

      } while (true);
    } catch (Exception exc) {
      //exc.printStackTrace();
      //Log.log("FC:" + cnt);
    }

    //Log.log("ParentElement:" + jsobject2.getMember("ParentElement").toString());

    h_offset = p.y;
    v_offset = p.x;
  }

  private void addOffsetIE(JSObject jsobj, Point p) {
    try {
      Float float1 = new Float(String.valueOf(jsobj.getMember("offsetTop")));
      p.x += float1.intValue();
    } catch (NumberFormatException _ex) {
      _ex = _ex;
    }
    try {
      Float float2 = new Float(String.valueOf(jsobj.getMember("offsetLeft")));
      p.y += float2.intValue();
    } catch (NumberFormatException _ex) {
      _ex = _ex;
    }
  }

  private void setOffsetNav(int x, int y) {
    int i = 0;
    int j = 0;
    int k = 0;
    int l = 0;
    int i1 = 0;
    byte byte2 = 0;

    if (js_w_object == null) {
      return;
    }

    if (((Boolean) js_w_object.eval("window.statusbar.visible")).booleanValue()) {
      byte2 = 26;
      //Log.log("window.statusbar.visible");
    }

    try {
      Float float2 = new Float(js_w_object.getMember("innerHeight").toString());
      i1 = float2.intValue();
    } catch (NumberFormatException _ex) {
      _ex = _ex;
    }

    try {
      Float float3 = new Float(js_w_object.getMember("outerHeight").toString());
      l = float3.intValue();
    } catch (NumberFormatException _ex) {
      _ex = _ex;
    }

    try {
      Float float1 = new Float(js_w_object.getMember("screenX").toString());
      i = float1.intValue() + x + 1;
    } catch (NumberFormatException _ex) {
      _ex = _ex;
    }

    try {
      Float float4 = null;
      float4 = new Float(js_w_object.getMember("screenY").toString());
      j = (float4.intValue() + l) - i1 + y - byte2;
    } catch (NumberFormatException _ex) {
      _ex = _ex;
    }

    try {
      Float float5 = new Float(js_w_object.getMember("pageYOffset").toString());
      k = float5.intValue();
    } catch (NumberFormatException _ex) {
      _ex = _ex;
    }

    Point point = getLocationOnScreen();
    try {
      point = new Point(apref.getLocationOnScreen());
    } catch (Exception e) {
      point = new Point();
    }

    h_offset = point.x - i;
    v_offset = point.y - (j - k);
  }

  Point getLocationOnScreen() {
    try {
      return apref.getLocationOnScreen();
    } catch (Exception e) {
      return new Point();
    }
  }

  private void setOffsetSuninIE(int x, int y) {
    x = x;
    y = y;
    
    h_offset = 0;
    v_offset = 0;

    //Point point = new Point(apref.getLocationOnScreen());

    //h_offset = point.x;
    //v_offset = point.y;
  }

  void setVOffset(int v_offset) {
    this.v_offset = v_offset;
  }

  int getVOffset() {
    return v_offset;
  }

  void setHOffset(int h_offset) {
    this.h_offset = h_offset;
  }

  int getHOffset() {
    return h_offset;
  }

  /*
  
     int i = 0;
     int j = 0;
     int k = 0;
     int l = 0;
     int i1 = 0;
     byte byte2 = 0;
  
  //if(((Boolean)js_w_object.eval("window.statusbar.visible")).booleanValue()) {
  //byte2 = 26;
  //Log.log("window.statusbar.visible");
  //}
  
     Log.log("111");
  
  try {
     Float float2 = new Float(js_w_object.getMember("innerHeight").toString());
     i1 = float2.intValue();
  }
     catch(NumberFormatException _ex) { 
     }
  
  Log.log("222");
  
  try {
     Float float3 = new Float(js_w_object.getMember("outerHeight").toString());
     l = float3.intValue();
  }
     catch(NumberFormatException _ex) { 
     }
  
  
     Log.log("333");
  
     try {
        Float float1 = new Float(js_w_object.getMember("screenX").toString());
        i = float1.intValue() + x + 1;
     }
        catch(NumberFormatException _ex) { 
        }
  
     Log.log("444");
  
     try {
        Float float4 = null;
        float4 = new Float(js_w_object.getMember("screenY").toString());
        j = (float4.intValue() + l) - i1 + y - byte2;
     }
        catch(NumberFormatException _ex) { 
        }
  
     Log.log("555");
  
     try {
        Float float5 = new Float(js_w_object.getMember("pageYOffset").toString());
        k = float5.intValue();
     }
        catch(NumberFormatException _ex) { 
        }
  
     Log.log("666");
  
     Point point = new Point(apref.getLocationOnScreen());
  
     h_offset = point.x + x - i;
     v_offset = point.y + y - (j - k);
  }
  */

}
