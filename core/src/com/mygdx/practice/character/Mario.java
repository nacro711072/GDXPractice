package com.mygdx.practice.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.practice.MarioWorld;
import com.mygdx.practice.component.UserController;
import com.mygdx.practice.model.BrickData;
import com.mygdx.practice.model.FixtureUserData;
import com.mygdx.practice.model.MarioBodyData;
import com.mygdx.practice.model.MarioFootData;
import com.mygdx.practice.model.MarioState;
import com.mygdx.practice.util.ZoomHelper;

import java.util.List;

/**
 * Nick, 2020-02-13
 */
public class Mario implements Character,
        UserController.TouchListener,
        ContactListener {
    private MarioWorld.CharacterId id;
    private Body body;
    private MarioBodyData bodyData;
    private MarioFootData footUserData;
    private List<Fixture> fixtures;

    private Texture marioSheet;
    private TextureRegion marioStandTexture;
    private TextureRegion marioJumpTexture;
    private Animation<TextureRegion> runAnimation;
    private SpriteBatch batch = new SpriteBatch();
//    private Sprite sprite = new Sprite();

    private float animationState = 0.1f;

    public Mario(World world, MarioWorld.CharacterId id) {
        marioSheet = new Texture("mario_sheet.png");
        marioStandTexture = new TextureRegion(marioSheet, 1, 1, 16, 32);
        marioJumpTexture = new TextureRegion(marioSheet, 1 + 5 * 17, 1, 16, 32);

        Array<TextureRegion> tempFrame = new Array<>();
        for (int i = 1; i < 4; ++i) {
            tempFrame.add(new TextureRegion(marioSheet, 1 + 17 * i, 1, 16, 32));
        }
        runAnimation = new Animation<>(1f, tempFrame, Animation.PlayMode.LOOP_PINGPONG);
        tempFrame.clear();

        this.id = id;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.18f, 0.38f);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 0.9f;
        fdef.friction = 0f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.linearDamping = 0.1f;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(2, 5);
        bodyDef.fixedRotation = true;

        bodyData = new MarioBodyData();

        body = world.createBody(bodyDef);
        body.setUserData(bodyData);

        Fixture marioBodyF = body.createFixture(fdef);
        marioBodyF.setUserData(new FixtureUserData("mario_body"));

        shape.setAsBox(0.16f, 0.001f, new Vector2(0, -0.38f), 0);
        Fixture f = body.createFixture(fdef);
        footUserData = new MarioFootData("mario_foot");
        f.setUserData(footUserData);
        f.setSensor(true);

        shape.setAsBox(0.16f, 0.001f, new Vector2(0, 0.38f), 0);
        Fixture head = body.createFixture(fdef);
        head.setUserData(new FixtureUserData("mario_head"));
        head.setSensor(true);
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

    public void preRender() {
        if (!footUserData.hasContactTarget() && body.getLinearVelocity().y <= 0) {
            Gdx.app.log("mario", "has No Contact, mario state: " + bodyData.getState());
            bodyData.changeState(MarioState.FALLING);
        } else {
            Gdx.app.log("mario", "hasContact, mario state: " + bodyData.getState());
            // TODO: 2020-02-29  
            if ((bodyData.getState() == MarioState.JUMP || bodyData.getState() == MarioState.FALLING) && body.getLinearVelocity().y <= 0) {
                bodyData.changeState(MarioState.STAND);
            }
        }
//        if (bodyData.getState() == MarioState.RUN) {
//            if (body.getLinearVelocity().y < -0.1) {
//                bodyData.changeState(MarioState.FALLING);
//            }
//        }
    }

    @Override
    public void render(Camera camera, ZoomHelper zh) {
        animationState = (bodyData.getState() == bodyData.getPreState() ? animationState + 0.1f : 0.1f);
//        Gdx.app.log("mario", "animationState: " + animationState);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        Vector2 p = body.getPosition();
        MarioBodyData userData = (MarioBodyData) body.getUserData();
//        sprite.setRegion(textureRegion);
        float w = zh.scalePixel(marioStandTexture.getRegionWidth()) * (userData.faceRight ? 1 : -1);
        float h = zh.scalePixel(marioStandTexture.getRegionHeight());
        float x = p.x - (userData.faceRight ? 1 : -1) * zh.scalePixel(marioStandTexture.getRegionWidth()) / 2f;
        float y = p.y - zh.scalePixel(marioStandTexture.getRegionHeight()) / 2f;

        // LEFT
        switch (bodyData.getState()) {
            case RUN:
                TextureRegion textureRegion = runAnimation.getKeyFrame(animationState, true);
                batch.draw(textureRegion,
                        x, y,
                        w, h
                );
                break;
            case JUMP:
                batch.draw(marioJumpTexture, x, y, w, h);
                break;

            case STAND:
            default:
                batch.draw(marioStandTexture, x, y, w, h);
                break;
        }
        // right

        batch.end();

    }

    @Override
    public void dispose() {
        batch.dispose();
        marioSheet.dispose();
    }

    @Override
    public void onTouchRight(int pointer) {
//        Body marioBody = body;
        if (body.getLinearVelocity().x < 0.4) {
            body.applyLinearImpulse(new Vector2(0.004f, 0), body.getWorldCenter(), true);
//            MarioBodyData userData = ((MarioBodyData) marioBody.getUserData());
            bodyData.faceRight = true;
            bodyData.changeState(MarioState.RUN);
        }
    }

    @Override
    public void onTouchLeft(int pointer) {
//        Body marioBody = body;
        if (body.getLinearVelocity().x > -0.4) {
            body.applyLinearImpulse(new Vector2(-0.004f, 0), body.getWorldCenter(), true);
            bodyData.faceRight = false;
            bodyData.changeState(MarioState.RUN);
        }
    }

    @Override
    public void onJump(int pointer) {

        Gdx.app.log("mario", "onJump");
        MarioState marioState = bodyData.getState();
        if (marioState != MarioState.JUMP && marioState != MarioState.FALLING) {
            body.applyLinearImpulse(new Vector2(0, 0.4f), body.getWorldCenter(), true);
            bodyData.changeState(MarioState.JUMP);
        }
    }

    @Override
    public void onNoAction() {
        if (bodyData.getState() != MarioState.JUMP) {
            bodyData.changeState(MarioState.STAND);
        }
    }

    @Override
    public void beginContact(Contact contact) {
        Gdx.app.log("mario", "beginContact");
        FixtureUserData dataA = ((FixtureUserData) contact.getFixtureA().getUserData());
        FixtureUserData dataB = ((FixtureUserData) contact.getFixtureB().getUserData());

        if (dataA != null && dataA.type.equals("mario_foot")) {
            ((MarioFootData) dataA).addContact(dataB);
        } else if (dataB != null && dataB.type.equals("mario_foot")) {
            ((MarioFootData) dataB).addContact(dataA);
        }
        if (dataA == null || dataB == null) return;

        checkBrickContactEvent(dataA, dataB, true);
        checkBrickContactEvent(dataB, dataA, true);
    }

    @Override
    public void endContact(Contact contact) {
        Gdx.app.log("mario", "endContact");
        FixtureUserData dataA = ((FixtureUserData) contact.getFixtureA().getUserData());
        FixtureUserData dataB = ((FixtureUserData) contact.getFixtureB().getUserData());
        if (dataA != null && dataA.type.equals("mario_foot")) {
            ((MarioFootData) dataA).removeContact(dataB);

        } else if (dataB != null && dataB.type.equals("mario_foot")) {
            ((MarioFootData) dataB).removeContact(dataA);
        }

        if (dataA == null || dataB == null) return;

        checkBrickContactEvent(dataA, dataB, false);
        checkBrickContactEvent(dataB, dataA, false);

    }

    private void checkBrickContactEvent(FixtureUserData dataA, FixtureUserData dataB, boolean isContact) {
        if (dataA.type.equals("brick")) {
            if (dataB.type.equals("mario_head")) {
                ((BrickData) dataA).isMarioHeadContact = isContact;
            } else if (dataB.type.equals("mario_body")) {
                ((BrickData) dataA).isMarioBodyContact = isContact;
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
