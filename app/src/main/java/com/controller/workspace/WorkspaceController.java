package com.controller.workspace;

import com.model.history.*;
import com.model.tree.*;
import com.view.vis.layout.*;
import com.view.vis.*;
import com.view.vis.animation.*;
import com.view.vis.render.*;
import com.controller.NavigationManager;
import com.controller.vis.TreeVisualizationController;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import com.view.vis.pseudocode.*;
import javafx.scene.input.MouseEvent;
import com.util.*;

public class WorkspaceController {

    @FXML
    private Pane visualizerPane;

    @FXML
    private ComboBox<String> treeTypeComboBox;

    @FXML
    private TextField valueTextField, parentValueTextField;

    @FXML
    private Label treeTypeLabel;

    @FXML
    private ListView<String> pseudoCodeListView;

    private ListViewPseudoCodeDisplay pseudoCodeDisplay;

    @FXML
    private Label heightLabel, numNodesLabel, rootValueLabel, balanceFactorLabel,
            balanceFactorTextLabel,
            traverseStatusLabel;

    @FXML
    private Slider speedSlider;

    @FXML
    private ProgressBar animationProgressBar;

    @FXML
    private ComboBox<String> traversalComboBox;

    @FXML
    private Button insertButton, deleteButton, updateButton, searchButton;

    @FXML
    private Button undoButton, redoButton;

    @FXML
    private Button pauseResumeButton;

    private HistoryManager historyManager;

    private TreeVisualizationController treeController;
    private AbstractTree<?> logicalTree;
    private boolean recordingTraverseStatus = false;
    private final List<Integer> traverseStatusValues = new ArrayList<>();

    // Default tree type
    public static TreeType currentTreeType = TreeType.BINARY_SEARCH;

    private void executeTreeOperation(Runnable operation) {
        if (treeController.isAnimating())
            return;

        if (pseudoCodeDisplay != null) {
            pseudoCodeDisplay.clear();
        }

        setOperationButtonsDisabled(true);

        boolean success = false;
        try {
            operation.run();
            success = true;
        } catch (NumberFormatException e) {
            AlertUtils.showErrorAlert("Invalid Input", "Please enter a valid integer value.");
        } catch (IllegalArgumentException | IllegalStateException | UnsupportedOperationException e) {
            AlertUtils.showErrorAlert("Operation Error", e.getMessage());
        } catch (Exception e) {
            AlertUtils.showErrorAlert("Unexpected Error", "An unexpected error occurred: " + e.getMessage());
        } finally {
            if (!success) {
                setOperationButtonsDisabled(false);
                recordingTraverseStatus = false;
            }
        }

        if (success) {
            treeController.playAnimations();
        }
    }

    @FXML
    private void handleInsertAction(ActionEvent event) {
        executeTreeOperation(() -> {
            int value = InputValidator.getValidInt(valueTextField);

            int parentValue = 0;
            if (!logicalTree.isEmpty() && (currentTreeType == TreeType.GENERAL || currentTreeType == TreeType.BINARY)) {
                parentValue = InputValidator.getValidInt(parentValueTextField);
            }

            if (logicalTree.isEmpty()) {
                logicalTree.create(value);
                if (!logicalTree.isEmpty()) {
                    historyManager.addOperation(new HistoryOperation(HistoryOperation.Type.CREATE, 0, value));
                    clearTraverseStatus();
                }
            } else {
                boolean inserted = (currentTreeType == TreeType.GENERAL || currentTreeType == TreeType.BINARY)
                        ? logicalTree.insert(parentValue, value)
                        : logicalTree.insert(0, value);
                if (inserted) {
                    historyManager.addOperation(new HistoryOperation(HistoryOperation.Type.INSERT, parentValue, value));
                    clearTraverseStatus();
                }
            }
        });
    }

    @FXML
    private void handleDeleteAction(ActionEvent event) {
        executeTreeOperation(() -> {
            int value = InputValidator.getValidInt(valueTextField);
            if (logicalTree.delete(value)) {
                historyManager.addOperation(new HistoryOperation(HistoryOperation.Type.DELETE, 0, value));
                clearTraverseStatus();
            }
        });
    }

