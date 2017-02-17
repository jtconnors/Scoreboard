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

package scoreboard.fx2.util;

public class FXUtils {
    /**
     * Converts the JavaFX color argument into a 6-digit hexadecimal number
     * representing the RGB value of the color.  The leftmost 2 hex digits
     * of the RGB value represent the Red value [0-255], the middle 2 digits
     * house the Green value [0-255] and the rightmost 2 digits keep the
     * Blue value [0-255].
     */
    public static int FXColorToIntValue(javafx.scene.paint.Color color) {
        int value = 0;
        value |= (((int)(color.getRed() * 255) & 0xFF) << 16);
        value |= (((int)(color.getGreen() * 255) & 0xFF) << 8);
        value |= (int)(color.getBlue() * 255) & 0xFF;
        return value;
    }

    /**
     * Converts the int "value" argument into a JavaFX Color instance. "value"
     * represents a 6-digit hexadecimal number containing the RGB value of
     * the color.  The leftmost 2 hex digits represent the Red value [0-255],
     * the middle 2 digits house the Green value [0-255] and the rightmost
     * 2 digits keep the Blue value [0-255].
     */
    public static javafx.scene.paint.Color intValueToFXColor(int value) {
        double red = ((value & 0xFF0000) >> 16) / 255f;
        double green = ((value & 0x00FF00) >> 8) / 255f;
        double blue = (value & 0x0000FF) / 255f;
        return new javafx.scene.paint.Color(red, green, blue, 1.0);
    }
}
