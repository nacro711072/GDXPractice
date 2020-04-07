package com.mygdx.practice.character.mario;


/**
 * Nick, 2020/4/7
 */
public class MarioDeadAnimation {
    private float totalTime = 4;
    private float currentTime = 0;

    private float trajectory(float time) {
        if (time < 1) return 0;
        else return - (time - 2) * (time - 2) + 1;
    }

    public float getNextY() {
        if (currentTime < totalTime) {
            currentTime += 0.025f;
            return trajectory(currentTime);
        } else {
            return trajectory(currentTime);
        }
    }

    public boolean isTimeEnd() {
        return currentTime >= totalTime;
    }
}
