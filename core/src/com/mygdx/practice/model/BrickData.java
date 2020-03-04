package com.mygdx.practice.model;

public class BrickData extends FixtureUserData {
    public boolean isMarioHeadContact = false;
    public boolean isMarioBodyContact = false;

    public BrickData() {
        super("brick");
    }

    public boolean isNeedDestory() {
        return isMarioBodyContact && isMarioHeadContact;
    }


}
