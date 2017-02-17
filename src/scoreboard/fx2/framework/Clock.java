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

package scoreboard.fx2.framework;

import scoreboard.common.Globals;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import static scoreboard.common.Constants.MAX_CLOCK_TIME;
import static scoreboard.common.Constants.BLANK_DIGIT;
import static scoreboard.common.Constants.DEFAULT_DIGIT_HEIGHT;
import static scoreboard.common.Constants.INTER_DIGIT_GAP_FRACTION;
import static scoreboard.fx2.framework.FxConstants.DEFAULT_DIGIT_COLOR;

/*
 * This abstract class defines the behavior of a scoreboard clock consisting
 * of 4 digits.  An implementation which extends this class must:
 *    1. Also extend the Digit class
 *    2. Associate that extended Digit class type with the 4 digit variables.
 *    3. Allocate the 4 digit objects inside the extended
 *       Clock constructor after the call to super() and before the
 *       call to init().
 *
 *  For an example implementation of Clock, look at the
 *  scoreboard.fx2.impl.bulb.BulbClock.java code.
 */
public abstract class Clock extends DisplayableWithDigits {
    
    private ScoreboardWithClock scoreboardWithClock;

    /*
     * Non-abstract instances of the following Digit variables must
     * be allocated in the implemeting subclass constructors:
     */

    /*
     * tenMinutesDigitNode displays the number of minutes in the tens place
     * remaining on the clock.  It is the first (leftmost) digit
     * in the Clock.
     */
    protected Digit tenMinutesDigit;
    /*
     * minutesDigit displays the number of minutes remaining on the
     * Clock.  It is the second (from left) digit in the Clock.
     */
    protected Digit minutesDigit;
    /*
     * tenSecondsDigit displays the number of seconds in the tens place
     * remaining on the clock.  It is the third (from left) digit
     * in the Clock.
     */
    protected Digit tenSecondsDigit;
    /*
     * secondsDigit displays the number of seconds in the ones place
     * remaining on the clock.  It is the fourth (from left) digit
     * in the Clock.
     */
    protected Digit secondsDigit;

    /**
     * A Timer instance must be provided to this class at
     * instantiation time.
     */
    protected Timer timer;

    public Timer getTimer() {
        return timer;
    }

    /*
     * Depending on how much time is left, either of these two things
     * will be displayed.  During normal operation of the clock,
     * a colon (":") is displayed between the minutesDigit and the
     * tenSecondsDigit.  When there is less than 60 seconds left on the clock,
     * display a decimal point (".") instead.  In this scenario, the
     * tenMinutesDigit represents the number tens of seconds remaining, the
     * minutesDigit represents the number of seconds remaining, and
     * the tenSecondsDigit represents ths number of tenth seconds remaining.
     */
    private Circle bottomPartOfColon;
    private Circle topPartOfColon;
    private Circle decimalPoint;

/****************************************************************************
 *   By virtue of extending the DisplayableWithDigits class, the following  *
 *   abstract methods declared in ParentsWithDigits must be defined.        *
 ****************************************************************************/

