package com.visualization.view.animation;

import com.visualization.model.VisualNode;
import javafx.animation.Transition;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class NodeColorAnimation implements TreeAnimation {
    private final Transition transition;

    public NodeColorAnimation(VisualNode node, String fromColorHex, String toColorHex, double durationMs) {

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
