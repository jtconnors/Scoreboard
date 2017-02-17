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

import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import scoreboard.common.xml.XMLSpec;

/*
 * DisplayableWithDigits encapsulates the common behavior that classes with one
 * or more variables of type Digit share.  Subclasses must extend this class
 * and implement the abstract methods declared below.
 */
public abstract class DisplayableWithDigits extends Displayable {

    /**
     * Defines the fill Color of the Digits.
     */
    private ObjectProperty<Color> color;

    public void refreshOnDigitColorChange(Color value) {
        for (Digit digit : digitArr) {
            digit.setColor(value);
        }
    }

    public final void setColor(Color value) {
        colorProperty().setValue(value);
        refreshOnDigitColorChange(value);
    }

    public final Color getColor() {
        return color == null ? Color.BLACK : (Color)color.getValue();
    }

    public final ObjectProperty<Color> colorProperty() {
        if (color == null) {
            color = new ObjectPropertyBase<Color>() {
                
                @Override
                public Object getBean() {
                    return DisplayableWithDigits.this;
                }
                
                @Override
                public String getName() {
                    return "color";
                }
            };
        }
        return color;
    }

    /**
     * Defines the height in pixels of the object.  This is only an
     * approximation of the real height.  To get the exact height
     * use getLayoutBounds().getHeight().
     */
    private DoubleProperty digitHeight;

    protected void refreshOnDigitHeightChange(double value) {
        for (Digit digit : digitArr) {
            digit.setDigitHeight(value);
        }
        positionDigits();
        for (final Digit d : digitArr) {
            if (d.keyPad != null) {
                getChildren().remove(d.keyPad);
            }
        }
        getChildren().add(createKeyPads());
    }

    public final void setDigitHeight(double value) {
        digitHeightProperty().set(value);
        refreshOnDigitHeightChange(value);
    }

    public final double getDigitHeight() {
        return digitHeight == null ? 0.0 : digitHeight.get();
    }

    public DoubleProperty digitHeightProperty() {
        if (digitHeight == null) {
            digitHeight = new DoublePropertyBase() {
                
                @Override
                public Object getBean() {
                    return DisplayableWithDigits.this;
                }
                
                @Override
                public String getName() {
                    return "digitHeight";
                }
            };
        }
        return digitHeight;
    }

    /**
     * When blocksInput is set to true, all keyboard and mouse input is
     * ignored.
     */
    private BooleanProperty blocksInput;

    public final void setBlocksInput(boolean value) {
        blocksInputProperty().setValue(value);
        if (value) {
            boundingRect.setVisible(true);
        } else {
            boundingRect.setVisible(false);
        }
    }

    public final boolean isBlocksInput() {
        return blocksInput == null ? false : blocksInput.getValue();
    }

    public final BooleanProperty blocksInputProperty() {
        if (blocksInput == null) {
            blocksInput = new BooleanPropertyBase() {
            
                @Override
                public Object getBean() {
                    return DisplayableWithDigits.this;
                }
                
                @Override
                public String getName() {
                    return "blocksInput";
                }
            };
        }
        return blocksInput;
    }

    /*
     * The default min and maxOverallValues are set as follows:
     *    minOverallValue = 0 (negative numbers not allowed)
     *    maxOverallValue = largest base 10 number that will fit based
     *        upon the number of digits allocated.  For example:
     *        if number of digits = 1 maxOverllValue defaults to 9
     *        if number of digits = 2 maxOverllValue defaults to 99
     *        ...
     * If the min and max range needs to be restricted further
     * (e.g. clock timer 60 sec. max), set minValue and maxValue in
     * the child's constructor.
     */
    protected int maxOverallValue;

    public int getMaxOverallValue() {
        return maxOverallValue;
    }

    protected int minOverallValue = 0;

    public int getMinOverallValue() {
        return minOverallValue;
    }

    /**
     * Defines the overall integer value of the implementing instance.
     */
    private IntegerProperty overallValue;

    public final void setOverallValue(int value) {
        if ((value <= maxOverallValue) &&
            (value >= minOverallValue)) {
            overallValueProperty().setValue(value);
            refreshOnOverallValueChange(value);
        }
    }

