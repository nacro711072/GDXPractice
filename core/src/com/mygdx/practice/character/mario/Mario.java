package com.mygdx.practice.character.mario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.practice.Config;
import com.mygdx.practice.SimpleCounter;
import com.mygdx.practice.character.Character;
import com.mygdx.practice.component.UserController;
import com.mygdx.practice.model.FixtureUserData;
import com.mygdx.practice.model.MarioBodyData;
import com.mygdx.practice.model.MarioBodyState;
import com.mygdx.practice.model.MarioFootData;
import com.mygdx.practice.model.MarioActionState;
import com.mygdx.practice.model.MarioMainFixtureData;
import com.mygdx.practice.util.ZoomHelper;

import java.util.List;

/**
 * Nick, 2020-02-13
 */
public class Mario implements Character, UserController.TouchListener, SimpleCounter.Callback {

    private MarioTextureRepository marioTextureRepository = new MarioTextureRepository("mario_sheet.png");

    private Body body;
    private MarioBodyData bodyData;
    private MarioFootData footUserData;
    private MarioDeadAnimation marioDeadAnimation;
    private Fixture mainFixture;

    private float animationState = 0.1f;

    private SimpleCounter invincibleCounter = new SimpleCounter(120, this);

    public Mario(World world, ZoomHelper zh) {
        bodyData = new MarioBodyData(marioTextureRepository, zh);

        BodyDef bodyDef = new BodyDef();
        bodyDef.linearDamping = 0.1f;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(2, 4);
        bodyDef.fixedRotation = true;

        body = world.createBody(bodyDef);
        body.setUserData(bodyData);

        createFixtures();
    }

    public void createFixtures() {
        short filterGroup = bodyData.invincible ? Config.FILER_DATA_ENEMY : 0;
        Array<Fixture> fixtures = body.getFixtureList();

        Gdx.app.log("mario", "fixture list size: " + fixtures.size);
        if (fixtures.size != 0) {
            for (Fixture f: fixtures) {
                body.destroyFixture(f);
            }
            createFixtures();
            return;
        }
        MarioBodyState state = getMarioBodyState();
        float halfX = bodyData.getBodyWidth() / 2;
        float halfY = bodyData.getBodyHeight() / 2;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(halfX, halfY);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = state.isSmallState() ? 1f : 0.5f;
        fdef.friction = 0f;
        fdef.filter.groupIndex = filterGroup;

        mainFixture = body.createFixture(fdef);
        mainFixture.setUserData(new MarioMainFixtureData(state));

        shape.setAsBox(halfX, 0.001f, new Vector2(0, -halfY), 0);
        fdef.shape = shape;
        Fixture f = body.createFixture(fdef);
        footUserData = new MarioFootData("mario_foot");
        f.setUserData(footUserData);
        f.setSensor(true);

        shape.setAsBox(halfX / 2, 0.001f, new Vector2(0, halfY), 0);
        Fixture head = body.createFixture(fdef);
        head.setUserData(new FixtureUserData("mario_head"));
        head.setSensor(true);

    }

    @Override
    public Body getBody() {
        return body;
    }

    public void preRender(ZoomHelper zh) {
        if (bodyData == null || body == null || !getLifeState().isAlive()) return;

        if (bodyData.invincible) {
            invincibleCounter.count();
        }

        if (getMarioBodyState() != ((MarioMainFixtureData) mainFixture.getUserData()).getBodyState()) {
            Array<Fixture> fixtures = body.getFixtureList();
            for (Fixture f: fixtures) {
                body.destroyFixture(f);
            }

            createFixtures();
        }

        if (footUserData.hasContactTarget() &&
                (bodyData.getState() == MarioActionState.JUMP || bodyData.getState() == MarioActionState.FALLING) &&
                (body.getLinearVelocity().y <= 0.000001f || body.getLinearVelocity().y >= -0.000001f)) {
            bodyData.changeState(MarioActionState.STAND);
        } else if (!footUserData.hasContactTarget()) {
            bodyData.changeState(MarioActionState.FALLING);
        }
        if (body.getLinearVelocity().y > 0.000001f) {
            bodyData.changeState(MarioActionState.JUMP);
        }

        if (bodyData.getLifeState().isDying()) {
            bodyData.addDyingCountIfDying();
        }
    }

