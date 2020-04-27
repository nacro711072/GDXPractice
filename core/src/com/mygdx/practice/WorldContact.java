package com.mygdx.practice;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.practice.model.BrickData;
import com.mygdx.practice.model.CharacterLifeState;
import com.mygdx.practice.model.FaceState;
import com.mygdx.practice.model.FixtureUserData;
import com.mygdx.practice.model.InteractiveWithMario;
import com.mygdx.practice.model.MarioBodyData;
import com.mygdx.practice.model.MarioFootData;

public class WorldContact implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        FixtureUserData dataA = ((FixtureUserData) contact.getFixtureA().getUserData());
        FixtureUserData dataB = ((FixtureUserData) contact.getFixtureB().getUserData());

//        馬力歐 踩踏敵人事件
        checkMarioStampEvent(contact.getFixtureA(), contact.getFixtureB());
        checkMarioStampEvent(contact.getFixtureB(), contact.getFixtureA());

        //Goomba
        checkGoombaEvent(contact.getFixtureA(), contact.getFixtureB());
        checkGoombaEvent(contact.getFixtureB(), contact.getFixtureA());

        if (dataA == null || dataB == null) return;

        checkBrickContactEvent(contact.getFixtureA(), contact.getFixtureB(), true);
        checkBrickContactEvent(contact.getFixtureB(), contact.getFixtureA(), true);

    }

    private void checkGoombaEvent(Fixture fixtureA, Fixture fixtureB) {
        if (fixtureA == null) return;
        FixtureUserData dataA = (FixtureUserData) fixtureA.getUserData();
        FixtureUserData dataB = (FixtureUserData) fixtureB.getUserData();
        Body bodyB = fixtureB.getBody();

        if (dataA != null && dataA.type.equals("face")) {
            Object bodyData = fixtureA.getBody().getUserData();

            if (bodyData instanceof InteractiveWithMario && dataB != null && dataB.type.equals("mario_body")) {
                MarioBodyData marioBodyData = (MarioBodyData) bodyB.getUserData();
                if (!(bodyData instanceof CharacterLifeState) || ((CharacterLifeState) bodyData).getLifeState().isAlive()) {
                    marioBodyData.onContact((InteractiveWithMario) bodyData, bodyB);
                }
            } else if (bodyData instanceof FaceState){
                checkFaceEvent((FaceState) bodyData);
            }
        }

    }

    private void checkFaceEvent(FaceState faceState) {
        if (faceState == null) return;
        faceState.changeFaceDirection();
    }

    private void checkMarioStampEvent(Fixture fixtureA, Fixture fixtureB) {
        FixtureUserData dataA = (FixtureUserData) fixtureA.getUserData();
        if (dataA == null || !dataA.type.equals("mario_foot")) {
            return;
        }

        PolygonShape shape = (PolygonShape) fixtureA.getShape();
        Vector2 temp = new Vector2();
        shape.getVertex(0, temp);

        Body marioBody = fixtureA.getBody();
        Body otherBody = fixtureB.getBody();

        float marioBottom = marioBody.getPosition().y + temp.y;
        float otherY = otherBody.getPosition().y;

        if (marioBottom > otherY) {
            ((MarioFootData) dataA).addContact((FixtureUserData) fixtureB.getUserData());
            Object bodyUserData = otherBody.getUserData();
            if (bodyUserData instanceof CharacterLifeState) {
                ((CharacterLifeState) bodyUserData).changeState(CharacterLifeState.LifeState.DYING);
                marioBody.setLinearVelocity(new Vector2(0, 0.5f));
            }
        }

    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA == null || fixtureB == null) return;

        FixtureUserData dataA = ((FixtureUserData) fixtureA.getUserData());
        FixtureUserData dataB = ((FixtureUserData) fixtureB.getUserData());
        if (dataA != null && dataA.type.equals("mario_foot")) {
            PolygonShape shape = (PolygonShape) fixtureA.getShape();
            Vector2 temp = new Vector2();
            shape.getVertex(0, temp);

            float marioBottom = fixtureA.getBody().getPosition().y + temp.y;
            float otherY = fixtureB.getBody().getPosition().y;
            if (marioBottom > otherY) {
                ((MarioFootData) dataA).removeContact(dataB);
            }

        } else if (dataB != null && dataB.type.equals("mario_foot")) {
            PolygonShape shape = (PolygonShape)  fixtureB.getShape();
            Vector2 temp = new Vector2();
            shape.getVertex(0, temp);

            float marioBottom = fixtureB.getBody().getPosition().y + temp.y;
            float otherY = fixtureA.getBody().getPosition().y;
            if (marioBottom > otherY) {
                ((MarioFootData) dataB).removeContact(dataA);
            }

        }

        if (dataA == null || dataB == null) return;

        checkBrickContactEvent(fixtureA, fixtureB, false);
        checkBrickContactEvent(fixtureB, fixtureA, false);

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void checkBrickContactEvent(Fixture fixtureA, Fixture fixtureB, boolean isContact) {
        if (fixtureA == null || fixtureB == null) return;
//        if (((FixtureUserData) fixtureA.getUserData()).type.contains("brick")) {
//            Gdx.app.log("test", "type name: " + ((FixtureUserData) fixtureA.getUserData()).type);
//        }

        if (((FixtureUserData) fixtureA.getUserData()).type.equals("brick_b")) {
            BrickData dataA = (BrickData) fixtureA.getBody().getUserData();
            FixtureUserData dataB = (FixtureUserData) fixtureB.getUserData();
            if (dataB.type.equals("mario_head")) {
                dataA.isMarioHeadContact = isContact;
            } else if (dataB.type.equals("mario_body")) {
                dataA.isMarioBodyContact = isContact;
            }
        }
    }

}
