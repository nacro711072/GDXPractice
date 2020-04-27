package com.mygdx.practice;

import com.badlogic.gdx.Gdx;
import com.mygdx.practice.model.FaceState;
import com.mygdx.practice.model.InteractiveWithMario;

/**
 * Nick, 2020/4/21
 */
public class MushroomBodyData implements FaceState, InteractiveWithMario {
    public boolean faceRight = true; // false: 左, true: 右
    public boolean hasEat = false;

    @Override
    public boolean isFaceRight() {
        return faceRight;
    }

    @Override
    public void changeFaceDirection() {
        faceRight = !faceRight;
    }

    @Override
    public Who getWho() {
        return Who.Mushroom;
    }

    @Override
    public void onContactMario() {
        hasEat = true;
    }
}
