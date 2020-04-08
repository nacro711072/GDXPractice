package com.mygdx.practice.model;

import com.badlogic.gdx.Gdx;

/**
 * Nick, 2020/3/12
 */
public class GoombaBodyData implements CharacterLifeState {
    public boolean faceRight = false; // false: 左, true: 右

    private int dyingCount = 0;
    private static final int UPPER_BOUND_OF_DYING = 10;
    private CharacterLifeState.LifeState lifeState = LifeState.ALIVE;


    @Override
    public void changeState(LifeState state) {
        if (lifeState == LifeState.ALIVE && state == LifeState.DYING) {
            lifeState = state;
            Gdx.app.log("goomba", "life: " + lifeState);
        }
    }

    @Override
    public LifeState getLifeState() {
        return lifeState;
    }

    public void addDyingCountIfDying() {
        if (lifeState != LifeState.DYING) return;
        if (++dyingCount >= UPPER_BOUND_OF_DYING) {
            lifeState = LifeState.DEAD;
        }
    }
}
