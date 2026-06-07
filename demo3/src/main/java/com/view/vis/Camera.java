package com.view.vis;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

/**
 * Camera encapsulates the mathematical state of the viewport.
 * It strictly adheres to the Single Responsibility Principle by
 * separating transformation logic from input handling.
 */
public class Camera {
    private Affine transform;

    private static final double MIN_SCALE = 0.2;
    private static final double MAX_SCALE = 5.0;

    public Camera() {
        this.transform = new Affine();
    }

    /**
     * Pans the camera by appending translation in screen-space coordinates.
     * @param deltaX The X distance to pan.
     * @param deltaY The Y distance to pan.
     */
    public void pan(double deltaX, double deltaY) {
        transform.prependTranslation(deltaX, deltaY);
    }

    /**
     * Zooms the camera by a specific factor towards a pivot point.
     * @param scaleFactor The multiplier for the current scale.
     * @param pivotX The X screen coordinate to zoom towards.
     * @param pivotY The Y screen coordinate to zoom towards.
     */
    public void zoom(double scaleFactor, double pivotX, double pivotY) {
        double currentScale = transform.getMxx();
        double newScale = currentScale * scaleFactor;

        // Clamp scaling
        if (newScale < MIN_SCALE) {
            scaleFactor = MIN_SCALE / currentScale;
        } else if (newScale > MAX_SCALE) {
            scaleFactor = MAX_SCALE / currentScale;
        }

        try {
            // Transform pivot from screen coordinates to local coordinates
            Point2D localPivot = transform.inverseTransform(pivotX, pivotY);
            transform.appendScale(scaleFactor, scaleFactor, localPivot.getX(), localPivot.getY());
        } catch (NonInvertibleTransformException e) {
            // Safely ignore if transform is not invertible
        }
    }

    /**
     * Resets the camera transformation back to its default identity state (no pan, no zoom).
     */
    public void reset() {
        this.transform.setToIdentity();
    }

    /**
     * Applies the camera's transformation to the given GraphicsContext.
     * @param gc The GraphicsContext to apply the transform to.
     */
    public void apply(GraphicsContext gc) {
        gc.save();
        gc.transform(transform);
    }

    /**
     * Restores the GraphicsContext back to its state before the camera was applied.
     * @param gc The GraphicsContext to restore.
     */
    public void restore(GraphicsContext gc) {
        gc.restore();
    }
}
