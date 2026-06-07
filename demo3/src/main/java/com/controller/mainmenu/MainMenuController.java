package com.controller.mainmenu;

import com.controller.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class MainMenuController {

    @FXML
    private ImageView MainMenuController;

    @FXML
    void startButtonClicked(MouseEvent event) {
        NavigationManager.getInstance().navigateTo("/com/view/tree-selection-view.fxml");
    }

    @FXML
    void settingsButtonClicked(MouseEvent event) {
        NavigationManager.getInstance().navigateTo("/com/view/settings-view.fxml");
    }

    @FXML
    void helpButtonClicked(MouseEvent event) {
        NavigationManager.getInstance().navigateTo("/com/view/credit-view.fxml");
    }
}
