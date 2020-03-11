package com.mygdx.practice.wrapper;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import java.util.ArrayList;
import java.util.List;

/**
 * Nick, 2020-02-24
 */
public class MultiContactListener implements ContactListener {
    private List<ContactListener> listeners = new ArrayList<>();

    public MultiContactListener() { }

    public MultiContactListener(List<ContactListener> listenerList) {
        listeners.addAll(listenerList);
    }

    public void addContactListener(ContactListener listener) {
        listeners.add(listener);
    }

    @Override
    public void beginContact(Contact contact) {
        for (ContactListener listener: listeners) {
            listener.beginContact(contact);
        }
    }

    @Override
    public void endContact(Contact contact) {
        for (ContactListener listener: listeners) {
            listener.endContact(contact);
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        for (ContactListener listener: listeners) {
            listener.preSolve(contact, oldManifold);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        for (ContactListener listener: listeners) {
            listener.postSolve(contact, impulse);
        }
    }
}
