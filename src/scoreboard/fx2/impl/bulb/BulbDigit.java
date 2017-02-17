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

package scoreboard.fx2.impl.bulb;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import scoreboard.fx2.framework.Digit;
import scoreboard.common.Globals;
import static scoreboard.common.Constants.DEFAULT_DIGIT_HEIGHT;
import static scoreboard.common.Constants.MIN_DIGIT_VALUE;
import static scoreboard.common.Constants.MAX_DIGIT_VALUE;
import static scoreboard.common.Constants.BLANK_DIGIT;
import static scoreboard.fx2.framework.FxConstants.DEFAULT_DIGIT_COLOR;

public class BulbDigit extends Digit {

    /*
     * A Digit is comprised of 27 Bulbs.  The individual
     * Bulbs can be addressed as follows:
     *
     *     0  1  2  3   4
     *     5            6
     *     7            8
     *     9           10
     *     11 12 13 14 15
     *     16          17
     *     18          19
     *     20          21
     *     22 23 24 25 26
     *
     * Being smaller than 32, a bitmask representing the 27 bulbs can be stored
     * in an Integer.  Setting a particular bit to 1 makes its corresponding
     * Bulb visible.  Here are the bitmasks for each addressable Bulb:
     */
    private static final int[] bulbBit = {
        0x1,         // bulb 0
        0x2,         // bulb 1
        0x4,         // bulb 2
        0x8,         // bulb 3
        0x10,        // bulb 4
        0x20,        // bulb 5
        0x40,        // bulb 6
        0x80,        // bulb 7
        0x100,       // bulb 8
        0x200,       // bulb 9
        0x400,       // bulb 10
        0x800,       // bulb 11
        0x1000,      // bulb 12
        0x2000,      // bulb 13
        0x4000,      // bulb 14
        0x8000,      // bulb 15
        0x10000,     // bulb 16
        0x20000,     // bulb 17
        0x40000,     // bulb 18
        0x80000,     // bulb 19
        0x100000,    // bulb 20
        0x200000,    // bulb 21
        0x400000,    // bulb 22
        0x800000,    // bulb 23
        0x1000000,   // bulb 24
        0x2000000,   // bulb 25
        0x4000000    // bulb 26
    };

    /*
     * Bit mask determines which bulbs should be turned on for each digit
     */
    private static final int[] digitBitMask = {
        0x07FF8FFF,  // 0
        0x042A8550,  // 1
        0x07D5FD5F,  // 2
        0x07EAFD5F,  // 3
        0x042AFFF1,  // 4
        0x07EAFABF,  // 5
        0x07FFFABF,  // 6
        0x042A855F,  // 7
        0x0FFFFFFF,  // 8
        0x042AFFFF,  // 9
        0x00000000   // Blank
    };

    /*
     * This initializer represents the X and Y offsets for each bulb.  The
     * numbers here will be multiplied by the size of the bulb in pixels to
     * determine the actual (X,Y) ccordinate of each bulb.
     */
    private static final double[][] bulbOffset = {
        {0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0},
        {0, 1},                         {4, 1},
        {0, 2},                         {4, 2},
        {0, 3},                         {4, 3},
        {0, 4}, {1, 4}, {2, 4}, {3, 4}, {4, 4},
        {0, 5},                         {4, 5},
        {0, 6},                         {4, 6},
        {0, 7},                         {4, 7},
        {0, 8}, {1, 8}, {2, 8}, {3, 8}, {4, 8},
    };

    /*
     * Abstract methods from Digit that must be defined.
     */
    protected void refreshOnColorChange(Color color) {
        for (Bulb bulb:bulbs) {
           bulb.setColor(color);
        }
    }

    protected void refreshOnDigitHeightChange(double digitHeight) {
        getChildren().clear();
        init();
    }

    protected void refreshOnValueChange(int value) {
        for (int i=0; i<NBULBS; i++) {
           bulbs[i].setBulbLit(
               (digitBitMask[value] & bulbBit[i]) != 0 ? true : false);
        }
    }

    /*
     * Needed in order to correctly layout 2 Digits side by side.
     */
    private double bulbRadius;
    public double getBulbRadius() { return bulbRadius; }

    /*
     * Implementation variables
     */
    private static final int NBULBS = bulbBit.length;
    private Bulb[] bulbs;
    private Rectangle boundingRect;

    /*
     * Constructors and helpers
     */
    public BulbDigit() {
        this(DEFAULT_DIGIT_COLOR, DEFAULT_DIGIT_HEIGHT, 0,
                MIN_DIGIT_VALUE, MAX_DIGIT_VALUE);
    }

    public BulbDigit(Color digitColor, double digitHeight) {
        this(digitColor, digitHeight, 0, MIN_DIGIT_VALUE, MAX_DIGIT_VALUE);
    }

    public BulbDigit(Color digitColor, double digitHeight, int value,
            int minValue, int maxValue) {
        super(digitColor, digitHeight, value, minValue, maxValue);
        init();
    }

    private void init() {
        bulbRadius = getDigitHeight() / 18;
        bulbs = new Bulb[NBULBS];
        getChildren().add(createBulbs());
        getChildren().add(createBoundingRectangle());
    }

    private Group createBulbs() {
        int displayValue = getValue();
        if ((displayValue == 0) && (isBlankIfZero())) {
            displayValue = BLANK_DIGIT;
        }
        for (int i=0; i<NBULBS; i++) {
           double centerX = bulbOffset[i][0] * bulbRadius * 2 + bulbRadius;
           double centerY = bulbOffset[i][1] * bulbRadius * 2 + bulbRadius;
           bulbs[i] = new Bulb(centerX, centerY, bulbRadius,
                   getColor(), Globals.unlitOpacity);
           bulbs[i].setBulbLit(
               (digitBitMask[displayValue] & bulbBit[i]) != 0 ? true : false);
        }
        Group group = new Group();
        group.getChildren().addAll(bulbs);
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
