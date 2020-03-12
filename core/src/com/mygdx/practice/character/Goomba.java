package com.mygdx.practice.character;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.practice.util.ZoomHelper;

import java.util.List;

/**
 * Nick, 2020/3/12
 */
public class Goomba implements Character, ContactListener {

    @Override
    public Body getBody() {
        return null;
    }

    @Override
    public List<Fixture> getFixtures() {
        return null;
    }

    @Override
    public void render(Camera camera, ZoomHelper zh, SpriteBatch spriteBatch) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
