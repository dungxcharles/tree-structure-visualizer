package com.view.vis.animation;

import com.model.vis.VisualEdge;
import com.model.vis.VisualNode;
import com.model.vis.VisualTree;
import javafx.animation.Transition;
import javafx.util.Duration;

public class FadeAnimation implements TreeAnimation {
    private final Transition transition;

    public FadeAnimation(VisualNode node, VisualTree tree, double startOpacity, double endOpacity, double durationMs) {
        VisualEdge parentEdge = findIncomingEdge(node, tree);

        node.setOpacity(startOpacity);

        if (parentEdge != null) {
            parentEdge.setOpacity(startOpacity);
        }

        final VisualEdge edge = parentEdge;

        this.transition = new Transition() {
            {
                setCycleDuration(Duration.millis(durationMs));
            }

            @Override
            protected void interpolate(double frac) {
                double opacity = startOpacity + (endOpacity - startOpacity) * frac;
                node.setOpacity(opacity);
                if (edge != null) {
                    edge.setOpacity(opacity);
                }
            }
        };
    }

    private VisualEdge findIncomingEdge(VisualNode node, VisualTree tree) {
        if (tree == null)
            return null;
        for (VisualEdge edge : tree.getEdges()) {
            if (edge.getTarget().equals(node)) {
                return edge;
            }
        }
        return null;
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
