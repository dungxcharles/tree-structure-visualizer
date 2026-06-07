package com.view.vis.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleConsumer;

public class AnimationManager {

    private Runnable onAllFinished;
    private DoubleConsumer onProgressChanged;

    public void setOnAllFinished(Runnable onAllFinished) {
        this.onAllFinished = onAllFinished;
    }

    public void setOnProgressChanged(DoubleConsumer onProgressChanged) {
        this.onProgressChanged = onProgressChanged;
    }

    private List<TreeAnimation> currentRunningAnimations = new ArrayList<>();
    private List<TreeAnimation> allAnimations = new ArrayList<>();
    private boolean isPaused = false;

    public void setRate(double rate) {
        for (TreeAnimation anim : allAnimations) {
            anim.setRate(rate);
        }
    }

    public void pause() {
        if (!isPaused) {
            isPaused = true;
            for (TreeAnimation anim : new ArrayList<>(currentRunningAnimations)) {
                anim.pause();
            }
        }
    }

    public void resume() {
        if (isPaused) {
            isPaused = false;
            for (TreeAnimation anim : new ArrayList<>(currentRunningAnimations)) {
                anim.play();
            }
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void playSequential(List<TreeAnimation> animations) {
        this.allAnimations = new ArrayList<>(animations);
        if (animations == null || animations.isEmpty()) {
            if (onProgressChanged != null) {
                onProgressChanged.accept(1.0);
            }
            if (onAllFinished != null) {
                onAllFinished.run();
            }
            return;
        }

        isPaused = false;
        currentRunningAnimations.clear();

        int total = animations.size();
        if (onProgressChanged != null) {
            onProgressChanged.accept(0.0);
        }

        for (int i = 0; i < animations.size() - 1; i++) {
            TreeAnimation current = animations.get(i);
            TreeAnimation next = animations.get(i + 1);
            final double progress = (double) (i + 1) / total;

            current.setOnFinished(() -> {
                currentRunningAnimations.remove(current);
                currentRunningAnimations.add(next);
                if (onProgressChanged != null) {
                    onProgressChanged.accept(progress);
                }
                if (!isPaused) {
                    next.play();
                }
            });
        }

        TreeAnimation last = animations.get(animations.size() - 1);
        last.setOnFinished(() -> {
            currentRunningAnimations.remove(last);
            if (onProgressChanged != null) {
                onProgressChanged.accept(1.0);
            }
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
        this.allAnimations = new ArrayList<>(animations);
        if (animations == null || animations.isEmpty()) {
            if (onProgressChanged != null) {
                onProgressChanged.accept(1.0);
            }
            if (onAllFinished != null) {
                onAllFinished.run();
            }
            return;
        }

        isPaused = false;
        currentRunningAnimations.clear();
        currentRunningAnimations.addAll(animations);

        int total = animations.size();
        if (onProgressChanged != null) {
            onProgressChanged.accept(0.0);
        }

        int[] finishedCount = { 0 };
        Runnable checkFinished = () -> {
            finishedCount[0]++;
            if (onProgressChanged != null) {
                onProgressChanged.accept((double) finishedCount[0] / total);
            }
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
