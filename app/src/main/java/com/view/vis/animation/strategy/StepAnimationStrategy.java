package com.view.vis.animation.strategy;

import com.model.step.AnimationStep;
import com.view.vis.viewmodel.VisualTree;
import com.view.vis.animation.TreeAnimation;

import java.util.List;

public interface StepAnimationStrategy {
    List<TreeAnimation> createAnimations(AnimationStep step, VisualTree tree);
}
