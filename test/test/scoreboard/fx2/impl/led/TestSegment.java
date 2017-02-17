
package test.scoreboard.fx2.impl.led;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import scoreboard.fx2.impl.led.Segment;

public class TestSegment extends Application {

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group, 200, 200);
        
        Segment segment1 = new Segment(100.0, false, Color.RED);
        group.getChildren().add(segment1);
        segment1.setLayoutX(20.0);
        segment1.setLayoutY(20.0);
        segment1.setSegmentLit(true);
        segment1.setSegmentLit(false);
        segment1.setSegmentLit(true);
        
        Segment segment2 = new Segment(100.0, true, Color.RED);
        group.getChildren().add(segment2);
        segment2.setLayoutX(70.0);
        segment2.setLayoutY(50.0);
        segment2.setSegmentLit(true);
        segment2.setSegmentLit(false);
        segment2.setSegmentLit(true);
        segment2.setColor(Color.BLUE);
        
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(TestSegment.class, args);
    }
}
