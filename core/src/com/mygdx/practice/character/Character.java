package com.mygdx.practice.character;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.practice.model.CharacterLifeState;
import com.mygdx.practice.util.ZoomHelper;

import java.util.List;

/**
 * Nick, 2020-02-20
 */
public interface Character extends CharacterLifeState {
    Body getBody();

    void render(Camera camera, ZoomHelper zh, SpriteBatch spriteBatch);

    void dispose();

}