    /*
     * Creates the KeyPads associated with each Clock Digit.
     */
    protected Group createKeyPads() {
        Group group = new Group();
        tenMinutesDigit.normalKeyPad = new KeyPad(
                tenMinutesDigit.getLayoutBounds().getWidth(),
                0, 9, tenMinutesDigit, (DisplayableWithDigits)this);
        tenMinutesDigit.keyPad = tenMinutesDigit.normalKeyPad;
        minutesDigit.keyPad = new KeyPad(
                minutesDigit.getLayoutBounds().getWidth(),
                0, 9, minutesDigit, (DisplayableWithDigits)this);
        tenSecondsDigit.normalKeyPad = new KeyPad(
                tenSecondsDigit.getLayoutBounds().getWidth(),
                0, 5, tenSecondsDigit, (DisplayableWithDigits)this);
        tenSecondsDigit.keyPad = tenSecondsDigit.normalKeyPad;
        secondsDigit.keyPad = new KeyPad(
                secondsDigit.getLayoutBounds().getWidth(),
                0, 9, secondsDigit, (DisplayableWithDigits)this);
        for (final Digit d : digitArr) {
            d.keyPad.setVisible(false);
            d.setAction(new FunctionPtr() {
                public void invoke() {
                    d.displayKeyPad();
                }
            });
            group.getChildren().add(d.keyPad);
        }
        /*
         * Aternate KeyPads needed for tenMinutesDigit and TenSecondsDigit
         */
        tenMinutesDigit.alternateKeyPad = new KeyPad(
                tenMinutesDigit.getLayoutBounds().getWidth(),
                0, 5, tenMinutesDigit, (DisplayableWithDigits)this);
        group.getChildren().add(tenMinutesDigit.alternateKeyPad);
        tenMinutesDigit.alternateKeyPad.setVisible(false);
        tenSecondsDigit.alternateKeyPad = new KeyPad(
                tenSecondsDigit.getLayoutBounds().getWidth(),
                0, 9, tenSecondsDigit, (DisplayableWithDigits)this);
        group.getChildren().add(tenSecondsDigit.alternateKeyPad);
        tenSecondsDigit.alternateKeyPad.setVisible(false);
        return group;
    }

    /*
     * This is where all of the work is done to properly size and position
     * the nodes that comprise the Clock object.  This method is called
     * at initialization, and anytime a digitHeight resize event takes place.
     */
    protected void positionDigits() {
        getChildren().clear();
        tenMinutesDigit.setDigitHeight(getDigitHeight());
        double digitWidth = tenMinutesDigit.getLayoutBounds().getWidth();

        minutesDigit.setDigitHeight(getDigitHeight());
        minutesDigit.setLayoutX(digitWidth +
                (INTER_DIGIT_GAP_FRACTION * digitWidth));
        
        double radius = getDigitHeight() / 18;
        bottomPartOfColon.setRadius(radius);
        bottomPartOfColon.setLayoutY((2d  * getDigitHeight()) / 3d + radius);
        bottomPartOfColon.setLayoutX(minutesDigit.getLayoutX() +
                digitWidth + (radius * 2d));
        topPartOfColon.setRadius(radius);
        topPartOfColon.setLayoutY(getDigitHeight() / 3d - radius);
        topPartOfColon.setLayoutX(bottomPartOfColon.getLayoutX());
        decimalPoint.setRadius(radius);
        decimalPoint.setLayoutY(getDigitHeight() - radius);
        decimalPoint.setLayoutX(bottomPartOfColon.getLayoutX());

        tenSecondsDigit.setDigitHeight(getDigitHeight());
        tenSecondsDigit.setLayoutX(topPartOfColon.getLayoutX() + radius +
                radius);

        secondsDigit.setDigitHeight(getDigitHeight());
        secondsDigit.setLayoutX(tenSecondsDigit.getLayoutX() +
                digitWidth + (INTER_DIGIT_GAP_FRACTION * digitWidth));

        getChildren().addAll(tenMinutesDigit, minutesDigit,
                tenSecondsDigit, secondsDigit,
                bottomPartOfColon, topPartOfColon, decimalPoint);

        boundingRect.setFill(Color.TRANSPARENT);
        boundingRect.setWidth(getLayoutBounds().getWidth());
        boundingRect.setHeight(getLayoutBounds().getHeight());
        getChildren().add(boundingRect);
        
        componentWidth = boundingRect.getWidth();
        componentHeight = boundingRect.getHeight();
    }

    protected void refreshOnOverallValueChange(int overallValue) {
        setDigits();
        sendMessageToSocket(varName, String.valueOf(overallValue));
    }

