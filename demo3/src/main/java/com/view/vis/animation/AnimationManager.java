package com.view.vis.animation;

import java.util.List;

public class AnimationManager {

    private Runnable onAllFinished;

    public void setOnAllFinished(Runnable onAllFinished) {
        this.onAllFinished = onAllFinished;
    }

    private java.util.List<TreeAnimation> currentRunningAnimations = new java.util.ArrayList<>();
    private boolean isPaused = false;

    public void pause() {
        if (!isPaused) {
            isPaused = true;
            for (TreeAnimation anim : new java.util.ArrayList<>(currentRunningAnimations)) {
                anim.pause();
            }
        }
    }

    public void resume() {
        if (isPaused) {
            isPaused = false;
            for (TreeAnimation anim : new java.util.ArrayList<>(currentRunningAnimations)) {
                anim.play();
            }
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void playSequential(List<TreeAnimation> animations) {
        if (animations == null || animations.isEmpty()) {
            if (onAllFinished != null) {
                onAllFinished.run();
            }
            return;
        }

        isPaused = false;
        currentRunningAnimations.clear();

        for (int i = 0; i < animations.size() - 1; i++) {
            TreeAnimation current = animations.get(i);
            TreeAnimation next = animations.get(i + 1);
            
            Runnable originalOnFinished = null; // In case we need to preserve existing, though usually there isn't one
            
            current.setOnFinished(() -> {
                currentRunningAnimations.remove(current);
                currentRunningAnimations.add(next);
                if (!isPaused) {
                    next.play();
                }
            });
        }

        TreeAnimation last = animations.get(animations.size() - 1);
        last.setOnFinished(() -> {
            currentRunningAnimations.remove(last);
            if (onAllFinished != null) {
                onAllFinished.run();
            }
        });

        currentRunningAnimations.add(animations.get(0));
        if (!isPaused) {
            animations.get(0).play();
        }
    }

    public void playParallel(List<TreeAnimation> animations) {
        if (animations == null || animations.isEmpty()) {
            if (onAllFinished != null) {
                onAllFinished.run();
            }
            return;
        }

        isPaused = false;
        currentRunningAnimations.clear();
        currentRunningAnimations.addAll(animations);

        int[] finishedCount = {0};
        Runnable checkFinished = () -> {
            finishedCount[0]++;
            if (finishedCount[0] == animations.size()) {
                if (onAllFinished != null) {
                    onAllFinished.run();
                }
            }
        };

        for (TreeAnimation anim : animations) {
            anim.setOnFinished(() -> {
                currentRunningAnimations.remove(anim);
                checkFinished.run();
            });
            if (!isPaused) {
                anim.play();
            }
        }
    }
}