    @FXML
    private void handleUpdateAction(ActionEvent event) {
        if (treeController.isAnimating()) {
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Node");
        dialog.setHeaderText(null);

        TextField currentValueField = new TextField();
        currentValueField.setPromptText("Current value");
        TextField newValueField = new TextField();
        newValueField.setPromptText("New value");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(12));
        form.addRow(0, new Label("Current value:"), currentValueField);
        form.addRow(1, new Label("New value:"), newValueField);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        com.theme.ThemeManager.getInstance().applyThemeToDialogPane(dialog.getDialogPane());

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(
                currentValueField.textProperty().isEmpty()
                        .or(newValueField.textProperty().isEmpty()));

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        executeTreeOperation(() -> {
            int currentValue = InputValidator.getValidInt(currentValueField);
            int newValue = InputValidator.getValidInt(newValueField);
            if (logicalTree.update(currentValue, newValue)) {
                historyManager.addOperation(new HistoryOperation(HistoryOperation.Type.UPDATE, currentValue, newValue));
                clearTraverseStatus();
            }
        });
    }

    @FXML
    private void handlePauseResumeAction(ActionEvent event) {
        if (treeController.isAnimationPaused()) {
            treeController.resumeAnimation();
            pauseResumeButton.setText("Pause");
        } else {
            treeController.pauseAnimation();
            pauseResumeButton.setText("Play");
        }
    }

    @FXML
    private void handleSearchAction(ActionEvent event) {
        executeTreeOperation(() -> {
            int value = InputValidator.getValidInt(valueTextField);
            logicalTree.search(value);
        });
    }

    @FXML
    private void handleRecenterAction(ActionEvent event) {
        if (treeController != null) {
            treeController.resetCamera();
            redrawTree();
        }
    }

    @FXML
    private void handleUndoAction(ActionEvent event) {
        if (historyManager == null || !historyManager.canUndo() || treeController.isAnimating())
            return;
        historyManager.undo();
        replayHistory();
    }

    @FXML
    private void handleRedoAction(ActionEvent event) {
        if (historyManager == null || !historyManager.canRedo() || treeController.isAnimating())
            return;
        historyManager.redo();
        replayHistory();
    }

    private void replayHistory() {
        logicalTree.setListener(null);

        logicalTree = TreeFactory.create(currentTreeType);

        List<HistoryOperation> operations = historyManager.getActiveHistory();
        for (HistoryOperation op : operations) {
            op.apply(logicalTree);
        }

        logicalTree.setListener(treeController);
        treeController.setTreeData(logicalTree);
        redrawTree();
    }

    private void updateUndoRedoButtons() {
        if (undoButton != null)
            undoButton.setDisable(!historyManager.canUndo());
        if (redoButton != null)
            redoButton.setDisable(!historyManager.canRedo());
    }

