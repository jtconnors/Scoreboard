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

import java.util.ArrayList;
import java.util.Formatter;
import scoreboard.fx2.framework.*;

public class HockeyScoreboardXMLSpec extends XMLSpec {
    /*
     * Variable names specific to a HockeyScoreboard implementation
     */
    public final static String NAME_homeText = "homeText";
    public final static String NAME_guestText = "guestText";
    public final static String NAME_periodText = "periodText";
    public final static String NAME_shotsOnGoalText = "shotsOnGoalText";
    public final static String NAME_homePenaltyText = "homePenaltyText";
    public final static String NAME_guestPenaltyText = "guestPenaltyText";
    public final static String NAME_playerNumber = "playerNumber";
    public final static String NAME_homePenalty1 = "homePenalty1";
    public final static String NAME_homePenalty1playerNumber =
            NAME_homePenalty1 + NAME_playerNumber;
    public final static String NAME_guestPenalty1 = "guestPenalty1";
    public final static String NAME_guestPenalty1playerNumber =
            NAME_guestPenalty1 + NAME_playerNumber;
    public final static String NAME_homePenalty2 = "homePenalty2";
    public final static String NAME_homePenalty2playerNumber =
            NAME_homePenalty2 + NAME_playerNumber;
    public final static String NAME_guestPenalty2 = "guestPenalty2";
    public final static String NAME_guestPenalty2playerNumber =
            NAME_guestPenalty2 + NAME_playerNumber;
    public final static String NAME_clock = "clock";
    public final static String NAME_homeScore = "homeScore";
    public final static String NAME_guestScore = "guestScore";
    public final static String NAME_period = "period";
    public final static String NAME_homeShotsOnGoal = "homeShotsOnGoal";
    public final static String NAME_guestShotsOnGoal = "guestShotsOnGoal";
    public final static String NAME_hockeyScoreboard = "hockeyScoreboard";
    public final static String NAME_logoImageView = "logoImageView";
    public final static String NAME_displaySocketText = "displaySocketText";
    public final static String NAME_horn = "horn";    
/*
 *****************************************************************************
 * The following ArrayLists are specific to this Scoreboard implementation.  *
 * These ArrayLists will contain the HockeyScorboard specific variables and  *
 * must be initialized in the init() method defined in this class.           *                                                                    *
 *****************************************************************************/
    protected static ArrayList<String> PenaltyVariableNames;
    protected static ArrayList<String> PlayerNumberVariableNames;       
    protected static ArrayList<String> HornVariableNames;
    protected static ArrayList<String> OtherVariableNames; 
 /*
  ****************************************************************************
  ****************************************************************************/
    
    /*
     * Type name Strings specific to HockeyScoreboard implementation
     */
    public final static String TYPE_Penalty = "Penalty";
    public final static String TYPE_HockeyScoreboard = "HockeyScoreboard";
    public final static String TYPE_ImageView = "ImageView";
    
    /*
     * Config element for HockeyScorboard instance
     */
    public final static String CONFIG_HockeyScoreboard_sd =
    START_CONFIG + NL +
    T + NAME_s + NL +
    T + BACKGROUNDCOLOR_d + NL +
    END_CONFIG;

    /*
     * Config element for ImageView instance
     */
    public final static String CONFIG_ImageView_sssss =
    START_CONFIG + NL +
    T + NAME_s + NL +
    T + URL_s + NL +
    T + LAYOUTX_OPTION_s + NL +
    T + TOPLEFT_OBJECT_s + NL +
    T + BOTTOMRIGHT_OBJECT_s + NL +
    END_CONFIG;
    
