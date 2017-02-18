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
import java.lang.reflect.Field;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import scoreboard.common.Constants;
import scoreboard.common.DigitsDisplayStates;

/*
 * This abstract class defines the behavior of a Displayable object
 * (comprised of 5 digits) describing a scoreboard penalty.
 * The leftmost two Digit represent the player number assigned the penalty.
 * The rightmost three Digits represent the amount of time (in minutes
 * and seconds) remaining in the penalty.
 * An implementation which extends this class must:
 *    1. Also extend the Digit class
 *    2. Associate that extended Digit class type with the playerNumber,
 *       minutesDigit, tenSecondsDigit and secondsDigit variables.
 *    3. Allocate the playerNumber, minutesDigit, tenSecondsDigit and
 *       secondsDigit objects inside the extended Penalty constructor
 *       after the call to super() and before the
 *       call to init().
 *
 *  For an example implementation of Penalty, look at the
 *  scoreboard.fx2.impl.bulb.BulbPenalty.java code.
 */
public abstract class Penalty extends DisplayableWithDigits {

    private static final double DASH_WIDTH_FRACTION = .10d;
    private static final double DASH_HEIGHT_FRACTION = .05d;

    /*
     * Non-abstract instances of the following Digit variables must
     * be allocated in the implemeting subclass constructors:
     */

