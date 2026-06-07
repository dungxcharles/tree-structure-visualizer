package com.view.vis.animation;

import com.view.vis.viewmodel.VisualEdge;
import javafx.animation.Transition;
import javafx.util.Duration;

public class EdgeTraversalAnimation implements TreeAnimation {
    private final Transition transition;

    public EdgeTraversalAnimation(VisualEdge edge, String highlightColor, double durationMs) {
        if (edge != null) {
            edge.setHighlightColor(highlightColor);
            edge.setHighlightProgress(0.0);
        }

        this.transition = new Transition() {
            {
                setCycleDuration(Duration.millis(durationMs));
            }

            @Override
            protected void interpolate(double frac) {
                if (edge != null) {
                    edge.setHighlightProgress(frac);
                }
            }
        };

        this.transition.setOnFinished(e -> {
            if (edge != null) {
                edge.setHighlightProgress(0.0);
            }
        });
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
