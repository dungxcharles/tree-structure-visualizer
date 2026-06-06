package com.view.vis.layout;

import com.model.vis.VisualTree;

public interface LayoutStrategy {
    void calculateLayout(VisualTree visualTree, double containerWidth, double containerHeight);
}
