package com.mygdx.practice.model;

import com.badlogic.gdx.Gdx;

/**
 * Nick, 2020/3/12
 */
public class GoombaBodyData implements CharacterLifeState, FaceState, InteractiveWithMario, BodyData {
    private boolean faceRight = false; // false: 左, true: 右
    private float width;
    private float height;

    private int dyingCount = 0;
    private static final int UPPER_BOUND_OF_DYING = 10;
    private CharacterLifeState.LifeState lifeState = LifeState.ALIVE;

    public GoombaBodyData(float width, float height) {
        this.width = width;
        this.height = height;
    }

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

    @Override
    public boolean isFaceRight() {
        return faceRight;
    }

    @Override
    public void changeFaceDirection() {
        faceRight = !faceRight;
    }

    @Override
    public Who getWho() {
        return Who.Goomba;
    }

    @Override
    public void onContactMario() {
        return; //nothing
    }

    @Override
    public float getBodyWidth() {
        return width;
    }

    @Override
    public float getBodyHeight() {
        return height;
    }
}
