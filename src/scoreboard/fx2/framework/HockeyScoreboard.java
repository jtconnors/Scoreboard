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

package scoreboard.fx2.framework;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.application.Platform;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.animation.Animation;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import scoreboard.common.ScoreboardOutputInterface;
import scoreboard.common.ScoreboardInputInterface;
import scoreboard.common.xml.XMLReaderInterface;
import scoreboard.common.LayoutXOptions;
import scoreboard.common.xml.XMLSpec;
import scoreboard.common.xml.XMLOutput;
import scoreboard.common.xml.XMLInput;
import scoreboard.fx2.networking.FxSocketReader;
import scoreboard.fx2.util.FXUtils;
import static scoreboard.common.Constants.DEFAULT_SCOREBOARD_WIDTH;
import static scoreboard.common.Constants.DEFAULT_SCOREBOARD_HEIGHT;
import static scoreboard.fx2.framework.FX2Constants.DEFAULT_TEXT_COLOR;
import static scoreboard.fx2.framework.FX2Constants.ONE_TENTH_SECOND;
import static scoreboard.fx2.framework.FX2Constants.
        DEFAULT_SECONDARY_COLOR;
import static scoreboard.common.Constants.DEBUG_SEND;
import static scoreboard.common.Constants.DEBUG_STATUS;
import scoreboard.fx2.networking.FxMulticastReader;
import scoreboard.fx2.networking.FxMulticastWriter;
import scoreboard.fx2.networking.FxMultipleSocketWriter;

/*
 * This abstract class defines the behavior of a hockey scoreboard object.
 * An implementation which extends this class must:
 *    1. Also extend the Digit, SingleDigit, TwoDigit, Penalty and
 *       Clock classes.
 *    2. Associate those extended class types with the many protected
 *       abstract variables declared here.
 *    3. Allocate the protected abstract variables found here in the
 *       Scoreboard constructor, after a call to super() and before a
 *       call to init().
 *
 *  For an example implementation of HockeyScoreboard, look at the
 *  scoreboard.fx2.impl.bulb.BulbHockeyScoreboard.java code.
 */
public abstract class HockeyScoreboard extends Parent 
implements XMLReaderInterface {

    protected double width;
    public double getWidth() { return width; }
    
    protected double overscanWidth;
    public double getOverscanWidth() { return overscanWidth; }

    protected double height;
    public double getHeight() { return height; }
    
    protected double overscanHeight;
    public double getOverscanHeight() { return overscanHeight; }

    protected Color textColor = Color.WHITE;
    public Color getTextColor() { return textColor; }

    protected Color backgroundColor = Color.BLACK;
    public Color getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        if (backgroundRect != null) {
            backgroundRect.setFill(backgroundColor);
            // Should use binding here instead?
            if (homeText != null) {
                homeText.setBackgroundColor(backgroundColor);
                guestText.setBackgroundColor(backgroundColor);
                periodText.setBackgroundColor(backgroundColor);
                shotsOnGoalText.setBackgroundColor(backgroundColor);
                homePenaltyText.setBackgroundColor(backgroundColor);
                guestPenaltyText.setBackgroundColor(backgroundColor);
            }
        }
    }

    /*
     * Timers associated with the Clock instance and 4 Penalty instances
     */
    protected Timer clockTimer;
    protected Timer homePenalty1Timer;
    protected Timer guestPenalty1Timer;
    protected Timer homePenalty2Timer;
    protected Timer guestPenalty2Timer;

    /*
     * These values, based upon the display width and height dimensions are
     * defined in the computeSizesAndOffsets() method.  They are used to size
     * and position nodes (absolute coordinates) within the display.
     */
    protected static double BORDER_RATIO = 0.05;
    protected double horizontalBorder;
    protected double horizontalSpacer;
    protected double verticalBorder;
    protected double verticalSpacer;
    protected double fontSize;
    protected double largeDigitSize;
    protected double mediumDigitSize;
    protected double smallDigitSize;

    /*
     * XML Writer for the scoreboard
     */
    XMLOutput xmlOutput;
    
    /*
     * XML Reader for the scoreboard
     */
    XMLInput xmlInput;

