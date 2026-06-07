package com.controller;

import com.theme.ThemeManager;
import com.theme.ThemeType;
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
        return ThemeManager.getInstance().isDarkMode();
    }

    public void setDarkMode(boolean isDarkMode) {
        ThemeManager.getInstance().setThemeType(isDarkMode ? ThemeType.DARK : ThemeType.LIGHT);
        if (stage != null && stage.getScene() != null) {
            applyTheme(stage.getScene());
        }
    }

    public void applyTheme(Scene scene) {
        ThemeManager.getInstance().applyTheme(scene);
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
