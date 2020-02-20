package com.mygdx.practice.character;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.practice.MarioWorld;

import java.util.List;

/**
 * Nick, 2020-02-20
 */
public interface Character {
    MarioWorld.CharacterId getId();
    Body getBody();
    List<Fixture> getFixtures();
}
