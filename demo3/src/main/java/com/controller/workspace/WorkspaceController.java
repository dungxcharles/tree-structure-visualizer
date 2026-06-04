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

    @FXML
    void handleInsertAction(ActionEvent event) {
        String valueStr = valueTextField.getText();
        if (valueStr == null || valueStr.trim().isEmpty())
            return;

        int value = Integer.parseInt(valueStr.trim());

        if (logicalTree.isEmpty()) {
            logicalTree.create(value);
        } else {
            if (currentTreeType == TreeType.GENERAL || currentTreeType == TreeType.BINARY) {
                int parentValue = Integer.parseInt(parentValueTextField.getText().trim());
                logicalTree.insert(parentValue, value);
            } else {
                logicalTree.insert(0, value);
            }
        }

        treeController.setTreeData(logicalTree);
        redrawTree(); // Fix: Force layout calculation AND render to the canvas
        treeController.playAnimations();
    }

    @FXML
    void handleDeleteAction(ActionEvent event) {
        String valueStr = valueTextField.getText();
        if (valueStr == null || valueStr.trim().isEmpty())
            return;

        System.out.println("Delete button clicked with value: " + valueStr);
        // TODO: Pass value to the logical tree model (e.g.
        // treeModel.delete(Integer.parseInt(valueStr)))
        // TODO: Inform TreeVisualizationController to update layout/animation
    }

    @FXML
    void handleSearchAction(ActionEvent event) {
        String valueStr = valueTextField.getText();
        if (valueStr == null || valueStr.trim().isEmpty())
            return;

        System.out.println("Search button clicked with value: " + valueStr);
        // TODO: Pass value to the logical tree model to perform search
        // TODO: Highlight the found node visually
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
