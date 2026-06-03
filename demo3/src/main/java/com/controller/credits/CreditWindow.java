package com.controller.credits;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controller for the Credit and Instructions Window.
 * Implements a paginated view with 7 pages of content.
 */
public class CreditWindow {

    @FXML
    private StackPane contentArea;
    @FXML
    private Button btnLeft;
    @FXML
    private Button btnRight;
    @FXML
    private Button btnBack;
    @FXML
    private HBox paginationDots;

    private final List<Node> pages = new ArrayList<>();
    private int currentPageIndex = 0;

    @FXML
    public void initialize() {
        setupPages();
        updateView();
    }

    /**
     * Initializes all 7 pages as JavaFX Nodes.
     */
    private void setupPages() {
        // Page 1: Acknowledgements (Text)
        pages.add(createTextPage(16, "Acknowledgements",
            "This Tree Visualization application was developed as a final project for the OOP course. " +
            "Our goal is to provide an intuitive and interactive way to understand various tree data structures, " +
            "including Binary Trees, AVL Trees, Red-Black Trees and so on. We hope this tool helps students and " +
            "enthusiasts alike to grasp the complexities of tree algorithms through visualization."));

        // Page 2: Credits (Text)
        pages.add(createTextPage(16, "About us",
            "Developed by Group-14 as the Capstone Project of Object-Oriented Programming class, All members are majoring in Global ICT, School of Information and Communication technology - Hanoi University of Science and Technology\n\n" +
            "Our team members:\n" +
            "• Nguyen Tien Dung - StudentID: 202417116\n" +
            "• Nguyen Khac Khiem - StudentID: 202417142\n" +
            "• Tran Quang Thang - StudentID: 202417196\n" +
            "• Vu Khanh Toan - StudentID: 202417204\n" +
            "• Nguyen Xuan Thuy - StudentID: 202417202\n\n" +
            "To make this project successfully, specially thanks to our teacher Ph.D. Nguyen Thi Thu Trang and our teaching assistants Nguyen Huu Hoang Hai Anh and Dang Van Nhan for their guidance throughout the semester. ありがとう　ツ"));

        // Page 3: How to use? (Text)
        pages.add(createTextPage(16, "How to use?",
            "Navigating the application is simple:\n\n" +
            "1. Select a tree type from the main menu.\n" +
            "2. Use the 'Insert' button to add nodes to the tree.\n" +
            "3. Use 'Delete' or 'Search' to interact with existing nodes.\n" +
            "4. Watch the animations to understand how the tree balances itself.\n" +
            "5. Use the settings menu to adjust animation speed."));

        // Pages 4-7: Step-by-Step Instructions (Image-based)
        for (int i = 1; i <= 4; i++) {
            pages.add(createImagePage("/com/view/help/instruction" + i + ".png"));
        }
    }

    /**
     * Helper to create a text-based page with improved layout and readability.
     */
    private Node createTextPage(int fontSize, String title, String content) {
        VBox vbox = new VBox(30); // Spacing between title and body
        vbox.setAlignment(Pos.TOP_CENTER);
        // Apply generous side padding (Top, Right, Bottom, Left)
        vbox.setPadding(new javafx.geometry.Insets(40, 80, 40, 80));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#2c3e50"));
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER); // Center the label itself
        titleLabel.setTextAlignment(TextAlignment.CENTER); // Center the text within

        Label contentLabel = new Label(content);
        contentLabel.setFont(Font.font("System", fontSize));
        contentLabel.setTextFill(Color.web("#34495e"));
        contentLabel.setWrapText(true);
        contentLabel.setLineSpacing(5.0); // Better readability
        contentLabel.setMaxWidth(Double.MAX_VALUE); // Expand to fill available width
        contentLabel.setAlignment(Pos.TOP_LEFT); // Left-align text relative to label
        contentLabel.setTextAlignment(TextAlignment.LEFT); // Left-align wrapped lines

        vbox.getChildren().addAll(titleLabel, contentLabel);
        return vbox;
    }

    /**
     * Helper to create an image-based page.
     */
    private Node createImagePage(String imagePath) {
        StackPane container = new StackPane();
        try {
            Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            ImageView imageView = new ImageView(img);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(600); // Fit within the center area
            imageView.setFitHeight(350);
            container.getChildren().add(imageView);
        } catch (Exception e) {
            Label errorLabel = new Label("Image not found: " + imagePath);
            errorLabel.setTextFill(Color.RED);
            container.getChildren().add(errorLabel);
        }
        return container;
    }

    /**
     * Updates the UI based on the current page index.
     */
    private void updateView() {
        if (pages.isEmpty()) return;

        // 1. Swap the content
        contentArea.getChildren().setAll(pages.get(currentPageIndex));

        // 2. Update pagination dots
        updatePaginationDots();

    }

    /**
     * Refreshes the pagination dots indicator.
     */
    private void updatePaginationDots() {
        paginationDots.getChildren().clear();
        for (int i = 0; i < pages.size(); i++) {
            Circle dot = new Circle(6);
            if (i == currentPageIndex) {
                dot.setFill(Color.web("#3498db")); // Active color
                dot.setRadius(8); // Slightly larger
            } else {
                dot.setFill(Color.web("#bdc3c7")); // Inactive color
            }
            paginationDots.getChildren().add(dot);
        }
    }

    @FXML
    private void handleLeft() {
        if (currentPageIndex > 0) {
            currentPageIndex--;
        }else{
            currentPageIndex = pages.size() - 1;
        }
        updateView();
    }

    @FXML
    private void handleRight() {
        if (currentPageIndex < pages.size() - 1) {
            currentPageIndex++;
        }else {
            currentPageIndex = 0;
        }
        updateView();
    }

    @FXML
    private void handleBack() {
        com.controller.NavigationManager.getInstance().navigateTo("/com/view/main-menu-view.fxml");
    }
}
