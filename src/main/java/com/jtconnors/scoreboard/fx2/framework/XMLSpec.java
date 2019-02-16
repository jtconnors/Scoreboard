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

import java.util.ArrayList;
import com.jtconnors.scoreboard.common.LayoutXOptions;
import java.util.Formatter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * This class contains the constants, rules and methods used to 
 * construct XML data relevant to the configuration and update of remote
 * scoreboard instances.
 * 
 * This class is abstract and contains the framework that all implementations
 * should share.  Real implementations must extend this class.  
 * For an implementation example, see
 * scoreboard.fx2.framework.hockey.HockeyScoreboardXMLSpec.java
 */
public abstract class XMLSpec {
    /*
     * These two constants represent a NEWLINE character and a TAB (4 spaces)
     * repectively and are set to make the config XML output look more human
     * readable.
     */
    public final static String NL = "\n";
    public final static String T = "    ";
    
    /*
     * Tag names
     */
    public final static String TAG_ALIGNOBJECT = "alignObject";
    public final static String START_ALIGNOBJECT =
            "<" + TAG_ALIGNOBJECT + ">";
    public final static String END_ALIGNOBJECT =
            "</" + TAG_ALIGNOBJECT + ">";
    
    public final static String TAG_BACKGROUNDCOLOR = "backgroundColor";
    public final static String START_BACKGROUNDCOLOR =
            "<" + TAG_BACKGROUNDCOLOR + ">";
    public final static String END_BACKGROUNDCOLOR =
            "</" + TAG_BACKGROUNDCOLOR + ">";
    
    public final static String TAG_BOTTOMRIGHTOBJECT = "bottomRightObject";
    public final static String START_BOTTOMRIGHTOBJECT =
            "<" + TAG_BOTTOMRIGHTOBJECT + ">";
    public final static String END_BOTTOMRIGHTOBJECT =
            "</" + TAG_BOTTOMRIGHTOBJECT + ">";
    
    public final static String TAG_COLOR = "color";
    public final static String START_COLOR = "<" + TAG_COLOR + ">";
    public final static String END_COLOR = "</" + TAG_COLOR + ">";
    
    public final static String TAG_CONFIG = "config";
    public final static String START_CONFIG = "<" + TAG_CONFIG + ">";
    public final static String END_CONFIG = "</" + TAG_CONFIG + ">";
    
    public final static String TAG_CONTENT = "content";
    public final static String START_CONTENT = "<" + TAG_CONTENT + ">";
    public final static String END_CONTENT = "</" + TAG_CONTENT + ">";
    
    public final static String TAG_DIGITHEIGHT = "digitHeight";
    public final static String START_DIGITHEIGHT = "<" + TAG_DIGITHEIGHT + ">";
    public final static String END_DIGITHEIGHT = "</" + TAG_DIGITHEIGHT + ">";
    
    public final static String TAG_FITHEIGHT = "fitHeight";
    public final static String START_FITHEIGHT = "<" + TAG_FITHEIGHT + ">";
    public final static String END_FITHEIGHT = "</" + TAG_FITHEIGHT + ">";
    
    public final static String TAG_FITWIDTH = "fitWidth";
    public final static String START_FITWIDTH = "<" + TAG_FITWIDTH + ">";
    public final static String END_FITWIDTH = "</" + TAG_FITWIDTH + ">";
    
    public final static String TAG_FONTSIZE = "fontSize";
    public final static String START_FONTSIZE = "<" + TAG_FONTSIZE + ">";
    public final static String END_FONTSIZE = "</" + TAG_FONTSIZE + ">";
    
    public final static String TAG_LAYOUTX = "layoutX";
    public final static String START_LAYOUTX = "<" + TAG_LAYOUTX + ">";
    public final static String END_LAYOUTX = "</" + TAG_LAYOUTX + ">";
    
    public final static String TAG_LAYOUTXOPTION = "layoutXOption";
    public final static String START_LAYOUTXOPTION =
            "<" + TAG_LAYOUTXOPTION + ">";
    public final static String END_LAYOUTXOPTION =
            "</" + TAG_LAYOUTXOPTION + ">";
    
    public final static String TAG_LAYOUTY = "layoutY";
    public final static String START_LAYOUTY = "<" + TAG_LAYOUTY + ">";
    public final static String END_LAYOUTY = "</" + TAG_LAYOUTY + ">";
    
    public final static String TAG_NAME = "name";
    public final static String START_NAME = "<" + TAG_NAME + ">";
    public final static String END_NAME = "</" + TAG_NAME + ">";
    
    public final static String TAG_OVERALLVALUE = "overallValue";
    public final static String START_OVERALLVALUE =
            "<" +TAG_OVERALLVALUE + ">";
    public final static String END_OVERALLVALUE =
            "</" + TAG_OVERALLVALUE + ">";
    
