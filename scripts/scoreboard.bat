
SET JAVAFXRTDIR="C:\Program Files\Oracle\JavaFX 2.1 Runtime"
SET CPATH=%JAVAFXRTDIR%\lib\jfxrt.jar;%JAVAFXRTDIR%\bin;Scoreboard.jar

SET ARGS=-DumpConfig:false -DisplaySocket:true -UseIPSocket

java -cp %CPATH% scoreboard.fx2.Main %ARGS%
