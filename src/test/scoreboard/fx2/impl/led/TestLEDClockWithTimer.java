
package test.scoreboard.fx2.impl.led;

import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.animation.Animation;
import javafx.scene.input.MouseEvent;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import scoreboard.fx2.impl.led.LEDClock;
import scoreboard.fx2.framework.Timer;
import scoreboard.fx2.framework.FxConstants;

public class TestLEDClockWithTimer extends Application {

    @Override
    public void start(Stage stage) {
        Group group = new Group();     
        final Timer timer = new Timer(FxConstants.instance().ONE_TENTH_SECOND);
        final Button button = new Button();
        
        final LEDClock clock = new LEDClock("clock", null, timer);
        clock.setLayoutX(50);
        clock.setLayoutY(50);
        clock.setColor(Color.BLUE);
        clock.setOverallValue(801);

        button.setText("Start");
        button.setFont(new Font(24));
        group.getChildren().addAll(clock, button);
        clock.setDigitHeight(175);

        Scene scene = new Scene(group, group.getLayoutBounds().getWidth()+100,
                group.getLayoutBounds().getHeight()+100);
        button.setLayoutY(scene.getHeight() -
//                button.getLayoutBounds().getHeight());
                30);
        button.setLayoutX(50);
        button.setOnMousePressed(new EventHandler<MouseEvent>() {
           public void handle(MouseEvent event) {
               Animation.Status status = timer.getTimeline().getStatus();
               if (status == Animation.Status.RUNNING) {
                   clock.getTimer().stop();
                   button.setText("Start");
                   clock.setBlocksInput(false);
               } else {
                   clock.getTimer().start();
                   button.setText("Stop");
                   clock.setBlocksInput(true);
               }
            }
        });

        clock.getTimer().getTimeline().statusProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                Animation.Status status = timer.getTimeline().getStatus();
                if (status == Animation.Status.RUNNING) {
                    button.setText("Stop");
                    clock.setBlocksInput(true);
                } else {
                    button.setText("Start");
                    clock.setBlocksInput(false);
                } 
            }
        });

        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
