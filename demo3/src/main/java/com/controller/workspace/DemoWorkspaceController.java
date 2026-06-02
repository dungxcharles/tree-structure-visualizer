package com.controller.workspace;

import com.visualization.model.VisualNode;
import com.visualization.model.VisualEdge;
import com.visualization.model.VisualTree;
import com.visualization.layout.GeneralTreeLayout;
import com.visualization.view.TreeCanvas;
import com.visualization.view.animation.AnimationManager;
import com.visualization.view.render.DefaultEdgeRenderer;
import com.visualization.view.render.DefaultNodeRenderer;
import com.visualization.controller.TreeVisualizationController;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

/**
 * A new controller for workspace.fxml to test the tree visualization
 * without modifying the existing WorkspaceController.java.
 * 
 * To use this, update your workspace.fxml to point to this controller:
 * fx:controller="com.controller.workspace.DemoWorkspaceController"
 */
public class DemoWorkspaceController {

    @FXML
    private Pane visualizerPane;

    @FXML
    private Button homeButton;

    @FXML
    private TextField treeTypeTF;

    private TreeVisualizationController treeController;
    private Canvas fxCanvas;

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
        treeController = new TreeVisualizationController(
                treeCanvas,
                new AnimationManager(),
                new GeneralTreeLayout()
        );

        // 3. Create a Dummy Tree for testing
        drawDummyTree();

        // 4. Force a layout and render when pane is resized
        visualizerPane.widthProperty().addListener((obs, oldVal, newVal) -> redrawTree());
        visualizerPane.heightProperty().addListener((obs, oldVal, newVal) -> redrawTree());
    }

    /**
     * Creates a dummy tree to test the layout and rendering logic.
     */
    private void drawDummyTree() {
        VisualTree tree = treeController.getVisualTree();
        tree.clear();
        
        VisualNode root = new VisualNode("root", "Root");
        root.setColorHex("#ff9999"); // Distinguish the root node with a color
        VisualNode child1 = new VisualNode("c1", "Child 1");
        VisualNode child2 = new VisualNode("c2", "Child 2");
        VisualNode child3 = new VisualNode("c3", "Child 3");

        tree.addNode(root);
        tree.addNode(child1);
        tree.addNode(child2);
        tree.addNode(child3);

        tree.addEdge(new VisualEdge(root, child1));
        tree.addEdge(new VisualEdge(root, child2));
        tree.addEdge(new VisualEdge(root, child3));
        
        // Optionally add a sub-child
        VisualNode subChild = new VisualNode("c1-1", "Sub 1");
        tree.addNode(subChild);
        tree.addEdge(new VisualEdge(child1, subChild));
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
    void homeButtonClicked(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/view/main-menu-view.fxml"));
        Parent workspaceView = loader.load();
        Scene workspaceScene = new Scene(workspaceView);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(workspaceScene);
        stage.show();
    }
}
