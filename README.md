# Scoreboard-v1.1

Overview

This archive contains the source code required to build an electronic 
scoreboard.  It is written in Java utilizing the JavaFX 2.x API, and is
packaged as a NetBeans project.  The type of scoreboard chosen for this
implementation pertains to ice hockey.  With an understanding of the overall
organization and structure of the code, it should be straightforward to extend
scoreboard functionality to include other sports.

At start up, the scoreboard executes in one of two modes: as a master
(the default), or as a slave.  In master mode, the scoreboard user interface is
active.  When a user moves his/her pointing device over an editable part of the
scoreboard (a scoreboard digit), that component will, via JavaFX animation,
increase in size.  This provides the user a visual cue about what component is
in focus.  By either clicking on the focused digit, or by utilizing keyboard
input, the user can change the value of the focused digit.  Each time a
scoreboard digit is modified, an XML packet is created describing the
modification, and sent out over an IP socket.

In slave or remote scoreboard mode, the scoreboard UI is inactive.  That is
to say, it will not respond to any mouse or keyboard input.  Its display
can only be updated by listening in on an agreed-upon IP socket
(configurable by command-line switch) for XML scoreboard update packets.
Upon receiving those packets, the remote scoreboard instance will parse the
XML data and change the scoreboard display accordingly.  To start up a
scoreboard in slave mode, use the -slave command-line switch.
It is possible (and desirable) to have multiple slave scoreboards
simultaneously receiving updates from one master scoreboard.

For more information consult the README-v1.1.html file
