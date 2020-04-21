package com.mygdx.practice;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.practice.model.FixtureUserData;
import com.mygdx.practice.util.ZoomHelper;

/**
 * Nick, 2020/4/20
 */
public class Mushroom {

    private Body body;
    private MushroomBodyData bodyData = new MushroomBodyData();

    private Fixture fixture;
    private FixtureDef fixtureDef;

    private TextureRegion texture;


    private boolean arise = true;
    private int ariseIteration = 0;
    private int ariseIterationLimit = 10;
    private float moveSpeed = 0.2f;
    private float boxRadius;

    public Mushroom(World world, Texture texture, Vector2 position, ZoomHelper zh) {
        this.texture = new TextureRegion(texture, 0, 0, 16 ,16);
        boxRadius = zh.scalePixel(16) / 2;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(position);
        body = world.createBody(bodyDef);
        body.setUserData(bodyData);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(boxRadius, boxRadius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 5;
        fixtureDef.shape = shape;

        this.fixtureDef = fixtureDef;

    }

    public void preRender(ZoomHelper zh) {
        if (body == null) return;

        if (ariseIteration++ > ariseIterationLimit) {
            arise = false;
        }

        float eachY = zh.scalePixel(16) / ariseIterationLimit;

        if (arise) {
            body.setTransform(body.getPosition().add(0, eachY), 0);
            return;
        }

        if (fixture == null) {
            createFixture();
        }

        Vector2 velocity = body.getLinearVelocity();
        velocity.x = moveSpeed * (bodyData.faceRight ? 1 : -1);
        body.setLinearVelocity(velocity);
    }

    private void createFixture() {
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureUserData("mushroom"));

        EdgeShape shape = new EdgeShape();
        shape.set(-boxRadius, -boxRadius, -boxRadius, boxRadius);

        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureUserData("face"));

        shape.set(boxRadius, -boxRadius, boxRadius, boxRadius);
        fixtureDef.isSensor = true;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(new FixtureUserData("face"));

    }

    public void render(Camera camera, ZoomHelper zh, SpriteBatch spriteBatch) {
        if (body == null) return;

        Vector2 p = body.getPosition();
        float w = zh.scalePixel(texture.getRegionWidth()) * (bodyData.faceRight ? 1 : -1);
        float h = zh.scalePixel(texture.getRegionHeight());
        float x = p.x - (bodyData.faceRight ? 1 : -1) * zh.scalePixel(texture.getRegionWidth()) / 2f;
        float y = p.y - zh.scalePixel(texture.getRegionHeight()) / 2f;

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        spriteBatch.draw(texture, x, y, w, h);

        spriteBatch.end();

    }
}