    /*
     * playerNumber displays the uniform number of the player assigned
     * the infraction.  It is the first (leftmost) digit sequence
     * in the Penalty object.  We give external access to the playerNumber
     * object via getPlayerNumber(), so that, for example, someone can
     * set the color of the playerNumber digits to a color different than
     * penalty timer digits.
     * 
     * Note: playerNumber is decalred public so that we can perform
     * reflection on it.  This is needed for receiving XML penalty player
     * number updates.
     */
    public TwoDigit playerNumber;
    public TwoDigit getPlayerNumber() { return playerNumber; }
    public Field getPlayerNumberField() {
        try {
            return getClass().getField("playerNumber");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * This is a bit of a kludge.  When using the keyboard right arrow key to
     * move between the digits of this object, need to place a transition node
     * between the two digits representing the playerNumber and the digits
     * that represent the time remaining time in the penalty.  This is required
     * because multiple calls to processKeyEvent() will take place during
     * the transition, causing the minutes digit to be passed over.
     */
    private FocusableParent transitionKludge;
    /*
     * minutesDigit displays the number of minutes remaining on the
     * penalty.  It is diplayed to the right of the plaeryNumber, and
     * represents leftmost digit in the Penalty timer.
     */
    protected Digit minutesDigit;
    /*
     * tenSecondsDigit displays the number of seconds in the tens place
     * remaining on the penalty.  It is the middle digit in the Penalty
     * timer.
     */
    protected Digit tenSecondsDigit;
    /*
     * secondsDigit displays the number of seconds in the ones place
     * remaining on the penalty.  It is the rightmost digit in both the
     * Penalty timer and the overall Penalty object.
     */
    protected Digit secondsDigit;

    /**
     * A Timer instance must be provided to this class at
     * instantiation time.  The resolution of the timer is in tenths of
     * seconds.  The visible resolution of the timer is in seconds.  The
     * number of seconds remaining in the penalty timer is stored in the
     * overallValue variable, inherited from the DisplayableWithDigits class.
     */
    private int tenthsRemaining;
    protected Timer timer;

    public Timer getTimer() {
        return timer;
    }

    /*
     * When a penalty is active, a dash ("-") is displayed between
     * the player number assigned the infraction and the penalty timer.
     * Additionally, a colon (":") is displayed between the minutesDigit
     * and the tenSecondsDigit of the timer.
     */
    private Rectangle dash;
    private Circle bottomPartOfColon;
    private Circle topPartOfColon;
    
    /*
     * Visual cue via popup to let user know that the Penalty player
     * number cannot be set until a penalty time is specified.
     */
    private final String tipStr = 
            "Penalty time must be\nset prior to player number";
    private final Tooltip tooltip = new Tooltip(tipStr);
    /*
     * Mechanism to block playerNumber when not in use.
     */
    private Rectangle playerNumberMouseBlocker;

/*
 ****************************************************************************
 *   By virtue of extending the DisplayableWithDigits class, the following  *
 *   abstract methods declared in ParentsWithDigits must be defined.        *
 ****************************************************************************/

    /*
     * Creates the KeyPads associated with the Penalty Timer Digits.  The
     * playerNumber Digits will be created by the TwoDigits createKeyPads()
     * method.
     */
    @Override
    protected Group createKeyPads() {
        Group group = new Group();
        minutesDigit.keyPad = new KeyPad(
                minutesDigit.getLayoutBounds().getWidth(),
                0, 9, minutesDigit, (DisplayableWithDigits)this);
        tenSecondsDigit.keyPad = new KeyPad(
                tenSecondsDigit.getLayoutBounds().getWidth(),
                0, 5, tenSecondsDigit, (DisplayableWithDigits)this);
        secondsDigit.keyPad = new KeyPad(
                secondsDigit.getLayoutBounds().getWidth(),
                0, 9, secondsDigit, (DisplayableWithDigits)this);
        for (final Digit d : digitArr) {
            d.keyPad.setVisible(false);
            d.setAction(new FunctionPtr() {
                @Override
                public void invoke() {
                    d.displayKeyPad();
                }
            });
            group.getChildren().add(d.keyPad);
        }
        return group;
    }

    /*
     * This is where all of the work is done to properly size and position
     * the nodes that comprise the Penalty object.  This method is called
     * at initialization, and anytime a digitHeight resize event takes place.
     */
    @Override
    protected void positionDigits() {
        getChildren().clear();

        playerNumber.setDigitHeight(getDigitHeight());
        double digitWidth =
                playerNumber.getOnesDigit().getLayoutBounds().getWidth();
       
        dash.setHeight(getDigitHeight() * DASH_HEIGHT_FRACTION);
        dash.setWidth(getDigitHeight() * DASH_WIDTH_FRACTION);
        dash.setLayoutX(playerNumber.getLayoutBounds().getWidth() +
                (Constants.instance().INTER_DIGIT_GAP_FRACTION * digitWidth));
        dash.setLayoutY((getDigitHeight() - dash.getHeight()) / 2);

        minutesDigit.setDigitHeight(getDigitHeight());
        minutesDigit.setLayoutX(dash.getLayoutX() +
                dash.getLayoutBounds().getWidth() +
                (Constants.instance().INTER_DIGIT_GAP_FRACTION * digitWidth));

        double radius = getDigitHeight() / 18;
        bottomPartOfColon.setRadius(radius);
        bottomPartOfColon.setLayoutY((2d  * getDigitHeight()) / 3d + radius);
        bottomPartOfColon.setLayoutX(minutesDigit.getLayoutX() +
                digitWidth + (radius * 2d));
        topPartOfColon.setRadius(radius);
        topPartOfColon.setLayoutY(getDigitHeight() / 3d - radius);
        topPartOfColon.setLayoutX(bottomPartOfColon.getLayoutX());

        tenSecondsDigit.setDigitHeight(getDigitHeight());
        tenSecondsDigit.setLayoutX(topPartOfColon.getLayoutX() + radius +
                radius);

        secondsDigit.setDigitHeight(getDigitHeight());
        secondsDigit.setLayoutX(tenSecondsDigit.getLayoutX() +
                digitWidth + (Constants.instance().INTER_DIGIT_GAP_FRACTION *
                digitWidth));
        /*
         * Mechanism to block input to player number when not in use
         */
        playerNumberMouseBlocker = new Rectangle();
        playerNumberMouseBlocker.setWidth(
                playerNumber.getLayoutBounds().getWidth());
        playerNumberMouseBlocker.setHeight(
                playerNumber.getLayoutBounds().getHeight());
        playerNumberMouseBlocker.setFill(Color.TRANSPARENT);
        playerNumberMouseBlocker.setVisible(false);

        getChildren().addAll(playerNumber, dash,
                minutesDigit, tenSecondsDigit, secondsDigit,
                bottomPartOfColon, topPartOfColon, playerNumberMouseBlocker);

        boundingRect.setWidth(getLayoutBounds().getWidth());
        boundingRect.setHeight(getLayoutBounds().getHeight());
        boundingRect.setFill(Color.TRANSPARENT);
        getChildren().add(boundingRect);
        
        componentWidth = boundingRect.getWidth();
        componentHeight = boundingRect.getHeight();
    }

    @Override
    protected void refreshOnOverallValueChange(int overallValue) {
        tenthsRemaining = overallValue * 10;
        setDigits();
        sendMessageToSocket(varName, String.valueOf(overallValue));
    }

    @Override
    protected int calculateKeyNumValue(Digit focusedDigit, KeyCode keyCode) {
        int key = keyCode.ordinal() - KeyCode.DIGIT0.ordinal();
        int updatedValue = getOverallValue();
        boolean validUpdate = true;
        if (focusedDigit == minutesDigit) {
            updatedValue = (key * 60) + (getOverallValue() % 60);
        } else if (focusedDigit == tenSecondsDigit) {
            if (key >= 6) {
                validUpdate = false;
            } else {
                updatedValue = key * 10 + getOverallValue() -
                       (getOverallValue() % 60) + (getOverallValue() % 10);
            }
        } else {
            updatedValue = key + getOverallValue() - (getOverallValue() % 10);
        }
        return (validUpdate) ? updatedValue : getOverallValue();
    }

    @Override
    protected int calculateKeyUpValue(Digit focusedDigit) {
        return getOverallValue() + focusedDigit.getIncrementValue();
    }

    @Override
    protected int calculateKeyDownValue(Digit focusedDigit) {
        return getOverallValue() - focusedDigit.getIncrementValue();
    }

/****************************************************************************
 *             End DisplayableWithDigits method definition section               *
 ****************************************************************************/

    /*
     * Based upon the amount of time left (overallValue), set the value
     * for each of the 3 displayable timer digits.
     */
    private void setDigits() {

        if (getOverallValue() == 0) {
            minutesDigit.setValue(0);
            tenSecondsDigit.setValue(0);
            secondsDigit.setValue(0);
            minutesDigit.setBlankIfZero(true);
            tenSecondsDigit.setBlankIfZero(true);
            secondsDigit.setBlankIfZero(true);
            topPartOfColon.setVisible(false);
            bottomPartOfColon.setVisible(false);
        } else {
            minutesDigit.setBlankIfZero((getOverallValue() < 60));
            tenSecondsDigit.setBlankIfZero(false);
            minutesDigit.setValue(getOverallValue() / 60);
            tenSecondsDigit.setValue((getOverallValue() % 60) / 10);
            secondsDigit.setBlankIfZero(false);
            secondsDigit.setValue(getOverallValue() % 10);
            topPartOfColon.setVisible(true);
            bottomPartOfColon.setVisible(true);
        }
    }

    /*
     * Constructors
     */
    public Penalty(String varName, Timer timer) {
        this(varName, timer, FxConstants.instance().DEFAULT_DIGIT_COLOR,
                Constants.instance().DEFAULT_DIGIT_HEIGHT);
    }

    public Penalty(String varName, Timer timer, Color color) {
        this(varName, timer, color,
                Constants.instance().DEFAULT_DIGIT_HEIGHT);
    }

    public Penalty(String varName, Timer timer, double digitHeight) {
        this(varName, timer, FxConstants.instance().DEFAULT_DIGIT_COLOR,
                digitHeight);
    }

    public Penalty(String varName, Timer timer, Color color,
            double digitHeight) {
        super();  // Must call superclass constructor first
        this.varName = varName;
        this.timer = timer;
        colorProperty().setValue(color);
        digitHeightProperty().setValue(digitHeight);
        overallValueProperty().setValue(0);
        this.minOverallValue = 0;
        this.maxOverallValue = Constants.instance().MAX_PENALTY_TIME;
    }
    
    /*
     * This method must be called by the implementing constructor and must
     * follow the allocation of the Clock digits.
     */
    protected void init() {
        
        dash = new Rectangle();
        dash.setFill(Color.WHITE);

        minutesDigit.setIncrementValue(60);
        digitArr.add(minutesDigit);

        tenSecondsDigit.setIncrementValue(10);
        tenSecondsDigit.setAlternateIncrementValue(0);
        digitArr.add(tenSecondsDigit);

        secondsDigit.setIncrementValue(1);
        secondsDigit.setAlternateIncrementValue(0);
        digitArr.add(secondsDigit);

        topPartOfColon = new Circle();
        topPartOfColon.setFill(Color.WHITE);
        bottomPartOfColon = new Circle();
        bottomPartOfColon.setFill(Color.WHITE);

        playerNumber.setAllowTrailingZeroes(true);
        playerNumber.setDigitsDisplayState(DigitsDisplayStates.BLANK);
        positionDigits();
        Tooltip.install(playerNumberMouseBlocker, tooltip);
        playerNumberMouseBlocker.setVisible(true);
        setDigits();

        getChildren().add(createKeyPads());

        /*
         * Set up Arrow Key Traversals.  Insert the transitionKludge
         * in between the onesDigit of the PlayerNumber and the minutesDigit
         * of the timer.
         */
        transitionKludge = new FocusableParent();
        playerNumber.getOnesDigit().setKeyRightNode(transitionKludge);
        transitionKludge.setKeyRightNode(minutesDigit);
        minutesDigit.setKeyLeftNode(playerNumber.getOnesDigit());
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
            @Override
            public void invoke() {
                if (tenthsRemaining > 0) {
                    tenthsRemaining -= 1;
                    if (tenthsRemaining == 0) {
                        playerNumber.setDigitsDisplayState(
                            DigitsDisplayStates.BLANK);
                        getTimer().stop();
                    }
                    if ((tenthsRemaining % 10) == 0) {
                        setOverallValue(tenthsRemaining / 10);
                    }
                } 
            }
        };
        if (timer != null) {
            getTimer().addHandler(handler);
        }

        /*
         * Listen in on any updates to the Penalty overallValue variable.
         * When it transitions to/from zero, change the display state.
         */
        overallValueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable ov) {
                // Transition from zero to non-zero
                if (getPrevOverallValue() == 0 && getOverallValue() > 0) {
                    playerNumber.setDigitsDisplayState(
                            DigitsDisplayStates.REGULAR);
                    playerNumberMouseBlocker.setVisible(false);
                } // Transition from non-zero to zero
                else if (getPrevOverallValue() > 0 && getOverallValue() == 0) {
                    playerNumber.setDigitsDisplayState(
                            DigitsDisplayStates.BLANK);
                    playerNumberMouseBlocker.setVisible(true);
                    playerNumber.setOverallValue(0);
                }
            }
        });
    }

    /*
     * Overrides the getFocusedDigit() method found in ParentsWithDigits to
     * accommodate for the inserted transitionKludge.
     */
    @Override
    protected FocusableParent getFocusedDigit() {
        for (Digit digit : digitArr) {
            if (digit.hasFocusHint()) {
                return digit;
            }
        }
        if (Globals.instance().lastFocused == transitionKludge) {
            return Globals.instance().lastFocused;
        }
        return null;
    }
}
