package com.mygdx.practice.model;

public class MarioBodyData {
    private MarioState state = MarioState.STAND;
    public boolean face = true; // false: 左, true: 右

    public void changeState(MarioState newState) {
        if (state == MarioState.STAND) {
            state = newState;
        } else if (state == MarioState.JUMP && newState == MarioState.STAND) {
            state = newState;
        } else if (state == MarioState.RUN) {
            state = newState;
        } else if (state == MarioState.SQUAT && (newState == MarioState.STAND || newState == MarioState.JUMP)) {
            state = newState;
        }
    }

    public MarioState getState() {
        return state;
    }
}
