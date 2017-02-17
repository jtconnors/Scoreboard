
package test.scoreboard.fx2.master.impl.bulb;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scoreboard.fx2.framework.KeyPad;

public class TestKeyPad extends Application {

    public static void main(String[] args) {
       launch(TestKeyPad.class, args);
    }

    @Override
    public void start(Stage stage) {
        KeyPad keyPad = new KeyPad(160d);
        stage.setTitle("KeyPad");
        stage.show();
        Scene scene = new Scene(keyPad,
            keyPad.getLayoutBounds().getWidth(),
            keyPad.getLayoutBounds().getHeight());
        stage.setScene(scene);
    }
}
