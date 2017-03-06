
package test.scoreboard.fx2.impl.bulb;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import scoreboard.fx2.impl.bulb.BulbPenalty;

public class TestBulbPenalty extends Application {

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        BulbPenalty penalty = new BulbPenalty("penalty", null);
        penalty.setLayoutX(50);
        penalty.setLayoutY(50);
        penalty.setOverallValue(120);
        penalty.getPlayerNumber().setColor(Color.GOLD);
        group.getChildren().add(penalty);
        penalty.setDigitHeight(125);
        Scene scene = new Scene(group, group.getLayoutBounds().getWidth()+100,
                group.getLayoutBounds().getHeight()+100);
        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
