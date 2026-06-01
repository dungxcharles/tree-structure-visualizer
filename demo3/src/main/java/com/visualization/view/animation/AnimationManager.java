package com.visualization.view.animation;

import java.util.List;

public class AnimationManager {
    public AnimationManager() {
    }

    public void playSequential(List<TreeAnimation> animations) {
        if (animations == null || animations.isEmpty())
            return;

        for (int i = 0; i < animations.size() - 1; i++) {
            TreeAnimation next = animations.get(i + 1);
            animations.get(i).setOnFinished(() -> next.play());
        }
        animations.get(0).play();
    }

    public void playParallel(List<TreeAnimation> animations) {
        if (animations == null || animations.isEmpty())
            return;

        for (TreeAnimation anim : animations) {
            anim.play();
        }
    }
}
