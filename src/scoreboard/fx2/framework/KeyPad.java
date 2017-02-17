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
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.geometry.VPos;

/*
 * KeyPad is a simple user interface control and represents a numeric key pad,
 * displaying the numbers [0-9].  By default, the KeyPad has a min value of 0,
 * and a max value of 9, but it can be initialized, via the constructor,
 * to have different minimum and maximum values.  In that case, the numbers
 * outside the min and max range will still be displayed, but disabled.
 */
public class KeyPad extends Parent {

    /*
     * Inner class responsible for rendering each individual number in
     * the KeyPad.
     */
    class NumberNode extends Parent {
        
        protected NumberNode leftNode;
        protected NumberNode rightNode;
        protected NumberNode upNode;
        protected NumberNode downNode;
        protected boolean focusHintDisplayed = false;

        private static final double DEFAULT_WIDTH = 20d;
        //The following ratios are a percentage of DEFAULT_WIDTH;
        private static final double HEIGHT_RATIO = 1d;
        private static final double FONT_SIZE_RATIO = 0.9d;
        private static final double ARC_WIDTH_RATIO = 0.2d;
        private static final double ARC_HEIGHT_RATIO = 0.2d;

        private int value ;
        private Rectangle roundedRect;
        private Text text;
        private NumberNode numberNodeRef = this;
       

        public NumberNode(int value) {
            this(value, DEFAULT_WIDTH, HEIGHT_RATIO * DEFAULT_WIDTH);
        }

        public NumberNode(int value, double width, double height) {
            this.value = (value < 0 || value > 9) ? 0 : value;
            roundedRect = new Rectangle();
            roundedRect.setWidth(width);
            roundedRect.setHeight(height);
            roundedRect.setArcWidth(height * ARC_WIDTH_RATIO);
            roundedRect.setArcHeight(height * ARC_HEIGHT_RATIO);
            roundedRect.setFill(Color.WHITE);
            roundedRect.setStroke(Color.BLACK);

            Rectangle boundingRect = new Rectangle();
            boundingRect.setWidth(width);
            boundingRect.setHeight(height);
            boundingRect.setFill(Color.WHITE);
            boundingRect.setStroke(Color.BLACK);
            boundingRect.setOpacity(0.01d);

            text = new Text();
            text.setFont(Font.font("Arial", height * FONT_SIZE_RATIO));
            text.setText(String.valueOf(value));
            text.setX((width - text.getLayoutBounds().getWidth()) / 2);
            text.setY((height - text.getLayoutBounds().getHeight()) / 2);
            text.setTextOrigin(VPos.TOP);
            text.setFill(Color.BLACK);
            text.setDisable(true);

            boundingRect.setOnMouseEntered(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent me) {
                    if (lastHighlightedNumberNode != numberNodeRef) {
                        lastHighlightedNumberNode.unShowFocusHint();
                        showFocusHint();
                    }
                }
            });

