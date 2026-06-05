package com.controller.workspace;

import com.model.vis.VisualNode;
import com.model.tree.AbstractTree;
import com.model.tree.TreeFactory;
import com.model.tree.TreeType;
import com.model.vis.VisualEdge;
import com.model.vis.VisualTree;
import com.view.vis.layout.GeneralTreeLayout;
import com.view.vis.TreeCanvas;
import com.view.vis.animation.AnimationManager;
import com.view.vis.render.DefaultEdgeRenderer;
import com.view.vis.render.DefaultNodeRenderer;
import com.controller.NavigationManager;
import com.controller.vis.TreeVisualizationController;
import com.view.vis.layout.BinaryTreeLayout;
import com.view.vis.layout.LayoutStrategy;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 * A new controller for workspace.fxml to test the tree visualization
 * without modifying the existing WorkspaceController.java.
 * 
 * To use this, update your workspace.fxml to point to this controller:
 * fx:controller="com.controller.workspace.DemoWorkspaceController"
 */
public class WorkspaceController {

    @FXML
    private Pane visualizerPane;

    @FXML
    private Button homeButton;

    @FXML
    private ComboBox<String> treeTypeComboBox;

    @FXML
    private TextField valueTextField;

    @FXML
    private TextField parentValueTextField;

    @FXML
    private Label treeTypeLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button insertButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button searchButton;

    private TreeVisualizationController treeController;
    private Canvas fxCanvas;
    private AbstractTree<?> logicalTree;

    // Static state to pass data between controllers without a new class
    public static TreeType currentTreeType = TreeType.BINARY_SEARCH;

    @FXML
    void handleSelectTreeType(ActionEvent event) {
        // Redundant since TreeSelection UI handles this, but kept to prevent FXML
        // LoadException.
    }

    private int getValueFromTextField(TextField textField) throws NullPointerException, NumberFormatException {
        if (textField == null)
            throw new NullPointerException("TextField is null");

        String valueStr = textField.getText();
        if (valueStr == null || valueStr.trim().isEmpty())
            throw new NumberFormatException("TextField is empty");

        try {
            return Integer.parseInt(valueStr.trim());
        } catch (NumberFormatException e) {
            throw e;
        }
    }

    @FXML
    void handleInsertAction(ActionEvent event) {
        if (treeController.isAnimating())
            return;

        int value = getValueFromTextField(valueTextField);

        setOperationButtonsDisabled(true);
        if (statusLabel != null) {
            statusLabel.setText("Inserting " + value + "...");
        }

        if (logicalTree.isEmpty()) {
            logicalTree.create(value);
        } else {
            if (currentTreeType == TreeType.GENERAL || currentTreeType == TreeType.BINARY) {
                String parentStr = parentValueTextField.getText();
                if (parentStr == null || parentStr.trim().isEmpty()) {
                    setOperationButtonsDisabled(false);
                    return;
                }
                int parentValue = Integer.parseInt(parentStr.trim());
                logicalTree.insert(parentValue, value);
            } else {
                logicalTree.insert(0, value);
            }
        }

        treeController.setTreeData(logicalTree);
        redrawTree();
        treeController.playAnimations();
    }

    @FXML
    void handleDeleteAction(ActionEvent event) {
        if (treeController.isAnimating())
            return;

        int value = getValueFromTextField(valueTextField);

        setOperationButtonsDisabled(true);
        if (statusLabel != null) {
            statusLabel.setText("Deleting " + value + "...");
        }

        logicalTree.delete(value);
        treeController.playAnimations();
    }

    @FXML
    void handleSearchAction(ActionEvent event) {
        if (treeController.isAnimating())
            return;

        int value = getValueFromTextField(valueTextField);

        setOperationButtonsDisabled(true);
        if (statusLabel != null) {
            statusLabel.setText("Searching for " + value + "...");
        }

        logicalTree.search(value);
        treeController.playAnimations();
    }

    /**
     * Disables or enables the Insert, Delete, Search buttons.
     */
    private void setOperationButtonsDisabled(boolean disabled) {
        if (insertButton != null)
            insertButton.setDisable(disabled);
        if (deleteButton != null)
            deleteButton.setDisable(disabled);
        if (searchButton != null)
            searchButton.setDisable(disabled);
    }

    @FXML
    public void initialize() {
        if (visualizerPane == null) {
            return;
        }

        // 1. Create the physical JavaFX Canvas and bind its size to the Pane
        fxCanvas = new Canvas();
        fxCanvas.widthProperty().bind(visualizerPane.widthProperty());
        fxCanvas.heightProperty().bind(visualizerPane.heightProperty());
        visualizerPane.getChildren().add(fxCanvas);

        // 2. Setup the MVC Visualization components
        TreeCanvas treeCanvas = new TreeCanvas(new DefaultNodeRenderer(), new DefaultEdgeRenderer());

        LayoutStrategy layoutStrategy;
        if (currentTreeType == TreeType.GENERAL) {
            layoutStrategy = new GeneralTreeLayout();
        } else {
            layoutStrategy = new BinaryTreeLayout();
        }

        treeController = new TreeVisualizationController(
                treeCanvas,
                new AnimationManager(),
                layoutStrategy);

        // Pass the canvas reference so the AnimationTimer can render
        treeController.setFxCanvas(fxCanvas);

        // Wire up status callback
        if (statusLabel != null) {
            treeController.setStatusCallback(msg -> statusLabel.setText(msg));
        }

        // Wire up animation finished callback to re-enable buttons
        treeController.setOnAnimationFinished(() -> setOperationButtonsDisabled(false));

        // 3. Initialize logical tree based on the selected static state
        logicalTree = TreeFactory.create(currentTreeType);
        logicalTree.setListener(treeController);
        treeController.setTreeData(logicalTree);

        // Setup UI dynamically based on the selected tree type
        boolean needsParent = (currentTreeType == TreeType.GENERAL || currentTreeType == TreeType.BINARY);
        if (parentValueTextField != null) {
            parentValueTextField.setVisible(needsParent);
            parentValueTextField.setManaged(needsParent);
        }
        if (treeTypeLabel != null) {
            treeTypeLabel.setText(currentTreeType.name().replace("_", " "));
        }
        if (treeTypeComboBox != null) {
            treeTypeComboBox.setValue(currentTreeType.name().replace("_", " "));
        }

        // 4. Force a layout and render when pane is resized
        visualizerPane.widthProperty().addListener((obs, oldVal, newVal) -> redrawTree());
        visualizerPane.heightProperty().addListener((obs, oldVal, newVal) -> redrawTree());
    }

    /**
     * Triggers the layout recalculation and redrawing of the canvas.
     */
    private void redrawTree() {
        double width = visualizerPane.getWidth();
        double height = visualizerPane.getHeight();

        if (width > 0 && height > 0) {
            treeController.updateLayout(width, height);
            treeController.renderFrame(fxCanvas.getGraphicsContext2D());
        }
    }

    @FXML
    void homeButtonClicked(MouseEvent event) {
        NavigationManager.getInstance().navigateTo("/com/view/main-menu-view.fxml");
    }
}
