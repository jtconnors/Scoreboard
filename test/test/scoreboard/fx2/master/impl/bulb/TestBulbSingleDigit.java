
package test.scoreboard.fx2.master.impl.bulb;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import scoreboard.fx2.impl.bulb.BulbSingleDigit;

public class TestBulbSingleDigit extends Application {

    @Override
    public void start(Stage stage) {
        Group group = new Group();
//        BulbSingleDigit singleDigit = new BulbSingleDigit();
//        BulbSingleDigit singleDigit = new BulbSingleDigit(Color.RED, 150d,
//                0, 0, 5);
        BulbSingleDigit singleDigit = new BulbSingleDigit("period",
                Color.RED, 150d, 1, 1, 4);
        group.getChildren().add(singleDigit);

        singleDigit.setLayoutX(50);
        singleDigit.setLayoutY(50);
        singleDigit.setColor(Color.BLUE);
        singleDigit.setDigitHeight(175f);

        Scene scene = new Scene(group, group.getLayoutBounds().getWidth() + 100,
                group.getLayoutBounds().getHeight() + 100);
        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

