package com.mygdx.practice.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

/**
 * Nick, 2020-02-04
 */
public final class UserController implements Disposable {
    private Texture mLeftAndRight;
    private Texture mJump;

    private Rectangle rectangleRight;
    private Rectangle rectangleLeft;

    private OrthographicCamera fixCamera = new OrthographicCamera();
    private SpriteBatch batch = new SpriteBatch();
    private List<OnTouchListener> touchListeners = new ArrayList<>();


    public UserController(String pathOfLeftAndRight, String pathOfJump) {
        mLeftAndRight = new Texture(pathOfLeftAndRight);
        mJump = new Texture(pathOfJump);

        float x = -fixCamera.viewportWidth / 2 + mLeftAndRight.getWidth() + 24;
        float y = -fixCamera.viewportHeight / 2;
        rectangleRight = new Rectangle(x, y, mLeftAndRight.getWidth(), mLeftAndRight.getHeight());
        rectangleLeft = new Rectangle(x - mLeftAndRight.getWidth() - 12, y, mLeftAndRight.getWidth(), mLeftAndRight.getHeight());
    }

    private void touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 vec = new Vector3(screenX, screenY, 0);
        fixCamera.unproject(vec);
        if (rectangleRight.contains(vec.x, vec.y)) {
            for (OnTouchListener listener: touchListeners) {
                listener.onTouchRight(pointer);
            }
        } else if (rectangleLeft.contains(vec.x, vec.y)) {
            for (OnTouchListener listener: touchListeners) {
                listener.onTouchLeft(pointer);
            }
        }

    }

    public void render() {
        batch.setProjectionMatrix(fixCamera.combined);

        batch.begin();
// right
        batch.draw(mLeftAndRight,
                rectangleRight.x, rectangleRight.y, // 圖片左下角的點
                0, 0, // 操作的中心點???
                mLeftAndRight.getWidth(), mLeftAndRight.getHeight(), // 渲染出來的寬高
                1f, 1f,
                0f,  // 逆時針旋轉 角度
                0, 0,  // 操作目標的起始點
                mLeftAndRight.getWidth(), mLeftAndRight.getHeight(), // 操作目標的寬高
                false, false);
// left
        batch.draw(mLeftAndRight,
                -fixCamera.viewportWidth / 2 + mLeftAndRight.getWidth() + 12, rectangleRight.y, // 圖片左下角的點
                0, mLeftAndRight.getHeight() / 2, // 操作的中心點???
                mLeftAndRight.getWidth(), mLeftAndRight.getHeight(), // 渲染出來的寬高
                1f, 1f,
                180f,  // 逆時針旋轉 角度
                0, 0,  // 操作目標的起始點
                mLeftAndRight.getWidth(), mLeftAndRight.getHeight(), // 操作目標的寬高
                false, false);

// jump
        batch.draw(mJump,
                fixCamera.viewportWidth / 2 - 60, -fixCamera.viewportHeight / 2 + 12,
                0, 0,
                mJump.getWidth(), mJump.getHeight(),
                0.09f, 0.09f,
                0f,
                0, 0,
                mJump.getWidth(), mJump.getHeight(),
                false, false);

        batch.end();

        if (Gdx.input.isTouched()) {
            touchDown(Gdx.app.getInput().getX(), Gdx.app.getInput().getY(), 0, 0);
        }
    }

    public void addOnTouchListener(OnTouchListener listener) {
        touchListeners.add(listener);
    }

    public void setCameraViewport(float width, float height) {
        fixCamera = new OrthographicCamera(width, height);
        float x = -fixCamera.viewportWidth / 2 + mLeftAndRight.getWidth() + 24;
        float y = -fixCamera.viewportHeight / 2;
        rectangleRight = new Rectangle(x, y, mLeftAndRight.getWidth(), mLeftAndRight.getHeight());
        rectangleLeft = new Rectangle(x - mLeftAndRight.getWidth() - 12, y, mLeftAndRight.getWidth(), mLeftAndRight.getHeight());
//        fixCamera.viewportWidth = width;
//        fixCamera.viewportHeight = height;
    }


    @Override
    public void dispose() {
        batch.dispose();
    }

    public interface OnTouchListener {
        void onTouchRight(int pointer);
        void onTouchLeft(int pointer);
        void onJump(int pointer);
    }

}
