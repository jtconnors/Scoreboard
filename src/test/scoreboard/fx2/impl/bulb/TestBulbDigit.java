
package test.scoreboard.fx2.impl.bulb;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import scoreboard.fx2.impl.bulb.BulbDigit;

public class TestBulbDigit extends Application {

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group, 200, 200);
        BulbDigit digit = new BulbDigit();
        group.getChildren().add(digit);
        stage.setScene(scene);
        stage.show();

        digit.setColor(Color.BLUE);
        digit.setValue(0);
        digit.setBlankIfZero(true);
        digit.setDigitHeight(175f);
        digit.setValue(4);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
