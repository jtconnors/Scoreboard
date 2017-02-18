/*
 * Copyright (c) 2013, Jim Connors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of this project nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package scoreboard.common;

/*
 * This class contains constatnts that are common across platforms.  Only
 * primitive types should be used here to avoid ambiguity.
 *
 * It follows the Singleton design pattern and takes advantage of the 
 * properties of the Java Virtual Machine such that initialiazion of the
 * class instance will be done in a thread safe manner.
 */

public class Constants {   
    private Constants() {}
    
    private static class LazyHolder {
        private static final Constants INSTANCE = new Constants();
    }
    
    public static Constants instance() {
        return LazyHolder.INSTANCE;
    }
    
    /*
     * These URLs are relative to jar root.
     * Use "/" to indicate the jar root, and then append path to it.
     * Using files outside the jar will have to specify a full URL.
     */
    public final String DEFAULT_CONFIG_FILE =
            "/scoreboard/config/config.xml";
    public final String DEFAULT_HORN_FILE =
            "/scoreboard/util/sounds/BUZZER.mp3";
    
    public final int DEFAULT_DIGIT_HEIGHT = 100;
    public final int BLANK_DIGIT = 10;
    public final int MIN_DIGIT_VALUE = 0;
    public final int MAX_DIGIT_VALUE = 9;
    public final int MIN_TWO_DIGIT_VALUE = 0;
    public final int MAX_TWO_DIGIT_VALUE = 99;
    public final int MAX_PENALTY_TIME = 599;
    public final int MAX_CLOCK_TIME = 59999;
    /*
     * Space between digits reprsented as a fraction of the digitHeight
     */
    public final double INTER_DIGIT_GAP_FRACTION = 0.14d;
    /*
     * LED segment constants:
     *
     * These first two constants below must add up to 1.0, according to
     * the following formula:
     *
     * 2L + 2E = 1.0
     */
    public final float SEGMENT_LENGTH_TO_DIGIT_HEIGHT_RATIO = 0.45f;
    public final float SEGMENT_EDGE_TO_DIGIT_HEIGHT_RATIO = 0.05f;
    public final float DIGIT_WIDTH_TO_DIGIT_HEIGHT_RATIO =
            SEGMENT_LENGTH_TO_DIGIT_HEIGHT_RATIO +
            (2 *SEGMENT_EDGE_TO_DIGIT_HEIGHT_RATIO);

    /*
     * Scoreboard constants
     */
    public final int DEFAULT_SCOREBOARD_WIDTH = 1024;
    public final int DEFAULT_SCOREBOARD_HEIGHT = 600;
    
    /*
     * Default opacity of an unlit scoreboard bulb (range: 0-1)
     */
    public final double DEFAULT_UNLIT_OPACITY = 0.1;

    /*
     * Debug flags are a multiple of 2
     * The following are defined in com.jtconnors.socket.DebugFlags.java:
     *
     * public final int DEBUG_NONE = 0x0;
     * public final int DEBUG_SEND = 0x1;
     * public final int DEBUG_RECV = 0x2;
     * public final int DEBUG_EXCEPTIONS = 0x4;
     * public final int DEBUG_STATUS = 0x8;
     */  
    public final int DEBUG_XMLOUTPUT = 0x10;
    /*
     * Horn on/off
     */
    public final int HORN_OFF = 0;
    public final int HORN_ON = 1;
}
