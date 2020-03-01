package com.mygdx.practice.model;

import com.badlogic.gdx.Gdx;

public class MarioBodyData {
    public boolean faceRight = true; // false: 左, true: 右
    private MarioState state = MarioState.JUMP;
    private MarioState preState = MarioState.JUMP;

    public void changeState(MarioState newState) {
        synchronized (this) {

            if (state == MarioState.STAND) {
                Gdx.app.log("mario", String.format("changeState: stand -> %s", newState));

                preState = state;
                state = newState;
            } else if ((state == MarioState.JUMP || state == MarioState.FALLING) && newState == MarioState.STAND) {
                Gdx.app.log("mario", String.format("changeState: jump -> %s", newState));
                preState = state;
                state = newState;
            } else if (state == MarioState.RUN) {
                preState = state;
                state = newState;
            } else if (state == MarioState.SQUAT && (newState == MarioState.STAND || newState == MarioState.JUMP)) {
                preState = state;
                state = newState;
            }
        }
    }

    public MarioState getState() {
        return state;
    }

    public MarioState getPreState() {
        return preState;
    }
}
