package com.controller.settings;

import com.controller.NavigationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class SettingsController {

    @FXML
    void changeResolution(ActionEvent event) {
        RadioButton selectedButton = (RadioButton) event.getSource();
        String[] res = selectedButton.getText().split(" x ");

        Stage stage = NavigationManager.getInstance().getStage();
        if (stage != null && !stage.isFullScreen()) {
            stage.setWidth(Double.parseDouble(res[0]));
            stage.setHeight(Double.parseDouble(res[1]));
            stage.centerOnScreen();
        }
    }

    @FXML
    void toggleFullscreen(ActionEvent event) {
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        Stage stage = NavigationManager.getInstance().getStage();

        if (stage != null) {
            stage.setFullScreen(toggleButton.isSelected());
            toggleButton.setText(toggleButton.isSelected() ? "ON" : "OFF");
        }
    }

    @FXML
    void backButtonClicked(ActionEvent event) {
        NavigationManager.getInstance().navigateTo("/com/view/main-menu-view.fxml");
    }
}