    @Override
    public void render(Camera camera, ZoomHelper zh, SpriteBatch spriteBatch) {

        if (bodyData == null || body == null) return;

        animationState = (bodyData.getState() == bodyData.getPreState() ? animationState + 0.1f : 0.1f);

        Vector2 p = body.getPosition();
        float w = bodyData.getBodyWidth() * (bodyData.faceRight ? 1 : -1);
        float h = bodyData.getBodyHeight();
        float x = p.x - (bodyData.faceRight ? 1 : -1) * bodyData.getBodyWidth() / 2f;
        float y = p.y - bodyData.getBodyHeight() / 2f;

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        TextureRegion textureRegion;
        if (getLifeState().isDying()) {
            if (marioDeadAnimation == null) {
                marioDeadAnimation = new MarioDeadAnimation();
            }

            if (marioDeadAnimation.isTimeEnd()) {
                changeState(LifeState.DEAD);
            }
            textureRegion = marioTextureRepository.getMarioDeadTexture(getMarioBodyState());
            y += marioDeadAnimation.getNextY();
            spriteBatch.draw(textureRegion,
                    x, y,
                    w, h
            );
            spriteBatch.end();
            return;
        }

        // LEFT
        switch (bodyData.getState()) {
            case RUN:
                textureRegion = marioTextureRepository.getMarioRunningTexture(getMarioBodyState(), animationState);
                spriteBatch.draw(textureRegion,
                        x, y,
                        w, h
                );
                break;
            case JUMP:
                textureRegion = marioTextureRepository.getMarioJumpTexture(getMarioBodyState());
                spriteBatch.draw(textureRegion, x, y, w, h);
                break;

            case STAND:
            default:
                textureRegion = marioTextureRepository.getMarioStandTexture(getMarioBodyState());
                spriteBatch.draw(textureRegion, x, y, w, h);
                break;
        }
        // right

        spriteBatch.end();

    }

    @Override
    public void dispose() {
        marioTextureRepository.dispose();
    }

    @Override
    public void onTouchRight(int pointer) {
        if (body == null || bodyData == null || !getLifeState().isAlive()) return;
        if (body.getLinearVelocity().x < 0.4) {
            body.applyLinearImpulse(new Vector2(0.004f, 0), body.getWorldCenter(), true);
        }
        bodyData.faceRight = true;
        bodyData.changeState(MarioActionState.RUN);
    }

    @Override
    public void onTouchLeft(int pointer) {
        if (body == null || bodyData == null || !getLifeState().isAlive()) return;
        if (body.getLinearVelocity().x > -0.4) {
            body.applyLinearImpulse(new Vector2(-0.004f, 0), body.getWorldCenter(), true);
        }
        bodyData.faceRight = false;
        bodyData.changeState(MarioActionState.RUN);
    }

    @Override
    public void onJump(int pointer) {
        if (body == null || bodyData == null || !getLifeState().isAlive()) return;

        MarioActionState marioActionState = bodyData.getState();
        if (marioActionState != MarioActionState.JUMP && marioActionState != MarioActionState.FALLING) {
            body.applyLinearImpulse(new Vector2(0, 0.275f), body.getWorldCenter(), true);
        }
    }

    @Override
    public void onNoAction() {
        if (body == null || bodyData == null) return;

        if (bodyData.getState() != MarioActionState.JUMP) {
            bodyData.changeState(MarioActionState.STAND);
        }
    }

    @Override
    public void changeState(LifeState state) {
        bodyData.changeState(state);
    }

    @Override
    public LifeState getLifeState() {
        return bodyData.getLifeState();
    }

    public MarioBodyState getMarioBodyState() {
        return bodyData.getMarioBodyState();
    }

    @Override
    public void onFinish() {
        Gdx.app.log("mario", "counter finish");
        bodyData.invincible = false;
        invincibleCounter.reset();

        for (Fixture f : body.getFixtureList()) {
            Filter filter = f.getFilterData();
            filter.groupIndex = 0;
            f.setFilterData(filter);
        }

    }
}