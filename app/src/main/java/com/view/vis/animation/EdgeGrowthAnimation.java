package com.view.vis.animation;

import com.view.vis.viewmodel.VisualEdge;
import javafx.animation.Transition;
import javafx.util.Duration;

public class EdgeGrowthAnimation implements TreeAnimation {
    private final Transition transition;

    public EdgeGrowthAnimation(VisualEdge edge, double startProgress, double endProgress, double durationMs) {
        this.transition = new Transition() {
            {
                setCycleDuration(Duration.millis(durationMs));
            }

            @Override
            protected void interpolate(double frac) {
                if (edge != null) {
                    double progress = startProgress + (endProgress - startProgress) * frac;
                    edge.setProgress(progress);
                }
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
