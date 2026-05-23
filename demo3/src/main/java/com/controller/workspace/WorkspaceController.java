package com.controller.workspace;

import com.model.tree.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import com.controller.treeselection.TreeSelectionController;

public class WorkspaceController {
    @FXML
    private Pane visualizerPane;

    @FXML
    private Button homeButton;

    @FXML
    private TextField treeTypeTF;
    private AbstractTree treeModel;

    public void initWorkspace() throws IOException {
        TreeSelectionController treeSelectionCtrl = new TreeSelectionController();
        treeSelectionCtrl.initWorkspace();
    }

    @FXML
    void homeButtonClicked(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/main-menu-view.fxml"));
        Parent workspaceView = loader.load();

        Scene workspaceScene = new Scene(workspaceView);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(workspaceScene);
        stage.show();
    }
}
