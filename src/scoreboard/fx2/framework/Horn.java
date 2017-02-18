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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import scoreboard.common.Constants;
import scoreboard.common.Globals;
import scoreboard.common.Utils;

/*
 * The Horn class represents a special case.  We want to utilize the XML update
 * framework created by the DisplayableWithDigits class to turn the horn on
 * and off, yet the horn cannot be displayed.  So, much of the required
 * implementation methods found here are basically null.
 */
public class Horn extends DisplayableWithDigits {
    
    private final static Logger LOGGER =
            Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    
    private final MediaPlayer mediaPlayer;
    
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

/****************************************************************************
 *  By virtue of extending the DisplayableWithDigits class, the following   *
 *  abstract methods declared in DisplayableWithDigits must be defined.     *
 *                                                                          *
 *  For this implementation, since we're not displaying anything, the       *
 *  following methods are for all intents and purposes, null.               *
 ****************************************************************************/

    @Override
    protected Group createKeyPads() {
        return null;
    }

    @Override
    protected void positionDigits() {
    }

    @Override
    protected void refreshOnOverallValueChange(int overallValue) {
    }

    @Override
    protected int calculateKeyNumValue(Digit focusedDigit, KeyCode keyCode) {
        return 0;
    }

    @Override
    protected int calculateKeyUpValue(Digit focusedDigit) {
        return 0;
    }

    @Override
    protected int calculateKeyDownValue(Digit focusedDigit) {
        return 0;
    }

/****************************************************************************
 *           End DisplayableWithDigits method definition section            *
 ****************************************************************************/

    /*
     * Constructors
     */
    public Horn(String varName, String url) {
        super();  // Must call superclass constructor first
        this.varName = varName;
        URL resource = null;
        boolean hornURLFound = false;
        /*
         * If the url starts with '/', then treat this as
         * a file inside the Scoreboard.jar archive
         */
        if (Globals.instance().hornURL != null) {
            LOGGER.log(Level.INFO, "Reading user-defined horn URL: {0}",
                    Globals.instance().hornURL);
            /*
             * If the hornURL starts with '/', then treat this as
             * a file inside the Scoreboard.jar archive
             */
            if (Globals.instance().hornURL.charAt(0) == '/') {
                try {
                    resource = getClass().getResource(
                            Globals.instance().hornURL);
                    hornURLFound = true;
                } catch (Exception e) {
                    LOGGER.info(Utils.ExceptionStackTraceAsString(e));
                    LOGGER.log(Level.INFO, 
                            "Not found in jar, Falling back to: {0}",
                            Constants.instance().DEFAULT_HORN_FILE);
                }
            /*
             * Otherwise treat this as a standard URL String
             */
            } else {
                try {
                    resource = new URL(Globals.instance().hornURL);
                    hornURLFound = true;
                } catch (MalformedURLException e) {
                    LOGGER.info(Utils.ExceptionStackTraceAsString(e));
                    LOGGER.log(Level.INFO, "Falling back to default: {0}",
                            Constants.instance().DEFAULT_HORN_FILE);
                }
            }
        }
        /*
         * If the user-defined horn resource is undefined, or if it can't
         * be found, use the default config.xml file found in the jar file.
         */
        if (!hornURLFound) {
            resource = getClass().getResource(
                    Constants.instance().DEFAULT_HORN_FILE);
        }

        final Media media = new Media(resource.toString());
        mediaPlayer = new MediaPlayer(media);
    }    
}
