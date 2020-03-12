package com.mygdx.practice.model;

/**
 * Nick, 2020/3/12
 */
public class GoombaBodyData implements Death {
    public boolean faceRight = false; // false: 左, true: 右
    private boolean hasDead = false;

    @Override
    public void dead() {
        hasDead = true;
    }

    @Override
    public boolean isDead() {
        return hasDead;
    }
}