/****************************************************************************
 *  The following variables represent configurable nodes that can be        *
 *  displayed on a remote hockey scoreboard.  These must be public so that  *
 *  we can use reflection to get their values.                              *
 *                                                                          *
 *  Note: any change below will require, at minimum, changes throughout     *
 *  this file and in the XMLSPec.java file.                                 *
 ****************************************************************************/
    /*
     * Reference to this instance
     */
    public HockeyScoreboard hockeyScoreboard = this;
    /*
     * TextNodes to display
     */
    public TextNode homeText;
    public TextNode guestText;
    public TextNode periodText;
    public TextNode shotsOnGoalText;
    public TextNode homePenaltyText;
    public TextNode guestPenaltyText;
    /*
     * Text Node for optional socket status
     */
    public TextNode displaySocketText;
    /*
     * These are implementation specific variables.  They must be allocated
     * in the constructor of an implementing class of this abstract class.
     */
    public Clock clock;
    public TwoDigit homeScore;
    public TwoDigit guestScore;
    public SingleDigit period;
    public TwoDigit homeShotsOnGoal;
    public TwoDigit guestShotsOnGoal;
    public Penalty homePenalty1;
    public Penalty guestPenalty1;
    public Penalty homePenalty2;
    public Penalty guestPenalty2;
    /*
     * The Penalty object contains two subcomponents: a time remaining 
     * value (represented externally by overallValue) and a playerNumber.
     * The Penalty playerNumber fields are updated by making a call
     * something like Penalty.getPlayerNumber().setOverallValue(...).  The
     * objects below break out the individual player numbers from the
     * displayable Penalty objects.
     */
    public Integer homePenalty1PlayerNumber;
    public Integer guestPenalty1PlayerNumber;
    public Integer homePenalty2PlayerNumber;
    public Integer guestPenalty2PlayerNumber;
    /*
     * Optional Logo Image
     */
    public ImageView logoImageView;