    /*
     * Because the clock switches between standard mode and alternate mode
     * (when less than 60 seconds remain, the digits shift to display
     * tenths of seconds remaining), there are 2 cases to
     * take into account when a user wants to adjust the time with the
     * keyboard number (0-9) keys.  That is encapsulated here.
     */
    public int calculateKeyNumValue(Digit focusedDigit, KeyCode keyCode) {
        int key = keyCode.ordinal() - KeyCode.DIGIT0.ordinal();
        if (key < 0 || key > 9) {
            return 0;
        }
        /*
         * Case 1: Clock is in standard mode prior to any key input
         */
        if (getOverallValue() >= 600 || getOverallValue() == 0) {
            int increment = focusedDigit.getIncrementValue() * key;
            if (focusedDigit == secondsDigit) {
                return getOverallValue() -
                    (getOverallValue() % tenSecondsDigit.getIncrementValue()) +
                    increment;
            } else if (focusedDigit == tenSecondsDigit) {
                if (key >= 6) {
                    return getOverallValue();
                }
                return getOverallValue() -
                  (getOverallValue() % minutesDigit.getIncrementValue()) +
                  (getOverallValue() % tenSecondsDigit.getIncrementValue()) +
                  + increment;
            } else if (focusedDigit == minutesDigit) {
                return getOverallValue() -
                  (getOverallValue() % tenMinutesDigit.getIncrementValue()) +
                  (getOverallValue() % minutesDigit.getIncrementValue()) +
                  + increment;
            } else if (focusedDigit == tenMinutesDigit) {
                return (getOverallValue() %
                    tenMinutesDigit.getIncrementValue()) + increment;
            }
        }
        /*
         * Case 2: Clock  is in alternate mode prior to any key input
         */
        else  {
            int alternateIncrement =
                focusedDigit.getAlternateIncrementValue() * key;
            if (focusedDigit == tenSecondsDigit) {
                return getOverallValue() -
                    (getOverallValue() %
                        minutesDigit.getAlternateIncrementValue()) +
                    alternateIncrement;
            }
            if (focusedDigit == minutesDigit) {
                return getOverallValue() -
                  (getOverallValue() %
                      tenMinutesDigit.getAlternateIncrementValue()) +
                  (getOverallValue() %
                      minutesDigit.getAlternateIncrementValue()) +
                  + alternateIncrement;
            }
            if (focusedDigit == tenMinutesDigit) {
                if (key >= 6) {
                    return getOverallValue();
                }
                return (getOverallValue() %
                    tenMinutesDigit.getAlternateIncrementValue()) +
                    alternateIncrement;
            }
        }
        return getOverallValue();
    }

    /*
     * Because the clock switches between standard mode and alternate mode
     * (when less than 60 seconds remain, the digits shift to display
     * tenths of seconds remaining), there are all types of crazy cases to
     * take into account when a user wants to adjust the time with the
     * keyboard up arrow keys.  That craziness is encapsulated here.
     */
    protected int calculateKeyUpValue(Digit focusedDigit) {
        int increment = focusedDigit.getIncrementValue();
        int alternateIncrement = focusedDigit.getAlternateIncrementValue();
        /*
         * Case 1 VK_UP: Standard Clock mode, increment focused digit with
         * its incrementValue, stays in standard mode.
         */
        if (getOverallValue() >= 600) {
            return getOverallValue() + increment;
        }
        /*
         * Case 2 VK_UP: If the clock is set to zero and incrementing the
         * focused digit by its incrementValue will put the
         * clock in alternate clock mode.
         */
        if ((getOverallValue() == 0) &&
            ((getOverallValue() + increment) < 600)) {
            if (focusedDigit == secondsDigit) {
                focusedDigit.unShowFocusHint();
                Globals.lastFocused = minutesDigit;
                Globals.lastFocused.showFocusHint();
                return getOverallValue() + increment;
            } else if (focusedDigit == tenSecondsDigit) {
                focusedDigit.unShowFocusHint();
                Globals.lastFocused = tenMinutesDigit;
                Globals.lastFocused.showFocusHint();
                return getOverallValue() + increment;
            }
        }
        /*
         * Case 3 VK_UP: If the clock is set to zero and incrementing the
         * focused digit by its incrementValue will put the clock in
         * standard clock mode.
         */
        if ((getOverallValue() == 0) &&
            ((getOverallValue() + increment) >= 600)) {
            return getOverallValue() + increment;
        }
        /*
         * Case 4 VK_UP: In alternate clock mode, incrementing the focused
         * digit by its alternateIncrementValue will still keep the clock
         * in alternate clock mode.
         */
        if ((getOverallValue() < 600) &&
            ((getOverallValue() + alternateIncrement) < 600)) {
            return getOverallValue() + alternateIncrement;
        }
        /*
         * Case 5 VK_UP: In alternate clock mode, incrementing the focused
         * digit by its alternateIncrementValue will put the clock in
         * standard clock mode.
         */
        if ((getOverallValue() < 600) &&
            ((getOverallValue() + alternateIncrement) >= 600)) {
            if (focusedDigit == tenMinutesDigit) {
                focusedDigit.unShowFocusHint();
                Globals.lastFocused = tenSecondsDigit;
                Globals.lastFocused.showFocusHint();
                return getOverallValue() + alternateIncrement;
            }
            if (focusedDigit == minutesDigit) {
                focusedDigit.unShowFocusHint();
                Globals.lastFocused = secondsDigit;
                Globals.lastFocused.showFocusHint();
                return getOverallValue() + alternateIncrement;
            }
            if (focusedDigit == tenSecondsDigit) {
                focusedDigit.unShowFocusHint();
                Globals.lastFocused = secondsDigit;
                Globals.lastFocused.showFocusHint();
                return getOverallValue() + alternateIncrement;
            }
        }
        System.err.println("Err: calculateNewValue() VK_UP fall through");
        return getOverallValue();
    }