    public final static String TAG_TOPLEFTOBJECT = "topLeftObject";
    public final static String START_TOPLEFTOBJECT =
            "<" + TAG_TOPLEFTOBJECT + ">";
    public final static String END_TOPLEFTOBJECT =
            "</" + TAG_TOPLEFTOBJECT + ">";
    
    public final static String TAG_UPDATE = "update";
    public final static String START_UPDATE = "<" + TAG_UPDATE + ">";
    public final static String END_UPDATE = "</" + TAG_UPDATE + ">";
    
    public final static String TAG_URL = "url";
    public final static String START_URL = "<" + TAG_URL + ">";
    public final static String END_URL = "</" + TAG_URL + ">";

    /*
     * Alignment options as defined in scoreboard.common.LayoutXOptions enum.
     * Any changes to the enum must be reflected here.
     */
    public final static String OPTION_UNDEFINED =
            LayoutXOptions.UNDEFINED.toString();
    public final static String OPTION_LEFT_JUSTIFY =
            LayoutXOptions.LEFT_JUSTIFY.toString();
    public final static String OPTION_RIGHT_JUSTIFY =
            LayoutXOptions.RIGHT_JUSTIFY.toString();
    public final static String OPTION_CENTER =
            LayoutXOptions.CENTER.toString();
    public final static String OPTION_ALIGN_LEFT_OF =
            LayoutXOptions.ALIGN_LEFT_OF.toString();
    public final static String OPTION_RIGHT_OF =
            LayoutXOptions.ALIGN_RIGHT_OF.toString();
    public final static String OPTION_ALIGN_WITH =
            LayoutXOptions.ALIGN_WITH.toString();
    public final static String OPTION_GROUP_CENTER_LEFT_WITH =
            LayoutXOptions.GROUP_CENTER_LEFT_WITH.toString();
    public final static String OPTION_GROUP_CENTER_RIGHT_WITH =
            LayoutXOptions.GROUP_CENTER_RIGHT_WITH.toString();
    public final static String OPTION_CENTER_BETWEEN =
            LayoutXOptions.CENTER_BETWEEN.toString();

    /*
     * Type name Strings
     */
    public final static String TYPE_Clock = "Clock";
    public final static String TYPE_SingleDigit = "SingleDigit";
    public final static String TYPE_TextNode = "TextNode";
    public final static String TYPE_TwoDigit = "TwoDigit";

    /*
     * Method name Strings
     */
    public final static String METHOD_getAlignWith = "getAlignWith";
    public final static String METHOD_getBackgroundColor = "getBackgroundColor";
    public final static String METHOD_getColor = "getColor";
    public final static String METHOD_getContent = "getContent";
    public final static String METHOD_getDigitHeight = "getDigitHeight";
    public final static String METHOD_getFontSize = "getFontSize";
    public final static String METHOD_getLayoutX = "getLayoutX";
    public final static String METHOD_getLayoutXOption = "getLayoutXOption";
    public final static String METHOD_getLayoutY = "getLayoutY";
    public final static String METHOD_getOverallValue = "getOverallValue";
    public final static String METHOD_getPlayerNumber = "getPlayerNumber";
    public final static String METHOD_getFitHeight = "getFitHeight";
    public final static String METHOD_getFitWidth = "getFitWidth";
    public final static String METHOD_getUrl = "getUrl";

    /*
     * XML property elements
     */
    public final static String NAME_s = START_NAME + "%s" + END_NAME;

    public final static String ALIGN_OBJECT_s =
            START_ALIGNOBJECT + "%s" + END_ALIGNOBJECT;
    public final static String BACKGROUNDCOLOR_d =
            START_BACKGROUNDCOLOR + "%s" + END_BACKGROUNDCOLOR;
    public final static String BOTTOMRIGHT_OBJECT_s =
            START_BOTTOMRIGHTOBJECT + "%s" + END_BOTTOMRIGHTOBJECT;
    public final static String COLOR_s = START_COLOR + "%s" + END_COLOR;
    public final static String CONTENT_s = START_CONTENT + "%s" + END_CONTENT;
    public final static String DIGITHEIGHT_f =
        START_DIGITHEIGHT + "%f" + END_DIGITHEIGHT;
    public final static String FONTSIZE_f =
        START_FONTSIZE + "%f" + END_FONTSIZE;
    public final static String LAYOUTX_f = START_LAYOUTX + "%f" + END_LAYOUTX;
    public final static String LAYOUTX_OPTION_s =
            START_LAYOUTXOPTION + "%s" + END_LAYOUTXOPTION;
    public final static String LAYOUTY_f = START_LAYOUTY + "%f" + END_LAYOUTY;
    public final static String OVERALLVALUE_s =
        START_OVERALLVALUE + "%s" + END_OVERALLVALUE;
    public final static String FITHEIGHT_f =
        START_FITHEIGHT + "%f" + END_FITHEIGHT;
    public final static String FITWIDTH_f =
        START_FITWIDTH + "%f" + END_FITWIDTH;
    public final static String TOPLEFT_OBJECT_s =
            START_TOPLEFTOBJECT + "%s" + END_TOPLEFTOBJECT;
    public final static String URL_s = START_URL + "%s" + END_URL;