            setOnMousePressed(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent me) {
                    doSelected((NumberNode)me.getSource());
                }
            });

            getChildren().addAll(roundedRect, boundingRect, text);
        }

        public int getValue() {
            return value;
        }

        public void showFocusHint() {
            focusHintDisplayed = true;
            setOpacity(1.0f);
            setEffect(innerShadow);
            text.setFill(Color.RED);
            lastHighlightedNumberNode = this;
        }

        public void unShowFocusHint() {
            focusHintDisplayed = false;
            setEffect(null);
            setOpacity(enabledOpacity);
            text.setFill(Color.BLACK);
        }

        public NumberNode getNextNode(ArrowKey direction) {
            NumberNode lastNode;
            NumberNode lastEnabledNode = this;
            NumberNode nextNode = this;
            boolean finished = false;
            do {
                lastNode = nextNode;
                switch (direction) {
                    case LEFT:
                        nextNode = lastNode.leftNode;
                        break;
                    case RIGHT:
                        nextNode = lastNode.rightNode;
                        break;
                    case UP:
                        nextNode = lastNode.upNode;
                        break;
                    case DOWN:
                        nextNode = lastNode.downNode;
                        break;
                }
                if (!nextNode.isDisabled()) {
                    lastEnabledNode = nextNode;
                }
                finished = (nextNode == lastNode || !nextNode.isDisabled())
                        ? true : false;
            } while (!finished);
            return lastEnabledNode;
        }

        public void setNextNode(ArrowKey direction, NumberNode node) {
            switch (direction) {
                case LEFT:
                    leftNode = node;
                    break;
                case RIGHT:
                    rightNode = node;
                    break;
                case UP:
                    upNode = node;
                    break;
                case DOWN:
                    downNode = node;
                    break;
            }
        }
    }

    private final double enabledOpacity = 0.75d;
    private final double disabledOpacity = 0.25d;
    private InnerShadow innerShadow = new InnerShadow();
    private NumberNode lastHighlightedNumberNode;
    private NumberNode[] nodeArr;
    private NumberNode zeroNumberNode;
    private int minValue;
    private int maxValue;

    // Can be set only by constructor
    private Digit digit = null;
    public Digit getDigit() { return digit; }
    private DisplayableWithDigits displayableWithDigits = null;
    public DisplayableWithDigits getDisplayableWithDigits() {
        return displayableWithDigits;
    }

    /*
     * For key traversal, this table determines which node to
     * highlight next.  It is based on the current highlighted numberNode
     * and the provided key input:
     * { ArrowKey.LEFT, ArrowKey.RIGHT, ArrowKey.UP, ArrowKey.DOWN }
     */
    static final int traversalArr[][] = {
        { 9, 1, 8, 0 },  // 0
        { 0, 2, 1, 4 },  // 1
        { 1, 3, 2, 5 },  // 2
        { 2, 4, 3, 6 },  // 3
        { 3, 5, 1, 7 },  // 4
        { 4, 6, 2, 8 },  // 5
        { 5, 7, 3, 9 },  // 6
        { 6, 8, 4, 0 },  // 7
        { 7, 9, 5, 0 },  // 8
        { 8, 0, 6, 0 }   // 9
    };

    public KeyPad(double width) {
        this(width, 0, 9, null, null);
    }

    public KeyPad(double width, int minValue, int maxValue) {
        this(width, minValue, maxValue, null, null);
    }

    public KeyPad(Digit digit, DisplayableWithDigits displayableWithDigits) {
        this(digit.getLayoutBounds().getWidth(),
             digit.getMinValue(), digit.getMaxValue(),
             digit, displayableWithDigits);
    }

    public KeyPad(double width, int minValue, int maxValue,
            Digit digit, DisplayableWithDigits displayableWithDigits) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.digit = digit;
        this.displayableWithDigits = displayableWithDigits;
        Group keyPadGroup;
        keyPadGroup = initKeyPad(width * 0.8d);
        /*
         * Enclosing Rectangle.  Set opacity to .01 so that this can
         * block mouse events, yet isn't visible.
         */
        Rectangle rect = new Rectangle();
        if (digit != null) {
            rect.setWidth(digit.getLayoutBounds().getWidth());
            rect.setHeight(digit.getLayoutBounds().getHeight());
        } else {
            rect.setWidth(keyPadGroup.getLayoutBounds().getWidth());
            rect.setHeight(keyPadGroup.getLayoutBounds().getHeight());
        }
        rect.setStroke(Color.TRANSPARENT);
        rect.setFill(Color.TRANSPARENT);
        rect.setVisible(true);
