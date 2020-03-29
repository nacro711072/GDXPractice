package com.mygdx.practice.model;

import com.badlogic.gdx.Gdx;

public class MarioBodyData implements CharacterLifeState {
    public boolean faceRight = true; // false: 左, true: 右
    private MarioBodyState marioBodyState = MarioBodyState.smallMario;

    private LifeState lifeState = LifeState.ALIVE;
    private short dyingCount = 0;
    private static final int UPPER_BOUND_OF_DYING = 10;

    private MarioActionState state = MarioActionState.JUMP;
    private MarioActionState preState = MarioActionState.JUMP;

    public void changeState(MarioActionState newState) {
        synchronized (this) {
            if (getState() == newState) {
                preState = state;
                return;
            }

            if (state == MarioActionState.STAND) {
                Gdx.app.log("mario", String.format("changeState: stand -> %s", newState));

                preState = state;
                state = newState;
            } else if ((state == MarioActionState.JUMP || state == MarioActionState.FALLING) && newState == MarioActionState.STAND) {
                Gdx.app.log("mario", String.format("changeState: jump -> %s", newState));
                preState = state;
                state = newState;
            } else if (state == MarioActionState.RUN) {
                preState = state;
                state = newState;
            } else if (state == MarioActionState.SQUAT && (newState == MarioActionState.STAND || newState == MarioActionState.JUMP)) {
                preState = state;
                state = newState;
            }
        }
    }

    public MarioActionState getState() {
        return state;
    }

    public MarioActionState getPreState() {
        return preState;
    }

    @Override
    public void changeState(LifeState state) {
        if (marioBodyState == MarioBodyState.smallMario && lifeState.isAlive() && state.isDying()) {
            lifeState = state;
        }
    }

    public void addDyingCountIfDying() {
        if (getLifeState().isDying()) {
            if (++dyingCount ==  UPPER_BOUND_OF_DYING) {
                lifeState = LifeState.DEAD;
            }
        }
    }

    @Override
    public LifeState getLifeState() {
        return lifeState;
    }

    public MarioBodyState getMarioBodyState() {
        return marioBodyState;
    }
}
