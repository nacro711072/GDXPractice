package com.mygdx.practice.character;

import com.badlogic.gdx.graphics.Camera;
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
import com.mygdx.practice.component.UserController;
import com.mygdx.practice.model.FixtureUserData;
import com.mygdx.practice.model.MarioBodyData;
import com.mygdx.practice.model.MarioFootData;
import com.mygdx.practice.model.MarioActionState;
import com.mygdx.practice.util.ZoomHelper;

import java.util.List;

/**
 * Nick, 2020-02-13
 */
public class Mario implements Character,
        UserController.TouchListener {
    private Body body;
    private MarioBodyData bodyData;
    private MarioFootData footUserData;
    private List<Fixture> fixtures;

    private MarioSheetHelper smallMario;
    private MarioSheetHelper bigMario;

    private float animationState = 0.1f;

    public Mario(World world, ZoomHelper zh) {
        smallMario = new MarioSheetHelper.Builder("mario_sheet.png")
                .setMargin(1)
                .setWidth(16)
                .setHeight(16)
                .setBeginY(33)
                .build();

        bigMario = new MarioSheetHelper.Builder("mario_sheet.png")
                .setMargin(1)
                .setWidth(16)
                .setHeight(32)
                .build();


        float halfX = zh.scalePixel(smallMario.getWidth()) / 2;
        float halfY = zh.scalePixel(smallMario.getHeight()) / 2;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(halfX, halfY);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1f;
        fdef.friction = 0f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.linearDamping = 0.1f;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(2, 4);
        bodyDef.fixedRotation = true;

        bodyData = new MarioBodyData();

        body = world.createBody(bodyDef);
        body.setUserData(bodyData);

        Fixture marioBodyF = body.createFixture(fdef);
        marioBodyF.setUserData(new FixtureUserData("mario_body"));

        shape.setAsBox(halfX, 0.001f, new Vector2(0, -halfY), 0);
//        EdgeShape edgeShape = new EdgeShape();
//        edgeShape.set(-halfX, -halfY, halfX, -halfY);
        fdef.shape = shape;
        Fixture f = body.createFixture(fdef);
        footUserData = new MarioFootData("mario_foot");
        f.setUserData(footUserData);
        f.setSensor(true);

        shape.setAsBox(halfX / 2, 0.001f, new Vector2(0, halfY), 0);
        Fixture head = body.createFixture(fdef);
        head.setUserData(new FixtureUserData("mario_head"));
        head.setSensor(true);

//        bodyDef.position.set(2, 5);
//        bodyDef.type = BodyDef.BodyType.StaticBody;
//        Body test = world.createBody(bodyDef);
//
//        edgeShape.set(-halfX, halfY, halfX, halfY);
//        test.createFixture(fdef).setUserData(new FixtureUserData("test"));

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
        if (bodyData == null || body == null) return;

//        if (!footUserData.hasContactTarget() && body.getLinearVelocity().y <= 0) {
//            Gdx.app.log("mario", "changeState: to falling");
//            bodyData.changeState(MarioState.FALLING);
//        } else {
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
//        }
        if (bodyData.getLifeState().isDying()) {
            bodyData.addDyingCountIfDying();
        }

    }

    @Override
    public void render(Camera camera, ZoomHelper zh, SpriteBatch spriteBatch) {
        if (bodyData == null || body == null) return;

        animationState = (bodyData.getState() == bodyData.getPreState() ? animationState + 0.1f : 0.1f);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        Vector2 p = body.getPosition();
        MarioBodyData userData = (MarioBodyData) body.getUserData();
        float w = zh.scalePixel(smallMario.getWidth()) * (userData.faceRight ? 1 : -1);
        float h = zh.scalePixel(smallMario.getHeight());
        float x = p.x - (userData.faceRight ? 1 : -1) * zh.scalePixel(smallMario.getWidth()) / 2f;
        float y = p.y - zh.scalePixel(smallMario.getHeight()) / 2f;

        // LEFT
        switch (bodyData.getState()) {
            case RUN:
                TextureRegion textureRegion = smallMario.getMarioRunningTexture(animationState);
                spriteBatch.draw(textureRegion,
                        x, y,
                        w, h
                );
                break;
            case JUMP:
                spriteBatch.draw(smallMario.getMarioJumpTexture(), x, y, w, h);
                break;

            case STAND:
            default:
                spriteBatch.draw(smallMario.getMarioStandTexture(), x, y, w, h);
                break;
        }
        // right

        spriteBatch.end();

    }

    @Override
    public void dispose() {
        smallMario.dispose();
    }

    @Override
    public void onTouchRight(int pointer) {
        if (body == null || bodyData == null) return;
        if (body.getLinearVelocity().x < 0.4) {
            body.applyLinearImpulse(new Vector2(0.004f, 0), body.getWorldCenter(), true);
        }
        bodyData.faceRight = true;
        bodyData.changeState(MarioActionState.RUN);
    }

    @Override
    public void onTouchLeft(int pointer) {
        if (body == null || bodyData == null) return;
        if (body.getLinearVelocity().x > -0.4) {
            body.applyLinearImpulse(new Vector2(-0.004f, 0), body.getWorldCenter(), true);
        }
        bodyData.faceRight = false;
        bodyData.changeState(MarioActionState.RUN);
    }

    @Override
    public void onJump(int pointer) {
        if (body == null || bodyData == null) return;

        MarioActionState marioActionState = bodyData.getState();
        if (marioActionState != MarioActionState.JUMP && marioActionState != MarioActionState.FALLING) {
            body.applyLinearImpulse(new Vector2(0, 0.3f), body.getWorldCenter(), true);
        }
    }

    @Override
    public void onNoAction() {
        if (body == null || bodyData == null) return;

        if (bodyData.getState() != MarioActionState.JUMP) {
            bodyData.changeState(MarioActionState.STAND);
        }
    }

//    @Override
//    public void beginContact(Contact contact) {
//        if (!getLifeState().isAlive()) {
//            return;
//        }
//
//        FixtureUserData dataA = ((FixtureUserData) contact.getFixtureA().getUserData());
//        FixtureUserData dataB = ((FixtureUserData) contact.getFixtureB().getUserData());
//
////        馬力歐 踩踏敵人事件
//        if (dataA != null && dataA.type.equals("mario_foot")) {
//            ((MarioFootData) dataA).addContact(dataB);
//
//            Object otherBodyData = contact.getFixtureB().getBody().getUserData();
//            if (otherBodyData instanceof CharacterLifeState) {
//                if (body.getPosition().y + 0.38f >= contact.getFixtureB().getBody().getPosition().y) {
//                    ((CharacterLifeState) otherBodyData).changeState(CharacterLifeState.LifeState.DYING);
//                }
//            }
//        } else if (dataB != null && dataB.type.equals("mario_foot")) {
//            ((MarioFootData) dataB).addContact(dataA);
//
//            Object otherBodyData = contact.getFixtureA().getBody().getUserData();
//            if (otherBodyData instanceof CharacterLifeState) {
//                if (body.getPosition().y + 0.38f >= contact.getFixtureB().getBody().getPosition().y) {
//                    ((CharacterLifeState) otherBodyData).changeState(CharacterLifeState.LifeState.DYING);
//                }
//            }
//        }
//
//        if (dataA == null || dataB == null) return;
//
//        checkBrickContactEvent(dataA, dataB, true);
//        checkBrickContactEvent(dataB, dataA, true);
//
//        if (dataA.type.equals("monster_face") && dataB.type.equals("mario_body")) {
//            bodyData.changeState(LifeState.DYING);
//        } else if (dataB.type.equals("monster_face") && dataA.type.equals("mario_body")) {
//            bodyData.changeState(LifeState.DYING);
//        }
//
//    }
//
//    @Override
//    public void endContact(Contact contact) {
//        FixtureUserData dataA = ((FixtureUserData) contact.getFixtureA().getUserData());
//        FixtureUserData dataB = ((FixtureUserData) contact.getFixtureB().getUserData());
//        if (dataA != null && dataA.type.equals("mario_foot")) {
//            ((MarioFootData) dataA).removeContact(dataB);
//        } else if (dataB != null && dataB.type.equals("mario_foot")) {
//            ((MarioFootData) dataB).removeContact(dataA);
//        }
//
//        if (dataA == null || dataB == null) return;
//
//        checkBrickContactEvent(dataA, dataB, false);
//        checkBrickContactEvent(dataB, dataA, false);
//
//    }
//
//    private void checkBrickContactEvent(FixtureUserData dataA, FixtureUserData dataB, boolean isContact) {
//        if (dataA.type.equals("brick")) {
//            if (dataB.type.equals("mario_head")) {
//                ((BrickData) dataA).isMarioHeadContact = isContact;
//            } else if (dataB.type.equals("mario_body")) {
//                ((BrickData) dataA).isMarioBodyContact = isContact;
//            }
//        }
//    }
//
//    @Override
//    public void preSolve(Contact contact, Manifold oldManifold) {
//    }
//
//    @Override
//    public void postSolve(Contact contact, ContactImpulse impulse) {
//
//    }

    @Override
    public void changeState(LifeState state) {
        bodyData.changeState(state);
    }

    @Override
    public LifeState getLifeState() {
        return bodyData.getLifeState();
    }
}