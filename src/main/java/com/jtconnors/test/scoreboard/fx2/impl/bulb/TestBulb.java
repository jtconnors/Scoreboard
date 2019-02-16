
package com.jtconnors.test.scoreboard.fx2.impl.bulb;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.jtconnors.scoreboard.fx2.impl.bulb.Bulb;

public class TestBulb extends Application {

    @Override
    public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group, 200, 200);
        Bulb bulb = new Bulb();
        group.getChildren().add(bulb);
        stage.setScene(scene);

        bulb.setCenterX(scene.getWidth() / 2);
        bulb.setCenterY(scene.getHeight() / 2);
        bulb.setBulbLit(true);
        bulb.setBulbLit(false);
        bulb.setBulbLit(true);
        bulb.setRadius(50f);
        bulb.setColor(Color.BLUE);
        
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(TestBulb.class, args);
    }
}