    public final int getOverallValue() {
        return overallValue == null ? 0 : overallValue.getValue();
    }

    public final IntegerProperty overallValueProperty() {
        if (overallValue == null) {
            overallValue = new IntegerPropertyBase() {
                
                @Override
                public Object getBean() {
                    return DisplayableWithDigits.this;
                }
                
                @Override
                public String getName() {
                    return "overallValue";
                }    
            };
        }
        return overallValue;
    }

    /*
     * An implementing class will contain one or more digits.  They must
     * be inserted into the digitArr List.
     */
    protected ArrayList<Digit> digitArr = new ArrayList<Digit>();
    
    /*
     * This method gets called by all DisplayableWithDigits instances that
     * want to send update message on to a socket.
     */
    protected void sendMessageToSocket(String varName, int value) {
        if (Globals.useIPSocket) {
            if (Globals.multipleSocketWriter != null) {
                Globals.multipleSocketWriter.postUpdate(
                        XMLSpec.updateStr(varName, value));
            }
        } else {
            if (Globals.multicastWriter != null) {
                Globals.multicastWriter.sendMessage(
                        XMLSpec.updateStr(varName, value));
            }
        }
    }

    /********************************************************************
     * The following abstract methods must be defined by implementing   *
     * subclasses.                                                      *
     ********************************************************************/

    protected abstract Group createKeyPads();

    protected abstract void positionDigits();

    protected abstract void refreshOnOverallValueChange(int overallValue);

    protected abstract int calculateKeyNumValue(Digit focusedDigit,
            KeyCode keyCode);

    protected abstract int calculateKeyUpValue(Digit focusedDigit);

    protected abstract int calculateKeyDownValue(Digit focusedDigit);

    /********************************************************************
     *           End abstract method declaration section                *
     ********************************************************************/   

   /*
    * Keyboard input processing methods
    */

    protected FocusableParent getFocusedDigit() {
        for (Digit digit : digitArr) {
            if (digit.hasFocusHint()) {
                return digit;
            }
        }
        return null;
    }

    protected void processKeyEvent(KeyCode keyCode) {
        /*
         * When the KeyPad associated with a Digit is visible, Key Events
         * get propagated both to the KeyPad and here.  Why?  This kludge
         * will prevent the key event from being processed twice.
         */
        if (Globals.keyEventAlreadyProcessed) {
            Globals.keyEventAlreadyProcessed = false;
            return;
        }
        /*
         * If none of the Digits comprising this Object are in
         * focus, just return.
         */
        FocusableParent focusedDigit = getFocusedDigit();
        if (focusedDigit == null) {
            return;
        }
        switch (keyCode) {
            case DIGIT0:
            case DIGIT1:
            case DIGIT2:
            case DIGIT3:
            case DIGIT4:
            case DIGIT5:
            case DIGIT6:
            case DIGIT7:
            case DIGIT8:
            case DIGIT9:
                setOverallValue(calculateKeyNumValue((Digit)focusedDigit,
                        keyCode));
                break;
            case UP:
                setOverallValue(calculateKeyUpValue((Digit)focusedDigit));
                break;
            case DOWN:
                setOverallValue(calculateKeyDownValue((Digit)focusedDigit));
                break;
            case LEFT:
                if (Globals.lastFocused != Globals.lastFocused.getKeyLeftNode()) {
                    Globals.lastFocused.unShowFocusHint();
                    Globals.lastFocused.getKeyLeftNode().showFocusHint();
                }
                break;
            case RIGHT:
                if (Globals.lastFocused != Globals.lastFocused.getKeyRightNode()) {
                    Globals.lastFocused.unShowFocusHint();
                    Globals.lastFocused.getKeyRightNode().showFocusHint();
                }
                break;
            case ENTER:
                ((Digit)focusedDigit).displayKeyPad();
                break;
        }
    }

    /*
     * Constructor - must be called first by all subclass constructors.
     */
    public DisplayableWithDigits() {
        boundingRect = new Rectangle();  // dimensions set by subclasses
        boundingRect.setFill(Color.TRANSPARENT);
        boundingRect.setVisible(false);
        setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                processKeyEvent(ke.getCode());
            }
        });
    }
}
