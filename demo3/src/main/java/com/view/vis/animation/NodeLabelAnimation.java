package com.view.vis.animation;

import com.model.vis.VisualNode;
import javafx.animation.Transition;
import javafx.util.Duration;

/**
 * Animation that changes a VisualNode's label (for successor-replace in BST
 * delete). Sets the new label at the start of the transition, then holds for
 * the specified duration so the user can see the change.
 */
public class NodeLabelAnimation implements TreeAnimation {
    private final Transition transition;

    public NodeLabelAnimation(VisualNode node, String newLabel, double durationMs) {
        this.transition = new Transition() {
            {
                setCycleDuration(Duration.millis(durationMs));
            }

            @Override
            protected void interpolate(double frac) {
                // Label is discrete (not continuous), so set it immediately
                node.setLabel(newLabel);
            }
        };
    }

    @Override
    public void play() {
        transition.play();
    }

    @Override
    public void pause() {
        transition.pause();
    }

    @Override
    public void stop() {
        transition.stop();
    }

    @Override
    public void setOnFinished(Runnable action) {
        transition.setOnFinished(e -> action.run());
    }

    @Override
    public void setRate(double rate) {
        transition.setRate(rate);
    }
}
