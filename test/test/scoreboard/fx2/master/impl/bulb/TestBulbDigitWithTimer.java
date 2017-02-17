
package test.scoreboard.fx2.master.impl.bulb;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import scoreboard.fx2.framework.FunctionPtr;
import scoreboard.fx2.framework.Timer;
import scoreboard.fx2.impl.bulb.BulbDigit;

public class TestBulbDigitWithTimer extends Application {

    int counter = 99;

    @Override
    public void start(Stage stage) {
        final BulbDigit digit = new BulbDigit();
        Group group = new Group();
        group.getChildren().add(digit);
        Scene scene = new Scene(group);
        stage.setScene(scene);
        stage.show();
        digit.setValue((counter % 100) / 10);
        digit.setBlankIfZero(true);
        Timer timer = new Timer(Duration.millis(100));  // 10th second
        FunctionPtr handler1 = new FunctionPtr() {
            public void invoke() {
                if (counter-- >= 0) {
                    if ((counter % 10) == 0) {
                        System.out.println((counter % 100) / 10);
                       digit.setValue((counter % 100) / 10);
                    }
                }
                else {
//                    Platform.exit();
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