 /*
  ****************************************************************************
  * Both the parent XMLSpec class and this class declare but do not define a *
  * series of ArrayLists.  These ArrayLists classify and hold the names of   * 
  * displayable scoreboard objects that must be initialized and populated.   *
  * The purpose of the init() method that follows is to do that              *
  * initialization.  This method should be called early on inside a          *
  * Scoreboard implementation's init() method.                               *
  ****************************************************************************/
    public static void init() {
    /*
     * HockeyScoreboardXMLSpec specific variable name ArrayList initializations
     */
        /*
         * Penalty variable names
         */
        HockeyScoreboardXMLSpec.PenaltyVariableNames = new ArrayList<String>();
        PenaltyVariableNames.add(NAME_homePenalty1);
        PenaltyVariableNames.add(NAME_guestPenalty1);
        PenaltyVariableNames.add(NAME_homePenalty2);
        PenaltyVariableNames.add(NAME_guestPenalty2);
        /*
         * Penalty playerNumber variable names.  These are not real variable
         * names per se, but rather a part of of the Penalty Object.
         * They are needed to send/receive updates for the playerNumber fields
         * for each of the Penalty objects.
         */
        HockeyScoreboardXMLSpec.PlayerNumberVariableNames =
                new ArrayList<String>();
        PlayerNumberVariableNames.add(NAME_homePenalty1playerNumber);
        PlayerNumberVariableNames.add(NAME_guestPenalty1playerNumber);
        PlayerNumberVariableNames.add(NAME_homePenalty2playerNumber);
        PlayerNumberVariableNames.add(NAME_guestPenalty2playerNumber);
        /*
         * Horn variable name
         */
        HockeyScoreboardXMLSpec.HornVariableNames = new ArrayList<String>();
        HornVariableNames.add(NAME_horn);  
        /*
         * Other variable names
         */
        HockeyScoreboardXMLSpec.OtherVariableNames = new ArrayList<String>();
        OtherVariableNames.add(NAME_hockeyScoreboard);
        OtherVariableNames.add(NAME_logoImageView);
    /*
     * Variable name initializations from ArrayList declarations in parent
     * XMLSpec class
     */
        /*
         * Variable names of the TextNode objects for this implementation
         */
        XMLSpec.TextNodeVariableNames = new ArrayList<String>();
        TextNodeVariableNames.add(NAME_homeText);
        TextNodeVariableNames.add(NAME_homeText);
        TextNodeVariableNames.add(NAME_guestText);
        TextNodeVariableNames.add(NAME_periodText);
        TextNodeVariableNames.add(NAME_shotsOnGoalText);
        TextNodeVariableNames.add(NAME_homePenaltyText);
        TextNodeVariableNames.add(NAME_guestPenaltyText);
        TextNodeVariableNames.add(NAME_displaySocketText);
        /*
         * Variable names of the Clock objects for this implementation
         */
        XMLSpec.ClockVariableNames = new ArrayList<String>();
        ClockVariableNames.add(NAME_clock);
        /*
         * Variable names of the TwoDigit objects for this implementation
         */
        XMLSpec.TwoDigitVariableNames = new ArrayList<String>();
        TwoDigitVariableNames.add(NAME_homeScore);
        TwoDigitVariableNames.add(NAME_guestScore);
        TwoDigitVariableNames.add(NAME_homeShotsOnGoal);
        TwoDigitVariableNames.add(NAME_guestShotsOnGoal);
        /*
         * Variable names of the SingleDigit objects for this implementation
         */
        XMLSpec.SingleDigitVariableNames = new ArrayList<String>();
        SingleDigitVariableNames.add(NAME_period);
        /*
         * Variable names of the DisplayableWithDigits objects for this
         * implementation
         */
        XMLSpec.DisplayableWithDigitsNames = new ArrayList<String>();
        DisplayableWithDigitsNames.add(NAME_clock);
        DisplayableWithDigitsNames.add(NAME_homeScore);
        DisplayableWithDigitsNames.add(NAME_guestScore);
        DisplayableWithDigitsNames.add(NAME_homeShotsOnGoal);
        DisplayableWithDigitsNames.add(NAME_guestShotsOnGoal);
        DisplayableWithDigitsNames.add(NAME_period);
        DisplayableWithDigitsNames.add(NAME_homePenalty1);
        DisplayableWithDigitsNames.add(NAME_guestPenalty1);
        DisplayableWithDigitsNames.add(NAME_homePenalty2);
        DisplayableWithDigitsNames.add(NAME_guestPenalty2);
    /*
     * ConfigVariableNames contains all of the possible variable names
     * that could be used within a <config> XML element.
     */
        XMLSpec.ConfigVariableNames = new ArrayList<String>();
        for (String s : ClockVariableNames) {
            ConfigVariableNames.add(s);
        }
        for (String s : TwoDigitVariableNames) {
            ConfigVariableNames.add(s);
        }
        for (String s : SingleDigitVariableNames) {
            ConfigVariableNames.add(s);
        }
        for (String s : PenaltyVariableNames) {
            ConfigVariableNames.add(s);
        }
        for (String s : TextNodeVariableNames) {
            ConfigVariableNames.add(s);
        }
        for (String s : OtherVariableNames) {
            ConfigVariableNames.add(s);
        }
    /*
     * UpdateVariableNames contains all of the possible variable names that
     * could be used within an <update> XML element.
     */
        XMLSpec.UpdateVariableNames = new ArrayList<String>();
        for (String s : ClockVariableNames) {
            UpdateVariableNames.add(s);
        }
        for (String s : TwoDigitVariableNames) {
            UpdateVariableNames.add(s);
        }
        for (String s : SingleDigitVariableNames) {
            UpdateVariableNames.add(s);
        }
        for (String s : PenaltyVariableNames) {
            UpdateVariableNames.add(s);
        }
        for (String s : PlayerNumberVariableNames) {
            UpdateVariableNames.add(s);
        }
        for (String s : HornVariableNames) {
            UpdateVariableNames.add(s);
        }    
    }

    /**
     * Determines if the name argument is one of the known Penalty variables
     */
    public static boolean isPenaltyVariable(String name) {
        for (String n : PenaltyVariableNames) {
            if (name.equals(n)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the name argument is a playerNumber variable.  This is
     * actually a sub component inside the Penalty component.
     */
    public static boolean isPlayerNumberVariable(String name) {
        for (String n : PlayerNumberVariableNames) {
            if (name.equals(n)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the name argument is a HockeyScoreboard variable
     */
    public static boolean isHockeyScoreboardVariable(String name) {
        return name.equals(NAME_hockeyScoreboard) ? true : false;
    }

    /**
     * Determines if the name argument is an ImageView variable
     */
    public static boolean isImageViewVariable(String name) {
        return name.equals(NAME_logoImageView) ? true : false;
    }

    /**
     * Creates an xml string for the <config> element, relevant for
     * properties associated with the hockeyScoreboard object.
     */
    public static String configHockeyScoreboardStr(String varName,
            int backgroundColorVal) {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);
        formatter.format(CONFIG_HockeyScoreboard_sd, varName,
                backgroundColorVal);
        return stringBuilder.toString();
    }

    /**
     * Creates an xml string for the <config> element, relevant for
     * properties associated with an ImageView object.
     */
    public static String configImageViewStr(String varName,
            String url, String layoutXOptionStr,
            String bottomRightObjectStr, String topLeftObjectStr) {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);
        formatter.format(CONFIG_ImageView_sssss, varName, url,
                layoutXOptionStr, bottomRightObjectStr, topLeftObjectStr);
        return stringBuilder.toString();
    }
}