    /*
     * Just as in calculateKeyUpValue() above, there are all types of
     * cases to account for when a user wants to adjust the time with the
     * keyboard down arrow key.
     */
    protected int calculateKeyDownValue(Digit focusedDigit) {
        int increment = focusedDigit.getIncrementValue();
        int alternateIncrement = focusedDigit.getAlternateIncrementValue();
        /*
         * Case 1 VK_DOWN: In standard clock mode, decrementing the
         * focused digit by its incrementValue will still keep the clock
         * in standard mode.
         */
        if ((getOverallValue() >= 600) &&
            ((getOverallValue() - increment) >= 600)) {
            return getOverallValue() - increment;
        }
        /*
         * Case 2 VK_DOWN: If the clock is in alternate mode and
         * decrementing the focused digit by its alternateIncrementValue
         * will set the clock to zero
         */
        if ((getOverallValue() < 600) &&
            ((getOverallValue() - alternateIncrement) == 0)) {
            if ((focusedDigit == tenSecondsDigit) ||
                (focusedDigit == minutesDigit)) {
                focusedDigit.unShowFocusHint();
                Globals.lastFocused = tenSecondsDigit;
                Globals.lastFocused.showFocusHint();
            } else if (focusedDigit == tenMinutesDigit) {
                focusedDigit.unShowFocusHint();
                Globals.lastFocused = tenSecondsDigit;
                Globals.lastFocused.showFocusHint();
            }
            return 0;
        }
         /*
         * Case 3 VK_DOWN: If the clock is in standard mode and
         * decrementing the focused digit by its incrementValue
         * will set the clock to zero
         */
        if ((getOverallValue() >= 600) &&
            ((getOverallValue() - increment) <= 9) &&
            ((getOverallValue() - increment) >= 0)) {
            return 0;
        }
        /*
         * Case 4 VK_DOWN: Alternate Clock mode, decrement focused digit
         * with its alternateIncrementValue
         */
        if (getOverallValue() < 600) {
            return getOverallValue() - alternateIncrement;
        }
        /*
         * Case 5 VK_DOWN: In standard clock mode where decrementing
         * the focused digit with its incrementValue will transition
         * the clock to the alternate clock mode.
         */
        if ((getOverallValue() >= 600) &&
            ((getOverallValue() - increment) < 600)) {
            if (focusedDigit == secondsDigit) {
                focusedDigit.unShowFocusHint();
                Globals.lastFocused = tenSecondsDigit;
                Globals.lastFocused.showFocusHint();
                return 599;
            } else if (focusedDigit == tenSecondsDigit) {
                focusedDigit.unShowFocusHint();
                Globals.lastFocused = minutesDigit;
                Globals.lastFocused.showFocusHint();
                return ((getOverallValue() - increment) -
                        (getOverallValue() - increment) % 10);
            } else if (focusedDigit == minutesDigit) {
                focusedDigit.unShowFocusHint();
                Globals.lastFocused = minutesDigit;
                Globals.lastFocused.showFocusHint();
                return ((getOverallValue() - increment) -
                        (getOverallValue() - increment) % 10);
            } else if (focusedDigit == tenMinutesDigit) {
                if (getOverallValue() < 3600) {
                    return -1;
                }
                focusedDigit.unShowFocusHint();
                Globals.lastFocused = minutesDigit;
                Globals.lastFocused.showFocusHint();
                return ((getOverallValue() - increment) -
                        (getOverallValue() - increment) % 10);
            }
        }
        System.err.println("Err: calculateNewValue() VK_DOWN fall through");
        return getOverallValue();
    }

/****************************************************************************
 *             End DisplayableWithDigits method definition section               *
 ****************************************************************************/

