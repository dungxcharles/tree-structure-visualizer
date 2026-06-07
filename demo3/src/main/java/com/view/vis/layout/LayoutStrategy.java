package com.view.vis.layout;

import com.view.vis.model.VisualTree;

public interface LayoutStrategy {
    void calculateLayout(VisualTree visualTree, double containerWidth, double containerHeight);
}
