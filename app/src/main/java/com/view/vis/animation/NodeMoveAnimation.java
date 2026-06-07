package com.view.vis.animation;

import com.view.vis.viewmodel.VisualNode;
import javafx.animation.Transition;
import javafx.util.Duration;

public class NodeMoveAnimation implements TreeAnimation {
    private final Transition transition;

    private double startX;
    private double startY;
    private boolean initialized = false;

    public NodeMoveAnimation(VisualNode node, double targetX, double targetY, double durationMs) {
        this.transition = new Transition() {
            {
                setCycleDuration(Duration.millis(durationMs));
            }

            @Override
            protected void interpolate(double frac) {
                if (frac == 0.0 || !initialized) {
                    startX = node.getX();
                    startY = node.getY();
                    initialized = true;
                }
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
        initialized = false;
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