    /*
     * Based upon the amount of time left (overallValue), set the value
     * for each of the 4 displayable clock digits.
     */
    private void setDigits() {
        /*
         * Case 1: Normal operation, when more than one minute remains on
         *         the clock.
         */
        if (getOverallValue() >= 600) {
            tenMinutesDigit.setBlankIfZero(true);
            tenMinutesDigit.setValue(getOverallValue() / 6000);
            minutesDigit.setBlankIfZero(
                    getOverallValue() < 600 ? true : false);
            tenSecondsDigit.setBlankIfZero(
                    getOverallValue() < 100 ? true : false);
            minutesDigit.setValue((getOverallValue() % 6000) / 600);
            tenSecondsDigit.setValue((getOverallValue() % 600) / 100);
            secondsDigit.setBlankIfZero(false);
            secondsDigit.setValue((getOverallValue() % 100) / 10);
            decimalPoint.setVisible(false);
            topPartOfColon.setVisible(true);
            bottomPartOfColon.setVisible(true);
            tenMinutesDigit.keyPad = tenMinutesDigit.normalKeyPad;
            tenSecondsDigit.keyPad = tenSecondsDigit.normalKeyPad;
        }
        /*
         * Case 2: When there is less than 1 minute left, the meaning of
         *         the digits change:
         *         tenMinutesDigit => tens of seconds temainaing
         *         minutesDigit => seconds remaining
         *         tenSecondsDigit => tenths of seconds remaining
         *         secondsDigit => blank
         *
         *         Also display a decimal point (".") rather than the
         *         usual colon (":") between the minutesDigit and the
         *         tenSecondsDigit
         */
        else if ((getOverallValue() < 600) && (getOverallValue() > 0)) {
            tenMinutesDigit.setBlankIfZero(
                    getOverallValue() < 1000 ? true : false);
            tenMinutesDigit.setValue(getOverallValue() / 100);
            minutesDigit.setBlankIfZero(false);
            minutesDigit.setValue((getOverallValue() % 100) / 10);
            tenSecondsDigit.setBlankIfZero(false);
            tenSecondsDigit.setValue(getOverallValue() % 10);
            secondsDigit.setValue(BLANK_DIGIT);
            decimalPoint.setVisible(true);
            topPartOfColon.setVisible(false);
            bottomPartOfColon.setVisible(false);
            tenMinutesDigit.keyPad = tenMinutesDigit.alternateKeyPad;
            tenSecondsDigit.keyPad = tenSecondsDigit.alternateKeyPad;
        }
        /*
         * Case 3: When no time is remaining, show all zeroes.
         */
        else if (getOverallValue() == 0) {
            tenMinutesDigit.setValue(0);
            minutesDigit.setValue(0);
            tenSecondsDigit.setValue(0);
            secondsDigit.setValue(0);
            tenMinutesDigit.setBlankIfZero(false);
            minutesDigit.setBlankIfZero(false);
            tenSecondsDigit.setBlankIfZero(false);
            secondsDigit.setBlankIfZero(false);
            decimalPoint.setVisible(false);
            topPartOfColon.setVisible(true);
            bottomPartOfColon.setVisible(true);
            tenMinutesDigit.keyPad = tenMinutesDigit.normalKeyPad;
            tenSecondsDigit.keyPad = tenSecondsDigit.normalKeyPad;
        }
    }

