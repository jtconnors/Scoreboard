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

package scoreboard.common.xml;

import scoreboard.common.LayoutXOptions;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import scoreboard.common.ScoreboardOutputInterface;
import static scoreboard.common.xml.XMLSpec.*;

public class XMLOutput {

    private ScoreboardOutputInterface scoreboardOutputInterface;
    private Class scoreboardClass;
    private Object scoreboard;
    private double scoreboardWidth;
    private double scoreboardHeight;

    public XMLOutput(ScoreboardOutputInterface scoreboardOutputInterface) {
        this.scoreboardOutputInterface = scoreboardOutputInterface;
        scoreboard = scoreboardOutputInterface.getScoreboard();
        scoreboardClass = scoreboard.getClass();
        scoreboardWidth = scoreboardOutputInterface.getScoreboardWidth();
        scoreboardHeight = scoreboardOutputInterface.getScoreboardHeight();
    }

    public void dumpDisplayableNodes() {
        Field field;
        for (String fieldName : XMLSpec.ConfigVariableNames) {
            try {
                field = scoreboardClass.getField(fieldName);
                String typeStr = field.getType().getSimpleName();
                if (typeStr.equals(TYPE_TextNode)) {
                    dumpTextNode(field);
                } else if (typeStr.equals(TYPE_SingleDigit) ||
                       typeStr.equals(TYPE_TwoDigit) ||
                       typeStr.equals(TYPE_Penalty) ||
                       typeStr.equals(TYPE_Clock)) {
                    dumpDisplayableWithDigits(field);
                } else if (typeStr.equals(TYPE_HockeyScoreboard)) {
                    dumpHockeyScoreboard(field);
                } else if (typeStr.equals(TYPE_ImageView)) {
                    dumpImageView(field);
                }
            }  catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private Object invokeMethod(Field field, String methodStr) {
        try {
            Class fieldClass = field.get(scoreboard).getClass();
            Method method = fieldClass.getMethod(methodStr);
            return method.invoke(field.get(scoreboard));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void dumpTextNode(Field field) {
        try {
            String variableName = field.getName();
            Double layoutY = (Double)
                invokeMethod(field, METHOD_getLayoutY) / scoreboardHeight;
            LayoutXOptions layoutXOption = (LayoutXOptions)
                invokeMethod(field, METHOD_getLayoutXOption);
            Object alignWith = invokeMethod(field, METHOD_getAlignWith);
            String alignWithStr =
                        scoreboardOutputInterface.getFieldName(alignWith);
            Double fontSize = (Double)
                invokeMethod(field, METHOD_getFontSize) / scoreboardHeight;
            String content = (String)invokeMethod(field, METHOD_getContent);
            System.out.println(XMLSpec.configTextNodeStr(variableName,
                layoutY, layoutXOption.toString(), alignWithStr,
                fontSize, content));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dumpDisplayableWithDigits(Field field) {
        try {
            Class fieldClass = field.get(scoreboard).getClass();
            String variableName = field.getName();
            Double layoutY = (Double)
                invokeMethod(field, METHOD_getLayoutY) / scoreboardHeight;
            LayoutXOptions layoutXOption = (LayoutXOptions)
                invokeMethod(field, METHOD_getLayoutXOption);
            Object alignWith = invokeMethod(field, METHOD_getAlignWith);
            String alignWithStr =
                scoreboardOutputInterface.getFieldName(alignWith);
            Double digitHeight = (Double)
                invokeMethod(field, METHOD_getDigitHeight) / scoreboardHeight;
            Method method = fieldClass.getMethod(METHOD_getColor);
            int colorVal = scoreboardOutputInterface.getColorVal(
                field.get(scoreboard), method);
            method = fieldClass.getMethod(METHOD_getOverallValue);
            int overallValue = (Integer)
                invokeMethod(field, METHOD_getOverallValue);
            System.out.println(
                XMLSpec.configDisplayableWithDigitsStr(variableName,
                layoutY, layoutXOption.toString(), alignWithStr,
                digitHeight, overallValue));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dumpHockeyScoreboard(Field field) {
        try {
            Class fieldClass = field.get(scoreboard).getClass();
            String variableName = field.getName();
            Method method = fieldClass.getMethod(METHOD_getBackgroundColor);
            int backgroundColorVal = scoreboardOutputInterface.getColorVal(
                    scoreboard, method);
            System.out.println(XMLSpec.configHockeyScoreboardStr(variableName,
                    backgroundColorVal));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dumpImageView(Field field) {
        try {
            if (field.get(scoreboard) != null) {
                Class fieldClass = field.get(scoreboard).getClass();
                String variableName = field.getName();
                /*
                 * Hard code these.  JavaFX ImageView class would have to
                 * be extended in order to use reflection to get the
                 * values below.
                 */
                String url = "logo.png";
                LayoutXOptions layoutXOption = LayoutXOptions.CENTER_BETWEEN;
                String tlObjStr = NAME_homePenalty1;
                String brObjStr = NAME_guestPenalty2;
                System.out.println(XMLSpec.configImageViewStr(variableName, url,
                        layoutXOption.toString(), tlObjStr, brObjStr));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
