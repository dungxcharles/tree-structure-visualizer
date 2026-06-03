package com.controller.treeselection;

import com.controller.NavigationManager;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class TreeSelectionController {

    @FXML
    private AnchorPane mainContainer;

    @FXML
    private Button btnBack;

    private static final double DESIGN_WIDTH = 1280.0;
    private static final double DESIGN_HEIGHT = 720.0;

    @FXML
    public void initialize() {
        mainContainer.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                applyResponsiveScaling(newScene);
            }
        });
    }

    private void applyResponsiveScaling(Scene scene) {
        DoubleBinding scaleBinding = Bindings.createDoubleBinding(() -> {
            double scaleX = scene.getWidth() / DESIGN_WIDTH;
            double scaleY = scene.getHeight() / DESIGN_HEIGHT;
            return Math.min(scaleX, scaleY);
        }, scene.widthProperty(), scene.heightProperty());

        mainContainer.scaleXProperty().bind(scaleBinding);
        mainContainer.scaleYProperty().bind(scaleBinding);
    }

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
    void handleBack(ActionEvent event){
        NavigationManager.getInstance().navigateTo("/com/view/main-menu-view.fxml");
    }
}
