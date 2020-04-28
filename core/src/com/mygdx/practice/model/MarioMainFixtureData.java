package com.mygdx.practice.model;

/**
 * Nick, 2020/4/27
 */
public class MarioMainFixtureData extends FixtureUserData {
    private MarioBodyState bodyState;

    public MarioMainFixtureData(MarioBodyState state) {
        super("mario_body");
        bodyState = state;
    }

    public MarioBodyState getBodyState() {
        return bodyState;
    }
}
