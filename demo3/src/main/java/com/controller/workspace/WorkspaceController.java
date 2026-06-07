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

import java.util.List;

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
    private Button undoButton;

    @FXML
    private Button redoButton;

    private HistoryManager historyManager;

    private TreeVisualizationController treeController;
    private Canvas fxCanvas;
    private AbstractTree<?> logicalTree;

    // Static state to pass data between controllers without a new class
    public static TreeType currentTreeType = TreeType.BINARY_SEARCH;

    private void executeTreeOperation(Runnable operation) {
        if (treeController.isAnimating())
            return;

        if (pseudoCodeDisplay != null) {
            pseudoCodeDisplay.clear();
        }

        setOperationButtonsDisabled(true);

        try {
            operation.run();
        } catch (Exception e) {
            // Re-enable if something failed before animation starts
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
            if (!logicalTree.isEmpty() && (currentTreeType == TreeType.GENERAL || currentTreeType == TreeType.BINARY)) {
                parentValue = InputValidator.getValidInt(parentValueTextField);
            }

            if (logicalTree.isEmpty()) {
                logicalTree.create(value);
                historyManager.addOperation(new HistoryOperation(HistoryOperation.Type.CREATE, 0, value));
            } else {
                if (currentTreeType == TreeType.GENERAL || currentTreeType == TreeType.BINARY) {
                    logicalTree.insert(parentValue, value);
                    historyManager.addOperation(new HistoryOperation(HistoryOperation.Type.INSERT, parentValue, value));
                } else {
                    logicalTree.insert(0, value);
                    historyManager.addOperation(new HistoryOperation(HistoryOperation.Type.INSERT, 0, value));
                }
            }
            updateUndoRedoButtons();
        });
    }

    @FXML
    void handleDeleteAction(ActionEvent event) {
        executeTreeOperation(() -> {
            int value = InputValidator.getValidInt(valueTextField);
            logicalTree.delete(value);
            historyManager.addOperation(new HistoryOperation(HistoryOperation.Type.DELETE, 0, value));
            updateUndoRedoButtons();
        });
    }

    @FXML
    private Button pauseResumeButton;

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

    @FXML
    void handleRecenterAction(ActionEvent event) {
        if (treeController != null) {
            treeController.resetCamera();
            redrawTree();
        }
    }

    @FXML
    void handleUndoAction(ActionEvent event) {
        if (historyManager == null || !historyManager.canUndo() || treeController.isAnimating()) return;
        historyManager.undo();
        replayHistory();
    }

    @FXML
    void handleRedoAction(ActionEvent event) {
        if (historyManager == null || !historyManager.canRedo() || treeController.isAnimating()) return;
        historyManager.redo();
        replayHistory();
    }

    private void replayHistory() {
        setOperationButtonsDisabled(true);
        
        // 1. Tạm thời ngắt kết nối màn hình để chạy ngầm
        logicalTree.setListener(null);
        
        // 2. Tạo cây mới trắng tinh
        logicalTree = TreeFactory.create(currentTreeType);
        
        // 3. Phát lại toàn bộ lịch sử trong nháy mắt
        List<HistoryOperation> operations = historyManager.getActiveHistory();
        for (HistoryOperation op : operations) {
            switch (op.getType()) {
                case CREATE:
                    logicalTree.create(op.getValue());
                    break;
                case INSERT:
                    logicalTree.insert(op.getParentValue(), op.getValue());
                    break;
                case DELETE:
                    logicalTree.delete(op.getValue());
                    break;
            }
        }
        
        // 4. Gắn lại màn hình và yêu cầu vẽ lại ngay lập tức
        logicalTree.setListener(treeController);
        treeController.setTreeData(logicalTree);
        redrawTree();
        
        setOperationButtonsDisabled(false);
    }

    private void updateUndoRedoButtons() {
        if (undoButton != null) undoButton.setDisable(!historyManager.canUndo());
        if (redoButton != null) redoButton.setDisable(!historyManager.canRedo());
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
            
        if (disabled) {
            if (undoButton != null) undoButton.setDisable(true);
            if (redoButton != null) redoButton.setDisable(true);
        } else {
            updateUndoRedoButtons();
        }
    }

    @FXML
    public void initialize() {
        if (visualizerPane == null) {
            return;
        }

        setupVisualization();
        setupControls();
        setupLogicalTree();
        setupComboBoxes();

        // 4. Force a layout and render when pane is resized
        visualizerPane.widthProperty().addListener((obs, oldVal, newVal) -> redrawTree());
        visualizerPane.heightProperty().addListener((obs, oldVal, newVal) -> redrawTree());
    }

    private void setupVisualization() {
        // 1. Create the physical JavaFX Canvas and bind its size to the Pane
        fxCanvas = new Canvas();
        fxCanvas.widthProperty().bind(visualizerPane.widthProperty());
        fxCanvas.heightProperty().bind(visualizerPane.heightProperty());
        visualizerPane.getChildren().add(fxCanvas);

        // 2. Setup the MVC Visualization components
        TreeCanvas treeCanvas = new TreeCanvas(fxCanvas, new DefaultNodeRenderer(), new DefaultEdgeRenderer());

        LayoutStrategy layoutStrategy = (currentTreeType == TreeType.GENERAL) ? new GeneralTreeLayout()
                : new BinaryTreeLayout();

        treeController = new TreeVisualizationController(treeCanvas, new AnimationManager(), layoutStrategy);
        treeController.setFxCanvas(fxCanvas);
        treeController.setOnAnimationFinished(() -> setOperationButtonsDisabled(false));

        treeCanvas.setRedrawCallback(() -> {
            if (!treeController.isAnimating()) {
                treeController.renderFrame(fxCanvas.getGraphicsContext2D());
            }
        });
    }

    private void setupControls() {
        // Connect animation speed slider
        if (speedSlider != null) {
            treeController.setAnimationSpeed(speedSlider.getValue());
            speedSlider.valueProperty()
                    .addListener((obs, oldVal, newVal) -> treeController.setAnimationSpeed(newVal.doubleValue()));
        }

        // Wire up pseudo code UI
        if (pseudoCodeListView != null) {
            pseudoCodeDisplay = new ListViewPseudoCodeDisplay(pseudoCodeListView);
            treeController.setStepHighlightCallback(pseudoCodeDisplay::addAndHighlightStep);
        }

        // Setup UI dynamically based on the selected tree type
        boolean needsParent = (currentTreeType == TreeType.GENERAL || currentTreeType == TreeType.BINARY);
        if (parentValueTextField != null) {
            parentValueTextField.setVisible(needsParent);
            parentValueTextField.setManaged(needsParent);
        }
        if (treeTypeLabel != null) {
            treeTypeLabel.setText(currentTreeType.name().replace("_", " "));
        }
    }

    private void setupLogicalTree() {
        // 3. Initialize logical tree based on the selected static state
        logicalTree = TreeFactory.create(currentTreeType);
        logicalTree.setListener(treeController);
        treeController.setTreeData(logicalTree);
        
        historyManager = new HistoryManager();
        updateUndoRedoButtons();
    }

    private void setupComboBoxes() {
        // Initialize traversal combobox
        if (traversalComboBox != null) {
            traversalComboBox.getItems().clear();
            for (TraversalType type : TraversalType.values()) {
                traversalComboBox.getItems().add(type.name().replace("_", " "));
            }
            traversalComboBox.setOnAction(event -> {
                if (treeController.isAnimating())
                    return;
                String selected = traversalComboBox.getValue();
                if (selected != null) {
                    TraversalType type = TraversalType
                            .valueOf(selected.replace(" ", "_"));
                    if (pseudoCodeDisplay != null) {
                        pseudoCodeDisplay.clear();
                    }
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
