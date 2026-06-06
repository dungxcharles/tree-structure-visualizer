package com.controller.settings;

import com.controller.NavigationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Toggle;

public class SettingsController {

    @FXML
    private ToggleButton fullscreenToggle;

    @FXML
    private ToggleGroup resolution;

    @FXML
    public void initialize() {
        if (NavigationManager.getInstance().getStage() != null) {
            boolean isFullScreen = NavigationManager.getInstance().getStage().isFullScreen();
            fullscreenToggle.setSelected(isFullScreen);
            fullscreenToggle.setText(isFullScreen ? "ON" : "OFF");
        }

        double currentWidth = NavigationManager.getInstance().getCurrentWidth();
        double currentHeight = NavigationManager.getInstance().getCurrentHeight();
        if (currentWidth > 0 && currentHeight > 0 && resolution != null) {
            String targetText = (int) currentWidth + " x " + (int) currentHeight;
            for (Toggle toggle : resolution.getToggles()) {
                if (toggle instanceof RadioButton radioButton) {
                    if (radioButton.getText().equals(targetText)) {
                        radioButton.setSelected(true);
                        break;
                    }
                }
            }
        }
    }

    @FXML
    void changeResolution(ActionEvent event) {
        RadioButton selectedButton = (RadioButton) event.getSource();
        String[] res = selectedButton.getText().split(" x ");

        double width = Double.parseDouble(res[0]);
        double height = Double.parseDouble(res[1]);

        NavigationManager.getInstance().setResolution(width, height);
    }

    @FXML
    void toggleFullscreen(ActionEvent event) {
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        boolean isSelected = toggleButton.isSelected();

        NavigationManager.getInstance().setFullScreen(isSelected);
        toggleButton.setText(isSelected ? "ON" : "OFF");
    }

    @FXML
    void backButtonClicked(ActionEvent event) {
        NavigationManager.getInstance().navigateTo("/com/view/main-menu-view.fxml");
    }
}