/****************************************************************************
 *  End section containing declarations of configurable nodes for a         *
 *  remote hockey scoreboard.                                               *
 ****************************************************************************/

    /*
     * Controls for UI screen
     */
    protected TextNode hornButton;
    protected TextNode startStopButton;
    protected TextNode quitButton;
    /*
     * Bounding Rectangle encompasses entire bounds of HockeyScoreboard
     */
    private Rectangle backgroundRect;
    /*
     * The mouseBlocker node is used to block mouse events on the scoreboard.
     * It is used to disable mouse input when (1) The scoreboard UI clock is
     * running or (2) if the scoreboard in question is a remote display.
     */
    private Rectangle mouseBlocker;
    /*
     * These offset variables represent the various layoutY offsets,
     * in pixels, from the top of the scoreboard, of the various rows that
     * comprose the scoreboard.  For sanity's sake, keep these in order from
     * top to bottom positioning (with the exception of logoOffset).
     */
    private double clockRowOffset;
    private double scoreRowOffset;
    private double periodRowOffset;
    private double shotsOnGoalOffset;
    private double lineRowOffset;
    private double penalty1RowOffset;
    private double penaltyTextRowOffset;
    private double penalty2RowOffset;
    private double logoOffset;
    private double controlRowOffset;

    /*
     * Set by Constructor, the remoteDislay variable determines if this
     * instance represents either a remote display of a Scoreboard
     * (true) or the UI version of the Scoreboard (false)
     */
    private boolean remoteDisplay = false;
    
    /*
     * Mapping between String name of a configurable scoreboard variable to
     * the actual variable Field.
     */
    private Map<String, Field> configVariableMap = new HashMap<String, Field>();
    {
        try {
            for (String varName : XMLSpec.ConfigVariableNames) {
                configVariableMap.put(varName, getClass().getField(varName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /*
     * Mapping between String name of a updatable scoreboard variable to
     * the actual variable Field.  Defined in init();
     */
    private Map<String, Field> updateVariableMap = new HashMap<String, Field>();
    {
        try {
            for (String varName : XMLSpec.DisplayableWithDigitsNames) {
                updateVariableMap.put(varName, getClass().getField(varName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Constructors
     */
    public HockeyScoreboard() {
        this(DEFAULT_SCOREBOARD_WIDTH, DEFAULT_SCOREBOARD_HEIGHT, false);
    }

    public HockeyScoreboard(double width, double height) {
        this(width, height, false);
    }

    public HockeyScoreboard(double width, double height,
            boolean remoteDisplay) {
        this.width = width;
        this.height = height;
        this.remoteDisplay = remoteDisplay;

        computeSizesAndOffsets();

        if (!remoteDisplay) {
            clockTimer = new Timer(ONE_TENTH_SECOND);
            homePenalty1Timer = new Timer(ONE_TENTH_SECOND);
            guestPenalty1Timer = new Timer(ONE_TENTH_SECOND);
            homePenalty2Timer = new Timer(ONE_TENTH_SECOND);
            guestPenalty2Timer = new Timer(ONE_TENTH_SECOND);
        }
    }

    protected void init() {
        /*
         * Add the Penalty player numbers to the updateVariableMap.  This
         * must be done here as opposed to in the initializer, because the
         * Penalty variables would not have been allocated yet.
         */
        try {
            updateVariableMap.put(XMLSpec.NAME_homePenalty1playerNumber,
                    homePenalty1.getPlayerNumberField());
            updateVariableMap.put(XMLSpec.NAME_guestPenalty1playerNumber,
                    guestPenalty1.getPlayerNumberField());
            updateVariableMap.put(XMLSpec.NAME_homePenalty2playerNumber,
                    homePenalty2.getPlayerNumberField());
            updateVariableMap.put(XMLSpec.NAME_guestPenalty2playerNumber,
                    guestPenalty2.getPlayerNumberField());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        positionNodes();
        xmlOutput = new XMLOutput(new ScoreboardOutputInterfaceImpl());
        setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                processKeyEvent(ke.getCode());
            }
        });
        setFocusTraversable(true);
        requestFocus();

        if (Globals.dumpConfig) {
            xmlOutput.dumpDisplayableNodes();
        }
    }

    /*
     * Not all screens are alike, and there really isn't one layout
     * scheme which works for all of the different resolutions.
     * We'll need to change a few size, positioning and offset constants
     * based upon the aspect ratio (width/height) provided
     */
    private void computeSizesAndOffsets() {
        if (Globals.isTV) {
            overscanHeight = height - (height * BORDER_RATIO * 2);
            overscanWidth = width - (width * BORDER_RATIO * 2);
        }
        double aspectRatio = width / height;
        /*
         * Anything with an aspect ratio (width/height) greater than 1.5
         * will be considered widescreen.  Examples:
         *    1440x900, 1024x640, 1280x800, 1680x1050 = 1.6
         *    1366x768 = 1.78
         */
        if (aspectRatio > 1.5) {
            horizontalBorder = width * (BORDER_RATIO / 2);
            horizontalSpacer = horizontalBorder / 2;
            verticalBorder = height * (BORDER_RATIO / 2);
            verticalSpacer = verticalBorder;
            fontSize = height * 0.045;
            largeDigitSize = height * 0.20;
            mediumDigitSize = height * 0.13;
            smallDigitSize = height * 0.09;
        }
        /*
         * This covers the "squarer" ratios.  Examples:
         *     1024x768, 800x600, 640x480 = 1.33
         *     1280x1024 = 1.25
         */
        else {
            horizontalBorder = width * (BORDER_RATIO / 2);
            horizontalSpacer = horizontalBorder;
            verticalBorder = height * (BORDER_RATIO / 2);
            verticalSpacer = verticalBorder;
            fontSize = height * 0.045;
            largeDigitSize = height * 0.18;
            mediumDigitSize = height * 0.12;
            smallDigitSize = height * 0.08;
        }
    }

    private void positionNodes() {
        getChildren().clear();

        backgroundRect = new Rectangle();
        backgroundRect.setWidth(width);
        backgroundRect.setHeight(height);
        backgroundRect.setFill(backgroundColor);
        getChildren().add(backgroundRect);

        mouseBlocker = new Rectangle();
        // height of mouseBlocker set below, contingent on remoteDisplay
        mouseBlocker.setWidth(getLayoutBounds().getWidth());
        mouseBlocker.setFill(Color.TRANSPARENT);

        if (!remoteDisplay) {
            setupClockRow();
            setupScoreRow();
            setupPeriodRow();
            setupShotsOnGoalRow();
            setupLineRow();
            setupPenalty1Row();
            setupPenaltyTextRow();
            setupPenalty2Row();
//            setupLogoRow();
//            logoImageView.setVisible(false);
            setupControlRow();
            setupStatusRow(0);
            /*
             * Leave bottom controls on UI scoreboard open (horn, stop.start,
             * quit).  Block everything else
             */
            mouseBlocker.setHeight(controlRowOffset);
            mouseBlocker.setVisible(false);
            getChildren().add(mouseBlocker);
            if (Globals.useIPSocket) {
                Globals.multipleSocketWriter = 
                        new FxMultipleSocketWriter(2011, DEBUG_SEND);
                new Thread(Globals.multipleSocketWriter).start();
            } else {
                Globals.multicastWriter =
                        new FxMulticastWriter(Globals.sessionAddr,
                        Globals.port, DEBUG_SEND);
                new Thread(Globals.multicastWriter).start();
            }
        } else {
            setupRemoteDisplay();
            // Block entire remote display
            mouseBlocker.setHeight(getLayoutBounds().getHeight());
            mouseBlocker.setVisible(true);
            getChildren().add(mouseBlocker);
        }
    }

    private void setupClockRow() {
        clockRowOffset = verticalBorder;
        homeText = new TextNode(XMLSpec.NAME_homeText, "HOME", fontSize);
        homeText.setLayoutY(clockRowOffset);
        homeText.setLayoutX(horizontalBorder);
        homeText.setLayoutXOption(LayoutXOptions.LEFT_JUSTIFY);
        getChildren().add(homeText);

        clock.setLayoutY(clockRowOffset);
        clock.setLayoutX((width - clock.getLayoutBounds().getWidth()) / 2);
        clock.setLayoutXOption(LayoutXOptions.CENTER);
        getChildren().add(clock);

        guestText = new TextNode(XMLSpec.NAME_guestText, "GUEST", fontSize);
        guestText.setLayoutY(clockRowOffset);
        guestText.setLayoutX(width - guestText.getLayoutBounds().getWidth() -
                horizontalBorder);
        guestText.setLayoutXOption(LayoutXOptions.RIGHT_JUSTIFY);
        getChildren().add(guestText);
    }

    private void setupScoreRow() {
        scoreRowOffset = clockRowOffset + homeText.getLayoutBounds().getHeight() +
                verticalSpacer;
        homeScore.setLayoutY(scoreRowOffset);
        homeScore.setLayoutX(horizontalBorder);
        homeScore.setLayoutXOption(LayoutXOptions.LEFT_JUSTIFY);
        homeScore.setColor(DEFAULT_SECONDARY_COLOR);
        guestScore.setLayoutY(scoreRowOffset);
        guestScore.setLayoutX(width - guestScore.getLayoutBounds().getWidth() -
                horizontalBorder);
        guestScore.setLayoutXOption(LayoutXOptions.RIGHT_JUSTIFY);
        guestScore.setColor(DEFAULT_SECONDARY_COLOR);
        getChildren().addAll(homeScore, guestScore);
    }

    private void setupPeriodRow() {
        periodRowOffset = clockRowOffset + clock.getLayoutBounds().getHeight() +
                verticalSpacer;
        periodText = new TextNode(XMLSpec.NAME_periodText, "Period", fontSize);
        Double totalWidth = periodText.getLayoutBounds().getWidth() +
                horizontalSpacer + period.getLayoutBounds().getWidth();
        periodText.setLayoutX((width - totalWidth) / 2);
        periodText.setLayoutXOption(LayoutXOptions.GROUP_CENTER_LEFT_WITH);
        periodText.setAlignWith(period);
        period.setLayoutX(periodText.getLayoutX() + 
                periodText.getLayoutBounds().getWidth() + horizontalSpacer);
        period.setLayoutXOption(LayoutXOptions.GROUP_CENTER_RIGHT_WITH);
        period.setAlignWith(periodText);
        periodText.setLayoutY(periodRowOffset +
                (period.getLayoutBounds().getHeight() -
                periodText.getLayoutBounds().getHeight()) / 2);
        period.setLayoutY(periodRowOffset);
        getChildren().addAll(periodText, period);
    }
    
    private void setupShotsOnGoalRow() {
        shotsOnGoalOffset = periodRowOffset +
                period.getLayoutBounds().getHeight() + verticalSpacer;
        shotsOnGoalText = new TextNode(XMLSpec.NAME_shotsOnGoalText,
                "Shots On Goal", fontSize);
        Double totalWidth = homeShotsOnGoal.getLayoutBounds().getWidth() +
                horizontalSpacer +
                shotsOnGoalText.getLayoutBounds().getWidth() +
                horizontalSpacer +
                guestShotsOnGoal.getLayoutBounds().getWidth();
        homeShotsOnGoal.setLayoutX((width - totalWidth) / 2);
        homeShotsOnGoal.setLayoutXOption(LayoutXOptions.ALIGN_LEFT_OF);
        homeShotsOnGoal.setAlignWith(shotsOnGoalText);
        homeShotsOnGoal.setLayoutY(shotsOnGoalOffset);
        shotsOnGoalText.setLayoutX(
                homeShotsOnGoal.getLayoutX() +
                homeShotsOnGoal.getLayoutBounds().getWidth() +
                horizontalSpacer);
        shotsOnGoalText.setLayoutXOption(LayoutXOptions.CENTER);
        shotsOnGoalText.setLayoutY(shotsOnGoalOffset +
                (homeShotsOnGoal.getLayoutBounds().getHeight() -
                shotsOnGoalText.getLayoutBounds().getHeight()) / 2);
        guestShotsOnGoal.setLayoutX(shotsOnGoalText.getLayoutX() +
                shotsOnGoalText.getLayoutBounds().getWidth() +
                horizontalSpacer);
        guestShotsOnGoal.setLayoutXOption(LayoutXOptions.ALIGN_RIGHT_OF);
        guestShotsOnGoal.setAlignWith(shotsOnGoalText);
        guestShotsOnGoal.setLayoutY(shotsOnGoalOffset);
        getChildren().addAll(homeShotsOnGoal, shotsOnGoalText,
                guestShotsOnGoal);
    }

    private void setupLineRow() {
        lineRowOffset = shotsOnGoalOffset +
                homeShotsOnGoal.getLayoutBounds().getHeight() + verticalSpacer;
        Line line = new Line();
        line.setStartX(0);
        line.setStartY(lineRowOffset);
        line.setEndX(width);
        line.setEndY(lineRowOffset);
        line.setStroke(DEFAULT_TEXT_COLOR);
        getChildren().add(line);
    }

    private void setupPenalty1Row() {
        penalty1RowOffset = lineRowOffset + verticalSpacer;
        homePenalty1.setLayoutY(penalty1RowOffset);
        homePenalty1.setLayoutX(horizontalBorder);
        homePenalty1.setLayoutXOption(LayoutXOptions.LEFT_JUSTIFY);
        guestPenalty1.setLayoutY(penalty1RowOffset);
        guestPenalty1.setLayoutX(width -
                guestPenalty1.getLayoutBounds().getWidth() - horizontalBorder);
        guestPenalty1.setLayoutXOption(LayoutXOptions.RIGHT_JUSTIFY);
        getChildren().addAll(homePenalty1, guestPenalty1);
    }

    private void setupPenaltyTextRow() {
        penaltyTextRowOffset = penalty1RowOffset +
                homePenalty1.getLayoutBounds().getHeight() + verticalSpacer;
        homePenaltyText = new TextNode(XMLSpec.NAME_homePenaltyText,
                "Player   Penalty", fontSize);
        homePenaltyText.setLayoutY(penaltyTextRowOffset);
        homePenaltyText.setLayoutX(homePenalty1.getLayoutX());
        homePenaltyText.setLayoutXOption(LayoutXOptions.LEFT_JUSTIFY);
        guestPenaltyText = new TextNode(XMLSpec.NAME_guestPenaltyText,
                "Player   Penalty", fontSize);
        guestPenaltyText.setLayoutY(penaltyTextRowOffset);
        guestPenaltyText.setLayoutX(guestPenalty1.getLayoutX());
        guestPenaltyText.setLayoutXOption(LayoutXOptions.ALIGN_WITH);
        guestPenaltyText.setAlignWith(guestPenalty1);
        getChildren().addAll(homePenaltyText, guestPenaltyText);
    }

    private void setupPenalty2Row() {
        penalty2RowOffset = penaltyTextRowOffset +
                homePenaltyText.getLayoutBounds().getHeight() +
                verticalSpacer;
        homePenalty2.setLayoutY(penalty2RowOffset);
        homePenalty2.setLayoutX(horizontalBorder);
        homePenalty2.setLayoutXOption(LayoutXOptions.LEFT_JUSTIFY);
        guestPenalty2.setLayoutY(penalty2RowOffset);
        guestPenalty2.setLayoutX(width -
                guestPenalty2.getLayoutBounds().getWidth() - horizontalBorder);
        guestPenalty2.setLayoutXOption(LayoutXOptions.RIGHT_JUSTIFY);
        getChildren().addAll(homePenalty2, guestPenalty2);
    }

    private void setupLogoRow() {
        logoOffset = penalty1RowOffset + verticalSpacer;
        double requestedHeight = homePenalty2.getLayoutY() +
                homePenalty2.getLayoutBounds().getHeight() - logoOffset;
        double requestedWidth = guestPenalty1.getLayoutX() -
                (homePenalty1.getLayoutX() +
                homePenalty1.getLayoutBounds().getWidth()) -
                (2 * horizontalSpacer);
        Image logoImage = new Image("logo.png");
        logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(requestedWidth);
        logoImageView.setFitHeight(requestedHeight);
        logoImageView.setPreserveRatio(true);
        logoImageView.setLayoutY(logoOffset);
        logoImageView.setLayoutX((width -
                logoImageView.getLayoutBounds().getWidth()) / 2);
        getChildren().add(logoImageView) ;
    }

    private void setupControlRow() {
        controlRowOffset = penalty2RowOffset +
                homePenalty2.getLayoutBounds().getHeight() +
                verticalSpacer;
        hornButton = new TextNode("hornButton", "Horn", fontSize);
        hornButton.setTextColor(Color.GRAY);
        hornButton.setLayoutY(controlRowOffset);
        hornButton.setLayoutX(horizontalBorder);

        startStopButton = new TextNode("startButton", "Start", fontSize);
        startStopButton.setTextColor(Color.GRAY);
        startStopButton.setLayoutY(controlRowOffset);
        startStopButton.setLayoutX((width -
                startStopButton.getLayoutBounds().getWidth()) / 2);
        startStopButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
           public void handle(MouseEvent event) {
               startStopButton.setTextColor(Color.RED);
           }
        });
        startStopButton.setOnMouseExited(new EventHandler<MouseEvent>() {
           public void handle(MouseEvent event) {
               startStopButton.setTextColor(Color.GRAY);
           }
        });
        startStopButton.setOnMousePressed(new EventHandler<MouseEvent>() {
           public void handle(MouseEvent event) {
               startStopAction();
           }
        });

       clockTimer.getTimeline().statusProperty().addListener(
               new InvalidationListener() {
                public void invalidated(Observable ov) {
                    Animation.Status status =
                            clockTimer.getTimeline().getStatus();
                    if (status == Animation.Status.RUNNING) {
                        startStopButton.setContent("Stop");
                        mouseBlocker.setVisible(true);
                    } else {
                        startStopButton.setContent("Start");
                        homePenalty1Timer.stop();
                        homePenalty2Timer.stop();
                        guestPenalty1Timer.stop();
                        guestPenalty2Timer.stop();
                        mouseBlocker.setVisible(false);
                    }
                }
            });

        quitButton = new TextNode("quitButton", "Quit", fontSize);
        quitButton.setTextColor(Color.GRAY);
        quitButton.setLayoutY(controlRowOffset);
        quitButton.setLayoutX(width - quitButton.getLayoutBounds().getWidth() -
                horizontalBorder);
        quitButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                quitButton.setTextColor(Color.RED);
            }
        });
        quitButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                quitButton.setTextColor(Color.GRAY);
            }
        });
        quitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    Platform.exit();
                }
            });
        getChildren().addAll(hornButton, startStopButton, quitButton);
    }
    
    public String updateStatusString(int numConnections) {
        StringBuilder sb = new StringBuilder();
        if (Globals.useIPSocket) {
            sb.append("Socket ");
            sb.append(Globals.socketAddr);
            sb.append("  Port ");
            sb.append(Integer.toString(Globals.port));
            sb.append("  (");
            if (remoteDisplay) {
                if (Globals.socketClosed) {
                    sb.append("not ");
                }
                sb.append("connected)");
            } else {
                sb.append(Integer.toString(numConnections));
                sb.append(" connection");
                if (numConnections != 1) {
                    sb.append("s");
                }
                sb.append(")");
            }
        } else {
            sb.append("IP Multicast " + Globals.sessionAddr + 
                    "  Port " + Globals.port);   
        }
        return new String(sb);
    }
    
    public void setupStatusRow(int numConnections) {
        if (Globals.displaySocket) {
            double statusRowOffset = height - fontSize;
            String displaySocketStr;
            displaySocketStr = updateStatusString(numConnections);
            displaySocketText = new TextNode("displaySocketText",
                displaySocketStr, fontSize/2);
            displaySocketText.setTextColor(Color.GRAY);
            displaySocketText.setFont(Font.font("Arial", FontWeight.NORMAL,
                FontPosture.REGULAR, fontSize/2));
            displaySocketText.setLayoutY(statusRowOffset);
            displaySocketText.setLayoutX(
                (width - displaySocketText.getLayoutBounds().getWidth()) / 2);
            displaySocketText.setLayoutXOption(LayoutXOptions.CENTER);
            getChildren().add(displaySocketText);
        }  
    }
    
    public void updateStatusRow(int numConnections) {
        String displaySocketStr = updateStatusString(numConnections);
        if (displaySocketText != null) {
            displaySocketText.setContent(displaySocketStr);
            displaySocketText.setLayoutX((width -
                    displaySocketText.getLayoutBounds().getWidth()) / 2);
            displaySocketText.setLayoutXOption(LayoutXOptions.LEFT_JUSTIFY);
        }
    }

    /*
     * Remote display methods
     */
    private void setupRemoteDisplay() {
        xmlInput = new XMLInput(new ScoreboardInputInterfaceImpl());
        xmlInput.readConfigFile();
        xmlInput.initStringXMLDocumentBuilder();
        if (Globals.useIPSocket) {
            Globals.socketReader = new FxSocketReader(this,
                    Globals.host, Globals.port, DEBUG_STATUS);
            Globals.socketReader.connect();
        } else {
            Globals.multicastReader =
                    new FxMulticastReader(Globals.sessionAddr,
                    Globals.port, DEBUG_STATUS);
            new Thread(Globals.multicastReader).start();
        }
    }
    
    public HockeyScoreboard getHockeyScoreboard(String name) {
        return hockeyScoreboard;
    }

    private DisplayableWithDigits getUpdateVariable(String name) {
        if (name.equals(XMLSpec.NAME_clock)) {
            return clock;
        } else if(name.equals(XMLSpec.NAME_homeShotsOnGoal)) {
            return homeShotsOnGoal;
        } else if (name.equals(XMLSpec.NAME_guestShotsOnGoal)) {
            return guestShotsOnGoal;
        } else if(name.equals(XMLSpec.NAME_homeScore)) {
            return homeScore;
        } else if (name.equals(XMLSpec.NAME_guestScore)) {
            return guestScore;
        } else if (name.equals(XMLSpec.NAME_period)) {
            return period;
        } else if (name.equals(XMLSpec.NAME_homePenalty1)) {
            return homePenalty1;
        } else if (name.equals(XMLSpec.NAME_guestPenalty1)) {
            return guestPenalty1;
        } else if(name.equals(XMLSpec.NAME_homePenalty2)) {
            return homePenalty2;
        } else if (name.equals(XMLSpec.NAME_guestPenalty2)) {
            return guestPenalty2;     
        } else if (XMLSpec.isPlayerNumberVariable(name)) {
            return getPlayerNumber(name);
        }
        return null;
    }
    
    private DisplayableWithDigits getPlayerNumber(String name) {
        if (name.equals(XMLSpec.NAME_homePenalty1playerNumber)) {
            if (homePenalty1 != null) {
                return homePenalty1.getPlayerNumber();
            }
        } else if (name.equals(XMLSpec.NAME_guestPenalty1playerNumber)) {
            if (guestPenalty1 != null) {
                return guestPenalty1.getPlayerNumber();
            }   
        } else if (name.equals(XMLSpec.NAME_homePenalty2playerNumber)) {
            if (homePenalty2 != null) {
                return homePenalty2.getPlayerNumber();
            }
        } else if (name.equals(XMLSpec.NAME_guestPenalty2playerNumber)) {
            if (guestPenalty2 != null) {
                return guestPenalty2.getPlayerNumber();
            }
        }
        return null;
    }

    private ImageView newImageView(String name, String url) {
        Image logoImage = new Image(url);
        if (name.equals(XMLSpec.NAME_logoImageView)) {
            logoImageView = new ImageView(logoImage);
            return logoImageView;
        }
        return null;
    }

    public void setupImageView(String name, String url,
            double fitWidth, double fitHeight,
            double layoutX, double layoutY) {
        ImageView imageView = newImageView(name, url);
        if (imageView != null) {
            imageView.setFitWidth(fitWidth);
            imageView.setFitHeight(fitHeight);
            imageView.setLayoutX(layoutX);
            imageView.setLayoutY(layoutY);
            imageView.setPreserveRatio(true);
            getChildren().add(logoImageView);
        }
    }

    public void doUpdate(String name, int overallValue) {
        DisplayableWithDigits updateVar = getUpdateVariable(name);
        if (updateVar != null) {
            updateVar.setOverallValue(overallValue);
        }
    }
    
    private void startStopAction() {
        Animation.Status status = clockTimer.getTimeline().getStatus();
        if (status == Animation.Status.RUNNING) {
            clock.getTimer().stop();
            homePenalty1Timer.stop();
            homePenalty2Timer.stop();
            guestPenalty1Timer.stop();
            guestPenalty2Timer.stop();
        } else {
            clock.getTimer().start();
            homePenalty1Timer.start();
            homePenalty2Timer.start();
            guestPenalty1Timer.start();
            guestPenalty2Timer.start();
        }
    }

    private void processKeyEvent(KeyCode keyCode) {
        if (!remoteDisplay) {
            if (keyCode == KeyCode.SPACE) {
                startStopAction();
            }
        }
    }
 
