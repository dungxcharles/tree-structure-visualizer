package com.view.vis.animation;

import com.view.vis.model.VisualNode;
import javafx.animation.Transition;
import javafx.util.Duration;

public class NodeLabelAnimation implements TreeAnimation {
    private final Transition transition;

    public NodeLabelAnimation(VisualNode node, String newLabel, double durationMs) {
        this.transition = new Transition() {
            {
                setCycleDuration(Duration.millis(durationMs));
            }

            @Override
            protected void interpolate(double frac) {
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
