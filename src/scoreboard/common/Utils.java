/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scoreboard.common;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author jtconnor
 */
public class Utils {
    
    /**
     * Convert an (@code Exception}'s stack trace to a {@code String}
     * @param e The {@code Exception} in question
     * @return the {@code Exception} stack trace as a {@code String}
     */
    public static String ExceptionStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return(sw.toString());
    }
    
}
