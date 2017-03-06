package test.scoreboard.fx2.impl.bulb;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import scoreboard.fx2.framework.FunctionPtr;
import scoreboard.fx2.framework.Timer;
import scoreboard.fx2.impl.bulb.BulbTwoDigit;

public class TestBulbTwoDigitWithTimer extends Application {

    int counter = 99;

    @Override
    public void start(Stage stage) {
        final BulbTwoDigit twoDigit = new BulbTwoDigit("testDigit");
        Group group = new Group();
        group.getChildren().add(twoDigit);
        Scene scene = new Scene(group);
        scene.setRoot(group);
        stage.setScene(scene);
        stage.show();
        twoDigit.setOverallValue(counter);
        Timer timer = new Timer(Duration.millis(100));  // 1/10 second
        FunctionPtr handler1 = new FunctionPtr() {
            public void invoke() {
                if (counter >= 0) {
                       twoDigit.setOverallValue(counter--);
                } else {
                    counter = 99;
                }
            }
        };
        timer.addHandler(handler1);
        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


