package com.mygdx.practice.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Nick, 2020-02-06
 */
public class CameraHelper {
    public static void lookAt(Camera camera, Vector2 target, Vector2 panRange, Rectangle cameraBound) {
        float diffX = target.x - camera.position.x;
        float diffY = target.y - camera.position.y;
        float moveX = 0;
        float moveY = 0;
//        Gdx.app.log("cameraHelper", String.format("camera: (%s, %s), target: (%s, %s)", camera.position.x, camera.position.y, target.x, target.y));

        if (diffX > panRange.x) { // target x 超出 追蹤 邊界
            moveX = diffX - panRange.x;
//            Gdx.app.log("cameraHelper", "out of x");
        } else if (diffX < -panRange.x) {
//            Gdx.app.log("cameraHelper", "out of x");
            moveX = diffX + panRange.x;
        }
        if (diffY > panRange.y) { // target y 超出 追蹤 邊界
//            Gdx.app.log("cameraHelper", "out of y");
            moveY = diffY - panRange.y;
        } else if (diffY < -panRange.y) {
//            Gdx.app.log("cameraHelper", "out of y");
            moveY = diffY + panRange.y;
        }

        float newX = camera.position.x + moveX;
//        Gdx.app.log("cameraHelper", String.format("new x:%s, bound x:[%s, %s]", newX, cameraBound.x, cameraBound.x + cameraBound.getWidth()));

        if (cameraBound.x < newX && cameraBound.x + cameraBound.getWidth() > newX) {
            camera.translate(moveX, 0, 0);
        }
        float newY = camera.position.y + moveY;
        if (cameraBound.y < newY && cameraBound.y + cameraBound.getHeight() > newY) {
            camera.translate(0, moveY, 0);
        }
    }
}
