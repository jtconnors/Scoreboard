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

package scoreboard.fx2.impl.led;

import javafx.scene.paint.Color;
import scoreboard.fx2.framework.TwoDigit;
import static scoreboard.common.Constants.DEFAULT_DIGIT_HEIGHT;
import static scoreboard.common.Constants.MIN_TWO_DIGIT_VALUE;
import static scoreboard.common.Constants.MAX_TWO_DIGIT_VALUE;
import static scoreboard.fx2.framework.FxConstants.DEFAULT_DIGIT_COLOR;

/*
 * This class extends the abstract TwoDigit class and must be written in
 * the following way:
 *    1. The constructor must call super(digitColor, digitHeight) first.
 *    2. A Specific implementation for the abstract Digit class must be created.
 *       Once done, the tensDigit and onesDigit variables (defined in the
 *       super class) must be assigned and allocated an object
 *       of the extended Digit class.  This has to be placed
 *       inside the constructor immediately after the call to super().
 *    3. Follwing the tensDigit and onesDigit allocations, a call to init()
 *       (defined in the super class) must be made in the constructor.
 */
public class LEDTwoDigit extends TwoDigit {

    public LEDTwoDigit(String varName) {
        this(varName, DEFAULT_DIGIT_COLOR, DEFAULT_DIGIT_HEIGHT, 0,
                MIN_TWO_DIGIT_VALUE, MAX_TWO_DIGIT_VALUE);
    }

    public LEDTwoDigit(String varName, Color color, double digitHeight) {
        this(varName, color, digitHeight, 0,
                MIN_TWO_DIGIT_VALUE, MAX_TWO_DIGIT_VALUE);
    }

    public LEDTwoDigit(String varName, Color color, double digitHeight,
            int overallValue, int minOverallValue, int maxOverallValue) {
        super(varName, color, digitHeight, overallValue, minOverallValue,
                maxOverallValue);
        /*
         * The following variables are implementation specific and must
         * be changed for each implementation of this class.
         */
        tensDigit = new LEDDigit(getColor(), getDigitHeight(),
                getOverallValue() / 10, minOverallValue, maxOverallValue);
        onesDigit = new LEDDigit(getColor(), getDigitHeight(),
                getOverallValue() % 10, minOverallValue, maxOverallValue);
        /*
         * End implementation specific section.
         */
        init();
    }
}
