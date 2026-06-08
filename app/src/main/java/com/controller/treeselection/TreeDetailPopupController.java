package com.controller.treeselection;

import com.controller.NavigationManager;
import com.controller.workspace.WorkspaceController;
import com.model.tree.TreeType;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TreeDetailPopupController {

    @FXML
    private Label lblTitle;

    @FXML
    private Label lblDescription;

    @FXML
    private Button btnStart;

    private Runnable onCloseAction;

    private TreeInfo currentTreeInfo;

    public void setTreeInfo(TreeInfo treeInfo) {
        this.currentTreeInfo = treeInfo;
        lblTitle.setText(treeInfo.getTitle());
        lblDescription.setText(treeInfo.getDescription());
        lblDescription.setStyle("-fx-text-alignment: center; -fx-alignment: center; -fx-line-spacing: 5px;");
    }

    public void setOnCloseAction(Runnable onCloseAction) {
        this.onCloseAction = onCloseAction;
    }

    @FXML
    void handleStartVisualization(ActionEvent event) {
        if (onCloseAction != null) {
            onCloseAction.run();
        }

        if (currentTreeInfo != null) {
            switch (currentTreeInfo) {
                case GENERAL_TREE:
                    WorkspaceController.currentTreeType = TreeType.GENERAL;
                    break;
                case BINARY_TREE:
                    WorkspaceController.currentTreeType = TreeType.BINARY;
                    break;
                case RED_BLACK_TREE:
                    WorkspaceController.currentTreeType = TreeType.RED_BLACK;
                    break;
                case AVL_TREE:
                    WorkspaceController.currentTreeType = TreeType.AVL;
                    break;
                case BINARY_SEARCH_TREE:
                    WorkspaceController.currentTreeType = TreeType.BINARY_SEARCH;
                    break;
            }
        }

        NavigationManager.getInstance().navigateTo("/com/view/fxml/workspace.fxml");
    }

    public enum TreeInfo {
        GENERAL_TREE("General Tree",
                "A general tree is a hierarchical data structure in which each node can have an arbitrary number of children.\n\n" +
                "✨ Time Complexity ✨\n" +
                "Search: O(n)   |   Insert: O(1) or O(n)   |   Delete: O(n)\n\n"),
        BINARY_TREE("Binary Tree",
                "A binary tree is a tree data structure in which each node has at most two children, referred to as the left child and the right child.\n\n" +
                "✨ Time Complexity ✨\n" +
                "Search: O(n)   |   Insert: O(n)   |   Delete: O(n)\n\n"),
        RED_BLACK_TREE("Red-Black Tree",
                "A red-black tree is a kind of self-balancing binary search tree where each node has an extra bit for color, used to ensure the tree remains balanced.\n\n" +
                "✨ Time Complexity ✨\n" +
                "Search: O(log n)   |   Insert: O(log n)   |   Delete: O(log n)\n\n"),
        AVL_TREE("AVL Tree",
                "An AVL tree is a self-balancing binary search tree where the difference between heights of left and right subtrees cannot be more than one for all nodes.\n\n" +
                "✨ Time Complexity ✨\n" +
                "Search: O(log n)   |   Insert: O(log n)   |   Delete: O(log n)\n\n"),
        BINARY_SEARCH_TREE("Binary Search Tree",
                "A binary search tree is a rooted binary tree whose internal nodes each store a key greater than all the keys in the node's left subtree and less than those in its right subtree.\n\n" +
                "✨ Time Complexity (Avg/Worst) ✨\n" +
                "Search: O(log n)/O(n)   |   Insert: O(log n)/O(n)   |   Delete: O(log n)/O(n)\n\n");

        private final String title;
        private final String description;

        TreeInfo(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }
}
