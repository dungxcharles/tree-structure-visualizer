package com.visualization.view.animation;

import com.visualization.model.VisualNode;
import javafx.animation.Transition;
import javafx.util.Duration;

public class NodeMoveAnimation implements TreeAnimation {
    private final Transition transition;

    public NodeMoveAnimation(VisualNode node, double targetX, double targetY, double durationMs) {

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
}
