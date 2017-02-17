package test.scoreboard.fx2.master.impl.bulb;


import javafx.animation.*;
import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SimpleTimer extends Application {
    
    public Timeline timeline;
    private KeyFrame kf;
    private int count = 5;

    public SimpleTimer() {
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        final KeyFrame kf = new KeyFrame(Duration.millis(1000),
            new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    if (count >= 0)
                    System.out.println(count--);
                else
                    System.exit(0);
                }
            }
        );
        timeline.getKeyFrames().add(kf);
    }

    @Override
    public void start(Stage stage) {
        SimpleTimer timer = new SimpleTimer();
        timer.timeline.play();

    }

    public static void main(String[] args) {
        Application.launch(SimpleTimer.class, args);
    }
}
