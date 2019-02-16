
package com.jtconnors.test.scoreboard.fx2.impl.bulb;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.jtconnors.scoreboard.fx2.impl.bulb.BulbHockeyScoreboard;

public class TestBulbHockeyScoreboard extends Application {

    @Override
    public void start(Stage stage) {
        Group group = new Group();
//        Stage stage = new Stage(StageStyle.TRANSPARENT);
//        BulbScoreboard bulbScoreboard = new BulbScoreboard(
//                Screen.getPrimary().getBounds().getWidth(),
//                Screen.getPrimary().getBounds().getHeight());
        BulbHockeyScoreboard bulbScoreboard =
                new BulbHockeyScoreboard(640, 400, false);
        group.getChildren().add(bulbScoreboard);
        Scene scene = new Scene(group, group.getLayoutBounds().getWidth(),
                group.getLayoutBounds().getHeight());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

