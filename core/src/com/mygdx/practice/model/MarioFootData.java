package com.mygdx.practice.model;

import com.badlogic.gdx.Gdx;

import java.util.HashSet;
import java.util.Set;

public class MarioFootData extends FixtureUserData {
    private Set<FixtureUserData> contactTarget = new HashSet<>();
    public MarioFootData(String type) {
        super(type);
    }

    public void addContact(FixtureUserData target) {
        synchronized (this) {
            if(contactTarget.add(target)) {
                Gdx.app.log("foot", "addContact, hasContact: " + hasContactTarget());
            }
        }
    }

    public void removeContact(FixtureUserData target) {
        synchronized (this) {
            if (contactTarget.remove(target)) {
                Gdx.app.log("foot", "removeContact, hasContact: " + hasContactTarget());
            }
        }
    }

    public boolean hasContactTarget() {
        return contactTarget.size() != 0;
    }
}
