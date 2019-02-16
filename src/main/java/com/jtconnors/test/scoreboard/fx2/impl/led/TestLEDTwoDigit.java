
package com.jtconnors.test.scoreboard.fx2.impl.led;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.jtconnors.scoreboard.fx2.impl.led.LEDTwoDigit;

public class TestLEDTwoDigit extends Application {

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group, 300, 275);
        LEDTwoDigit twoDigits = new LEDTwoDigit("homeScore",
                Color.RED, 100, 6, 0, 59);
        group.getChildren().add(twoDigits);
        scene.setRoot(group);
        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        stage.show();

        twoDigits.setLayoutX(50);
        twoDigits.setLayoutY(50);
        twoDigits.setColor(Color.BLUE);
        twoDigits.setDigitHeight(175f);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
