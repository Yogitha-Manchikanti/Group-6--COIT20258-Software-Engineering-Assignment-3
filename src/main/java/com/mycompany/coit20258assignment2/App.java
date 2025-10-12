package com.mycompany.coit20258assignment2;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX entry point. Initialises the SceneNavigator, then shows Login.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Initialise navigator ONCE with the primary stage + dimensions
        SceneNavigator.getInstance().init(stage, 1100, 700);
        // First page
        SceneNavigator.getInstance().goToLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
