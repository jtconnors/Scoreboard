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

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.geometry.VPos;
import static scoreboard.fx2.framework.FX2Constants.
        DEFAULT_BACKGROUND_COLOR;
import static scoreboard.fx2.framework.FX2Constants.
        DEFAULT_TEXT_COLOR;

/*
 * This component places Text inside a bounding Rectangle, making it
 * easier to position.  Dynamic changes are allowed for content,
 * backgroundColor and textColor.  The fontSize can only be specified by
 * the constructor.  A fontSize change requires a new object allocation.
 */
public class TextNode extends Displayable {

    public String getContent() {
        return text.getText();
    }
    public void setContent(String content) {
        text.setText(content);
        boundingRect.setWidth(text.getLayoutBounds().getWidth());
    }

    private double fontSize = 30d;
    public double getFontSize() { return fontSize; }

    private Color backgroundColor = DEFAULT_BACKGROUND_COLOR;
    public Color getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        boundingRect.setFill(backgroundColor);
    }

    private Color textColor = DEFAULT_TEXT_COLOR;
    public Color getTextColor() { return textColor; }
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        text.setFill(textColor);
        text.setStroke(textColor);
    }
    
    private Font font;
    public Font getFont() { return font; }
    public void setFont(Font font) {
        text.setFont(font);
    }

    private Text text;

    public TextNode(String varName, String content, double fontSize) {
        this(varName, content, fontSize,
                DEFAULT_BACKGROUND_COLOR, DEFAULT_TEXT_COLOR);
    }

    public TextNode(String varName, String content, double fontSize,
            Color backgroundColor, Color textColor) {
        this.varName = varName;
        this.fontSize = fontSize;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        text = new Text();
        font = Font.font("Lucida Sans", FontWeight.BOLD,
                FontPosture.REGULAR, fontSize);
        text.setFont(font);
        text.setFill(textColor);
        text.setStroke(textColor);
        text.setTextOrigin(VPos.TOP);
        text.setText(content);

        boundingRect = new Rectangle();
        boundingRect.setHeight(text.getLayoutBounds().getHeight());
        boundingRect.setWidth(text.getLayoutBounds().getWidth());
        boundingRect.setFill(backgroundColor);
        getChildren().addAll(boundingRect, text);
        
        componentWidth = boundingRect.getWidth();
        componentHeight = boundingRect.getHeight();
    }
}
