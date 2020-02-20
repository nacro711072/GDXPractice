package com.mygdx.practice.character;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.practice.MarioWorld;
import com.mygdx.practice.model.FixtureUserData;
import com.mygdx.practice.model.MarioBodyData;

import java.util.List;

/**
 * Nick, 2020-02-13
 */
public class Mario implements Character {
    private MarioWorld.CharacterId id;
    private Body body;
    private List<Fixture> fixtures;

    public Mario(World world, MarioWorld.CharacterId id) {
        this.id = id;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.18f, 0.33f);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 0.9f;
        fdef.friction = 0f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.linearDamping = 0.1f;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(2, 5);
        bodyDef.fixedRotation = true;

        body = world.createBody(bodyDef);
        body.setUserData(new MarioBodyData());
        Fixture marioBodyF = body.createFixture(fdef);
        marioBodyF.setUserData(new FixtureUserData("mario_body"));

        shape.setAsBox(0.18f, 0.1f, new Vector2(0, -0.33f), 0);
        Fixture f = body.createFixture(fdef);
        FixtureUserData footUserData = new FixtureUserData("mario_foot");
        f.setUserData(footUserData);
    }

    @Override
    public MarioWorld.CharacterId getId() {
        return id;
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public List<Fixture> getFixtures() {
        return fixtures;
    }
}
