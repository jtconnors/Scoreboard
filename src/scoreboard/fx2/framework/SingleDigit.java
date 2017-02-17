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

import javafx.scene.input.KeyCode;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import static scoreboard.common.Constants.DEFAULT_DIGIT_HEIGHT;
import static scoreboard.common.Constants.MIN_DIGIT_VALUE;
import static scoreboard.common.Constants.MAX_DIGIT_VALUE;
import static scoreboard.fx2.framework.FxConstants.DEFAULT_DIGIT_COLOR;

/*
 * This abstract class defines the behavior of a Displayable object comprised
 * of one digit.  An implementation which extends this class must:
 *    1. Also extend the Digit class
 *    2. Associate that extended Digit class type with the singleDigit
 *       variable.
 *    3. Allocate the singleDigit object inside the extended
 *       SingleDigit constructor after the call to super() and before the
 *       call to init().
 *
 *  For an example implementation of SingleDigit, look at the
 *  scoreboard.fx2.impl.bulb.BulbSingleDigit.java code.
 */
public abstract class SingleDigit extends DisplayableWithDigits {

    /*
     * Non-abstract instances of the this Digit variable must
     * be allocated in the implemeting subclass constructors.
     */
    protected Digit singleDigit;
    public Digit getSingleDigit() { return singleDigit; }

/****************************************************************************
 *  By virtue of extending the DisplayableWithDigits class, the following   *
 *  abstract methods declared in DisplayableWithDigits must be defined.     *
 ****************************************************************************/

    protected Group createKeyPads() {
        Group group = new Group();
        singleDigit.keyPad = new KeyPad(
                singleDigit.getLayoutBounds().getWidth(),
                minOverallValue, maxOverallValue, singleDigit,
                (DisplayableWithDigits)this);
        singleDigit.keyPad.setVisible(false);
        singleDigit.setAction(new FunctionPtr() {
            public void invoke() {
                singleDigit.displayKeyPad();
            }
        });
        group.getChildren().add(singleDigit.keyPad);
        return group;
    }

    protected void positionDigits() {
        getChildren().clear();
        boundingRect.setWidth(singleDigit.getLayoutBounds().getWidth());
        boundingRect.setHeight(singleDigit.getLayoutBounds().getHeight());
        getChildren().addAll(singleDigit, boundingRect);
        
        componentWidth = boundingRect.getWidth();
        componentHeight = boundingRect.getHeight();
    }

    protected void refreshOnOverallValueChange(int overallValue) {
        singleDigit.setValue(overallValue % 10);
        sendMessageToSocket(varName, String.valueOf(overallValue));
    }

    protected int calculateKeyNumValue(Digit focusedDigit, KeyCode keyCode) {
        return keyCode.ordinal() - KeyCode.DIGIT0.ordinal();
    }

    protected int calculateKeyUpValue(Digit focusedDigit) {
        return getOverallValue() + focusedDigit.getIncrementValue();
    }

    protected int calculateKeyDownValue(Digit focusedDigit) {
        return getOverallValue() - focusedDigit.getIncrementValue();
    }

/****************************************************************************
 *           End DisplayableWithDigits method definition section            *
 ****************************************************************************/

    /*
     * Constructors
     */
    public SingleDigit(String varName) {
        this(varName, DEFAULT_DIGIT_COLOR, DEFAULT_DIGIT_HEIGHT, 0,
                MIN_DIGIT_VALUE, MAX_DIGIT_VALUE);
    }

    public SingleDigit(String varName, Color color) {
        this(varName, color, DEFAULT_DIGIT_HEIGHT, 0,
                MIN_DIGIT_VALUE, MAX_DIGIT_VALUE);
    }

    public SingleDigit(String varName, double digitHeight) {
        this(varName, DEFAULT_DIGIT_COLOR, digitHeight, 0,
                MIN_DIGIT_VALUE, MAX_DIGIT_VALUE);
    }

    public SingleDigit(String varName, Color color, double digitHeight) {
        this(varName, color, digitHeight, 0, MIN_DIGIT_VALUE, MAX_DIGIT_VALUE);
    }

    public SingleDigit(String varName, Color color, double digitHeight,
            int overallValue, int minOverallValue, int maxOverallValue) {
        super();  // Must call superclass constructor first
        this.varName = varName;
        colorProperty().setValue(color);
        digitHeightProperty().setValue(digitHeight);
        overallValueProperty().setValue(overallValue);
        this.minOverallValue = (minOverallValue >= MIN_DIGIT_VALUE &&
                minOverallValue <= maxOverallValue)
                ? minOverallValue : MIN_DIGIT_VALUE;
        this.maxOverallValue = (maxOverallValue <= MAX_DIGIT_VALUE &&
                maxOverallValue >= minOverallValue)
                ? maxOverallValue : MAX_DIGIT_VALUE;
    }

    /*
     * This method must be called by the implementing constructor and must
     * follow the allocation of the singleDigit object.
     */
    protected void init() {
        singleDigit.setIncrementValue(1);
        digitArr.add(singleDigit);
        positionDigits();
        getChildren().add(createKeyPads());
        /*
         * Set up Arrow Key Traversal, or lack thereof, in this case
         */
        singleDigit.setKeyLeftNode(singleDigit);
        singleDigit.setKeyRightNode(singleDigit);
    }
}

