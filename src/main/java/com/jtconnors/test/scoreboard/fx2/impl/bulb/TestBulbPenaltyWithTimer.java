
package com.jtconnors.test.scoreboard.fx2.impl.bulb;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.animation.Animation;
import com.jtconnors.scoreboard.fx2.impl.bulb.BulbPenalty;
import javafx.scene.input.MouseEvent;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import com.jtconnors.scoreboard.fx2.framework.Timer;
import com.jtconnors.scoreboard.fx2.framework.FxConstants;

public class TestBulbPenaltyWithTimer extends Application {

    private Timer timer;

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        timer = new Timer(FxConstants.instance().ONE_TENTH_SECOND);
        final Button button = new Button();

        final BulbPenalty penalty = new BulbPenalty("penalty", timer);
        penalty.setLayoutX(50);
        penalty.setLayoutY(50);
        penalty.setColor(Color.BLUE);
        penalty.getPlayerNumber().setColor(Color.GOLD);
        penalty.setOverallValue(120);

        button.setText("Start");
        button.setFont(new Font(24));
        group.getChildren().addAll(penalty, button);
        penalty.setDigitHeight(175);

        Scene scene = new Scene(group, group.getLayoutBounds().getWidth()+100,
                group.getLayoutBounds().getHeight()+100);
        button.setLayoutY(scene.getHeight() -
//                button.getLayoutBounds().getHeight());
                40);
        button.setLayoutX(50);
        button.setOnMousePressed(new EventHandler<MouseEvent>() {
           public void handle(MouseEvent event) {
               Animation.Status status = timer.getTimeline().getStatus();
               if (status == Animation.Status.RUNNING) {
                   penalty.getTimer().stop();
               } else {
                   penalty.getTimer().start();
              }
           }
        });

        timer.getTimeline().statusProperty().addListener(
                new InvalidationListener() {
            public void invalidated(Observable ov) {
                Animation.Status status = timer.getTimeline().getStatus();
                if (status == Animation.Status.RUNNING) {
                    button.setText("Stop");
                    penalty.setBlocksInput(true);
                } else {
                    button.setText("Start");
                    penalty.setBlocksInput(false);
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
