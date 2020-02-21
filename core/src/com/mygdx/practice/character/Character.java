package com.mygdx.practice.character;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.practice.MarioWorld;
import com.mygdx.practice.util.ZoomHelper;

import java.util.List;

/**
 * Nick, 2020-02-20
 */
public interface Character {
    MarioWorld.CharacterId getId();
    Body getBody();
    List<Fixture> getFixtures();

    void render(Camera camera, ZoomHelper zh);

    void dispose();
}