/****************************************************************************
 *  XMLReaderInterface implementation method                                *
 ****************************************************************************/
    
    @Override
    public void handleUpdate(String msg) {
        xmlInput.readUpdateStr(msg);
    }
    
/****************************************************************************
 *  HockeyScorboardInputInterface implementation methods                    *
 ****************************************************************************/

    class ScoreboardInputInterfaceImpl implements ScoreboardInputInterface {
        
        /*
         * Given the string name of a displayable object on the scoreboard,
         * return the reference to the real object.
         */
        private Object getConfigVariable(String name) {
            if (name != null && !name.equals("null")) {
                try {
                    return configVariableMap.get(name).get(hockeyScoreboard);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        
        /*
         * Calculate the X ccordinate of the Displayable object,
         * if it is possible at the curent time.  If not, return -1,
         * signifying that the calculation needs to be deferred until all
         * Displayable objects' config information has been read in.
         */
        private double computeLayoutX(Displayable displayable,
                LayoutXOptions layoutXOption, String alignWithStr) {
            switch (layoutXOption) {
                case LEFT_JUSTIFY:
                    if (Globals.isTV) {
                        return ((width - overscanWidth) / 2);  
                    } else {
                        return horizontalBorder;
                    }
                case RIGHT_JUSTIFY:
                    if (Globals.isTV) {
                        return (width - (width - overscanWidth) / 2) -
                                displayable.getComponentWidth();
                    } else {
                        return width - displayable.getComponentWidth() -
                                horizontalBorder;
                    }
                case CENTER:
                    return (width - displayable.getComponentWidth()) / 2;
            }
            return -1;
        }
        
        /*
         * This function is used to caluculate the actual height in pixels
         * of a component, based upon whether we're compensating for
         * TV overscan or not.
         */
        private double computeRealHeight(double yValue) {
            if (Globals.isTV) {
                return yValue * overscanHeight;
            } else {
                return yValue * height;
            }
        }
        
        /*
         * This function is used to caluculate the actual vertical layout
         * of a component, based upon whether we're compensating for
         * TV overscan or not.
         */
        private double computeRealLayoutY(double yValue) {
            if (Globals.isTV) {
                double yOffset = (height - overscanHeight) / 2;
                return yOffset + (yValue * overscanHeight);
            } else {
                return yValue * height;
            }
        }
        
        /*
         * Creates a new TextNode and uses reflection to update the
         * configuration variable specified by "name" with this new TetxNode.
         */
        private TextNode newTextNode(String name, String content,
                double height) {
            Field field = configVariableMap.get(name);
            TextNode textNode = new TextNode(name, content, height);
            try {
                field.set(hockeyScoreboard, textNode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return textNode;
        }
        
        public void setupTextNode(String name, double layoutY,
            LayoutXOptions layoutXoption, String alignWithStr,
            double fontSize, String content) {
//            TextNode textNode = newTextNode(name, content, (fontSize * height));
            TextNode textNode = newTextNode(name, content,
                    computeRealHeight(fontSize));
            double layoutX = computeLayoutX(textNode, layoutXoption,
                    alignWithStr);
            textNode.setLayoutXOption(layoutXoption);
            textNode.setAlignWithStr(alignWithStr);
            textNode.setLayoutX(layoutX);
//            textNode.setLayoutY(layoutY * height);
            textNode.setLayoutY(computeRealLayoutY(layoutY));
            // Use binding instead here?
            textNode.setBackgroundColor(backgroundColor);
            getChildren().add(textNode);
        }

        public void setupDisplayableWithDigits(String name, double layoutY,
                LayoutXOptions layoutXoption, String alignWithStr,
                double digitHeight, int overallValue) {
            DisplayableWithDigits dwd = (DisplayableWithDigits)
                    getConfigVariable(name);
            if (dwd != null) {
//                dwd.setDigitHeight((int)(digitHeight * height));
                dwd.setDigitHeight(computeRealHeight(digitHeight));
                dwd.setOverallValue(overallValue);
                double layoutX =
                        computeLayoutX(dwd, layoutXoption, alignWithStr);
                dwd.setLayoutXOption(layoutXoption);
                dwd.setAlignWithStr(alignWithStr);
                dwd.setLayoutX(layoutX);
//                dwd.setLayoutY(layoutY * height);
                dwd.setLayoutY(computeRealLayoutY(layoutY));
                getChildren().add(dwd);
            }
        }

        public void setupScoreboard(String name, int backgroundColorVal) {
            HockeyScoreboard hs = (HockeyScoreboard) getConfigVariable(name);
            if (hs != null) {
                hs.setBackgroundColor(
                    FXUtils.intValueToFXColor(backgroundColorVal));
            }
        }

        public void setupImageView(String name, String url,
                LayoutXOptions layoutXoption,
                String topLeftObjStr, String bottomRightObjStr) {
        }

        public void updateVariable(String name, int overallValue) {
            DisplayableWithDigits updateVar = getUpdateVariable(name);
            if (updateVar != null) {
                updateVar.setOverallValue(overallValue);
            }
        }

        public void resolveXlocations() {
            for (String varName : XMLSpec.ConfigVariableNames) {
                Object obj = getConfigVariable(varName);
                if (obj instanceof Displayable) {
                    Displayable d = (Displayable) obj;
                    Displayable alignWith;
                    double groupWidth;
                    switch (d.getLayoutXOption()) {
                        case ALIGN_WITH:
                            d.setLayoutX(((Displayable)getConfigVariable(
                                    d.getAlignWithStr())).getLayoutX());
                            break;
                        case ALIGN_LEFT_OF:
                            d.setLayoutX(((Displayable)getConfigVariable(
                                    d.getAlignWithStr())).getLayoutX() -
                                    horizontalSpacer - d.getComponentWidth());
                            break;
                        case ALIGN_RIGHT_OF:
                            alignWith = (Displayable)getConfigVariable(
                                    d.getAlignWithStr());
                            d.setLayoutX(alignWith.getLayoutX() +
                                    horizontalSpacer +
                                    alignWith.getComponentWidth());
                            break;
                        case GROUP_CENTER_LEFT_WITH:
                            alignWith = (Displayable)
                                getConfigVariable(d.getAlignWithStr());
                            groupWidth = d.getComponentWidth() +
                                horizontalSpacer +
                                alignWith.getComponentWidth();
                            d.setLayoutX((width - groupWidth) / 2);
                            break;
                        case GROUP_CENTER_RIGHT_WITH:
                            alignWith = (Displayable)
                                getConfigVariable(d.getAlignWithStr());
                            groupWidth = d.getComponentWidth() +
                                horizontalSpacer +
                                alignWith.getComponentWidth();
                            d.setLayoutX((width - groupWidth) / 2 +
                                alignWith.getComponentWidth() +
                                horizontalSpacer);
                            break;
                        default:
                            continue;
                    }
                }
            }
        }
    }


/****************************************************************************
 *  ScorboardOutputInterface implementation methods                         *
 ****************************************************************************/

    class ScoreboardOutputInterfaceImpl implements ScoreboardOutputInterface {

        public Object getScoreboard() {
            return hockeyScoreboard;
        }

        public Double getScoreboardWidth() {
            return width;
        }

        public double getScoreboardHeight() {
            return height;
        }

        public int getColorVal(Object obj, Method method) {
            Color color = Color.PURPLE;
            try {
                color = (Color)method.invoke(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return FXUtils.FXColorToIntValue(color);
        }

        public String getFieldName(Object displayable) {
            if (displayable != null) {
                for(Field field : hockeyScoreboard.getClass().getFields()) {
                    try {
                        Object fieldObj = field.get(hockeyScoreboard);
                        if (fieldObj instanceof Displayable) {
                            if (fieldObj.equals(displayable)) {
                                return field.getName();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        public int getPlayerNumber(Object obj, Method method) {
            TwoDigit playerNumber = null;
            try {
                playerNumber = (TwoDigit)method.invoke(obj);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
            return playerNumber.getOverallValue();
        }
    }
}
