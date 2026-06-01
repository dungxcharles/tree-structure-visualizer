package com.controller;

import com.controller.mainmenu.MainMenuApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainMenuApplication.class.getResource("/com/view/workspace.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Help");
        stage.setScene(scene);
        stage.show();
    }
}
