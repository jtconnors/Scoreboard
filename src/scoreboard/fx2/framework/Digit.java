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
 *   * Neither the name of the This project nor the names of its
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

import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.paint.Color;
import scoreboard.common.Constants;

/*
 * This abstract class defines the common behavior of a scoreboard digit.
 * 
 * For an example implementation of Digit, look at the
 * scoreboard.fx2.impl.bulb.BulbDigit.java code.
 */
public abstract class Digit extends FocusableParent {

    /**
     * Defines the fill Color of the Digit.
     */
    private ObjectProperty<Color> color;
    protected abstract void refreshOnColorChange(Color value);

    public final void setColor(Color value) {
        colorProperty().setValue(value);
        refreshOnColorChange(value);
    }

    public final Color getColor() {
        return color == null ? Color.BLACK : (Color)color.getValue();
    }

    public final ObjectProperty colorProperty() {
        if (color == null) {
            color = new ObjectPropertyBase<Color>() {
                
                @Override
                public Object getBean() {
                    return Digit.this;
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
     * Defines the height in pixels of the Digit.  This is only an
     * approximation of the real height.  To get the exact height
     * use getLayoutBounds().getHeight().
     *
     */
    private DoubleProperty digitHeight;
    protected abstract void refreshOnDigitHeightChange(double value);

    public final void setDigitHeight(double value) {
        digitHeightProperty().setValue(value);
        refreshOnDigitHeightChange(value);
    }

    public final double getDigitHeight() {
        return digitHeight == null ? 0.0 : digitHeight.getValue();
    }

    public final DoubleProperty digitHeightProperty() {
        if (digitHeight == null) {
            digitHeight = new DoublePropertyBase() {
                
                @Override
                public Object getBean() {
                    return Digit.this;
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
     * Defines the integer value of the Digit.
     */
    private IntegerProperty value;
    protected abstract void refreshOnValueChange(int value);

    public final void setValue(int value) {
        valueProperty().setValue(value);
        if ((value == 0) && (isBlankIfZero())) {
            refreshOnValueChange(Constants.instance().BLANK_DIGIT);
        } else {
            refreshOnValueChange(value);
        }
    }

    public final int getValue() {
        return value == null ? 0 : value.getValue();
    }

    public final IntegerProperty valueProperty() {
        if (value == null) {
            value = new IntegerPropertyBase() {
                
                @Override
                public Object getBean() {
                    return Digit.this;
                }
                
                @Override
                public String getName() {
                    return "value";
                }    
            };
        }
        return value;
    }

    /**
     * Defines whether a digit with a value of 0, either displays a '0' digit
     * or a blank.  This is needed for displays with multiple digits where
     * the most significant digit(s) are zero.  They shouldn't be displayed.
     */
    private BooleanProperty blankIfZero;

    public final void setBlankIfZero(boolean value) {
        blankIfZeroProperty().setValue(value);
        if (getValue() == 0) {
            if (value) {
                refreshOnValueChange(Constants.instance().BLANK_DIGIT);
            } else {
                refreshOnValueChange(0);
            }
        }
    }

    public final boolean isBlankIfZero() {
        return blankIfZero == null ? false : blankIfZero.getValue();
    }

    public final BooleanProperty blankIfZeroProperty() {
        if (blankIfZero == null) {
            blankIfZero = new BooleanPropertyBase() {
            
                @Override
                public Object getBean() {
                    return Digit.this;
                }
                
                @Override
                public String getName() {
                    return "blankIfZero";
                }
            };
        }
        return blankIfZero;
    }

    /**
     * When part of a string of Digits, the incrementValue variable determines
     * how much the overall value of the number representing the digits is
     * increased when this digit object is incremented by one.
     *
     * Example:  For a clock with a 10th of a second resolution
     *
     * If the digit is in the seconds place, incrementValue = 10
     * If the digit is in the ten seconds place, incrementValue = 100
     * If the digit is in the minutes place, incrementValue = 600
     */
    private int incrementValue = 1;

    public int getIncrementValue() {
        return incrementValue;
    }

    public void setIncrementValue(int incrementValue) {
        this.incrementValue = incrementValue;
    }

    /**
     * During normal clock operation, when a digit is incremented, the
     * overall value of the remaining time will be increased/decreased
     * by the digit's incrementValue variable.  However, when less than one
     * minute remains on the clock, digits take on different meanings.
     * The alternateIncrementValue variable determines
     * how much the overall value of the remaining time will be
     * increased/decreased when this digit object is in this special mode.
     *
     * Example:  For a clock with a 10th of a second resolution
     *
     * If the digit is in the seconds place, alternate = 0 (N/A)
     * If the digit is in the ten seconds place, alternate = 1
     * If the digit is in the minutes place, alternate = 10
     * If the digit is in the ten Minutes place, alternate = 100
     */
    private int alternateIncrementValue = 0;

    public int getAlternateIncrementValue() {
        return alternateIncrementValue;
    }

    public void setAlternateIncrementValue(int alternateIncrementValue) {
        this.alternateIncrementValue = alternateIncrementValue;
    }

    /*
     * Every Digit has associated with it a KeyPad UI Control.  While in focus
     * if the user either (1) mouse clicks or (2) types a keyboard <RETURN>,
     * the KeyPad instance will be displyed giving the user the option of
     * selecting a valid number to assin this Digit instance.
     */
    protected KeyPad keyPad;
    /*
     * Digits that are part of any subclass of Clock may have two different
     * keyPads depending upon context.  When the clock is in alternate mode
     * (i.e. less than a minute remains and tenths of seconds are being
     * displayed) the alternateKeyPad rather than the normalKeyPad should be
     * utilized.
     */
    protected KeyPad normalKeyPad = null;
    protected KeyPad alternateKeyPad = null;
    /*
     * This method is called whenever this instance is in focus
     * and either the user (1) mouse clicks (2) types a keyboard <ENTER>
     */
    public void displayKeyPad() {
        keyPad.setLayoutX(getLayoutX() + (getLayoutBounds().getWidth() -
             keyPad.getLayoutBounds().getWidth()) / 2);
        keyPad.setLayoutY(getLayoutY() + (getLayoutBounds().getHeight() -
            keyPad.getLayoutBounds().getHeight()) / 2);
        keyPad.setVisible(true);
        keyPad.requestFocus();
    }

    /*
     * Normally, Digit instances default to a range of integer values
     * from [0-9]. If you want to further restrict the range, set
     * minValue and maxValue in the Digit constructor.
     */
    protected int minValue = Constants.instance().MIN_DIGIT_VALUE;
    public int getMinValue() { return minValue; }
    protected int maxValue = Constants.instance().MAX_DIGIT_VALUE;
    public int getMaxValue() { return maxValue; }

    /*
     * Constructors
     */
    public Digit() {
        this(FxConstants.instance().DEFAULT_DIGIT_COLOR,
                Constants.instance().DEFAULT_DIGIT_HEIGHT, 0,
                Constants.instance().MIN_DIGIT_VALUE, 
                Constants.instance().MAX_DIGIT_VALUE);
    }

    public Digit(Color color) {
        this(color, Constants.instance().DEFAULT_DIGIT_HEIGHT, 0,
                Constants.instance().MIN_DIGIT_VALUE,
                Constants.instance().MAX_DIGIT_VALUE);
    }

    public Digit(double digitHeight) {
        this(FxConstants.instance().DEFAULT_DIGIT_COLOR, digitHeight, 0,
                Constants.instance().MIN_DIGIT_VALUE,
                Constants.instance().MAX_DIGIT_VALUE);
    }

    public Digit(Color color, double digitHeight) {
        this(color, digitHeight, 0, Constants.instance().MIN_DIGIT_VALUE,
                Constants.instance().MAX_DIGIT_VALUE);
    }

    public Digit(Color color, double digitHeight, int value,
            int minValue, int maxValue) {
        super();  // Must call this first!
        colorProperty().setValue(color);
        digitHeightProperty().setValue(digitHeight);
        blankIfZeroProperty().setValue(false);
        valueProperty().setValue(value);
        this.minValue = minValue >= Constants.instance().MIN_DIGIT_VALUE
                ? minValue : Constants.instance().MIN_DIGIT_VALUE;
        this.maxValue = maxValue <= Constants.instance().MAX_DIGIT_VALUE
                ? maxValue : Constants.instance().MAX_DIGIT_VALUE;
    }
}
