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

package com.jtconnors.scoreboard.fx2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import com.jtconnors.scoreboard.fx2.impl.led.LEDHockeyScoreboard;
import com.jtconnors.scoreboard.common.Globals;


public class MainRemoteLED extends Application {

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        LEDHockeyScoreboard bulbScoreboard =
//                new LEDHockeyScoreboard(711, 400, Globals.isSlave);
                new LEDHockeyScoreboard(1280, 700, Globals.instance().isSlave);
        Globals.instance().hockeyScoreboardRef = bulbScoreboard;
        group.getChildren().add(bulbScoreboard);
        Scene scene = new Scene(group, group.getLayoutBounds().getWidth(),
                group.getLayoutBounds().getHeight());
        stage.setScene(scene);
        stage.setOnCloseRequest((WindowEvent event) -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();
    }

    public static void main(String[] args) {
        Globals.instance().isSlave = true;
//        Globals.isTV = true;
//        Globals.unlitOpacity = 0.05;
        Globals.instance().useIPSocket = true;
        Globals.instance().displaySocket = true;
        Globals.instance().hornURL = "/scoreboard/util/sounds/AirHorn.mp3";
        
        Globals.instance().parseArgs(args);
        
        Application.launch(MainRemoteLED.class, args);
    }
}