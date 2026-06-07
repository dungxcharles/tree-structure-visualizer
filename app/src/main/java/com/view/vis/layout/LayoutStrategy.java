package com.view.vis.layout;

import com.view.vis.viewmodel.VisualTree;

public interface LayoutStrategy {
    void calculateLayout(VisualTree visualTree, double containerWidth, double containerHeight);
}
