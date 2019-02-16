/*
 * Copyright (c) 2019, Jim Connors
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

package com.jtconnors.scoreboard.fx2.impl.led;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import com.jtconnors.scoreboard.common.Constants;
import com.jtconnors.scoreboard.fx2.framework.Digit;
import com.jtconnors.scoreboard.common.Globals;
import com.jtconnors.scoreboard.fx2.framework.FxConstants;

public class LEDDigit extends Digit {
    
    /*
     * An LEDDigitNode is comprised of 7 segments.  The individual
     * Segments can be addressed as follows:
     *
     *      000000000000
     *     1            2
     *     1            2
     *     1            2
     *      333333333333
     *     4            5
     *     4            5
     *     4            5
     *      666666666666
     *
     * A bitmask representing the 7 segments is stored in an Integer.
     * Setting a particular bit to 1 makes its corresponding
     * segment visible.  Here are the bitmasks for each addressable segment:
     */
    private static final int[] segmentBit = {
        0x1,         // segment 0
        0x2,         // segment 1
        0x4,         // segment 2
        0x8,         // segment 3
        0x10,        // segment 4
        0x20,        // segment 5
        0x40,        // segment 6
    };

    /*
     * Bit mask determines which segments should be turned on for each number
     */
    private static final int[] segmentBitMask = {
        0x77,  // 0
        0x24,  // 1
        0x5D,  // 2
        0x6D,  // 3
        0x2E,  // 4
        0x6B,  // 5
        0x7B,  // 6
        0x25,  // 7
        0x7F,  // 8
        0x2F,  // 9
        0x00   // Blank
    };
    
    private double segmentLength;
    private double edgeSz;  // Segment width = 2 * edgeSz
    
    private Segment[] segments = new Segment[segmentBit.length];
    
    /*
     * Abstract methods from Digit that must be defined.
     */
    @Override
    protected void refreshOnColorChange(Color color) {
        for (Segment segment:segments) {
           segment.setColor(color);
        }
    }

    @Override
    protected void refreshOnDigitHeightChange(double digitHeight) {
        getChildren().clear();
        init();
    }

    @Override
    protected void refreshOnValueChange(int value) {
        for (int i=0; i<segmentBit.length; i++) {
           segments[i].setSegmentLit(
                   ((segmentBitMask[value] & segmentBit[i]) != 0));
        }
    }

    
    private Rectangle boundingRect;

    /*
     * Constructors and helpers
     */
    public LEDDigit() {
        this(FxConstants.instance().DEFAULT_DIGIT_COLOR,
                Constants.instance().DEFAULT_DIGIT_HEIGHT, 0,
                Constants.instance().MIN_DIGIT_VALUE,
                Constants.instance().MAX_DIGIT_VALUE);
    }

    public LEDDigit(Color digitColor, double digitHeight) {
        this(digitColor, digitHeight, 0,
                Constants.instance().MIN_DIGIT_VALUE,
                Constants.instance().MAX_DIGIT_VALUE);
    }

    public LEDDigit(Color digitColor, double digitHeight, int value,
            int minValue, int maxValue) {
        super(digitColor, digitHeight, value, minValue, maxValue);
        init();
    }

    private void init() {
        segmentLength =
                (getDigitHeight() * 
                Constants.instance().SEGMENT_LENGTH_TO_DIGIT_HEIGHT_RATIO);
        edgeSz = (getDigitHeight() * 
                Constants.instance().SEGMENT_EDGE_TO_DIGIT_HEIGHT_RATIO);
        segments = new Segment[segmentBit.length];
        getChildren().add(createSegments());
        getChildren().add(createBoundingRectangle());
    }

    private Group createSegments() {
        int displayValue = getValue();
        if ((displayValue == 0) && (isBlankIfZero())) {
            displayValue = Constants.instance().BLANK_DIGIT;
        }
        
        segments[0] = new Segment(segmentLength, false, getColor(),
                Globals.instance().unlitOpacity);
        segments[0].setLayoutX(edgeSz);
        segments[0].setLayoutY(0);
        segments[1] = new Segment(segmentLength, true, getColor(),
                Globals.instance().unlitOpacity);
        segments[1].setLayoutX(0);
        segments[1].setLayoutY(edgeSz);
        segments[2] = new Segment(segmentLength, true, getColor(),
                Globals.instance().unlitOpacity);
        segments[2].setLayoutX(segmentLength);
        segments[2].setLayoutY(edgeSz);
        segments[3] = new Segment(segmentLength, false, getColor(),
                Globals.instance().unlitOpacity);
        segments[3].setLayoutX(edgeSz);
        segments[3].setLayoutY(segmentLength);
        segments[4] = new Segment(segmentLength, true, getColor(),
                Globals.instance().unlitOpacity);
        segments[4].setLayoutX(0);
        segments[4].setLayoutY(segmentLength + edgeSz);
        segments[5] = new Segment(segmentLength, true, getColor(),
                Globals.instance().unlitOpacity);
        segments[5].setLayoutX(segmentLength);
        segments[5].setLayoutY(segmentLength + edgeSz);
        segments[6] = new Segment(segmentLength, false, getColor(),
                Globals.instance().unlitOpacity);
        segments[6].setLayoutX(edgeSz);
        segments[6].setLayoutY((2*segmentLength));
        
        refreshOnValueChange(displayValue);

        Group group = new Group();
        group.getChildren().addAll(segments);
        return group;
    }

    private Group createBoundingRectangle() {
        boundingRect = new Rectangle();
        boundingRect.setWidth(getLayoutBounds().getWidth());
        boundingRect.setHeight(getLayoutBounds().getHeight());
        boundingRect.setFill(Color.TRANSPARENT);
        Group group = new Group();
        group.getChildren().add(boundingRect);
        return group;
    }
}
