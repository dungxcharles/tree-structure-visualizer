package com.controller.treeselection;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class TreeSelectionController {
    @FXML
    public void initWorkspace() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/tree-selection-view.fxml"));
        Stage stage = new Stage();
        Scene treeSelectionView = new Scene(loader.load());
        stage.setTitle("Tree Selection");
        stage.setScene(treeSelectionView);
        stage.show();
    }

    @FXML
    public void treeSelected(MouseEvent event) throws IOException {
        Node selected = (Node) event.getSource();
        String treeType = selected.getId();

    }
}
