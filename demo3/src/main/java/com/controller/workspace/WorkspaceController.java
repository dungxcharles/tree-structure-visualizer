package com.controller.workspace;

import com.model.tree.AbstractTree;
import com.model.tree.TraversalType;
import com.model.tree.TreeFactory;
import com.model.tree.TreeType;
import com.view.vis.layout.GeneralTreeLayout;
import com.view.vis.TreeCanvas;
import com.view.vis.animation.AnimationManager;
import com.view.vis.render.DefaultEdgeRenderer;
import com.view.vis.render.DefaultNodeRenderer;
import com.controller.NavigationManager;
import com.controller.vis.TreeVisualizationController;
import com.view.vis.layout.BinaryTreeLayout;
import com.view.vis.layout.LayoutStrategy;

import javafx.application.Platform;
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
import com.util.InputValidator;

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
    private Label leafNodesLabel;

    @FXML
    private Label rootValueLabel;

    @FXML
    private Slider speedSlider;

    @FXML
    private ComboBox<String> traversalComboBox;

    @FXML
    private Button insertButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button searchButton;

    @FXML
    private Button pauseResumeButton;

    private TreeVisualizationController treeController;
    private Canvas fxCanvas;
    private AbstractTree<?> logicalTree;

    public static TreeType currentTreeType;

    private void executeTreeOperation(Runnable operation) {
        if (treeController.isAnimating())
            return;

        if (pseudoCodeDisplay != null)
            pseudoCodeDisplay.clear();

        setOperationButtonsDisabled(true);

        try {
            operation.run();
        } catch (Exception e) {
            setOperationButtonsDisabled(false);
            return;
        }

        treeController.playAnimations();
    }

    @FXML
    void handleInsertAction(ActionEvent event) {
        executeTreeOperation(() -> {
            int value = InputValidator.getValidInt(valueTextField);

            int parentValue = 0;
            if (!logicalTree.isEmpty() && (currentTreeType == TreeType.GENERAL || currentTreeType == TreeType.BINARY))
                parentValue = InputValidator.getValidInt(parentValueTextField);

            if (logicalTree.isEmpty())
                logicalTree.create(value);
            else {
                if (currentTreeType == TreeType.GENERAL || currentTreeType == TreeType.BINARY)
                    logicalTree.insert(parentValue, value);
                else
                    logicalTree.insert(0, value);
            }
        });
    }

    @FXML
    void handleDeleteAction(ActionEvent event) {
        executeTreeOperation(() -> {
            int value = InputValidator.getValidInt(valueTextField);
            logicalTree.delete(value);
        });
    }

    @FXML
    void handlePauseResumeAction(ActionEvent event) {
        if (treeController.isAnimationPaused()) {
            treeController.resumeAnimation();
            pauseResumeButton.setText("Pause");
        } else {
            treeController.pauseAnimation();
            pauseResumeButton.setText("Play");
        }
    }

    @FXML
    void handleSearchAction(ActionEvent event) {
        executeTreeOperation(() -> {
            int value = InputValidator.getValidInt(valueTextField);
            logicalTree.search(value);
        });
    }

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
        if (visualizerPane == null)
            return;

        setupVisualization();
        setupControls();
        setupLogicalTree();
        setupComboBoxes();

        visualizerPane.widthProperty().addListener((obs, oldVal, newVal) -> redrawTree());
        visualizerPane.heightProperty().addListener((obs, oldVal, newVal) -> redrawTree());
    }

    private void setupVisualization() {
        fxCanvas = new Canvas();
        fxCanvas.widthProperty().bind(visualizerPane.widthProperty());
        fxCanvas.heightProperty().bind(visualizerPane.heightProperty());
        visualizerPane.getChildren().add(fxCanvas);

        TreeCanvas treeCanvas = new TreeCanvas(new DefaultNodeRenderer(), new DefaultEdgeRenderer());

        LayoutStrategy layoutStrategy = (currentTreeType == TreeType.GENERAL) ? new GeneralTreeLayout()
                : new BinaryTreeLayout();

        treeController = new TreeVisualizationController(treeCanvas, new AnimationManager(), layoutStrategy);
        treeController.setFxCanvas(fxCanvas);
        treeController.setOnAnimationFinished(() -> setOperationButtonsDisabled(false));
    }

    private void setupControls() {
        if (speedSlider != null) {
            treeController.setAnimationSpeed(speedSlider.getValue());
            speedSlider.valueProperty()
                    .addListener((obs, oldVal, newVal) -> treeController.setAnimationSpeed(newVal.doubleValue()));
        }

        if (pseudoCodeListView != null) {
            pseudoCodeDisplay = new ListViewPseudoCodeDisplay(pseudoCodeListView);
            treeController.setStepHighlightCallback(pseudoCodeDisplay::addAndHighlightStep);
        }

        boolean needsParent = (currentTreeType == TreeType.GENERAL || currentTreeType == TreeType.BINARY);
        if (parentValueTextField != null) {
            parentValueTextField.setVisible(needsParent);
            parentValueTextField.setManaged(needsParent);
        }
        if (treeTypeLabel != null)
            treeTypeLabel.setText(currentTreeType.name().replace("_", " "));
    }

    private void setupLogicalTree() {
        logicalTree = TreeFactory.create(currentTreeType);
        logicalTree.setListener(treeController);
        treeController.setTreeData(logicalTree);
    }

    private void setupComboBoxes() {
        if (traversalComboBox != null) {
            traversalComboBox.getItems().clear();
            for (TraversalType type : TraversalType.values())
                traversalComboBox.getItems().add(type.name().replace("_", " "));
            traversalComboBox.setOnAction(event -> {
                if (treeController.isAnimating())
                    return;
                String selected = traversalComboBox.getValue();
                if (selected != null) {
                    TraversalType type = TraversalType.valueOf(selected.replace(" ", "_"));
                    if (pseudoCodeDisplay != null)
                        pseudoCodeDisplay.clear();
                    setOperationButtonsDisabled(true);
                    logicalTree.traverse(type);
                    treeController.playAnimations();
                    Platform.runLater(() -> traversalComboBox.getSelectionModel().clearSelection());
                }
            });
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
    }

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
            if (logicalTree.getRoot() == null)
                rootValueLabel.setText("None");
            else
                rootValueLabel.setText(String.valueOf(logicalTree.getRoot().getValue()));
        }
    }

    @FXML
    void homeButtonClicked(MouseEvent event) {
        NavigationManager.getInstance().navigateTo("/com/view/tree-selection-view.fxml");
    }
}
