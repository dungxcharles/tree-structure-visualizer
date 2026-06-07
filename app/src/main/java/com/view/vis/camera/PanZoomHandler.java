package com.view.vis.camera;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * PanZoomHandler handles mouse and scroll input from the user
 * and translates it into mathematical operations on the Camera.
 */
public class PanZoomHandler {
    private final Camera camera;
    private final Runnable redrawCallback;

    private double lastMouseX;
    private double lastMouseY;

    private static final double ZOOM_FACTOR = 1.1;

    public PanZoomHandler(Canvas canvas, Camera camera, Runnable redrawCallback) {
        this.camera = camera;
        this.redrawCallback = redrawCallback;

        attachListeners(canvas);
    }

    private void attachListeners(Canvas canvas) {
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnScroll(this::handleScroll);
    }

    private void handleMousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown() || event.isSecondaryButtonDown() || event.isMiddleButtonDown()) {
            lastMouseX = event.getX();
            lastMouseY = event.getY();
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (event.isPrimaryButtonDown() || event.isSecondaryButtonDown() || event.isMiddleButtonDown()) {
            double deltaX = event.getX() - lastMouseX;
            double deltaY = event.getY() - lastMouseY;

            camera.pan(deltaX, deltaY);

            lastMouseX = event.getX();
            lastMouseY = event.getY();

            triggerRedraw();
        }
    }

    private void handleScroll(ScrollEvent event) {
        double scaleFactor = (event.getDeltaY() > 0) ? ZOOM_FACTOR : (1 / ZOOM_FACTOR);

        camera.zoom(scaleFactor, event.getX(), event.getY());

        triggerRedraw();
    }

    private void triggerRedraw() {
        if (redrawCallback != null) {
            redrawCallback.run();
        }
    }
}
