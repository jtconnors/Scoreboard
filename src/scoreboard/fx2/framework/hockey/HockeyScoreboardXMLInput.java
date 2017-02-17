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

import scoreboard.fx2.framework.XMLInput;
import scoreboard.common.LayoutXOptions;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import scoreboard.common.ScoreboardInputInterface;
import scoreboard.fx2.framework.XMLSpec;

/*
 * This class provides the XML Input functionality specific to a Hockey
 * Scoreboard implementation and extends the abstract XMLInput class.
 */
public class HockeyScoreboardXMLInput extends XMLInput {

    public HockeyScoreboardXMLInput(ScoreboardInputInterface
            scoreboardInputInterface) {
        super(scoreboardInputInterface);
    }

    public void readImageViewElement(Element element, String name) {
        String url = XMLSpec.getTagValue(XMLSpec.TAG_URL, element);
        LayoutXOptions layoutXOption = LayoutXOptions.valueOf(
                XMLSpec.getTagValue(XMLSpec.TAG_LAYOUTXOPTION, element));
        String topLeftObjStr = XMLSpec.getTagValue(
                XMLSpec.TAG_TOPLEFTOBJECT, element);
        String bottomRightObjStr = XMLSpec.getTagValue(
                XMLSpec.TAG_BOTTOMRIGHTOBJECT, element);
        scoreboardInputInterface.setupImageView(name, url, layoutXOption,
                topLeftObjStr, bottomRightObjStr);
    }

    /*
     * Implementation of abstract readConfigNode() method.  This is declared in
     * XMLInput super class.
     */
    @Override
    public void readConfigNode(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            String name = XMLSpec.getTagValue(XMLSpec.TAG_NAME, element);
            if (HockeyScoreboardXMLSpec.isHockeyScoreboardVariable(name)) {
                readScoreboardElement(element, name);
            } else if (XMLSpec.isTextNodeVariable(name)) {
                readTextNodeElement(element, name);
            } else if (XMLSpec.isClockVariable(name) ||
                    HockeyScoreboardXMLSpec.isPenaltyVariable(name) ||
                    XMLSpec.isTwoDigitVariable(name) ||
                    XMLSpec.isSingleDigitVariable(name)) {
                readDisplayableWithDigitsElement(element, name);
            } else if (HockeyScoreboardXMLSpec.isImageViewVariable(name)) {
                readImageViewElement(element, name);
            }
        }
    }
}
