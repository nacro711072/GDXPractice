package com.mygdx.practice.model;

public enum MarioBodyState {
    smallMario, bigMario, superMario;

    public boolean isSmallState() {
        return this == smallMario;
    }

    public boolean isBigState() {
        return this == bigMario;
    }

    public MarioBodyState getNextState() {
        switch (this) {
            case smallMario:
                return bigMario;
            case bigMario:
            case superMario:
                return superMario;
        }
        return null;
    }

    public MarioBodyState getPreState() {
        switch (this) {
            case smallMario:
            case bigMario:
                return smallMario;
            case superMario:
                return bigMario;
        }
        return null;
    }
}