//        rect.setBlocksMouse(true);

        setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                processKeyEvent(ke.getCode());
            }
        });

        innerShadow.setRadius(10d);
        innerShadow.setColor(Color.RED);
        setFocusTraversable(true);
        requestFocus();
        lastHighlightedNumberNode = nodeArr[minValue];
        nodeArr[minValue].showFocusHint();
        getChildren().add(rect);
        getChildren().add(keyPadGroup);
        keyPadGroup.setLayoutX((rect.getWidth() -
                keyPadGroup.getLayoutBounds().getWidth()) / 2);
        keyPadGroup.setLayoutY((rect.getHeight() -
                keyPadGroup.getLayoutBounds().getHeight()) / 2);

        rect.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent ke) {
                setVisible(false);
            }
        });
    }

    private Group initKeyPad(double width) {
        Group group = new Group();
        nodeArr = new NumberNode[10];
        /* 
         * First do NumberNodes 1..9, three per row
         */
        for (int row=0; row<=2; row++) {
            for (int col=0; col<=2; col++) {
                int index = col * 3 + row + 1;
                NumberNode numberNode = new NumberNode(index, width/3, width/3);
                numberNode.setLayoutX(row *
                        numberNode.getLayoutBounds().getWidth());
                numberNode.setLayoutY(col *
                        numberNode.getLayoutBounds().getHeight());
                nodeArr[index] = numberNode;
                group.getChildren().add(numberNode);
            }
        }
        /*
         * NumberNode with '0' at bottom, encompassing entire width
         */
        zeroNumberNode = new NumberNode(0,
            2d * nodeArr[1].getLayoutBounds().getWidth() + width/3,
            width/3);
        zeroNumberNode.setLayoutX(0f);
        zeroNumberNode.setLayoutY(nodeArr[7].getLayoutY() +
                nodeArr[7].getLayoutBounds().getHeight());
        nodeArr[0] = zeroNumberNode;
        group.getChildren().add(zeroNumberNode);
        /*
         * Set up KeyTraversal
         */
        for (int i=0; i<nodeArr.length; i++) {
            NumberNode node = nodeArr[i];
            int[] traverseIx = traversalArr[i];
            for (ArrowKey direction : ArrowKey.values()) {
                node.setNextNode(direction,
                    nodeArr[traverseIx[direction.ordinal()]]);
            }
        }
        enableValidNumbersOnKeyPad(minValue, maxValue);
        return group;
    }

    /*
     * Based upon the minValue and maxValue arguments, enable only those
     * numbers on the KeyPad that represent valid input.
     * Both minValue and maxValue should be in the range of 0-9, where
     * minValue must be less than or equal to maxValue.
     */
    public void enableValidNumbersOnKeyPad(int minValue, int maxValue) {
        for (NumberNode n : nodeArr) {
            if (n.value >= minValue && n.value <= maxValue) {
                n.setOpacity(enabledOpacity);
                n.setDisable(false);
            } else {
                n.setOpacity(disabledOpacity);
                n.setDisable(true);
            }
        }
    }

    private void processKeyEvent(KeyCode key) {
        NumberNode highlight = null;
        boolean validKey = false;
        boolean selected = false;
        int numberKey = key.ordinal() - KeyCode.DIGIT0.ordinal();
        switch(key) {
            case LEFT:
                highlight=lastHighlightedNumberNode.getNextNode(ArrowKey.LEFT);
                validKey = true;
                break;
            case RIGHT:
            case TAB:
                highlight=lastHighlightedNumberNode.getNextNode(ArrowKey.RIGHT);
                validKey = true;
                break;
            case UP:
                highlight=lastHighlightedNumberNode.getNextNode(ArrowKey.UP);
                validKey = true;
                break;
            case DOWN:
                highlight=lastHighlightedNumberNode.getNextNode(ArrowKey.DOWN);
                validKey = true;
                break;
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
                if (!nodeArr[numberKey].isDisabled()) {
                    highlight = nodeArr[numberKey];
                    validKey = true;
                    selected = true;
                }
                break;
            case ENTER:
                highlight = nodeArr[
                        ((NumberNode)lastHighlightedNumberNode).getValue()];
                validKey = true;
                selected = true;
                break;
        }
        if (validKey) {
            lastHighlightedNumberNode.unShowFocusHint();
            ((NumberNode)highlight).showFocusHint();
            Globals.keyEventAlreadyProcessed = true;
        }
        if (selected) {
            if ((key == KeyCode.ENTER) ||
                (numberKey >= minValue && numberKey <= maxValue)) {
                doSelected(highlight);
            }
        }
    }

    /*
     * When a number from the KeyPad is selected either by a mouse click or
     * from the keyboard, call the updateOverallValue() method from the
     * displayableWithDigits container.
     */
    private void doSelected(NumberNode numberNode) {
        if (displayableWithDigits != null && digit != null) {
            KeyCode keyCode = KeyCode.DIGIT0;
            switch (numberNode.getValue()) {
                case 0: keyCode = KeyCode.DIGIT0; break;
                case 1: keyCode = KeyCode.DIGIT1; break;
                case 2: keyCode = KeyCode.DIGIT2; break;
                case 3: keyCode = KeyCode.DIGIT3; break;
                case 4: keyCode = KeyCode.DIGIT4; break;
                case 5: keyCode = KeyCode.DIGIT5; break;
                case 6: keyCode = KeyCode.DIGIT6; break;
                case 7: keyCode = KeyCode.DIGIT7; break;
                case 8: keyCode = KeyCode.DIGIT8; break;
                case 9: keyCode = KeyCode.DIGIT9; break;
            }
            displayableWithDigits.setOverallValue(
                    displayableWithDigits.calculateKeyNumValue(digit, keyCode));
        }
        setVisible(false);
    }
}