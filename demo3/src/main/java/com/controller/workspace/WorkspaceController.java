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
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import com.view.vis.pseudocode.ListViewPseudoCodeDisplay;
import javafx.scene.input.MouseEvent;

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
    private ListView<String> pseudoCodeListView;

    private ListViewPseudoCodeDisplay pseudoCodeDisplay;

    @FXML
    private Label heightLabel;

    @FXML
    private Label numNodesLabel;

    @FXML
    private Label rootValueLabel;

    @FXML
    private Slider speedSlider;

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

        int value;
        try {
            value = getValueFromTextField(valueTextField);
        } catch (Exception e) {
            return;
        }

        if (pseudoCodeDisplay != null) {
            pseudoCodeDisplay.clear();
        }

        int parentValue = 0;
        if (!logicalTree.isEmpty() && (currentTreeType == TreeType.GENERAL || currentTreeType == TreeType.BINARY)) {
            try {
                parentValue = getValueFromTextField(parentValueTextField);
            } catch (Exception e) {
                return;
            }
        }

        setOperationButtonsDisabled(true);

        if (logicalTree.isEmpty()) {
            logicalTree.create(value);
        } else {
            if (currentTreeType == TreeType.GENERAL || currentTreeType == TreeType.BINARY) {
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

        int value;
        try {
            value = getValueFromTextField(valueTextField);
        } catch (Exception e) {
            return;
        }

        if (pseudoCodeDisplay != null) {
            pseudoCodeDisplay.clear();
        }

        setOperationButtonsDisabled(true);

        logicalTree.delete(value);
        treeController.playAnimations();
    }

    @FXML
    void handleSearchAction(ActionEvent event) {
        if (treeController.isAnimating())
            return;

        int value;
        try {
            value = getValueFromTextField(valueTextField);
        } catch (Exception e) {
            return;
        }

        if (pseudoCodeDisplay != null) {
            pseudoCodeDisplay.clear();
        }

        setOperationButtonsDisabled(true);

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

        // Wire up animation finished callback to re-enable buttons
        treeController.setOnAnimationFinished(() -> setOperationButtonsDisabled(false));

        // Connect animation speed slider
        if (speedSlider != null) {
            // Set initial speed
            treeController.setAnimationSpeed(speedSlider.getValue());
            // Listen for slider changes
            speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                treeController.setAnimationSpeed(newVal.doubleValue());
            });
        }

        // Wire up pseudo code UI
        if (pseudoCodeListView != null) {
            pseudoCodeDisplay = new ListViewPseudoCodeDisplay(pseudoCodeListView);
            treeController.setStepHighlightCallback(pseudoCodeDisplay::addAndHighlightStep);
        }

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
            treeTypeComboBox.getItems().clear();
            for (TreeType type : TreeType.values()) {
                treeTypeComboBox.getItems().add(type.name().replace("_", " "));
            }
            treeTypeComboBox.setValue(currentTreeType.name().replace("_", " "));
            treeTypeComboBox.setOnAction(event -> {
                String selected = treeTypeComboBox.getValue();
                if (selected != null) {
                    WorkspaceController.currentTreeType = TreeType.valueOf(selected.replace(" ", "_"));
                    NavigationManager.getInstance().navigateTo("/com/view/workspace.fxml");
                }
            });
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
        updateStatistics();
    }

    private void updateStatistics() {
        if (logicalTree == null)
            return;

        if (heightLabel != null)
            heightLabel.setText(String.valueOf(logicalTree.getHeight()));
        if (numNodesLabel != null)
            numNodesLabel.setText(String.valueOf(logicalTree.getNumberOfNodes()));

        if (rootValueLabel != null) {
            if (logicalTree.getRoot() == null) {
                rootValueLabel.setText("None");
            } else {
                rootValueLabel.setText(String.valueOf(logicalTree.getRoot().getValue()));
            }
        }
    }

    @FXML
    void homeButtonClicked(MouseEvent event) {
        NavigationManager.getInstance().navigateTo("/com/view/tree-selection-view.fxml");
    }
}
