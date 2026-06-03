package com.controller.settings;

import com.controller.NavigationManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class SettingsController {

    @FXML
    void backButtonClicked(ActionEvent event) {
        NavigationManager.getInstance().navigateTo("/com/view/main-menu-view.fxml");
    }
}

