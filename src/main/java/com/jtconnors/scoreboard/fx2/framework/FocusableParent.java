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

import com.jtconnors.scoreboard.common.Globals;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.animation.ScaleTransition;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/*
 * Common behavior for nodes that accept keyboard and mouse focus.  Among
 * the capabilies included are:
 *   1. Animation which expands the size of the node when the node is
 *      in focus (either by mouseover event or keyboard focus)
 *   2. Mechanism to set up node focus traversal to the left and right based
 *      upon keyboard input.
 *   3. Means to associate an action which will be invoked by a mouse press.
 *      That same action can be used when a keyboard enter takes place on
 *      a node in focus.
 */
public class FocusableParent extends Parent {

    private static final Duration SCALE_DURATION = Duration.millis(200d);
    private static final double INCREASE_SCALE = 1.25f;

    /*
     * Only traverse to the left and right.  Up and down keys will
     * be used to increment/decrement the digit displays.
     */
    protected FocusableParent leftNode;
    protected FocusableParent rightNode;

    protected FunctionPtr action;
    protected boolean focusHintDisplayed = false;
    private ScaleTransition scaleOut;
    private ScaleTransition scaleIn;
    private FocusableParent thisObject = this;

    public FocusableParent() {
        scaleOut = new ScaleTransition(SCALE_DURATION, this);
        scaleOut.setFromX(1.0f);
        scaleOut.setFromY(1.0f);
        scaleOut.setToX(INCREASE_SCALE);
        scaleOut.setToY(INCREASE_SCALE);

        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if ((Globals.instance().lastFocused != null) &&
                        (Globals.instance().lastFocused != thisObject)) {
                    Globals.instance().lastFocused.unShowFocusHint();
                }
                showFocusHint();
            }
        });

        scaleIn = new ScaleTransition(SCALE_DURATION, this);
        scaleIn.setFromX(INCREASE_SCALE);
        scaleIn.setFromY(INCREASE_SCALE);
        scaleIn.setToX(1.0f);
        scaleIn.setToY(1.0f);

        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                unShowFocusHint();
            }
        });
        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (action != null) {
                    action.invoke();
                }
            }
        });
        setFocusTraversable(true);
    }

    public FocusableParent getKeyLeftNode() {
        return leftNode;
    }

    public FocusableParent getKeyRightNode() {
        return rightNode;
    }
    
    public void setKeyLeftNode(FocusableParent leftNode) {
        this.leftNode = leftNode;
    }

    public void setKeyRightNode(FocusableParent rightNode) {
        this.rightNode = rightNode;
    }

    public void setAction(final FunctionPtr F) {
        action = F;
    }

    public void invokeAction() {
        if (action != null) {
            action.invoke();
        }
    }

    public boolean hasFocusHint() {
        return focusHintDisplayed;
    }

    public void showFocusHint() {
        focusHintDisplayed = true;
        Globals.instance().lastFocused = this;
        scaleOut.stop();
        scaleOut.setFromX(getScaleX());
        scaleOut.setFromY(getScaleY());
        scaleOut.playFromStart();
        requestFocus();
    }
   
    public void unShowFocusHint() {
        focusHintDisplayed = false;
        scaleIn.stop();
        scaleIn.setFromX(getScaleX());
        scaleIn.setFromY(getScaleY());
        scaleIn.playFromStart();
    }
}