    /*
     * Constructors
     */
    public Clock(String varName, ScoreboardWithClock scoreboardWithClock,
            Timer timer) {
        this(varName, scoreboardWithClock, timer,
                DEFAULT_DIGIT_COLOR, DEFAULT_DIGIT_HEIGHT);
    }

    public Clock(String varName, ScoreboardWithClock scoreboardWithClock, 
            Timer timer, Color color) {
        this(varName, scoreboardWithClock, timer, color, DEFAULT_DIGIT_HEIGHT);
    }

    public Clock(String varName, ScoreboardWithClock scoreboardWithClock,
            Timer timer, double digitHeight) {
        this(varName, scoreboardWithClock, timer,
                DEFAULT_DIGIT_COLOR, digitHeight);
    }

    public Clock(String varName, ScoreboardWithClock scoreboardWithClock,
            Timer timer, Color color, double digitHeight) {
        super();  // Must call superclass constructor first
        this.varName = varName;
        this.scoreboardWithClock = scoreboardWithClock;
        this.timer = timer;
        colorProperty().setValue(color);
        digitHeightProperty().setValue(digitHeight);
        overallValueProperty().setValue(0);
        this.minOverallValue = 0;
        this.maxOverallValue = MAX_CLOCK_TIME;
    }

    /*
     * This method must be called by the implementing constructor and must
     * follow the allocation of the Clock digits.
     */
    protected void init() {
        tenMinutesDigit.setIncrementValue(6000);
        tenMinutesDigit.setAlternateIncrementValue(100);
        digitArr.add(tenMinutesDigit);

        minutesDigit.setIncrementValue(600);
        minutesDigit.setAlternateIncrementValue(10);
        digitArr.add(minutesDigit);

        tenSecondsDigit.setIncrementValue(100);
        tenSecondsDigit.setAlternateIncrementValue(1);
        digitArr.add(tenSecondsDigit);

        secondsDigit.setIncrementValue(10);
        secondsDigit.setAlternateIncrementValue(0);
        digitArr.add(secondsDigit);

        topPartOfColon = new Circle();
        topPartOfColon.setFill(Color.WHITE);
        bottomPartOfColon = new Circle();
        bottomPartOfColon.setFill(Color.WHITE);
        decimalPoint = new Circle();
        decimalPoint.setFill(Color.WHITE);
        decimalPoint.setVisible(false);

        positionDigits();
        getChildren().add(createKeyPads());

        /*
         * Set up Arrow Key Traversal
         */
        tenMinutesDigit.setKeyLeftNode(tenMinutesDigit);
        tenMinutesDigit.setKeyRightNode(minutesDigit);
        minutesDigit.setKeyLeftNode(tenMinutesDigit);
        minutesDigit.setKeyRightNode(tenSecondsDigit);
        tenSecondsDigit.setKeyLeftNode(minutesDigit);
        tenSecondsDigit.setKeyRightNode(secondsDigit);
        secondsDigit.setKeyLeftNode(tenSecondsDigit);
        secondsDigit.setKeyRightNode(secondsDigit);

        /*
         * Set up the Timer handler.  A Timer instance is provided to this
         * constructor.
         */
        FunctionPtr handler = new FunctionPtr() {
            public void invoke() {
                if (getOverallValue() > 0) {
                    setOverallValue(getOverallValue()-1);
                } else {
                    getTimer().stop();
                    if (scoreboardWithClock != null) {
                        scoreboardWithClock.soundHorn();
                    }
                }
            }
        };
        if (timer != null) {
            getTimer().addHandler(handler);
        }
    }
}