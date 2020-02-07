package com.mygdx.practice.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Nick, 2020-02-06
 */
public class CameraHelper {
    public static void lookAt(Camera camera, Actor target, Vector2 range) {
        float absX = Math.abs(camera.position.x - target.getX());
        float absY = Math.abs(camera.position.y - target.getY());

        if (absX > range.x) {
            camera.translate(absX - range.x ,0 ,0);
        } else if (absY > range.y){
            camera.translate(absY - range.y ,0 ,0);
        }
    }
}
