package com.mygdx.practice.model;

/**
 * Nick, 2020/3/12
 */
public interface CharacterLifeState {
    void changeState(LifeState state);
    LifeState getLifeState();

    enum LifeState {
        ALIVE, DYING, DEAD;

        public boolean isDead() {
            return this == DEAD;
        }

        public boolean isAlive() {
            return this == ALIVE;
        }

        public boolean isDying() {
            return this == DYING;
        }
    }
}
