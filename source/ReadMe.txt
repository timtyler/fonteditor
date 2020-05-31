FontEditor
==========


Introduction
------------
The main class is "Run.java".
It works for applets or applications.


Building
--------
I haven't included any build scripts - but they
should not really be needed. 

"javac Run.java" ought to do the trick.


Execution
---------
"java Run" should execute the program.

Press the "Demo" button in the UI to view the
"type-and-see" kerning demonstration.


Documentation
-------------
"ToDo.txt" is the main ToDo list for the project.
It may be rather "shorthand" - sorry 'bout that.

The function of most of the classes is documented
using javadoc comments.  However there's currently
not very much documentation apart from that.

To understand how to render a kerned line of text,
see the contents of package org.fonteditor.demonstration.


Switches
--------
Set org.fonteditor.FEConstants.JAVA_2 in order
to access the full range of Java 2 fonts.

Set org.fonteditor.FEConstants.DEVELOPMENT_VERSION
to turn on some minor additional monitoring.


Interface
---------
The public API is not remotely fixed.

THe API is not currently similar to that used by
Java's main font rendering system.

An external veneer needs to be written - which
exposes FontMetrics (and the like) for the
internal fonts.

Also font coordinates currently work from the
top-left-hand corner of glyphs.  There is no
current notion of the baseline (the baseline
currently goes all wobbly when hinted as a
consequence).  This all needs fixing before
there can be much in the way of drop-in
replacement for applications that use the
native Java fonts.

Good luck.
--
__________
 |im |yler  http://timtyler.org/  tim@tt1.org
