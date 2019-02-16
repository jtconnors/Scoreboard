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

import java.io.FileNotFoundException;
import java.net.URL;
import com.jtconnors.scoreboard.common.LayoutXOptions;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.jtconnors.scoreboard.common.Constants;
import com.jtconnors.scoreboard.common.ScoreboardInputInterface;
import com.jtconnors.scoreboard.common.Globals;
import com.jtconnors.scoreboard.common.Utils;

/*
 * This base class is used by Scoreboard implementations to read scoreboard
 * configuration and/or update information in XML.
 */
public abstract class XMLInput {
    
    private final static Logger LOGGER =
            Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    protected DocumentBuilder documentBuilder;
    protected ScoreboardInputInterface scoreboardInputInterface;

    public XMLInput(ScoreboardInputInterface scoreboardInputInterface) {
        this.scoreboardInputInterface = scoreboardInputInterface;
    }
    
    public abstract void readConfigNode(Node node);

    /*
     * Methods associated with XML <config> element
     */
    public void readScoreboardElement(Element element,
            String name) {
        int colorVal =
                Integer.parseInt(XMLSpec.getTagValue(
                XMLSpec.TAG_BACKGROUNDCOLOR, element));
        scoreboardInputInterface.setupScoreboard(name, colorVal);
    }

    public void readTextNodeElement(Element element, String name) {
        double layoutY = Double.parseDouble(
                XMLSpec.getTagValue(XMLSpec.TAG_LAYOUTY, element));
        double fontSize = Double.parseDouble(
                XMLSpec.getTagValue(XMLSpec.TAG_FONTSIZE, element));
        LayoutXOptions layoutXOption = LayoutXOptions.valueOf(
                XMLSpec.getTagValue(XMLSpec.TAG_LAYOUTXOPTION, element));
        String alignWithStr = XMLSpec.getTagValue(XMLSpec.TAG_ALIGNOBJECT,
                element);
        String content = XMLSpec.getTagValue(XMLSpec.TAG_CONTENT, element);
        scoreboardInputInterface.setupTextNode(name, layoutY, layoutXOption,
                alignWithStr, fontSize, content);
    }

    public void readDisplayableWithDigitsElement(Element element, String name) {
        double layoutY = Double.parseDouble(
                XMLSpec.getTagValue(XMLSpec.TAG_LAYOUTY, element));
        LayoutXOptions layoutXOption = LayoutXOptions.valueOf(
                XMLSpec.getTagValue(XMLSpec.TAG_LAYOUTXOPTION, element));
        String alignWithStr = XMLSpec.getTagValue(XMLSpec.TAG_ALIGNOBJECT,
                element);
        double digitHeight = Double.parseDouble(
                XMLSpec.getTagValue(XMLSpec.TAG_DIGITHEIGHT, element));
        int overallValue = Integer.parseInt(
                XMLSpec.getTagValue(XMLSpec.TAG_OVERALLVALUE, element));
        scoreboardInputInterface.setupDisplayableWithDigits(name, layoutY,
                layoutXOption, alignWithStr, digitHeight, overallValue);
    }

    public void readConfigFile() {
        try {
            boolean configURLFound = false;
            URL fileURL;
            InputStream in = null;
            /*
             * First try reading the user-defined configURL, that is, if it's
             * defined.
             */
            if (Globals.instance().configURL != null) {
                LOGGER.log(Level.INFO, "Reading remote config file: {0}",
                        Globals.instance().configURL);
                /*
                 * If the configURL starts with '/', then treat this as
                 * a file inside the Scoreboard.jar archive
                 */
                if (Globals.instance().configURL.charAt(0) == '/') {
                    try {
                        fileURL = getClass().getResource(
                                Globals.instance().configURL);
                        in = fileURL.openStream();
                        configURLFound = true;
                    } catch (IOException e) {
                        LOGGER.info(Utils.ExceptionStackTraceAsString(e));
                        LOGGER.log(Level.INFO,
                                "Not found in jar, Falling back to: {0}",
                                Constants.instance().DEFAULT_CONFIG_FILE);
                    }
                /*
                 * Otherwise treat this as a standard URL String
                 */
                } else {
                    try {
                        fileURL = new URL(Globals.instance().configURL);
                        in = fileURL.openStream();
                        configURLFound = true;
                    } catch (IOException e) {
                        LOGGER.info(Utils.ExceptionStackTraceAsString(e));
                        LOGGER.log(Level.INFO, "Falling back to default: ",
                                Constants.instance().DEFAULT_CONFIG_FILE);
                    }
                }
            }
            /*
             * If the user-defined configFile is undefined, or if it can't
             * be found, use the default config.xml file found in the jar file.
             */
            if (!configURLFound) {
                LOGGER.log(Level.INFO,
                        "Reading default remote config file: {0}",
                        Constants.instance().DEFAULT_CONFIG_FILE);
                fileURL = getClass().getResource(
                        Constants.instance().DEFAULT_CONFIG_FILE);
                if (fileURL == null) {
                    throw new FileNotFoundException(
                            Constants.instance().DEFAULT_CONFIG_FILE);
                }
                in = fileURL.openStream();
            }
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(in);

            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("config");

            for (int s = 0; s < nodeLst.getLength(); s++) {
                readConfigNode(nodeLst.item(s));
            }
            scoreboardInputInterface.resolveXlocations();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            LOGGER.severe(Utils.ExceptionStackTraceAsString(e));
            System.exit(1);
        }
    }

    /*
     * Methods associated with XML <update> element
     */

    public void initStringXMLDocumentBuilder() {
        try {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        documentBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOGGER.severe(Utils.ExceptionStackTraceAsString(e));
        }
    }

    public void readUpdateNode(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            String name = XMLSpec.getTagValue(XMLSpec.TAG_NAME, element);
            if (XMLSpec.isUpdateVariable(name)) {
                scoreboardInputInterface.updateVariable(name,
                        XMLSpec.getTagValue(XMLSpec.TAG_OVERALLVALUE, element));
            }
        }
    }

    public void readUpdateStr(String msg) {
        try {
            InputStream msgStream = new
                    ByteArrayInputStream(msg.getBytes("UTF-8"));
            Document doc = documentBuilder.parse(msgStream);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName("update");
            readUpdateNode(nodeLst.item(0));
        } catch (IOException | SAXException e) {
            LOGGER.info(Utils.ExceptionStackTraceAsString(e));
        }
    }
}
