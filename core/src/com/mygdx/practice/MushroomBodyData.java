package com.mygdx.practice;

import com.badlogic.gdx.Gdx;
import com.mygdx.practice.model.BodyData;
import com.mygdx.practice.model.FaceState;
import com.mygdx.practice.model.InteractiveWithMario;

/**
 * Nick, 2020/4/21
 */
public class MushroomBodyData implements FaceState, InteractiveWithMario, BodyData {
    public boolean faceRight = true; // false: 左, true: 右
    public boolean hasEat = false;

    private float width;
    private float height;

    public MushroomBodyData(float width, float height) {
        this.width = width;
        this.height = height;
    }

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

    @Override
    public float getBodyWidth() {
        return width;
    }

    @Override
    public float getBodyHeight() {
        return height;
    }
}