    private void setOperationButtonsDisabled(boolean disabled) {
        for (Node node : new Node[] { insertButton, deleteButton, updateButton, searchButton,
                traversalComboBox }) {
            if (node != null)
                node.setDisable(disabled);
        }

        if (disabled) {
            if (undoButton != null)
                undoButton.setDisable(true);
            if (redoButton != null)
                redoButton.setDisable(true);
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

        visualizerPane.widthProperty().addListener((obs, oldVal, newVal) -> redrawTree());
        visualizerPane.heightProperty().addListener((obs, oldVal, newVal) -> redrawTree());
    }

    private void setupVisualization() {
        Canvas fxCanvas = new Canvas();
        fxCanvas.widthProperty().bind(visualizerPane.widthProperty());
        fxCanvas.heightProperty().bind(visualizerPane.heightProperty());
        visualizerPane.getChildren().add(fxCanvas);

        TreeCanvas treeCanvas = new TreeCanvas(fxCanvas, new DefaultNodeRenderer(), new DefaultEdgeRenderer());

        LayoutStrategy layoutStrategy = (currentTreeType == TreeType.GENERAL) ? new GeneralTreeLayout()
                : new BinaryTreeLayout();

        treeController = new TreeVisualizationController(treeCanvas, new AnimationManager(), layoutStrategy);
        treeController.setOnAnimationFinished(() -> {
            updateStatistics();
            recordingTraverseStatus = false;
            setOperationButtonsDisabled(false);
        });

        treeCanvas.setRedrawCallback(() -> {
            if (!treeController.isAnimating()) {
                treeController.renderFrame();
            }
        });
    }

    private void setupControls() {
        if (speedSlider != null) {
            treeController.setAnimationSpeed(speedSlider.getValue());
            speedSlider.valueProperty()
                    .addListener((obs, oldVal, newVal) -> treeController.setAnimationSpeed(newVal.doubleValue()));
        }

        treeController.setProgressCallback(progress -> {
            Platform.runLater(() -> {
                if (animationProgressBar != null) {
                    animationProgressBar.setProgress(progress);
                }
            });
        });

        if (pseudoCodeListView != null) {
            pseudoCodeDisplay = new ListViewPseudoCodeDisplay(pseudoCodeListView);
            treeController.setStepHighlightCallback(message -> {
                pseudoCodeDisplay.addAndHighlightStep(message);
                appendTraverseStatusFromStep(message);
            });
        }

        boolean needsParent = (currentTreeType == TreeType.GENERAL || currentTreeType == TreeType.BINARY);
        if (parentValueTextField != null) {
            parentValueTextField.setVisible(needsParent);
            parentValueTextField.setManaged(needsParent);
            parentValueTextField.setPromptText("Parent");
        }

        boolean hasBalanceFactor = (currentTreeType == TreeType.AVL);
        if (balanceFactorTextLabel != null) {
            balanceFactorTextLabel.setVisible(hasBalanceFactor);
            balanceFactorTextLabel.setManaged(hasBalanceFactor);
        }
        if (balanceFactorLabel != null) {
            balanceFactorLabel.setVisible(hasBalanceFactor);
            balanceFactorLabel.setManaged(hasBalanceFactor);
        }
        if (valueTextField != null) {
            valueTextField.setPromptText("Value");
        }
        if (treeTypeLabel != null) {
            treeTypeLabel.setText(currentTreeType.name().replace("_", " "));
        }
        clearTraverseStatus();
    }

    private void setupLogicalTree() {
        logicalTree = TreeFactory.create(currentTreeType);
        logicalTree.setListener(treeController);
        treeController.setTreeData(logicalTree);

        historyManager = new HistoryManager();
        updateUndoRedoButtons();
    }

    private void setupComboBoxes() {
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
                    beginTraverseStatus();
                    executeTreeOperation(() -> logicalTree.traverse(type));
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
                    NavigationManager.getInstance().navigateTo("/com/view/fxml/workspace.fxml");
                }
            });
        }
    }

    private void redrawTree() {
        double width = visualizerPane.getWidth();
        double height = visualizerPane.getHeight();

        if (width > 0 && height > 0) {
            treeController.updateLayout(width, height);
            if (!treeController.isAnimating()) {
                treeController.renderFrame();
            }
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
            rootValueLabel
                    .setText(logicalTree.getRoot() == null ? "None" : String.valueOf(logicalTree.getRoot().getValue()));
        }

        if (logicalTree instanceof AVLTree avlTree && balanceFactorLabel != null) {
            balanceFactorLabel.setText(String.valueOf(avlTree.getBalanceFactor(avlTree.getRoot())));
        }
    }

    private void clearTraverseStatus() {
        recordingTraverseStatus = false;
        traverseStatusValues.clear();
        if (traverseStatusLabel != null) {
            traverseStatusLabel.setText("None");
        }
    }

    private void beginTraverseStatus() {
        recordingTraverseStatus = true;
        traverseStatusValues.clear();
        if (traverseStatusLabel != null) {
            traverseStatusLabel.setText("Running...");
        }
    }

    private void appendTraverseStatusFromStep(String message) {
        if (!recordingTraverseStatus || message == null || !message.startsWith("Add ")
                || !message.endsWith(" to result list")) {
            return;
        }

        String valueText = message.substring("Add ".length(), message.length() - " to result list".length()).trim();
        try {
            traverseStatusValues.add(Integer.parseInt(valueText));
            updateTraverseStatusLabel();
        } catch (NumberFormatException ignored) {
        }
    }

    private void updateTraverseStatusLabel() {
        if (traverseStatusLabel == null) {
            return;
        }
        if (traverseStatusValues.isEmpty()) {
            traverseStatusLabel.setText("None");
            return;
        }

        String result = traverseStatusValues.toString();
        traverseStatusLabel.setText(result.substring(1, result.length() - 1).replace(", ", " -> "));
    }

    @FXML
    private void homeButtonClicked(MouseEvent event) {
        NavigationManager.getInstance().navigateTo("/com/view/fxml/tree-selection-view.fxml");
    }
}
