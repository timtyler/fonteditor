//import java.applet.Applet;
//import java.awt.Font;
//import java.awt.Frame;
//import java.awt.Graphics;
//import java.awt.Point;
//
//import org.fonteditor.graphics.ImageWrapper;
//import org.fonteditor.utilities.applet.TTAppletFrame;
//import org.fonteditor.utilities.random.JUR;
//
///*
// * "Moving window" breeze...
// * Mouse-sensitive...
// * Debug particles...
// * Moz / NS "issues"...
// * Sun in IE...
// * Fewer classes - for speed...? 
// * Optimise springs to only do one end of each spring...
// * Display before image loaded...?
// * Spring grid class...?
// *
// * Change granularity...?
// * Anti-aliased text...?
// * udlr on springs...???
// * PNG support...
// */
//
//public class TextWarper extends Applet implements Runnable {
//  private long old_time;
//  private long current_time;
//  private long one_frame_takes;
//
//  private Point last_location;
//
//  private Spark[] sparks;
//  private Spark[] ashes;
//
//  private Thread runner;
//  private AppletOffsets applet_offsets;
//
//  private JUR rnd = new JUR();
//
//  private int width;
//  private int height;
//
//  private int width_mo;
//  private int height_mo;
//
//  private int size;
//
//  private int[] pixels;
//
//  private ImageWrapper tt_image;
//
//  private byte[] buffer;
//  private byte[] tempbuffer;
//
//  private byte[] my_cooling_buffer;
//
//  private byte[] global_src_buf;
//  private byte[] global_dst_buf;
//
//  private Particle[][] particle;
//
//  private float[][] normals;
//
//  private int[] colour_lut;
//  private int colourSt;
//
//  private boolean done_loading_image;
//  private boolean application;
//
//  private int particle_grid_width;
//  private int particle_grid_height;
//
//  private int background_width;
//  private int background_height;
//  private int[] background_pixels;
//
//  private BackgroundText b_g_t;
//  private boolean thread_terminated;
//
//  private float spring_fudge_factor_x;
//  private float spring_fudge_factor_y;
//
//  private final static int log2_granularity = 3;
//  private final static int granularity = 1 << log2_granularity;
//  private final static int granularity_mo = granularity - 1;
//  //final static int    granularity_squared = granularity * granularity;
//
//  private final static int log2_p_granularity = log2_granularity;
//  private final static int p_granularity = 1 << log2_p_granularity;
//
//  private final static int cooling_buffer_size = 512; // power of 2
//  private final static int cooling_buffer_size_mo = cooling_buffer_size - 1;
//
//  private final static float amplitude = 0.0f; // initial...
//  private final static float damping = 0.9f;
//
//  private final static float stimulation_intensity = p_granularity * 0.9f;
//  private final static int stimulation_number = 240; // 32
//
//  private final static float spring_fudge_factor = 0.01f;
//
//  private final static int decay_rate = 30;
//
//  private final static int ashes_rate_min = 32;
//  private final static int ashes_rate_mask = 127;
//
//  private final static int spark_rate_min = 64;
//  private final static int spark_rate_mask = 127;
//
//  private final static float sfx = 1.0f;
//  private final static float sfy = 1.0f; //1.0065f;
//  private final static float spring_strength = 0.03f; // 0.0003f;
//
//  private final static float maximum_velocity_x = 3.0f;
//  private final static float minimum_velocity_x = -maximum_velocity_x;
//  private final static float maximum_velocity_y = maximum_velocity_x;
//  private final static float minimum_velocity_y = -maximum_velocity_y;
//
//  private final static int fuzzy_edge_size = 0; // width
//  private final static int fuzzy_edge_decay = 0; // rate
//
//  private final static int shadow_intensity = 0x70;
//  private final static int shadow_threshold = 0xB0;
//
//  private final static String main_text = "Mersenne Consulting";
//  private final static String font_name = "Helvetica";
//  private final static int font_style = Font.BOLD;
//  private final static int font_size = 69;
//  private final static int font_pos_x = 12;
//  private final static int font_pos_y = 22;
//
//  private final static int shadow_offset_x = 2;
//  private final static int shadow_offset_y = 2;
//
//  private final static boolean development_version = false;
//
//  private final static boolean add_border = development_version;
//  private final static boolean show_status = development_version;
//  private final static boolean debug_particles = development_version;
//
//  private int framecount;
//  private int returned_offset_y;
//  private int returned_offset_x;
//  private boolean redraw_all;
//
//  public TextWarper() {
//    done_loading_image = false;
//  }
//
//  public void init() {
//    redraw_all = true;
//    applet_offsets = new AppletOffsets(this);
//
//    show();
//
//    width = (size().width / granularity) * granularity;
//    height = (size().height / granularity) * granularity;
//
//    sparks = new Spark[width];
//    ashes = new Spark[(width * 4) / 3];
//
//    //setupSparks(sparks);
//    //setupSparks(ashes);
//
//    width_mo = width - 1;
//    height_mo = height - 1;
//
//    ImageWrapper background_image = tryToLoadImage(tTGetParameter("background"));
//    background_width = background_image.getWidth();
//    background_height = background_image.getHeight();
//
//    background_pixels = background_image.getArray();
//
//    pixels = new int[width * height];
//    tt_image = new ImageWrapper(pixels, width, height);
//
//    particle_grid_width = (width >> log2_granularity) + 1;
//    particle_grid_height = (height >> log2_granularity) + 1;
//
//    initNormals();
//
//    particle = new Particle[particle_grid_width][particle_grid_height];
//    for (int i = 0; i < particle_grid_width; i++) {
//      for (int j = 0; j < particle_grid_height; j++) {
//        particle[i][j] = new Particle();
//      }
//    }
//
//    size = width * height;
//
//    buffer = new byte[size];
//    tempbuffer = new byte[size];
//
//    initialiseSprings(amplitude);
//    my_cooling_buffer = setUpMyCoolingBuffer(decay_rate, cooling_buffer_size);
//
//    for (int i = 0; i < size; i++) {
//      buffer[i] = 0;
//    }
//
//    colour_lut = new int[256];
//    colourSt = 0;
//
//    b_g_t = new BackgroundText();
//    b_g_t.setup(this, width, height, main_text, font_name, font_style, font_size, font_pos_x, height - font_pos_y);
//
//    done_loading_image = true;
//
//    last_location = applet_offsets.getLocationOnScreen();
//
//    validate();
//  }
//
//  public void start() {
//    //Log.log("START");
//
//    thread_terminated = false;
//
//    if (runner == null) {
//      //Log.log("STARTED");
//
//      runner = new Thread(this);
//      runner.start(); // doesn't call run() on netscape?
//      //run();
//    }
//  }
//
//  public void stop() {
//    //Log.log("STOP");
//    if (runner != null) {
//      //Log.log("STOPPED");
//
//      runner.stop();
//    }
//
//    thread_terminated = true;
//    runner = null;
//  }
//
//  public void run() {
//    doSetColor();
//    //Log.log("RUN");
//
//    size = width * height;
//    arrayToPixels(pixels, buffer, 0, 0);
//
//    try {
//      do {
//        //do {
//        try {
//          Thread.sleep(4L);
//        } catch (InterruptedException _ex) {
//        }
//        //} while (!isVisible());
//        iterate();
//        //Log.log("RUNNING");
//      } while (!thread_terminated);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    runner = null;
//  }
//
//  // main loop...
//  protected void iterate() {
//    repaint();
//
//    if (show_status) {
//      timing();
//    }
//  }
//
//  public void paint(Graphics g) {
//    update(g);
//  }
//
//  public void update(Graphics g) {
//    if (done_loading_image) { // hack...
//      updateParticles();
//
//      arrayToPixels(pixels, buffer);
//
//      stimulation(stimulation_number);
//
//      breeze();
//
//      //tt_image.update();
//
//      g.drawImage(tt_image.getImage(), 0, 0, null);
//    }
//  }
//
//  // springs - behind texture warping...
//  private void updateParticles() {
//    updateParticleVelocities();
//    updateParticlePositions();
//  }
//
//  private void updateParticleVelocities() {
//    int samp_x = particle_grid_width >> 1;
//    int samp_y = particle_grid_height >> 1;
//
//    Particle p = particle[samp_x][samp_y];
//
//    float spring_fudge_factor_x = (p.x > samp_x << log2_granularity) ? spring_fudge_factor : -spring_fudge_factor;
//    float spring_fudge_factor_y = (p.y > samp_y << log2_granularity) ? spring_fudge_factor : -spring_fudge_factor;
//
//    for (int y = 1; y < particle_grid_height - 1; y++) {
//      int yh = y + 1;
//      int yl = y - 1;
//
//      for (int x = 1; x < particle_grid_width - 1; x++) {
//        int xh = x + 1;
//        int xl = x - 1;
//
//        for (int yi = yl; yi <= yh; yi++) {
//          for (int xi = xl; xi <= xh; xi++) {
//            if ((xi != x) || (yi != y)) {
//              float norm = normals[myabs(xi - x)][myabs(yi - y)];
//
//              Particle p1 = particle[xi][yi];
//              Particle p2 = particle[x][y];
//
//              float Xspring = p1.x - p2.x + spring_fudge_factor_x;
//              float Yspring = p1.y - p2.y + spring_fudge_factor_y;
//
//              double force = (norm - Math.sqrt(Xspring * Xspring + Yspring * Yspring)) * spring_strength;
//
//              p1.xv += Xspring * force;
//              p1.yv += Yspring * force;
//            }
//          }
//        }
//      }
//    }
//  }
//
//  private void updateParticlePositions() {
//    int pos_y = granularity;
//    for (int y = 1; y < particle_grid_height - 1; y++) {
//      int pos_x = granularity;
//      for (int x = 1; x < particle_grid_width - 1; x++) {
//
//        Particle p = particle[x][y];
//        p.x += p.xv;
//        p.y += p.yv;
//
//        if (p.x > pos_x + maximum_velocity_x) {
//          p.x = pos_x + maximum_velocity_x;
//        }
//        if (p.x < pos_x + minimum_velocity_x) {
//          p.x = pos_x + minimum_velocity_x;
//        }
//
//        if (p.y > pos_y + maximum_velocity_y) {
//          p.y = pos_y + maximum_velocity_y;
//        }
//        if (p.y < pos_y + minimum_velocity_y) {
//          p.y = pos_y + minimum_velocity_y;
//        }
//
//        p.xv *= damping;
//        p.yv *= damping;
//
//        pos_x += granularity;
//      }
//
//      pos_y += granularity;
//    }
//  }
//
//  private void TextureWarp(byte[] src_buf, byte[] dst_buf) {
//    float dpg = (float) p_granularity;
//    int offset1 = 0;
//
//    int yi = 0;
//    for (int yo = 0; yo < particle_grid_height - 1; yo++) {
//      int xi = 0;
//      for (int xo = 0; xo < particle_grid_width - 1; xo++) {
//        Particle p00 = particle[xo][yo];
//        Particle p10 = particle[xo + 1][yo];
//        Particle p01 = particle[xo][yo + 1];
//        Particle p11 = particle[xo + 1][yo + 1];
//
//        float TX1 = p00.x * sfx;
//        float TY1 = p00.y * sfy;
//        float TX2 = p10.x * sfx;
//        float TY2 = p10.y * sfy;
//
//        float VLDx = (p01.x * sfx - TX1) / dpg;
//        float VRDx = (p11.x * sfx - TX2) / dpg;
//        float VLDy = (p01.y * sfy - TY1) / dpg;
//        float VRDy = (p11.y * sfy - TY2) / dpg;
//
//        int offset2 = offset1;
//        for (int y = yi; y < yi + granularity; y++) {
//          float HDx = (TX2 - TX1) / dpg;
//          float HDy = (TY2 - TY1) / dpg;
//          float tx = TX1;
//          float ty = TY1;
//          for (int x = xi; x < xi + granularity; x++) {
//            int sx = (int) tx;
//            int sy = (int) ty;
//
//            if (sx < 0) {
//              sx = 0;
//            }
//
//            if (sy < 0) {
//              sy = 0;
//            }
//
//            if (sx >= width) {
//              sx = width_mo;
//            }
//
//            if (sy >= height) {
//              sy = height_mo;
//            }
//
//            dst_buf[offset2] = src_buf[width * sy + sx];
//            offset2++;
//            tx += HDx;
//            ty += HDy;
//          }
//
//          offset2 += width - granularity;
//          TX1 += VLDx;
//          TY1 += VLDy;
//          TX2 += VRDx;
//          TY2 += VRDy;
//        }
//
//        xi += granularity;
//        offset1 += granularity;
//      }
//
//      yi += granularity;
//      offset1 += +granularity * width - width;
//    }
//  }
//
//  private void arrayToPixels(int[] pix, byte[] byte_buffer) {
//    arrayToPixels(pix, byte_buffer, redraw_all ? 11 : 0, redraw_all ? 3 : 0);
//    redraw_all = false;
//  }
//
//  private void arrayToPixels(int[] pix, byte[] byte_buffer, int dx, int dy) {
//    byte_buffer = byte_buffer;
//
//    int bg_offset_y = 0;
//    int i_bg_offset_x = 0;
//
//    if (applet_offsets != null) {
//      applet_offsets.setOffsets();
//
//      bg_offset_y = (myabs(applet_offsets.getVOffset()) + dy) % (background_height);
//      i_bg_offset_x = (myabs(applet_offsets.getHOffset()) + dx) % (background_width);
//    }
//
//    int index = dx + dy * width;
//    int negative_offset = shadow_offset_x + shadow_offset_y * width;
//
//    for (int j = dy; j < height - dy; j++) {
//      int bg_offset_x = i_bg_offset_x;
//      int bg_offset_idx = 0 + bg_offset_y * background_width;
//
//      for (int i = dx; i < width - dx; i++) {
//        //int n0 = byte_buffer[j * width + i];
//        calculateOffsets(i, j);
//
//        int n0 = getPointAA(returned_offset_x, returned_offset_y);
//        int val;
//
//        if (n0 == 0) {
//          if ((index > negative_offset) && ((pix[index - negative_offset] & 0xFF) < shadow_threshold)) {
//            val = combineColours(background_pixels[bg_offset_idx + bg_offset_x], pix[index - negative_offset], shadow_intensity);
//          } else {
//            val = background_pixels[bg_offset_idx + bg_offset_x];
//          }
//        } else {
//          if ((index > negative_offset) && ((pix[index - negative_offset] & 0xFF) < shadow_threshold)) {
//            val = combineThreeColours(colour_lut[n0], background_pixels[bg_offset_idx + bg_offset_x], pix[index - negative_offset], n0);
//          } else {
//            val = combineColours(background_pixels[bg_offset_idx + bg_offset_x], colour_lut[n0], n0);
//          }
//        }
//
//        if (pix[index++] != val) {
//          pix[index - 1] = val;
//        }
//
//        if (++bg_offset_x == background_width) {
//          bg_offset_x = 0;
//        }
//      }
//
//      index += dx << 1;
//
//      if (++bg_offset_y == background_height) {
//        bg_offset_y = 0;
//      }
//    }
//
//    if (add_border) {
//      addBorder(pix);
//    }
//
//    if (debug_particles) {
//      debugParticles(pix);
//    }
//  }
//
//  private void calculateOffsets(int x, int y) {
//    if (x >= width) {
//      x = width - 1;
//    }
//    if (y >= height) {
//      y = height - 1;
//    }
//    // on the basis of the jelly...
//
//    int b_x = x >> log2_granularity;
//    int b_y = y >> log2_granularity;
//
//    int r_x = x & granularity_mo;
//    int r_y = y & granularity_mo;
//
//    Particle p00 = particle[b_x][b_y];
//    Particle p10 = particle[b_x + 1][b_y];
//    Particle p01 = particle[b_x][b_y + 1];
//    Particle p11 = particle[b_x + 1][b_y + 1];
//
//    if (r_x > r_y) {
//      float v00_10_x = p10.x - p00.x;
//      float v00_10_y = p10.y - p00.y;
//      float v10_11_x = p11.x - p10.x;
//      float v10_11_y = p11.y - p10.y;
//
//      returned_offset_x = ((int) (p00.x * granularity + v00_10_x * r_x + v10_11_x * r_y) * 256) >> log2_granularity;
//      returned_offset_y = ((int) (p00.y * granularity + v00_10_y * r_x + v10_11_y * r_y) * 256) >> log2_granularity;
//    } else {
//      float v00_01_x = p01.x - p00.x;
//      float v00_01_y = p01.y - p00.y;
//
//      float v01_11_x = p11.x - p01.x;
//      float v01_11_y = p11.y - p01.y;
//
//      returned_offset_x = ((int) (p00.x * granularity + v01_11_x * r_x + v00_01_x * r_y) * 256) >> log2_granularity;
//      returned_offset_y = ((int) (p00.y * granularity + v01_11_y * r_x + v00_01_y * r_y) * 256) >> log2_granularity;
//    }
//  }
//
//  private void addBorder(int pix[]) {
//    int c = 0x404040;
//    for (int j = 0; j < height; j++) {
//      pix[width * j] ^= c;
//      pix[width * j + width - 1] ^= c;
//    }
//
//    for (int i = 1; i < width - 1; i++) {
//      pix[i] ^= c;
//      pix[width * height - width + i] ^= c;
//    }
//  }
//
//  private void debugParticles(int pix[]) {
//    for (int y = 0; y < particle_grid_height - 1; y++) {
//      for (int x = 0; x < particle_grid_width - 1; x++) {
//        Particle p = particle[x][y];
//
//        int px = (int) p.x;
//        int py = (int) p.y;
//
//        if (px < 0) {
//          px = 0;
//        }
//
//        if (py < 0) {
//          py = 0;
//        }
//
//        if (px >= width) {
//          px = width_mo;
//        }
//
//        if (py >= height) {
//          py = height_mo;
//        }
//
//        pix[width * py + px] ^= 0xFFC040;
//        pix[(width * y + x) * granularity] ^= 0x40C0FF;
//      }
//    }
//  }
//
//  private int min(int a, int b) {
//    return (a < b) ? a : b;
//  }
//
//  // c1 - FG  c2 = BG  c3 = shadow
//  private int combineThreeColours(int c1, int c2, int c3, int d) {
//    if (d >= 240)
//      return c1;
//
//    int c4 = combineColours(c2, c3, shadow_intensity);
//    return combineColours(c4, c1, d);
//    //minimumColour(c1,c3);
//
//    //return minimumColour(c1,c3); //combineColours(c4,c2,d);
//  }
//
//  private int minimumColour(int c1, int c2) {
//    int r1 = (c1 >>> 16) & 0xFF;
//    int g1 = (c1 >>> 8) & 0xFF;
//    int b1 = (c1) & 0xFF;
//
//    int r2 = (c2 >>> 16) & 0xFF;
//    int g2 = (c2 >>> 8) & 0xFF;
//    int b2 = (c2) & 0xFF;
//
//    int r3 = min(r1, r2);
//    int g3 = min(g1, g2);
//    int b3 = min(b1, b2);
//
//    return b3 | g3 << 8 | r3 << 16 | 0xFF000000;
//  }
//
//  private int combineColours(int c1, int c2, int d) {
//    int r1 = (c1 >>> 16) & 0xFF;
//    int g1 = (c1 >>> 8) & 0xFF;
//    int b1 = (c1) & 0xFF;
//
//    int r2 = (c2 >>> 16) & 0xFF;
//    int g2 = (c2 >>> 8) & 0xFF;
//    int b2 = (c2) & 0xFF;
//
//    int r3 = (r2 * d + r1 * (0x100 - d)) >> 8;
//    int g3 = (g2 * d + g1 * (0x100 - d)) >> 8;
//    int b3 = (b2 * d + b1 * (0x100 - d)) >> 8;
//
//    return b3 | g3 << 8 | r3 << 16 | 0xFF000000;
//  }
//
//  private void doSetColor() {
//    rangeFill(colour_lut, 000, 256, 0xFFFFFF, 0x000000);
//  }
//
//  private void rangeFill(int[] colours, int start, int end, int start_rgb, int end_rgb) {
//    int start_r = (start_rgb >> 16) & 0xFF;
//    int start_g = (start_rgb >> 8) & 0xFF;
//    int start_b = (start_rgb) & 0xFF;
//
//    int end_r = (end_rgb >> 16) & 0xFF;
//    int end_g = (end_rgb >> 8) & 0xFF;
//    int end_b = (end_rgb) & 0xFF;
//
//    int delta_r = end_r - start_r;
//    int delta_g = end_g - start_g;
//    int delta_b = end_b - start_b;
//
//    int dist = end - start;
//    for (int x = 0; x < dist; x++) {
//      int r = start_r + ((x * delta_r) / dist);
//      int g = start_g + ((x * delta_g) / dist);
//      int b = start_b + ((x * delta_b) / dist);
//
//      colours[start + x] = 0xFF000000 | (r << 16) | (g << 8) | b;
//    }
//  }
//
//  private void stimulation(int n) {
//    for (int i = 0; i < n; i++) {
//      int x = 1 + rnd.nextInt(particle_grid_width - 2);
//      int y = 1 + rnd.nextInt(particle_grid_height - 2);
//
//      Particle p = particle[x][y];
//
//      p.xv += stimulation_intensity * rnd.nextFloat();
//      p.xv -= stimulation_intensity * rnd.nextFloat();
//      p.yv += stimulation_intensity * rnd.nextFloat();
//      p.yv -= stimulation_intensity * rnd.nextFloat();
//    }
//  }
//
//  private void translate(float dx, float dy) {
//    for (int y = 1; y < particle_grid_height - 2; y++) {
//      for (int x = 1; x < particle_grid_width - 2; x++) {
//        Particle p = particle[x][y];
//
//        p.x += dx;
//        p.y += dy;
//      }
//    }
//  }
//
//  private void initNormals() {
//    float ff = p_granularity;
//
//    normals = new float[2][2];
//
//    normals[0][0] = 0.0f;
//    normals[0][1] = ff;
//    normals[1][0] = ff;
//    normals[1][1] = (float) (Math.sqrt(2)) * ff;
//  }
//
//  private byte[] setUpMyCoolingBuffer(int dr, int siz) {
//    byte[] temp = new byte[siz];
//
//    for (int i = 0; i < siz; i++) {
//      temp[i] = ((rnd.nextInt() & 31) == 0) ? (byte) (1 + dr) : 0;
//    }
//
//    return temp;
//  }
//
//  private int myabs(int v) {
//    return (v < 0) ? -v : v;
//  }
//
//  private float myabs(float v) {
//    return (v < 0) ? -v : v;
//  }
//
//  private void timing() {
//    if (!application) {
//      if (framecount++ == 10) {
//
//        framecount = 0;
//
//        old_time = current_time;
//        current_time = System.currentTimeMillis();
//        one_frame_takes = current_time - old_time;
//
//        showStatus("T: " + one_frame_takes);
//      }
//    }
//  }
//
//  private void initialiseSprings(float amp) {
//    float t = 0.0f;
//    float t1 = t;
//    float t2 = t;
//    float t3 = t;
//    for (int y = 0; y < particle_grid_height; y++) {
//      float yf = y;
//      for (int x = 0; x < particle_grid_width; x++) {
//        float xf = x;
//        t += 0.19f;
//        t1 += 0.17f;
//        t2 += 0.11f;
//        t3 += 0.13f;
//        if (x == 0 || x == particle_grid_width - 1 || y == 0 || y == particle_grid_height - 1) {
//          particle[x][y].x = x << log2_p_granularity;
//          particle[x][y].y = y << log2_p_granularity;
//        } else {
//          particle[x][y].x = (float) (amp * ((Math.sin(t2 - xf) + Math.cos(t2 + yf) + Math.sin(t - yf)) - Math.cos(t + xf)) + (float) (x << log2_p_granularity));
//          particle[x][y].y = (float) (amp * ((Math.sin(t1 - xf) + Math.cos(t3 + yf) + Math.sin(t3 - yf)) - Math.cos(t1 + xf)) + (float) (y << log2_p_granularity));
//        }
//      }
//    }
//  }
//
//  private ImageWrapper tryToLoadImage(String name) {
//    try {
//      return ImageLoader.getImageNow(name);
//    } catch (RuntimeException e) {
//      e = e;
//      return ImageLoader.getRelativeImageNow(this, name);
//    }
//  }
//
//  private String tTGetParameter(String s) {
//    if (application) {
//      return "worms.jpg"; // ???
//    } else {
//      return getParameter(s);
//      //"background")
//    }
//  }
//
//  private void breeze() {
//    Point current_location = applet_offsets.getLocationOnScreen();
//    if (!current_location.equals(last_location)) {
//      float dx = current_location.x - last_location.x;
//      float dy = current_location.y - last_location.y;
//
//      while ((myabs(dx) > maximum_velocity_x * 2) || (myabs(dy) > maximum_velocity_y * 2)) {
//        dx *= 0.9;
//        dy *= 0.9;
//      }
//
//      translate(dx, dy);
//
//      redraw_all = true;
//    }
//
//    last_location = current_location;
//  }
//
//  //0-255
//  private int getPointAA(int x, int y) {
//    int _x_0 = x >> 8;
//
//    if (_x_0 < 0) {
//      _x_0 = 0;
//    }
//
//    if (_x_0 >= width - 1) {
//      _x_0 = width - 2;
//    }
//
//    int _x_1 = _x_0 + 1;
//
//    int _y_0 = y >> 8;
//
//    if (_y_0 < 0) {
//      _y_0 = 0;
//    }
//
//    if (_y_0 >= height - 1) {
//      _y_0 = height - 2;
//    }
//
//    int _y_1 = _y_0 + 1;
//
//    x &= 0xff;
//    y &= 0xff;
//
//    int rv_00 = b_g_t.buffer[_x_0 + width * _y_0] & 0xFF;
//    int rv_11 = b_g_t.buffer[_x_1 + width * _y_1] & 0xFF;
//
//    //return rv_00;
//
//    if (x > y) {
//      int rv_10 = b_g_t.buffer[_x_1 + width * _y_0] & 0xFF;
//      int res = rv_00 + ((x * (rv_10 - rv_00) + y * (rv_11 - rv_10)) >> 8);
//      return (res < 0) ? 0 : (res > 255) ? 255 : res;
//    } else {
//
//      //return rv_00;
//
//      //int rv_01  = b_g_t.buffer[_x_1 + width * _y_0] & 0xFF;
//
//      int rv_01 = b_g_t.buffer[_x_1 + width * _y_0] & 0xFF;
//      int res = rv_00 + ((x * (rv_11 - rv_01) + y * (rv_01 - rv_00)) >> 8);
//      return (res < 0) ? 0 : (res > 255) ? 255 : res;
//    }
//  }
//
//  // main method
//  protected static void main(String args[]) {
//    args = args;
//
//    TextWarper _applet = new TextWarper();
//
//    _applet.application = true;
//
//    Frame frame = new TTAppletFrame("Text Warper", (Applet) _applet, 744, 120);
//    frame.show();
//  }
//
//}
