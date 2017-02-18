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

import java.lang.invoke.MethodHandles;
import scoreboard.common.LayoutXOptions;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import scoreboard.common.ScoreboardOutputInterface;
import scoreboard.common.Utils;
import static scoreboard.fx2.framework.XMLSpec.*;

/*
 * This base class is used by Scoreboard implementations to dump scoreboard
 * configuration information in XML.  A version of that XML data can be
 * subsequently read in by a remote scorebord to configure its display.
 */
public abstract class XMLOutput {
    
    private final static Logger LOGGER =
            Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    protected ScoreboardOutputInterface scoreboardOutputInterface;
    protected Class scoreboardClass;
    protected Object scoreboard;
    protected double scoreboardWidth;
    protected double scoreboardHeight;

    public XMLOutput(ScoreboardOutputInterface scoreboardOutputInterface) {
        this.scoreboardOutputInterface = scoreboardOutputInterface;
        scoreboard = scoreboardOutputInterface.getScoreboard();
        scoreboardClass = scoreboard.getClass();
        scoreboardWidth = scoreboardOutputInterface.getScoreboardWidth();
        scoreboardHeight = scoreboardOutputInterface.getScoreboardHeight();
    }
    
    public abstract void dumpDisplayableNodes();
    
    public abstract void dumpDisplayableWithDigits(Field field);
    
    protected Object invokeMethod(Field field, String methodStr) {
        try {
            Class fieldClass = field.get(scoreboard).getClass();
            Method method = fieldClass.getMethod(methodStr);
            return method.invoke(field.get(scoreboard));
        } catch (IllegalAccessException | IllegalArgumentException | 
                NoSuchMethodException | SecurityException |
                InvocationTargetException e) {
            LOGGER.info(Utils.ExceptionStackTraceAsString(e));
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
            LOGGER.info(Utils.ExceptionStackTraceAsString(e));
        }
    }
}
