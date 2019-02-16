/*
 * Copyright (c) 2019, Jim Connors
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

package com.jtconnors.scoreboard.fx2.framework;

import javafx.scene.Parent;
import javafx.scene.shape.Rectangle;
import com.jtconnors.scoreboard.common.LayoutXOptions;

/*
 * All Nodes which can be displayed on the scoreboard are ultimately
 * derived from this class.
 */
public class Displayable extends Parent {

    /*
     * Variable name of the instance.  Whenever one of these objects is
     * created, the varName must exactly match the name of the variable
     * representing this object.  This is needed so that each instance can
     * be uniquely identifed when sending out XML update elements.
     */
    protected String varName;

    /*
     * Rectangle which encompasses entire bounds of the Displayable object.
     */
    protected Rectangle boundingRect;
    
    /*
     * Represents the overall width, in pixels, of the Displayable component.
     * Implementors are responsible for setting this value in subclasses.
     */
    protected double componentWidth;

    /**
     * Returns the overall width of the component.
     */
    public double getComponentWidth() {
        return componentWidth;
    }
    
    /*
     * Represents the overall height, in pixels, of the Displayable component.
     * Implementors are responsible for setting this value in subclasses.
     */
    protected double componentHeight;

    /**
     * Returns the overall height of the component.
     */
    public double getComponentHeight() {
        return componentHeight;
    }

    /*
     * When spitting out the XML config elements, the X coordinates of
     * the configurable objects will be assigned a LayoutXOption value.
     * The remote side, reading the XML config elements, will be
     * responsible for placing the objects.
     */
    private LayoutXOptions layoutXOption = LayoutXOptions.UNDEFINED;

    public LayoutXOptions getLayoutXOption() {
        return layoutXOption;
    }

    public void setLayoutXOption(LayoutXOptions layoutXOption) {
        this.layoutXOption = layoutXOption;
    }

    /*
     * Some of the LayoutXOptions align relative to another object.  The
     * "alignWith" variable represents the object to align with.
     */
    private Parent alignWith = null;

    public Parent getAlignWith() {
        return alignWith;
    }

    public void setAlignWith(Displayable alignWith) {
        this.alignWith = alignWith;
    }
    
    /*
     * Some of the LayoutXOptions align relative to another object.  The
     * alignWithStr variable is the String name of the variable to align with.
     */
    private String alignWithStr = null;

    public String getAlignWithStr() {
        return alignWithStr;
    }

    public void setAlignWithStr(String alignWithStr) {
        this.alignWithStr = alignWithStr;
    }
}
