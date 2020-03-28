package com.mygdx.practice;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
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
        if (dataA != null && dataA.type.equals("mario_foot")) {
            float marioBottom = contact.getFixtureA().getBody().getPosition().y - 0.38f;
            float otherY = contact.getFixtureB().getBody().getPosition().y;
            if (marioBottom > otherY) {
                ((MarioFootData) dataA).addContact(dataB);
            }

            Object otherBodyData = contact.getFixtureB().getBody().getUserData();
            if (otherBodyData instanceof CharacterLifeState) {
                if (marioBottom > otherY) {
                    ((CharacterLifeState) otherBodyData).changeState(CharacterLifeState.LifeState.DYING);
                }
            }
        } else if (dataB != null && dataB.type.equals("mario_foot")) {
            float marioBottom = contact.getFixtureB().getBody().getPosition().y - 0.38f;
            float otherY = contact.getFixtureA().getBody().getPosition().y;
            if (marioBottom > otherY) {
                ((MarioFootData) dataB).addContact(dataA);
            }


            Object otherBodyData = contact.getFixtureA().getBody().getUserData();
            if (otherBodyData instanceof CharacterLifeState) {
                if (marioBottom > otherY) {
                    ((CharacterLifeState) otherBodyData).changeState(CharacterLifeState.LifeState.DYING);
                }
            }
        }

        if (dataA == null || dataB == null) return;

        checkBrickContactEvent(dataA, dataB, true);
        checkBrickContactEvent(dataB, dataA, true);

        if (dataA.type.equals("monster_face") && dataB.type.equals("mario_body")) {
            ((MarioBodyData) contact.getFixtureB().getBody().getUserData()).changeState(CharacterLifeState.LifeState.DYING);
        } else if (dataB.type.equals("monster_face") && dataA.type.equals("mario_body")) {
            ((MarioBodyData) contact.getFixtureB().getBody().getUserData()).changeState(CharacterLifeState.LifeState.DYING);
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
                bodyData = ((GoombaBodyData) contact.getFixtureA().getBody().getUserData());
                bodyData.faceRight = !bodyData.faceRight;
            }
        }

    }

    @Override
    public void endContact(Contact contact) {
        FixtureUserData dataA = ((FixtureUserData) contact.getFixtureA().getUserData());
        FixtureUserData dataB = ((FixtureUserData) contact.getFixtureB().getUserData());
        if (dataA != null && dataA.type.equals("mario_foot")) {
            float marioBottom = contact.getFixtureA().getBody().getPosition().y - 0.38f;
            float otherY = contact.getFixtureB().getBody().getPosition().y;
            if (marioBottom > otherY) {
                ((MarioFootData) dataA).removeContact(dataB);
            }

        } else if (dataB != null && dataB.type.equals("mario_foot")) {
            float marioBottom = contact.getFixtureB().getBody().getPosition().y - 0.38f;
            float otherY = contact.getFixtureA().getBody().getPosition().y;
            if (marioBottom > otherY) {
                ((MarioFootData) dataB).removeContact(dataA);
            }

        }

        if (dataA == null || dataB == null) return;

        checkBrickContactEvent(dataA, dataB, false);
        checkBrickContactEvent(dataB, dataA, false);

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

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

}
