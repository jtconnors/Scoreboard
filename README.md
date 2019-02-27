# Scoreboard

Overview

This archive contains the source code required to build an electronic 
scoreboard.  It is written in Java utilizing the JavaFX 2.x API, and is
packaged as a maven project.  The type of scoreboard chosen for this
implementation pertains to ice hockey.  With an understanding of the overall
organization and structure of the code, it should be straightforward to extend
scoreboard functionality to include other sports.

At start up, the scoreboard executes in one of two modes: as a __master__
(the default), or as a slave.  In master mode, the scoreboard user interface is
active.  When a user moves his/her pointing device over an editable part of the
scoreboard (a scoreboard digit), that component will, via JavaFX animation,
increase in size.  This provides the user a visual cue about what component is
in focus.  By either clicking on the focused digit, or by utilizing keyboard
input, the user can change the value of the focused digit.  Each time a
scoreboard digit is modified, an XML packet is created describing the
modification, and sent out over an IP socket.

In __slave__ or remote scoreboard mode, the scoreboard UI is inactive.  That is
to say, it will not respond to any mouse or keyboard input.  Its display
can only be updated by listening in on an agreed-upon IP socket
(configurable by command-line switch) for XML scoreboard update packets.
Upon receiving those packets, the remote scoreboard instance will parse the
XML data and change the scoreboard display accordingly.  To start up a
scoreboard in slave mode, use the -slave command-line switch.
It is possible (and desirable) to have multiple slave scoreboards
simultaneously receiving updates from one master scoreboard.

For more information about the Scoreboard project, consult the **README.html** file

This latest version of the source code is tagged ```v1.2-JDK11-maven```.  It is modularized and as its name suggests, works with JDK11
and is built with the ```apache maven``` build lifecycle system.

Of note, the following maven goals can be executed:

   - ```mvn clean```
   - ```mvn dependency:copy-dependencies``` - to pull down dependent ```javafx``` and ```com.jtconnors.socket``` modules
   - ```mvn compile``` - to build the application
   - ```mvn package``` - to create the ```SocketClientFX``` module as a jar file
   - ```mvn exec:java``` to run the Scoreboard application

There are multiple Main classes available, representing different Scoreboard
configurations, that can be run using the following maven commands:

   - ```mvn  test -PMainMulticast``` - Scoreboard using Multicast scokets
   - ```mvn  test -PMainMulticastRemote``` - Remote Scoreboard instance using Multicast scokets
   - ```mvn  test -PMainRemote``` - Remote instance of the Scoreboard
   - ```mvn  test -PMainRemoteClockOnly``` - Remote instance of the Clock only
   - ```mvn  test -PMainRemoteFullScreen``` - Full Screen remote instance of the Scoreboard
   - ```mvn  test -PMainRemoteFullScreenTV``` - Full Screen (minus TV overscan) remote instance of the Scoreboard
   - ```mvn  test -PMainRemoteLED``` - Alternate remote Scoreboard implementation using LED Segments
    
Furthermore, additional ```.sh``` and ```.ps1``` files are provided in the ```sh/```
and ```ps1\``` directories respectively:
   - ```sh/run.sh``` or ```ps1\run.ps1``` - script file to run the application from the module path
   - ```sh/run-simplified.sh``` or ```ps1\run-simplified.ps1``` - alternative script file to run the application, determines main class from ```SocketClientFX``` module
   - ```sh/link.sh``` or ```ps1\link.ps1``` - creates a runtime image using the ```jlink``` utility
   - ```sh/create-image.sh``` or ```ps1\create-image.ps1``` - creates a native package image of application using JEP-343 jpackage tool
   - ```sh/create-dmg-installer.sh``` - creates a native MacOS dmg installer of this application using JEP-343 jpackage tool
   - ```ps1\create-exe-installer.ps1``` - creates a native Windows EXE installer of this application using JEP-343 jpackage tool
   - ```ps1\create-msi-installer.ps1``` - creates a native Windows MSI installer of this application using JEP-343 jpackage tool

Notes:
   - These scripts have a few available command-line options.  To print out
the options, add ```-?``` or ```--help``` as an argument to any script.
   - These scripts share common properties that can be found in ```env.sh``` or ```env.ps1```.  These may need to be slightly modified to match  your specific configuration.
   - In order to generate ```EXE``` or ```MSI``` installers for Windows, ISSC and/or WiX toolkits must be installed respectively and placed on the %PATH% variable.
   
See also:

- maven-com.jtconnors.socket: https://github.com/jtconnors/maven-com.jtconnors.socket
