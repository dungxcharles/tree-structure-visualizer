package com.view.vis.animation;

import com.view.vis.viewmodel.VisualNode;
import javafx.animation.Transition;
import javafx.util.Duration;

public class FadeAnimation implements TreeAnimation {
    private final Transition transition;

    public FadeAnimation(VisualNode node, double startOpacity, double endOpacity, double durationMs) {
        this.transition = new Transition() {
            {
                setCycleDuration(Duration.millis(durationMs));
            }

            @Override
            protected void interpolate(double frac) {
                double opacity = startOpacity + (endOpacity - startOpacity) * frac;
                node.setOpacity(opacity);
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
