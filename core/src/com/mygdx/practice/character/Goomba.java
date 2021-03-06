package com.mygdx.practice.character;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.practice.Config;
import com.mygdx.practice.model.FixtureUserData;
import com.mygdx.practice.model.GoombaBodyData;
import com.mygdx.practice.util.ZoomHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Nick, 2020/3/12
 */
public class Goomba implements Character {
    private Animation<TextureRegion> runAnimation;
    private TextureRegion deadTexture;
    private float animationState = 0.1f;

    private Body body;
    private GoombaBodyData bodyData;
    private List<Fixture> fixtures = new ArrayList<>();

    public Goomba(World world, Texture texture, TiledMapTileMapObject tileMapObject, ZoomHelper zh) {
        Vector2 position = new Vector2(zh.scalePixel(tileMapObject.getX()), zh.scalePixel(tileMapObject.getY()));

        deadTexture = new TextureRegion(texture, 16 * 2, 16, 16, 16);
        Array<TextureRegion> tempFrame = new Array<>();
        for (int i = 0; i < 2; ++i) {
            tempFrame.add(new TextureRegion(texture, 16 * i, 16, 16, 16));
        }
        runAnimation = new Animation<>(1f, tempFrame, Animation.PlayMode.LOOP_PINGPONG);
        tempFrame.clear();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(position);

        body = world.createBody(bodyDef);

        bodyData = new GoombaBodyData(zh.scalePixel(16), zh.scalePixel(16));
        body.setUserData(bodyData);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.18f, 0.18f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.groupIndex = Config.FILER_DATA_ENEMY;

        Fixture fixture = body.createFixture(fixtureDef);
        fixtures.add(fixture);

        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(0.18f, -0.09f, 0.18f, 0.09f);
        fixtureDef.shape = edgeShape;

        fixture = body.createFixture(fixtureDef);
        fixture.setSensor(true);
        fixture.setUserData(new FixtureUserData("face"));

        edgeShape.set(-0.18f, -0.09f, -0.18f, 0.09f);
        fixture = body.createFixture(fixtureDef);
        fixture.setSensor(true);
        fixture.setUserData(new FixtureUserData("face"));
    }

    @Override
    public Body getBody() {
        return body;
    }

    @Override
    public void render(Camera camera, ZoomHelper zh, SpriteBatch spriteBatch) {
        if (bodyData == null) return;

        animationState += 0.1f;

        if (!bodyData.getLifeState().isAlive()) {
            body.setLinearVelocity(new Vector2(0, 0));
            bodyData.addDyingCountIfDying();
        } else if (bodyData.isFaceRight()) {
            body.setLinearVelocity(new Vector2(0.1f, 0));
        } else {
            body.setLinearVelocity(new Vector2(-0.1f, 0));
        }

        if (bodyData.getLifeState().isDead()) {
            return;
        }

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        Vector2 p = body.getPosition();
        float w = zh.scalePixel(deadTexture.getRegionWidth()) * (bodyData.isFaceRight() ? 1 : -1);
        float h = zh.scalePixel(deadTexture.getRegionHeight());
        float x = p.x - (bodyData.isFaceRight() ? 1 : -1) * zh.scalePixel(deadTexture.getRegionWidth()) / 2f;
        float y = p.y - zh.scalePixel(deadTexture.getRegionHeight()) / 2f;

        if (bodyData.getLifeState().isDying()) {
            spriteBatch.draw(deadTexture, x, y, w, h);
        } else {
            spriteBatch.draw(runAnimation.getKeyFrame(animationState, true), x, y, w, h);
        }

        spriteBatch.end();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void changeState(LifeState state) {
        bodyData.changeState(state);
    }

    @Override
    public LifeState getLifeState() {
        return bodyData.getLifeState();
    }
}
