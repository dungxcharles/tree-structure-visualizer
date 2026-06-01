package com.visualization.view.animation;

public interface TreeAnimation {
    void play();
    void pause();
    void stop();
    void setOnFinished(Runnable action);
}
