package com.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreditWindow {

    @FXML
    private ImageView imageView;

    @FXML
    private Button btnLeft;

    @FXML
    private Button btnRight;

    @FXML
    private Button btnBack;

    @FXML
    private HBox paginationHBox;

    private final List<Image> images = new ArrayList<>();
    private int currentIndex = 0;
    private double xOffset;

    @FXML
    public void initialize() {
        // Load your group information images here.
        // Example:
        images.add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/view/member1.png"))));
        images.add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/view/member2.png"))));
        images.add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/view/member3.png"))));
        
        // Note: Make sure the image paths are correct and files exist in resources.
        
        updateUI();
    }

    @FXML
    private void handleLeft() {
        if (images.isEmpty()) return;
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = images.size() - 1; // Wrap to end
        }
        updateUI();
    }

    @FXML
    private void handleRight() {
        if (images.isEmpty()) return;
        if (currentIndex < images.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0; // Wrap to beginning
        }
        updateUI();
    }

    @FXML
    private void handleBack() {
        // Logic to return to the previous view
        System.out.println("Back button clicked. Implement navigation logic here.");
        // Example: 
        // SceneManager.switchTo("hello-view.fxml");
    }

    @FXML
    private void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
    }

    @FXML
    private void handleMouseReleased(MouseEvent event) {
        double xDelta = event.getSceneX() - xOffset;
        // Threshold of 50 pixels to trigger a slide
        if (Math.abs(xDelta) > 50) {
            if (xDelta > 0) {
                // Swiped right -> go to previous image
                handleLeft();
            } else {
                // Swiped left -> go to next image
                handleRight();
            }
        }
    }

    private void updateUI() {
        if (!images.isEmpty()) {
            imageView.setImage(images.get(currentIndex));
        }

        updatePagination();
        
        // Buttons always enabled for infinite looping
        btnLeft.setDisable(false);
        btnRight.setDisable(false);
    }

    private void updatePagination() {
        paginationHBox.getChildren().clear();
        for (int i = 0; i < images.size(); i++) {
            Circle dot = new Circle(10);
            dot.setStroke(Color.BLACK);
            dot.setStrokeWidth(2);
            if (i == currentIndex) {
                dot.setFill(Color.WHITE); // Highlighted dot
            } else {
                dot.setFill(Color.TRANSPARENT); // Normal dot
            }
            paginationHBox.getChildren().add(dot);
        }
    }

    /**
     * Helper method for the user to add images programmatically if needed.
     */
    public void setImages(List<Image> newImages) {
        this.images.clear();
        this.images.addAll(newImages);
        this.currentIndex = 0;
        updateUI();
    }
}
