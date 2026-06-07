package com.view.vis.animation;

import com.view.vis.viewmodel.VisualNode;
import javafx.animation.Transition;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class NodeColorAnimation implements TreeAnimation {
    private final Transition transition;

    public NodeColorAnimation(VisualNode node, String fromColorHex, String toColorHex, double durationMs) {
        final Color startColor = Color.web(fromColorHex);
        final Color endColor = Color.web(toColorHex);

        this.transition = new Transition() {
            {
                setCycleDuration(Duration.millis(durationMs));
            }

            @Override
            protected void interpolate(double frac) {
                Color interpolated = startColor.interpolate(endColor, frac);
                String hex = String.format("#%02X%02X%02X",
                        (int) (interpolated.getRed() * 255),
                        (int) (interpolated.getGreen() * 255),
                        (int) (interpolated.getBlue() * 255));
                node.setColorHex(hex);
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
