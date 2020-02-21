package com.mygdx.practice.model;

public class MarioBodyData {
    public boolean faceRight = true; // false: 左, true: 右
    private MarioState state = MarioState.JUMP;
    private MarioState preState = MarioState.JUMP;

    public void changeState(MarioState newState) {
        if (state == MarioState.STAND) {
            preState = state;
            state = newState;
        } else if (state == MarioState.JUMP && newState == MarioState.STAND) {
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

    public MarioState getState() {
        return state;
    }

    public MarioState getPreState() {
        return preState;
    }
}
