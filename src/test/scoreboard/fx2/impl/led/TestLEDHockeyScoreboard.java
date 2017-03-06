
package test.scoreboard.fx2.impl.led;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scoreboard.fx2.impl.led.LEDHockeyScoreboard;

public class TestLEDHockeyScoreboard extends Application {

    @Override
    public void start(Stage stage) {
        Group group = new Group();
//        Stage stage = new Stage(StageStyle.TRANSPARENT);
//        LEDScoreboard bulbScoreboard = new LEDScoreboard(
//                Screen.getPrimary().getBounds().getWidth(),
//                Screen.getPrimary().getBounds().getHeight());
        LEDHockeyScoreboard bulbScoreboard =
                new LEDHockeyScoreboard(640, 400, false);
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

