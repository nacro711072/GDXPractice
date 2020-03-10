package com.mygdx.practice.model;

import com.badlogic.gdx.maps.MapProperties;

public class BrickData extends FixtureUserData {
    public boolean isMarioHeadContact = false;
    public boolean isMarioBodyContact = false;
    private MapProperties properties;

    public BrickData(MapProperties properties) {
        super("brick");
        this.properties = properties;
    }

    public boolean isMarioHitBrick() {
        return isMarioBodyContact && isMarioHeadContact;
    }

    public boolean isBreakable() {
        return properties.get("breakable", Boolean.class);
    }

    public MapProperties getProperties() {
        return properties;
    }
}
