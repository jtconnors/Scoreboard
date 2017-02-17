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
 *   * Neither the name of the TimingFramework project nor the names of its
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
 */

public class Constants {
    /*
     * This URL is relative to jar root.
     * Use "/" to indicate the jar root, and then append path to it.
     * Using config files outside the jar will have to specify a full URL.
     */
    public static final String DEFAULT_CONFIG_FILE =
            "/scoreboard/config/config.xml";
    
    public static final int DEFAULT_DIGIT_HEIGHT = 100;
    public static final int BLANK_DIGIT = 10;
    public final static int MIN_DIGIT_VALUE = 0;
    public final static int MAX_DIGIT_VALUE = 9;
    public final static int MIN_TWO_DIGIT_VALUE = 0;
    public final static int MAX_TWO_DIGIT_VALUE = 99;
    public final static int MAX_PENALTY_TIME = 599;
    public static final int MAX_CLOCK_TIME = 59999;
    /*
     * Space between digits reprsented as a fraction of the digitHeight
     */
    public static final double INTER_DIGIT_GAP_FRACTION = 0.14d;
    /*
     * LED segment constants:
     *
     * These first two constants below must add up to 1.0, according to
     * the following formula:
     *
     * 2L + 2E = 1.0
     */
    public static final float SEGMENT_LENGTH_TO_DIGIT_HEIGHT_RATIO = 0.45f;
    public static final float SEGMENT_EDGE_TO_DIGIT_HEIGHT_RATIO = 0.05f;
    public static final float DIGIT_WIDTH_TO_DIGIT_HEIGHT_RATIO =
            SEGMENT_LENGTH_TO_DIGIT_HEIGHT_RATIO +
            (2 *SEGMENT_EDGE_TO_DIGIT_HEIGHT_RATIO);

    /*
     * Scoreboard constants
     */
    public static final int DEFAULT_SCOREBOARD_WIDTH = 1024;
    public static final int DEFAULT_SCOREBOARD_HEIGHT = 600;
    
    /*
     * Default opacity of an unlit scoreboard bulb (range: 0-1)
     */
    public static final double DEFAULT_UNLIT_OPACITY = 0.1;

    /*
     * Messages sent over MulticastSocket.  Must specify a packet size
     * for received DatagramSocket messages.  Messages must not be
     * larger than the constant specified below.
     */
    public static final int MAX_DATAGRAM_MSG_SIZE = 1000;
    /*
     * Multicast Socket constants
     */
    public final static int DEFAULT_PORT = 2011;
    public final static String DEFAULT_SESSION_ADDR = "227.27.27.27";
    public final static String DEFAULT_HOST = "localhost";
    /*
     * Debug flags are a multiple of 2
     */
    public static final int DEBUG_NONE = 0x0;
    public static final int DEBUG_SEND = 0x1;
    public static final int DEBUG_RECV = 0x2;
    public static final int DEBUG_IO = DEBUG_SEND | DEBUG_RECV;
    public static final int DEBUG_EXCEPTIONS = 0x4;
    public static final int DEBUG_STATUS = 0x8;
    public static final int DEBUG_ALL =
            DEBUG_IO | DEBUG_EXCEPTIONS | DEBUG_STATUS;
}
