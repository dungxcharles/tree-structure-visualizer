package com.view.vis.animation;

public interface TreeAnimation {
    void play();
    void pause();
    void stop();
    void setOnFinished(Runnable action);
}
