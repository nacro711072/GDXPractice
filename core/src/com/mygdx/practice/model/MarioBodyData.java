package com.mygdx.practice.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.practice.Config;

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
        if (marioBodyState.isSmallState()) {
            lifeState = state;
        }
    }

    public void onContact(InteractiveWithMario interactiveObject, Body marioBody) {
        switch (interactiveObject.getWho()) {
            case Goomba: {
                if (marioBodyState.isSmallState()) {
                    marioBody.setLinearVelocity(new Vector2(0, 0));

                    for (Fixture f : marioBody.getFixtureList()) {
                        Filter filter = f.getFilterData();
                        filter.groupIndex = Config.FILER_DATA_ENEMY;
                        f.setFilterData(filter);
                    }

                    goDie();
                } else if (marioBodyState.isBigState()) {
                    marioBody.setLinearVelocity(new Vector2(0, 0));
                    changeMarioBodyState(false);
                }
                break;
            }
            case Mushroom: {
                changeMarioBodyState(true);
                Gdx.app.log("mario", "onContact with mushroom");
                break;
            }
        }
        interactiveObject.onContactMario();
    }

    private void changeMarioBodyState(boolean up) {
        if (up) {
            marioBodyState = marioBodyState.getNextState();
        } else {
            marioBodyState = marioBodyState.getPreState();
        }
    }

    private void goDie() {
        if (lifeState.isAlive()) {
            lifeState = LifeState.DYING;
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