    /*
     * Config element for TextNode variables
     */
    public final static String CONFIG_TextNode_sfssfs =
    START_CONFIG + NL +
    T + NAME_s + NL +
    T + LAYOUTY_f + NL +
    T + LAYOUTX_OPTION_s + NL +
    T + ALIGN_OBJECT_s + NL +
    T + FONTSIZE_f +  NL +
    T + CONTENT_s + NL +
    END_CONFIG;

    /*
     * Config element for other DisplayableWithDigit variables
     */
    public final static String CONFIG_DisplayableWithDigits_sfssfd =
    START_CONFIG + NL +
    T + NAME_s + NL +
    T + LAYOUTY_f + NL +
    T + LAYOUTX_OPTION_s + NL +
    T + ALIGN_OBJECT_s + NL +
    T + DIGITHEIGHT_f + NL +
    T + OVERALLVALUE_s + NL +
    END_CONFIG;

    /*
     * Update element for DisplayableWithDigits variables
     */
    public final static String UPDATE_DisplayableWithDigits_ss =
    START_UPDATE +
    NAME_s +
    OVERALLVALUE_s +
    END_UPDATE;
    
/***************************************************************************
 *  The following ArrayList objects must be initialized by a subclass      *
 *  implementation of XMLSpec.  For an example, see                        *
 *  scoreboard.fx2.framework.hockey.HockeyScoreboardXMLSpec.java           *
 ***************************************************************************/
    public static ArrayList<String> TextNodeVariableNames;
    public static ArrayList<String> ClockVariableNames;
    public static ArrayList<String> TwoDigitVariableNames;
    public static ArrayList<String> SingleDigitVariableNames;
    public static ArrayList<String> DisplayableWithDigitsNames;
    public static ArrayList<String> ConfigVariableNames;
    public static ArrayList<String> UpdateVariableNames;
/****************************************************************************
 *  End ArrayList declarations                                              *
 ****************************************************************************/

    /**
     * Determines if the name argument is one of the known TextNode variables
     */
    public static boolean isTextNodeVariable(String name) {
        for (String n : TextNodeVariableNames) {
            if (name.equals(n)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the name argument is a Clock variable
     */
    public static boolean isClockVariable(String name) {
        for (String n : ClockVariableNames) {
            if (name.equals(n)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the name argument is a TwoDigit variable
     */
    public static boolean isTwoDigitVariable(String name) {
        for (String n : TwoDigitVariableNames) {
            if (name.equals(n)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the name argument is a SingleDigit variable
     */
    public static boolean isSingleDigitVariable(String name) {
        for (String n : SingleDigitVariableNames) {
            if (name.equals(n)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Determines if the name argument is the name of any of the configurable
     * variables.
     */
    public static boolean isConfigVariable(String name) {
        for (String n : ConfigVariableNames) {
            if (name.equals(n)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the name argument is the name of a variable that
     * can be updated
     */
    public static boolean isUpdateVariable(String name) {
        for (String n : UpdateVariableNames) {
            if (name.equals(n)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the String value associated with the XML tag
     */
    public static String getTagValue(String tag, Element element) {
        NodeList nodeList =
                element.getElementsByTagName(tag).item(0).getChildNodes();
        return ((Node)nodeList.item(0)).getNodeValue();
    }

    /**
     * Creates an xml string for the <config> element, relevant for
     * variables of type TextNode
     */
    public static String configTextNodeStr(String varName,
            double layoutY, String layoutXOption, String alignWithStr,
            double fontSize, String content) {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);
        formatter.format(CONFIG_TextNode_sfssfs, varName, layoutY,
                layoutXOption, alignWithStr, fontSize, content);
        return stringBuilder.toString();
    }

    /**
     * Creates an xml string for the <config> element, relevant for
     * variables derived from DisplayableWithDigts (except Penalty)
     */
    public static String configDisplayableWithDigitsStr(String varName,
        double layoutY, String layoutXOptionStr, String alignWithStr,
        double digitHeight, int overallValue) {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);
        formatter.format(CONFIG_DisplayableWithDigits_sfssfd, varName,
                layoutY, layoutXOptionStr, alignWithStr,
                digitHeight, overallValue);
        return stringBuilder.toString();
    }

    /**
     * Creates an xml string for the <update> element.
     */
    public static String updateStr(String varName,
            String overallValueStr) {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);
        formatter.format(UPDATE_DisplayableWithDigits_ss, varName,
                overallValueStr);
        return stringBuilder.toString();
    }
}
