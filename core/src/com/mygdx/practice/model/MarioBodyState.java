package com.mygdx.practice.model;

public enum MarioBodyState {
    smallMario, bigMario, superMario;

    public boolean isSmallState() {
        return this == smallMario;
    }

    public boolean isBigState() {
        return this == bigMario;
    }

}
