
package test.scoreboard.fx2.master.impl.bulb;

import javafx.stage.Stage;
import javafx.application.Application;
import scoreboard.fx2.framework.FunctionPtr;
import scoreboard.fx2.framework.Timer;
import static scoreboard.fx2.framework.FX2Constants.ONE_TENTH_SECOND;

public class TestTimer extends Application {

    int counter = 50;
    
    @Override
    public void start(Stage stage) {
        Timer timer = new Timer(ONE_TENTH_SECOND);
        FunctionPtr handler1 = new FunctionPtr() {
            public void invoke() {
                if (counter-- >= 0) {
                    if ((counter % 10) == 0) {
                       System.out.println("Inside handler1: " + counter / 10);
                    }
                }
                else {
                    System.exit(0);
                }
            }
        };
        timer.addHandler(handler1);
        FunctionPtr handler2 = new FunctionPtr() {
            public void invoke() {
               System.out.println("Inside handler2");
            }
        };
        timer.addHandler(handler2);
        timer.addHandler(handler1);
        timer.removeHandler(handler2);
        timer.addHandler(null);
        timer.removeHandler(null);
        timer.start();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
