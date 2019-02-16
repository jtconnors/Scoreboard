
module scoreboard {
    requires java.base;
    requires com.jtconnors.socket;
    requires java.datatransfer;
    requires java.desktop;
    requires java.logging;
    requires java.prefs;
    requires java.xml;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires jdk.jsobject;
    exports com.jtconnors.scoreboard.fx2;
    /*
     * Test packages - can be removed in production
     */
    exports com.jtconnors.test.scoreboard.fx2.impl.bulb;
    exports com.jtconnors.test.scoreboard.fx2.impl.led;
}
