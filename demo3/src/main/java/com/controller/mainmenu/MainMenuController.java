package com.controller.mainmenu;

import com.controller.workspace.WorkspaceController;
import com.model.tree.GeneralTree;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;

public class MainMenuController {

    @FXML
    private ImageView MainMenuController;

    @FXML
    void startButtonClicked(MouseEvent event) throws IOException {
        // Get workspace FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/workspace.fxml"));

        // Initialize default settings
        Parent workspaceView = loader.load();
        WorkspaceController workspaceCtrl = loader.getController();
        workspaceCtrl.initWorkspace();

        // Setting up the workspace scene
        Scene workspaceScene = new Scene(workspaceView);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(workspaceScene);
        stage.show();
    }
}
