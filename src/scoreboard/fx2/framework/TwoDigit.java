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

package scoreboard.fx2.framework;

import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import static scoreboard.common.Constants.DEFAULT_DIGIT_HEIGHT;
import static scoreboard.common.Constants.MIN_TWO_DIGIT_VALUE;
import static scoreboard.common.Constants.MAX_TWO_DIGIT_VALUE;
import static scoreboard.common.Constants.INTER_DIGIT_GAP_FRACTION;
import static scoreboard.fx2.framework.FX2Constants.DEFAULT_DIGIT_COLOR;

/*
 * This abstract class defines the behavior of a Displayable object comprised
 * of two digits.  An implementation which extends this class must:
 *    1. Also extend the Digit class
 *    2. Associate that extended Digit class type with the tensDigit and
 *       onesDigit variables.
 *    3. Allocate the tensDigit and onesDigit objects inside the extended
 *       TwoDigit constructor after the call to super() and before the
 *       call to init().
 *
 *  For an example implementation of TwoDigit, look at the
 *  scoreboard.fx2.impl.bulb.BulbTwoDigit.java code.
 */
public abstract class TwoDigit extends DisplayableWithDigits {

    /*
     * Non-abstract instances of the following Digit variables must
     * be allocated in the implemeting subclass constructors.  External
     * access to the digits is allowed via get{Ones,Tens}Digit().
     */
    protected Digit tensDigit;
    public Digit getTensDigit() { return tensDigit; }
    protected Digit onesDigit;
    public Digit getOnesDigit() { return onesDigit; }

/****************************************************************************
 *  By virtue of extending the DisplayableWithDigits class, the following   *
 *  abstract methods declared in DisplayableWithDigits must be defined.     *
 ****************************************************************************/

    protected void positionDigits() {
        getChildren().clear();
        double digitWidth = tensDigit.getLayoutBounds().getWidth();
        onesDigit.setLayoutX(digitWidth +
                (INTER_DIGIT_GAP_FRACTION * digitWidth));
        getChildren().addAll(tensDigit, onesDigit);
        boundingRect.setWidth(getLayoutBounds().getWidth());
        boundingRect.setHeight(getLayoutBounds().getHeight());
        boundingRect.setFill(Color.TRANSPARENT);
        boundingRect.setVisible(false);
        getChildren().add(boundingRect);
        
        componentWidth = boundingRect.getWidth();
        componentHeight = boundingRect.getHeight();
    }

    protected Group createKeyPads() {
        Group group = new Group();
        tensDigit.keyPad = new KeyPad(tensDigit.getLayoutBounds().getWidth(),
                0, maxOverallValue / 10, tensDigit, (DisplayableWithDigits)this);
        onesDigit.keyPad = new KeyPad(onesDigit.getLayoutBounds().getWidth(),
                0, 9, onesDigit, (DisplayableWithDigits)this);
        for (final Digit d : digitArr) {
            d.keyPad.setVisible(false);
            d.setAction(new FunctionPtr() {
                public void invoke() {
                    d.displayKeyPad();
                }
            });
            group.getChildren().add(d.keyPad);
        }
        return group;
    }

    protected void refreshOnOverallValueChange(int overallValue) {
        tensDigit.setValue((overallValue % 100) / 10);
        onesDigit.setValue(overallValue % 10);
        sendMessageToSocket(varName, overallValue);
    }

    protected int calculateKeyNumValue(Digit focusedDigit, KeyCode keyCode) {
        int key = keyCode.ordinal() - KeyCode.DIGIT0.ordinal();
        int updatedValue;
        if (focusedDigit == tensDigit) {
            updatedValue = key *
                focusedDigit.getIncrementValue() + (getOverallValue() % 10);
        } else {
            updatedValue = getOverallValue() - (getOverallValue() % 10) + key;
        }
        return updatedValue;
    }

    protected int calculateKeyUpValue(Digit focusedDigit) {
        return getOverallValue() + focusedDigit.getIncrementValue();
    }

    protected int calculateKeyDownValue(Digit focusedDigit) {
        return getOverallValue() - focusedDigit.getIncrementValue();
    }

/****************************************************************************
 *          End DisplayableWithDigits method definition section             *
 ****************************************************************************/

    /*
     * Constructors
     */
    public TwoDigit(String varName) {
        this(varName, DEFAULT_DIGIT_COLOR, DEFAULT_DIGIT_HEIGHT, 0,
                MIN_TWO_DIGIT_VALUE, MAX_TWO_DIGIT_VALUE);
    }

    public TwoDigit(String varName, Color color) {
        this(varName, color, DEFAULT_DIGIT_HEIGHT, 0,
                MIN_TWO_DIGIT_VALUE, MAX_TWO_DIGIT_VALUE);
    }

    public TwoDigit(String varName, double digitHeight) {
        this(varName, DEFAULT_DIGIT_COLOR, digitHeight, 0,
                MIN_TWO_DIGIT_VALUE, MAX_TWO_DIGIT_VALUE);
    }

    public TwoDigit(String varName, Color color, double digitHeight) {
        this(varName, color, digitHeight, 0,
                MIN_TWO_DIGIT_VALUE, MAX_TWO_DIGIT_VALUE);
    }

    public TwoDigit(String varName, Color color, double digitHeight,
            int overallValue, int minOverallValue, int maxOverallValue) {
        super();  // Must call superclass constructor first
        this.varName = varName;
//        this.color = color;
        colorProperty().setValue(color);
//        this.digitHeight = digitHeight;
        digitHeightProperty().setValue(digitHeight);
//        this.overallValue = overallValue;
        overallValueProperty().setValue(overallValue);
        this.minOverallValue = (minOverallValue >= MIN_TWO_DIGIT_VALUE &&
                minOverallValue <= maxOverallValue)
                ? minOverallValue : MIN_TWO_DIGIT_VALUE;
        this.maxOverallValue = (maxOverallValue <= MAX_TWO_DIGIT_VALUE &&
                maxOverallValue >= minOverallValue)
                ? maxOverallValue : MAX_TWO_DIGIT_VALUE;
    }

    /*
     * This method must be called by the implementing constructor and must
     * follow the allocation of the tensDigit and onesDigit objects.
     */
    protected void init() {
        tensDigit.setIncrementValue(10);
        tensDigit.setBlankIfZero(true);
        digitArr.add(tensDigit);
        onesDigit.setIncrementValue(1);
        digitArr.add(onesDigit);
        positionDigits();
        getChildren().add(createKeyPads());
        /*
         * Set up Arrow Key Traversal
         */
        tensDigit.setKeyLeftNode(tensDigit);
        tensDigit.setKeyRightNode(onesDigit);
        onesDigit.setKeyLeftNode(tensDigit);
        onesDigit.setKeyRightNode(onesDigit);
    }
}
