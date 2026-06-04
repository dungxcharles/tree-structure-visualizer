package com.view.vis.animation.strategy;

import com.model.step.AnimationStep;
import com.model.vis.VisualTree;
import com.view.vis.animation.TreeAnimation;

import java.util.List;

/**
 * Interface defining the strategy for generating TreeAnimations from an AnimationStep.
 */
public interface StepAnimationStrategy {
    
    /**
     * Generates a list of animations for the given step.
     *
     * @param step The recorded step containing type, node value, and message.
     * @param tree The visual tree to manipulate or query.
     * @return A list of TreeAnimations to be played sequentially or in parallel.
     */
    List<TreeAnimation> createAnimations(AnimationStep step, VisualTree tree);
}
