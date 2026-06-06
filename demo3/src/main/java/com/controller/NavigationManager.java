package com.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class NavigationManager {

    private Stage stage;
    private static NavigationManager instance;
    private double currentWidth = 1280;
    private double currentHeight = 720;
    private boolean isDarkMode = false;

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

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void setDarkMode(boolean isDarkMode) {
        this.isDarkMode = isDarkMode;
        if (stage != null && stage.getScene() != null) {
            applyTheme(stage.getScene());
        }
    }

    public void applyTheme(Scene scene) {
        if (scene == null) return;
        try {
            String baseStyle = getClass().getResource("/com/view/base-style.css").toExternalForm();
            String darkStyle = getClass().getResource("/com/view/dark-mode.css").toExternalForm();

            if (!scene.getStylesheets().contains(baseStyle)) {
                scene.getStylesheets().add(baseStyle);
            }

            if (isDarkMode) {
                if (!scene.getStylesheets().contains(darkStyle)) {
                    scene.getStylesheets().add(darkStyle);
                }
            } else {
                scene.getStylesheets().remove(darkStyle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                double targetStageWidth = width + widthDiff;
                double targetStageHeight = height + heightDiff;

                Screen screen = Screen.getPrimary();
                if (stage.getWidth() > 0 && stage.getHeight() > 0) {
                    List<Screen> screens = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
                    if (!screens.isEmpty()) {
                        screen = screens.get(0);
                    }
                }
                Rectangle2D screenBounds = screen.getVisualBounds();
                double maxStageWidth = screenBounds.getWidth();
                double maxStageHeight = screenBounds.getHeight();

                if (targetStageWidth > maxStageWidth) {
                    targetStageWidth = maxStageWidth;
                }
                if (targetStageHeight > maxStageHeight) {
                    targetStageHeight = maxStageHeight;
                }

                stage.setWidth(targetStageWidth);
                stage.setHeight(targetStageHeight);
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

            if (stage.getScene() != null) {
                stage.getScene().setRoot(root);
                applyTheme(stage.getScene());
                if (wasFullScreen) {
                    stage.setFullScreen(true);
                }
            } else {
                Scene scene = new Scene(root, currentWidth, currentHeight);
                applyTheme(scene);
                stage.setScene(scene);
                if (wasFullScreen) {
                    stage.setFullScreen(true);
                }
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
