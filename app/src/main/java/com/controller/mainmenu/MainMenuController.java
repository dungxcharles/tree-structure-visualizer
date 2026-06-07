package com.controller.mainmenu;

import com.controller.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class MainMenuController {

    @FXML
    void startButtonClicked(MouseEvent event) {
        NavigationManager.getInstance().navigateTo("/com/view/fxml/tree-selection-view.fxml");
    }

    @FXML
    void settingsButtonClicked(MouseEvent event) {
        NavigationManager.getInstance().navigateTo("/com/view/fxml/settings-view.fxml");
    }

    @FXML
    void helpButtonClicked(MouseEvent event) {
        NavigationManager.getInstance().navigateTo("/com/view/fxml/credit-view.fxml");
    }
}
