package com.mygdx.practice;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.practice.model.BrickData;
import com.mygdx.practice.model.CharacterLifeState;
import com.mygdx.practice.model.FixtureUserData;
import com.mygdx.practice.model.GoombaBodyData;
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

        if (dataA == null || dataB == null) return;

        checkBrickContactEvent(contact.getFixtureA(), dataB, true);
        checkBrickContactEvent(contact.getFixtureB(), dataA, true);

        if (dataA.type.equals("monster_face") && dataB.type.equals("mario_body")) {
            Object bodyData = contact.getFixtureA().getBody().getUserData();
            if (bodyData instanceof CharacterLifeState &&
                    ((CharacterLifeState) bodyData).getLifeState().isAlive()) {
                ((MarioBodyData) contact.getFixtureB().getBody().getUserData()).onEnemyContact();
            }
        } else if (dataB.type.equals("monster_face") && dataA.type.equals("mario_body")) {
            Object bodyData = contact.getFixtureB().getBody().getUserData();
            if (bodyData instanceof CharacterLifeState &&
                    ((CharacterLifeState) bodyData).getLifeState().isAlive()) {

                ((MarioBodyData) contact.getFixtureA().getBody().getUserData()).onEnemyContact();
            }
        }

        //Goomba
        GoombaBodyData bodyData;
        if (dataA.type.equals("monster_face")) {
            if (dataB.type.equals("mario_body")) {
                // nothing
            } else {
                bodyData = ((GoombaBodyData) contact.getFixtureA().getBody().getUserData());
                bodyData.faceRight = !bodyData.faceRight;
            }
        }

        if (dataB.type.equals("monster_face")) {
            if (dataA.type.equals("mario_body")) {
                // nothing
            } else {
                bodyData = ((GoombaBodyData) contact.getFixtureB().getBody().getUserData());
                bodyData.faceRight = !bodyData.faceRight;
            }
        }

    }

    private void checkMarioStampEvent(Fixture fixtureA, Fixture fixtureB) {
        FixtureUserData dataA = (FixtureUserData) fixtureA.getUserData();
        if (dataA == null || !dataA.type.equals("mario_foot")) {
            return;
        }

        PolygonShape shape = (PolygonShape) fixtureA.getShape();
        Vector2 temp = new Vector2();
        shape.getVertex(0, temp);

        float marioBottom = fixtureA.getBody().getPosition().y + temp.y;
        float otherY = fixtureB.getBody().getPosition().y;
        if (marioBottom > otherY) {
            ((MarioFootData) dataA).addContact((FixtureUserData) fixtureB.getUserData());
            changeLifeState(fixtureB.getBody().getUserData());
        }

    }

    private void changeLifeState(Object bodyUserData) {
        if (bodyUserData instanceof CharacterLifeState) {
            ((CharacterLifeState) bodyUserData).changeState(CharacterLifeState.LifeState.DYING);
        }
    }

    @Override
    public void endContact(Contact contact) {
        FixtureUserData dataA = ((FixtureUserData) contact.getFixtureA().getUserData());
        FixtureUserData dataB = ((FixtureUserData) contact.getFixtureB().getUserData());
        if (dataA != null && dataA.type.equals("mario_foot")) {
            PolygonShape shape = (PolygonShape) contact.getFixtureA().getShape();
            Vector2 temp = new Vector2();
            shape.getVertex(0, temp);

            float marioBottom = contact.getFixtureA().getBody().getPosition().y + temp.y;
            float otherY = contact.getFixtureB().getBody().getPosition().y;
            if (marioBottom > otherY) {
                ((MarioFootData) dataA).removeContact(dataB);
            }

        } else if (dataB != null && dataB.type.equals("mario_foot")) {
            PolygonShape shape = (PolygonShape)  contact.getFixtureB().getShape();
            Vector2 temp = new Vector2();
            shape.getVertex(0, temp);

            float marioBottom = contact.getFixtureB().getBody().getPosition().y + temp.y;
            float otherY = contact.getFixtureA().getBody().getPosition().y;
            if (marioBottom > otherY) {
                ((MarioFootData) dataB).removeContact(dataA);
            }

        }

        if (dataA == null || dataB == null) return;

        checkBrickContactEvent(contact.getFixtureA(), dataB, false);
        checkBrickContactEvent(contact.getFixtureB(), dataA, false);

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void checkBrickContactEvent(Fixture fixtureA, FixtureUserData dataB, boolean isContact) {
        if (((FixtureUserData) fixtureA.getUserData()).type.contains("brick")) {
            Gdx.app.log("test", "type name: " + ((FixtureUserData) fixtureA.getUserData()).type);
        }
        if (((FixtureUserData) fixtureA.getUserData()).type.equals("brick_b")) {
            BrickData dataA = (BrickData) fixtureA.getBody().getUserData();
            if (dataB.type.equals("mario_head")) {
                dataA.isMarioHeadContact = isContact;
                Gdx.app.log("test", "isMarioHeadContact: " + isContact);
            } else if (dataB.type.equals("mario_body")) {
                dataA.isMarioBodyContact = isContact;
                Gdx.app.log("test", "isMarioBodyContact: " + isContact);
            }
        }
    }

}
