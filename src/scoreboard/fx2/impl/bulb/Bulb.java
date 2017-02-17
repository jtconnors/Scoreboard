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

package scoreboard.fx2.impl.bulb;

import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import static scoreboard.fx2.framework.FX2Constants.DEFAULT_DIGIT_COLOR;
import scoreboard.fx2.framework.Globals;

public class Bulb extends Circle {

    private static final double DEFAULT_RADIUS = 30f;
    private RadialGradient fillPattern;
    private Stop[] stops;

    public Bulb() {
        this(DEFAULT_RADIUS, DEFAULT_RADIUS, DEFAULT_RADIUS,
                DEFAULT_DIGIT_COLOR, Globals.unlitOpacity);
    }

    public Bulb (double radius, Color color) {
        this(radius, radius, radius, color, Globals.unlitOpacity);
    }

    public Bulb (double centerX, double centerY, double radius,
            Color color, double unlitOpacity) {
        setCenterX(centerX);
        setCenterY(centerY);
        setRadius(radius);
        setColor(color);
        setUnlitOpacity(unlitOpacity);
        setStrokeWidth(1.0f);
        updateBulbDisplay();
    }

    private void updateBulbDisplay() {
        stops = new Stop[] {
            new Stop(0f, Color.WHITE),
            new Stop(1.0f, getColor())
        };
        fillPattern = new RadialGradient(0, 0, .25f, .25f, .7f,
            true, CycleMethod.NO_CYCLE, stops);
        setFill(fillPattern);
        setOpacity(isBulbLit() ? 1.0d : Globals.unlitOpacity);
    }

    /**
     * Defines the fill color of the Bulb.
     */
    private ObjectProperty<Color> color;

    public final void setColor(Color value) {
        colorProperty().setValue(value);
        updateBulbDisplay();
    }

    public final Color getColor() {
        return color == null ? Color.BLACK : (Color)color.getValue();
    }

    public ObjectProperty<Color> colorProperty() {
        if (color == null) {
            color = new ObjectPropertyBase<Color>() {
                
                @Override
                public Object getBean() {
                    return Bulb.this;
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
     * Determines if the Bulb is lit or not.
     *
     * @defaultvalue false
     */
    private BooleanProperty bulbLit;

    public final void setBulbLit(boolean value) {
        bulbLitProperty().setValue(value);
        setOpacity(value ? 1.0d : Globals.unlitOpacity);
    }

    public final boolean isBulbLit() {
        return bulbLit == null ? false : bulbLit.getValue();
    }

    public BooleanProperty bulbLitProperty() {
        if (bulbLit == null) {
            bulbLit = new BooleanPropertyBase() {
            
                @Override
                public Object getBean() {
                    return Bulb.this;
                }
                
                @Override
                public String getName() {
                    return "bulbLit";
                }
            };
        }
        return bulbLit;
    }
    
    /**
     * Determines the opacity of the Bulb when not lit.
     *
     * @defaultvalue 0.1
     */
    private DoubleProperty unlitOpacity;

    public final void setUnlitOpacity(double value) {
        unlitOpacityProperty().setValue(value);
    }

    public final double getUnlitOpacity() {
        return unlitOpacity == null ? Globals.unlitOpacity :
                (double)unlitOpacity.getValue();
    }

    public DoubleProperty unlitOpacityProperty() {
        if (unlitOpacity == null) {
            unlitOpacity = new DoublePropertyBase() {
            
                @Override
                public Object getBean() {
                    return Bulb.this;
                }
                
                @Override
                public String getName() {
                    return "unlitOpacity";
                }
            };
        }
        return unlitOpacity;
    }
}