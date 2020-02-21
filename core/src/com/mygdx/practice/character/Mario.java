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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.practice.MarioWorld;
import com.mygdx.practice.component.UserController;
import com.mygdx.practice.model.FixtureUserData;
import com.mygdx.practice.model.MarioBodyData;
import com.mygdx.practice.model.MarioState;
import com.mygdx.practice.util.ZoomHelper;

import java.util.List;

/**
 * Nick, 2020-02-13
 */
public class Mario implements Character, UserController.TouchListener {
    private MarioWorld.CharacterId id;
    private Body body;
    private MarioBodyData bodyData;
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

        bodyData = new MarioBodyData();

        body = world.createBody(bodyDef);
        body.setUserData(bodyData);
        Fixture marioBodyF = body.createFixture(fdef);
        marioBodyF.setUserData(new FixtureUserData("mario_body"));

        shape.setAsBox(0.16f, 0.1f, new Vector2(0, -0.33f), 0);
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
                        p.x, p.y,
                        w, h,
                        1, 1, 0f
                );
                break;
            case JUMP:
                batch.draw(marioJumpTexture, x, y, w, h);
                break;

            case STAND:
            default:
                batch.draw(marioStandTexture, x, y, p.x, p.y, w, h, 1, 1, 0f);
                break;
        }
//        if (userData.faceRight) {
//            batch.draw(marioStandTexture,
//                    x, y,
//                    marioStandTexture.getRegionWidth() * zh.scalePixel(), marioStandTexture.getRegionHeight() * zh.scalePixel()
//            );
//        } else {
//            batch.draw(marioStandTexture,
//                    p.x + zh.scalePixel(marioStandTexture.getRegionWidth() / 2f), p.y - zh.scalePixel(marioStandTexture.getRegionHeight() / 2f),
//                    p.x, p.y,
//                    -zh.scalePixel(marioStandTexture.getRegionWidth()), zh.scalePixel(marioStandTexture.getRegionHeight()),
//                    1, 1,
//                    0f);
//        }
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
        Body marioBody = body;
        if (marioBody.getLinearVelocity().x < 0.4) {
            marioBody.applyLinearImpulse(new Vector2(0.004f, 0), marioBody.getWorldCenter(), true);
            MarioBodyData userData = ((MarioBodyData) marioBody.getUserData());
            userData.faceRight = true;
            userData.changeState(MarioState.RUN);
        }
    }

    @Override
    public void onTouchLeft(int pointer) {
        Body marioBody = body;
        if (marioBody.getLinearVelocity().x > -0.4) {
            marioBody.applyLinearImpulse(new Vector2(-0.004f, 0), marioBody.getWorldCenter(), true);
            MarioBodyData userData = ((MarioBodyData) marioBody.getUserData());
            userData.faceRight = false;
            userData.changeState(MarioState.RUN);
        }
    }

    @Override
    public void onJump(int pointer) {
        Body marioBody = body;
        MarioBodyData userData = ((MarioBodyData) marioBody.getUserData());

        if (userData.getState() != MarioState.JUMP) {
            marioBody.applyLinearImpulse(new Vector2(0, 0.4f), marioBody.getWorldCenter(), true);
            userData.changeState(MarioState.JUMP);
        }
    }

    @Override
    public void onNoAction() {
        Body marioBody = body;
        MarioBodyData userData = ((MarioBodyData) marioBody.getUserData());

        if (userData.getState() != MarioState.JUMP) {
            userData.changeState(MarioState.STAND);
        }
    }
}
