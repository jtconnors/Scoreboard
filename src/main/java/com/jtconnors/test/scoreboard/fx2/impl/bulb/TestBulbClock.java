
package com.jtconnors.test.scoreboard.fx2.impl.bulb;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.jtconnors.scoreboard.fx2.impl.bulb.BulbClock;

public class TestBulbClock extends Application {

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        BulbClock clock = new BulbClock("clock", null, null);
        clock.setLayoutX(50);
        clock.setLayoutY(50);
        clock.setColor(Color.BLUE);
        clock.setOverallValue(601);
        group.getChildren().add(clock);
        clock.setDigitHeight(175);
        Scene scene = new Scene(group, group.getLayoutBounds().getWidth()+100,
                group.getLayoutBounds().getHeight()+100);
        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(TestBulbClock.class, args);
    }
}
