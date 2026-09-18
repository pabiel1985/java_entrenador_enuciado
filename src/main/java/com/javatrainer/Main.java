package com.javatrainer;

import com.javatrainer.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        MainView mainView = new MainView();
        Scene scene = new Scene(mainView, 1100, 750);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        stage.setTitle("Java Trainer");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
