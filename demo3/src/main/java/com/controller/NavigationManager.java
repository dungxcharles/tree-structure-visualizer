package com.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationManager {

    private Stage stage;
    private static NavigationManager instance;
    private double currentWidth = 1280;
    private double currentHeight = 720;

    public static NavigationManager getInstance() {
        if (instance == null)
            instance = new NavigationManager();
        return instance;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public double getCurrentWidth() {
        return currentWidth;
    }

    public double getCurrentHeight() {
        return currentHeight;
    }

    public void setResolution(double width, double height) {
        this.currentWidth = width;
        this.currentHeight = height;

        if (stage != null && !stage.isFullScreen()) {
            Scene scene = stage.getScene();
            if (scene != null) {
                double widthDiff = stage.getWidth() - scene.getWidth();
                double heightDiff = stage.getHeight() - scene.getHeight();

                stage.setWidth(width + widthDiff);
                stage.setHeight(height + heightDiff);
                stage.centerOnScreen();
            }
        }
    }

    public void setFullScreen(boolean isFullScreen) {
        if (stage != null) {
            stage.setFullScreen(isFullScreen);
            if (!isFullScreen)
                Platform.runLater(() -> setResolution(currentWidth, currentHeight));
        }
    }

    public void navigateTo(String fxmlPath) {
        try {
            boolean wasFullScreen = stage.isFullScreen();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (stage.getScene() != null) {
                stage.getScene().setRoot(root);
                if (wasFullScreen)
                    stage.setFullScreen(true);
            } else {
                Scene scene = new Scene(root, currentWidth, currentHeight);
                stage.setScene(scene);
                if (wasFullScreen)
                    stage.setFullScreen(true);
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
