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

package com.jtconnors.scoreboard.fx2.framework.hockey;

import java.lang.invoke.MethodHandles;
import com.jtconnors.scoreboard.fx2.framework.XMLOutput;
import com.jtconnors.scoreboard.common.LayoutXOptions;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import com.jtconnors.scoreboard.common.ScoreboardOutputInterface;
import com.jtconnors.scoreboard.common.Utils;
import com.jtconnors.scoreboard.fx2.framework.XMLSpec;

/*
 * This class provides the XML Output functionality specific to a Hockey
 * Scoreboard implementation and extends the abstract XMLOutput class.
 */
public class HockeyScoreboardXMLOutput extends XMLOutput {
    
    private final static Logger LOGGER =
            Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public HockeyScoreboardXMLOutput(
            ScoreboardOutputInterface scoreboardOutputInterface) {
        super(scoreboardOutputInterface);
    }

    /*
     * Implementation of abstract dumpDisplayableNodes() defined in
     * XMLOuput super class
     */
    @Override
    public void dumpDisplayableNodes() {
        Field field;
        for (String fieldName : XMLSpec.ConfigVariableNames) {
            try {
                field = scoreboardClass.getField(fieldName);
                String typeStr = field.getType().getSimpleName();
                switch (typeStr) {
                    case XMLSpec.TYPE_TextNode:
                        dumpTextNode(field);
                        break;
                    case XMLSpec.TYPE_SingleDigit:
                    case XMLSpec.TYPE_TwoDigit:
                    case HockeyScoreboardXMLSpec.TYPE_Penalty:
                    case XMLSpec.TYPE_Clock:
                        dumpDisplayableWithDigits(field);
                        break;
                    case HockeyScoreboardXMLSpec.TYPE_HockeyScoreboard:
                        dumpHockeyScoreboard(field);
                        break;
                    case HockeyScoreboardXMLSpec.TYPE_ImageView:
                        dumpImageView(field);
                        break;
                    default:
                        break;
                }
            }  catch (NoSuchFieldException | SecurityException e) {
                LOGGER.info(Utils.ExceptionStackTraceAsString(e));
            }
        }
    }

    /*
     * Implementation of abstract dumpDisplayableWithDigits() found in
     * XMLOuput super class
     */
    @Override
    public void dumpDisplayableWithDigits(Field field) {
        try {
            Class fieldClass = field.get(scoreboard).getClass();
            String variableName = field.getName();
            Double layoutY = (Double)
                invokeMethod(field, XMLSpec.METHOD_getLayoutY) /
                    scoreboardHeight;
            LayoutXOptions layoutXOption = (LayoutXOptions)
                invokeMethod(field, XMLSpec.METHOD_getLayoutXOption);
            Object alignWith = invokeMethod(field, XMLSpec.METHOD_getAlignWith);
            String alignWithStr =
                scoreboardOutputInterface.getFieldName(alignWith);
            Double digitHeight = (Double)
                invokeMethod(field, XMLSpec.METHOD_getDigitHeight) /
                    scoreboardHeight;
            Method method = fieldClass.getMethod(XMLSpec.METHOD_getColor);
            int colorVal = scoreboardOutputInterface.getColorVal(
                field.get(scoreboard), method);
            method = fieldClass.getMethod(XMLSpec.METHOD_getOverallValue);
            int overallValue = (Integer)
                invokeMethod(field, XMLSpec.METHOD_getOverallValue);
            System.out.println(
                XMLSpec.configDisplayableWithDigitsStr(variableName,
                layoutY, layoutXOption.toString(), alignWithStr,
                digitHeight, overallValue));
        } catch (IllegalAccessException | IllegalArgumentException |
                NoSuchMethodException | SecurityException e) {
            LOGGER.severe(Utils.ExceptionStackTraceAsString(e));
        }
    }

    public void dumpHockeyScoreboard(Field field) {
        try {
            Class fieldClass = field.get(scoreboard).getClass();
            String variableName = field.getName();
            Method method = fieldClass.getMethod(
                    XMLSpec.METHOD_getBackgroundColor);
            int backgroundColorVal = scoreboardOutputInterface.getColorVal(
                    scoreboard, method);
            System.out.println(
                    HockeyScoreboardXMLSpec.configHockeyScoreboardStr(
                    variableName, backgroundColorVal));
        } catch (IllegalAccessException | IllegalArgumentException |
                NoSuchMethodException | SecurityException e) {
            LOGGER.info(Utils.ExceptionStackTraceAsString(e));
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
                String url = "/scoreboard/images/logo.png";
                LayoutXOptions layoutXOption = LayoutXOptions.CENTER_BETWEEN;
                System.out.println(
                        HockeyScoreboardXMLSpec.configImageViewStr(
                            variableName, url,
                            layoutXOption.toString(),
                            HockeyScoreboardXMLSpec.NAME_homePenalty1,
                            HockeyScoreboardXMLSpec.NAME_guestPenalty2));
            }
        } catch (IllegalAccessException | IllegalArgumentException e) {
            LOGGER.info(Utils.ExceptionStackTraceAsString(e));
        }
    }
}
