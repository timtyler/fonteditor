package org.fonteditor.tests;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.fonteditor.FEFontRenderer;
import org.fonteditor.cache.CachedGlyph;
import org.fonteditor.font.FEFont;
import org.fonteditor.font.FEFontMetrics;
import org.fonteditor.font.FontFind;
import org.fonteditor.graphics.ImageWrapper;
import org.fonteditor.kerning.Kerning;
import org.fonteditor.options.coords.CoordsFactory;
import org.fonteditor.options.display.DisplayOptions;
import org.fonteditor.options.pen.PenRound;
import org.fonteditor.utilities.general.Forget;

public class FontMetricsTest extends JComponent {
  private FEFontRenderer renderer = new FEFontRenderer(this);
  private FEFont font;
  private DisplayOptions display_options;
  private FEFontMetrics metrics;

  private static String test_text = "The quick brown fox jumped over the lazy dog";

  public void setFont(String font_name) {
    font = FontFind.find(font_name, getGraphics(), 32, 128, false, false);
    display_options = DisplayOptions.getGDOrender(10, 1, 1, new PenRound(0x0), true);
    int quality = 2;
    display_options.setCoords(CoordsFactory.makeCoords(36, quality, quality));
  }

  public void printStats(String text) {
    ensureMetrics();

    System.err.println("Max ascent = " + metrics.getMaxAscent());
    System.err.println("Max descent = " + metrics.getMaxDescent());
    System.err.println("Max advance = " + metrics.getMaxAdvance());
    System.err.println("Width(" + text + ") = " + metrics.stringWidth(text));
  }

  FEFontMetrics getMetrics() {
    ensureMetrics();

    return metrics;
  }
  void ensureMetrics() {
    if (metrics == null) {
      metrics = new FEFontMetrics(font, renderer, display_options);
    }
  }

  public void paintComponent(Graphics graphics) {
    char previous = ' ';
    int x = 10;
    ensureMetrics();
    for (int i = 0; i < test_text.length(); i++) {
      char ch = test_text.charAt(i);
      int kd = renderer.getKerningDistance(previous, font, display_options, ch, font, display_options);
      x += kd + Kerning.getKerningGap(kd);
      previous = ch;
      CachedGlyph cached_glyph = renderer.getCachedGlyph(ch, font, display_options);
      ImageWrapper i_w = cached_glyph.getImageWrapper();
      int offset_y = cached_glyph.getOffsetY();
      graphics.drawImage(i_w.getImage(), x, offset_y - metrics.getMaxAscent(), null);
    }
  }

  public static void main(String[] argv) {
    Forget.about(argv);

    JFrame frame = new JFrame("Font test");
    FontMetricsTest tester = new FontMetricsTest();
    tester.setFont("lucky");
    frame.getContentPane().add(tester);
    frame.toFront();
    frame.setSize(new Dimension(300, 300));
    frame.validate();
    frame.show();
    tester.printStats(test_text);
    frame.repaint();
  }
}
