package com.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationManager {

    private Stage stage;
    private static NavigationManager instance;
    private double currentWidth = 1280;
    private double currentHeight = 720;

    private NavigationManager() {
    }

    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
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
            if (!isFullScreen) {
                Platform.runLater(() -> setResolution(currentWidth, currentHeight));
            }
        }
    }

    public void navigateTo(String fxmlPath) {
        if (stage == null) {
            System.err.println("Stage is not set in NavigationManager.");
            return;
        }
        try {
            boolean wasFullScreen = stage.isFullScreen();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene;
            if (currentWidth > 0 && currentHeight > 0) {
                scene = new Scene(root, currentWidth, currentHeight);
            } else if (stage.getScene() != null) {
                scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
            } else {
                scene = new Scene(root);
            }

            stage.setScene(scene);

            if (wasFullScreen) {
                stage.setFullScreen(true);
            }

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
