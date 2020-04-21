package com.mygdx.practice;

import com.badlogic.gdx.Gdx;
import com.mygdx.practice.model.FaceState;

/**
 * Nick, 2020/4/21
 */
public class MushroomBodyData implements FaceState {
    public boolean faceRight = true; // false: 左, true: 右

    @Override
    public boolean isFaceRight() {
        return faceRight;
    }

    @Override
    public void changeFaceDirection() {
        faceRight = !faceRight;
    }
}
