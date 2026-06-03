package com.controller.settings;

import com.controller.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class SettingsController {

    @FXML
    void backButtonClicked(MouseEvent event) {
        NavigationManager.getInstance().navigateTo("/com/view/main-menu-view.fxml");
    }
}
