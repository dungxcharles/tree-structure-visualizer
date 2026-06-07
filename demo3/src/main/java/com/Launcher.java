package com;

import com.controller.NavigationManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Tree Operation");
        NavigationManager.getInstance().setStage(stage);
        NavigationManager.getInstance().navigateTo("/com/view/main-menu-view.fxml");
    }
}
