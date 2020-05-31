/* Done
 * ====
 * Allow variable aspect ratio...
 * Apply "italic" transform...
 * Three: Regular/Outline/Bold...?
 * Outline/Bold/Regular...
 * Italics...
 * Wide fonts and italic fonts...
 * Pen type (and weight) in demonstration...
 * Optimise out width = 0...
 * Pen type in frame...
 * Deal with chopping off the RHSs of glyphs (done)
 * Offer pen shape options...
 * Move to using pens...
 * LHS is chopped off...
 * Stop cloning GDO so much...
 * AA:
 *  Option controls AA of main window...
 *  Main window AA *sometimes*...
 *  256(!)-times oversampling rendering...
 * Wrap GDO with *mutable* wrapper class - for the sake of clients...???
 * Make GDO *mutable* - and rework hash function and cache accordingly... ;-)
 * Non-power-of-two qualities...
 * Take copy when executing "put"...
 * Need hashcode...
 * GDOEditor extends GDO...?
 * Allow non-power-of-2 qualities...
 * Editor selection moved somewhere safer...
 *  Consider using a Hashtable for the selection...?!?
 * Go quickly from index to path...
 * Dragbox...
 * Drag-stop-drag...
 * "A" and "4" ...?
 * Make an effort to stop doing everything twice for X and Y...
 * Link each slider to a point...
 * Don't hint on separate paths - hint *once* - on the entire glyph...
 * Option to eliminate all "springs" code from the runtime...
 * Make more packages..?
 * Springs (almost) never go "outwards"... 
 *  One of two ways:
 *   One - using "inside polygon!"...
 *    Two - using cw/acw detection on 3 vectors...
 * Think about a strategy of not giving out array references - especially when the length is wrong...
 *  Don't put internal arrays in the public API at all...
 *   Hitlist: InstructionStream.getFEPathList();  InstructionStream.getFEPointList()
 * Investigate how many straight lines are involved...
 * Springs in middle straight sections (hard)...
 * Too many redraws while editing...
 * Frame.destroy...(done?)
 * "FEG = null exception"
 * Hinting of LC's "M" - two sliders - wrong one wins...
 * Look into how LC's "Z" is hinted...???
 * Store slider lengths...
 * Make the "longest" slider win out...
 * Slider-direction rendering...
 * Size...
 * Quality selector...
 * Sub-pixel anti-aliasing...
 * Font selector... * Allow configurable image colours...?
 * Invert image???
 * Fix kerning...
 *  Allow input of AA grid size in editor...
 * Image crop top and left with offsets...?
 * "Blur" kerning data ;-)
 * Tidy kerning...
 * Transparent during resize...
 * Deal with big characters using "*"s...
 * Web site...
 * Deploy on site...
 * Hinting width problems...
 * Set global Frame options...
 * Align LHS when in outline...
 * Preserve aspect ratio (option)...?
 * Configurable line width...
 * Configurable grab point size...
 * "Rescale font" button...
 * Garish colours...
 * Scale glyphs during grab operation...
 * Editor class...
 * Load/save - needs finishing off...
 * Make default...
 * See how compressible the result is...
 * Relative coordinates...
 * 1/2/3 byte...
 * Next glyph...
 * InstructionStream.fepointlist and InstructionStream.fepathlist need moving... 
 * Update modified glyphs in the editor...
 * Font bug: Exception with "Mister Earl BT"
 * Progressive" quality choice...
 * "Odessa LET" - too filled - why....!?!?
 * Sort paths after
 *  ripping (not easy)...
 * Quality choice (1,2.4,8,16)...
 * Cache rendered glyphs...
 * Preview...
 * Spring display option...
 *  Allow thick pens...
 * org.fonteditor Package...
 * Directions on sliders...
 * From Point to Path...
 *  * Elastic connections between "close" points...
 * Hints only affect the nearby section of that path...
 * Grabber
 *  Font selector...
 * Use "simplest-possible polygon" to determine direction...
 * Use FontMetrics to measure font and get overall characteristics...
 * Kill duplicated "sliders"...
 * Sort "sliders"...
 * Multiple "Coords" objects...
 * Outline only display...
 * If path selected deselect points...
 * LHS align...
 * Change size
 * Show grid...
 * Optionally, don't display control points...
 * GlyphDisplayOptions/GlyphEditOptions object...
 * Disable sliders when not visible...
 * Disable points when not visible...
 * Draggable Vertical / Horizontal sliders...
 * Include min and max points in slider list...
 * Grab all glyphs once - at start / Preserve glyphs...
 * "Outline" checkbox works...
 * New window should appear at start...?
 * New window should appear on top...
 * Display all glyphs...
 * Glyphs in frames...
 * Dragbox selects multiple strokes...
 * Selection of multiple whole strokes using drag-box...
 * Drag one thing at a time...?
 * Drag points...
 * Have a map class that links the Instructions with the Paths?  No.
 * FP FPStream
 * Scale factor controlled by window size...
 * Double buffer...?
 * Drag box around objects to select them...
 * Anti-alias main editing window... ;-)
 * Make end points shared...?
 * isClockwise...
 * % crash!
 * Inside/outside checking needs improvement...
 * Test quadratic bezier code...
 * Move control points with points...
 * Wide line drawing algorithm...
 * Linked list?
 * More thick lines...
 * Drag whole stroke...
 * Think about winding rules and filling...
 * Scale factor...
 * Avoid multiple selections...
 * Select one point...
 * Make editor manipulate InstructionStream...
 * Reselect selected items...
 * Hires box / circle display...
 * Resize bug...
 * Kill off FEGlyph.fepa
 * (NO) Factory for GDO that *doesn't* hand out duplicates...??? (NO)
 */
