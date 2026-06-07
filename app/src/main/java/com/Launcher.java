package com;

import com.controller.NavigationManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Tree Operation");
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/view/images/icon.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        NavigationManager.getInstance().setStage(stage);
        NavigationManager.getInstance().navigateTo("/com/view/fxml/main-menu-view.fxml");
    }
}
