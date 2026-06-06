package com.view.vis.animation;

import com.view.vis.model.VisualNode;
import javafx.animation.Transition;
import javafx.util.Duration;

public class NodeMoveAnimation implements TreeAnimation {
    private final Transition transition;

    public NodeMoveAnimation(VisualNode node, double targetX, double targetY, double durationMs) {
        final double startX = node.getX();
        final double startY = node.getY();

        this.transition = new Transition() {
            {
                setCycleDuration(Duration.millis(durationMs));
            }

            @Override
            protected void interpolate(double frac) {
                node.setX(startX + (targetX - startX) * frac);
                node.setY(startY + (targetY - startY) * frac);
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
