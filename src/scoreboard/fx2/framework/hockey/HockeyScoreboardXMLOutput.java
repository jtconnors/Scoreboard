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

package scoreboard.fx2.framework.hockey;

import scoreboard.fx2.framework.XMLOutput;
import scoreboard.common.LayoutXOptions;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import scoreboard.common.ScoreboardOutputInterface;
import scoreboard.fx2.framework.XMLSpec;

/*
 * This class provides the XML Output functionality specific to a Hockey
 * Scoreboard implementation and extends the abstract XMLOutput class.
 */
public class HockeyScoreboardXMLOutput extends XMLOutput {

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
                if (typeStr.equals(XMLSpec.TYPE_TextNode)) {
                    dumpTextNode(field);
                } else if (typeStr.equals(XMLSpec.TYPE_SingleDigit) ||
                       typeStr.equals(XMLSpec.TYPE_TwoDigit) ||
                       typeStr.equals(HockeyScoreboardXMLSpec.TYPE_Penalty) ||
                       typeStr.equals(XMLSpec.TYPE_Clock)) {
                    dumpDisplayableWithDigits(field);
                } else if (typeStr.equals(
                        HockeyScoreboardXMLSpec.TYPE_HockeyScoreboard)) {
                    dumpHockeyScoreboard(field);
                } else if (typeStr.equals(
                        HockeyScoreboardXMLSpec.TYPE_ImageView)) {
                    dumpImageView(field);
                }
            }  catch (Exception e) {
                e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
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
                System.out.println(
                        HockeyScoreboardXMLSpec.configImageViewStr(
                            variableName, url,
                            layoutXOption.toString(),
                            HockeyScoreboardXMLSpec.NAME_homePenalty1,
                            HockeyScoreboardXMLSpec.NAME_guestPenalty2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
