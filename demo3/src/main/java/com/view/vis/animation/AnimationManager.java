package com.view.vis.animation;

import java.util.List;

public class AnimationManager {

    private Runnable onAllFinished;

    public void setOnAllFinished(Runnable onAllFinished) {
        this.onAllFinished = onAllFinished;
    }

    public void playSequential(List<TreeAnimation> animations) {
        if (animations == null || animations.isEmpty()) {
            if (onAllFinished != null) {
                onAllFinished.run();
            }
            return;
        }

        for (int i = 0; i < animations.size() - 1; i++) {
            TreeAnimation next = animations.get(i + 1);
            animations.get(i).setOnFinished(() -> next.play());
        }

        animations.get(animations.size() - 1).setOnFinished(() -> {
            if (onAllFinished != null) {
                onAllFinished.run();
            }
        });

        animations.get(0).play();
    }

    public void playParallel(List<TreeAnimation> animations) {
        if (animations == null || animations.isEmpty()) {
            if (onAllFinished != null) {
                onAllFinished.run();
            }
            return;
        }

        for (TreeAnimation anim : animations) {
            anim.play();
        }
    }
}
