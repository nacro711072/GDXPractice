package com.mygdx.practice.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.practice.Config;
import com.mygdx.practice.character.mario.MarioTextureRepository;
import com.mygdx.practice.util.ZoomHelper;

public class MarioBodyData implements CharacterLifeState, BodyData {
    public boolean faceRight = true; // false: 左, true: 右
    public boolean invincible = false;

    private MarioBodyState marioBodyState = MarioBodyState.smallMario;
    private MarioTextureRepository marioTextureRepository;


    private LifeState lifeState = LifeState.ALIVE;
    private short dyingCount = 0;
    private static final int UPPER_BOUND_OF_DYING = 10;

    private MarioActionState state = MarioActionState.JUMP;
    private MarioActionState preState = MarioActionState.JUMP;

    private ZoomHelper zh;

    public MarioBodyData(MarioTextureRepository textureRepository, ZoomHelper zh) {
        marioTextureRepository = textureRepository;
        this.zh = zh;
    }

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
                Gdx.app.log("mario", "onContact goomba");
                if (marioBodyState.isSmallState()) {
                    marioBody.setLinearVelocity(new Vector2(0, 0));

                    for (Fixture f : marioBody.getFixtureList()) {
                        Filter filter = f.getFilterData();
                        filter.groupIndex = Config.FILER_DATA_ENEMY;
                        f.setFilterData(filter);
                    }

                    goDie();
                } else if (marioBodyState.isBigState()) {
                    if (invincible) return;

                    marioBody.setLinearVelocity(new Vector2(0, 0));
                    changeMarioBodyState(false);

                    for (Fixture f : marioBody.getFixtureList()) {
                        Filter filter = f.getFilterData();
                        filter.groupIndex = Config.FILER_DATA_ENEMY;
                        f.setFilterData(filter);
                    }

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
            Gdx.app.log("mario", "changeMarioBodyState: growing up");
            marioBodyState = marioBodyState.getNextState();
        } else {
            marioBodyState = marioBodyState.getPreState();
            invincible = true;

            Gdx.app.log("mario", "changeMarioBodyState: growing down & invincible -> true");
        }
    }

    private void goDie() {
        if (invincible) return;
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

    @Override
    public float getBodyWidth() {
        return zh.scalePixel(marioTextureRepository.getWidth(marioBodyState));
    }

    @Override
    public float getBodyHeight() {
        return zh.scalePixel(marioTextureRepository.getHeight(marioBodyState));
    }
}
