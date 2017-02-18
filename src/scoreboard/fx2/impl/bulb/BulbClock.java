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

import javafx.scene.paint.Color;
import scoreboard.common.Constants;
import scoreboard.fx2.framework.Timer;
import scoreboard.fx2.framework.Clock;
import scoreboard.fx2.framework.FxConstants;
import scoreboard.fx2.framework.ScoreboardWithClock;

/*
 * This class extends the abstract Clock class and must be written in
 * the following way:
 *    1. The constructor must call super(digitColor, digitHeight) first.
 *    2. A Specific implementation for the abstract Digit class must be created.
 *       Once done, the Digit variables (defined in the
 *       super class) must be assigned and allocated an object
 *       of the extended Digit class.  This has to be placed
 *       inside the constructor immediately after the call to super().
 *    3. Follwing the Digit allocations, a call to init()
 *       (defined in the super class) must be made in the constructor.
 */
public class BulbClock extends Clock {

    public BulbClock(String varName, ScoreboardWithClock scoreboardWithClock,
            Timer timer) {
        this(varName, scoreboardWithClock, timer,
                FxConstants.instance().DEFAULT_DIGIT_COLOR,
                Constants.instance().DEFAULT_DIGIT_HEIGHT);
    }

    public BulbClock(String varName, ScoreboardWithClock scoreboardWithClock,
            Timer timer, Color digitColor,
            double digitHeight) {
        super(varName, scoreboardWithClock, timer, digitColor, digitHeight);
        /*
         * The following variables are implementation specific and must
         * be changed for each implementation of this class.
         */
        tenMinutesDigit = new BulbDigit(getColor(), getDigitHeight());
        minutesDigit = new BulbDigit(getColor(), getDigitHeight());
        tenSecondsDigit = new BulbDigit(getColor(), getDigitHeight());
        secondsDigit = new BulbDigit(getColor(), getDigitHeight());
        /*
         * End implementation specific section.
         */
        init();
    }
}
