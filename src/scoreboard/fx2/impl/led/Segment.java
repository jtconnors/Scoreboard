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

import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import scoreboard.common.Constants;
import scoreboard.common.Globals;

public class Segment extends Polygon {
    
    private static final double LENGTH_TO_WIDTH_RATIO =
        (Constants.instance().SEGMENT_EDGE_TO_DIGIT_HEIGHT_RATIO /
        Constants.instance().SEGMENT_LENGTH_TO_DIGIT_HEIGHT_RATIO) * 2d;

    private boolean vertical;
    public boolean isVertical() { return vertical; }
    
    private double segmentWidth;
    public double getSegmentWidth() { return segmentWidth; }
    
    private double segmentHeight;
    public double getSegmentHeight() { return segmentHeight; }
    
    private double edgeSz;
    private double gap;
  
    public Segment (double length, boolean vertical, Color color) {
        this(length, vertical, color, Globals.instance().unlitOpacity);
    }

    public Segment (double length, boolean vertical, Color color,
            double unlitOpacity) {
        this.vertical = vertical;
        if (vertical) {
            segmentHeight = length;
            segmentWidth = segmentHeight * LENGTH_TO_WIDTH_RATIO;
            edgeSz = segmentWidth / 2.0;
            gap = (segmentWidth > 4.0) ? 1.0 : 0.0;
        } else {
            segmentWidth = length;
            segmentHeight = segmentWidth * LENGTH_TO_WIDTH_RATIO;
            edgeSz = segmentHeight / 2.0;
            gap = (segmentHeight > 4.0) ? 1.0 : 0.0;
        }
        setColor(color);
        setUnlitOpacity(unlitOpacity);
        setStrokeWidth(1.0f);
        updateSegmentDisplay();
    }

    private void updateSegmentDisplay() {
        getPoints().clear();
        if (isVertical()) {
            getPoints().addAll(new Double[] {
                edgeSz, gap,
                segmentWidth - gap, edgeSz,
                segmentWidth - gap, segmentHeight - edgeSz,
                edgeSz, segmentHeight-gap,
                gap, segmentHeight - edgeSz,
                gap, edgeSz
            });    
        } else {
             getPoints().addAll(new Double[] {
                gap, edgeSz,
                edgeSz, gap,
                segmentWidth-edgeSz, gap,
                segmentWidth-gap, edgeSz,
                segmentWidth-edgeSz, segmentHeight-gap,
                edgeSz, segmentHeight-gap
            });    
        }
        setFill(getColor());
        setOpacity(isSegmentLit() ? 1.0d : Globals.instance().unlitOpacity);
    }

    /**
     * Defines the fill color of the Segment.
     */
    private ObjectProperty<Color> color;

    public final void setColor(Color value) {
        colorProperty().setValue(value);
        updateSegmentDisplay();
    }

    public final Color getColor() {
        return color == null ? Color.BLACK : (Color)color.getValue();
    }

    public ObjectProperty<Color> colorProperty() {
        if (color == null) {
            color = new ObjectPropertyBase<Color>() {
                
                @Override
                public Object getBean() {
                    return Segment.this;
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
     * Determines if the Segment is lit or not.
     *
     * @defaultvalue false
     */
    private BooleanProperty segmentLit;

    public final void setSegmentLit(boolean value) {
        segmentLitProperty().setValue(value);
        setOpacity(value ? 1.0d : Globals.instance().unlitOpacity);
    }

    public final boolean isSegmentLit() {
        return segmentLit == null ? false : segmentLit.getValue();
    }

    public BooleanProperty segmentLitProperty() {
        if (segmentLit == null) {
            segmentLit = new BooleanPropertyBase() {
            
                @Override
                public Object getBean() {
                    return Segment.this;
                }
                
                @Override
                public String getName() {
                    return "segmentLit";
                }
            };
        }
        return segmentLit;
    }
    
    /**
     * Determines the opacity of the Segment when not lit.
     *
     * @defaultvalue 0.1
     */
    private DoubleProperty unlitOpacity;

    public final void setUnlitOpacity(double value) {
        unlitOpacityProperty().setValue(value);
    }

    public final double getUnlitOpacity() {
        return unlitOpacity == null ? Globals.instance().unlitOpacity :
                (double)unlitOpacity.getValue();
    }

    public DoubleProperty unlitOpacityProperty() {
        if (unlitOpacity == null) {
            unlitOpacity = new DoublePropertyBase() {
            
                @Override
                public Object getBean() {
                    return Segment.this;
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