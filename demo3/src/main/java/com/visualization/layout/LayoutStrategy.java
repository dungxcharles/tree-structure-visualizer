package com.visualization.layout;

import com.visualization.model.VisualTree;

public interface LayoutStrategy {
    void calculateLayout(VisualTree visualTree, double containerWidth, double containerHeight);
}
