package com.controller.treeselection;

import com.controller.NavigationManager;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class TreeSelectionController {
    private static final double POPUP_MAXWIDTH_RATIO = 0.65;
    private static final double POPUP_MAXHEIGHT_RATIO = 0.75;

    @FXML
    private StackPane rootStackPane;

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

    @FXML
    void handleClickedTree1(MouseEvent event){
        showInformationPopUp(TreeDetailPopupController.TreeInfo.GENERAL_TREE);
    }

    @FXML
    void handleClickedTree2(MouseEvent event){
        showInformationPopUp(TreeDetailPopupController.TreeInfo.BINARY_TREE);
    }

    @FXML
    void handleClickedTree3(MouseEvent event){
        showInformationPopUp(TreeDetailPopupController.TreeInfo.RED_BLACK_TREE);
    }

    @FXML
    void handleClickedTree4(MouseEvent event){
        showInformationPopUp(TreeDetailPopupController.TreeInfo.AVL_TREE);
    }

    @FXML
    void handleClickedTree5(MouseEvent event){
        showInformationPopUp(TreeDetailPopupController.TreeInfo.BINARY_SEARCH_TREE);
    }

    private void showInformationPopUp(TreeDetailPopupController.TreeInfo info) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/tree-detail-popup.fxml"));
            Region popupContent = loader.load();
            TreeDetailPopupController controller = loader.getController();

            Scene scene = rootStackPane.getScene();
            DoubleBinding scaleBinding = Bindings.createDoubleBinding(() -> {
                double scaleX = scene.getWidth() / DESIGN_WIDTH;
                double scaleY = scene.getHeight() / DESIGN_HEIGHT;
                return Math.min(scaleX, scaleY);
            }, scene.widthProperty(), scene.heightProperty());

            popupContent.scaleXProperty().bind(scaleBinding);
            popupContent.scaleYProperty().bind(scaleBinding);

            Region dimOverlay = new Region();
            dimOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

            StackPane popupContainer = new StackPane(dimOverlay, popupContent);

            Runnable closePopup = () -> rootStackPane.getChildren().remove(popupContainer);

            dimOverlay.setOnMouseClicked(e -> closePopup.run());
            popupContent.setOnMouseClicked(MouseEvent::consume);
            controller.setOnCloseAction(closePopup);
            controller.setTreeInfo(info);

            rootStackPane.getChildren().add(popupContainer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

