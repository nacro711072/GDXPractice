package com.mygdx.practice.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.practice.model.PressModel;
import com.mygdx.practice.util.ZoomHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Nick, 2020-02-04
 */
public final class UserController implements Disposable, InputProcessor {
    private Texture mLeftAndRight;
    private Texture mJump;

    private Rectangle rectangleRight;
    private Rectangle rectangleLeft;
    private Rectangle rectangleJump;

    private PressModel isLeftPress = new PressModel();
    private PressModel isRightPress = new PressModel();
    private PressModel isJumpPress = new PressModel();

    private OrthographicCamera fixCamera = new OrthographicCamera();
    private SpriteBatch batch = new SpriteBatch();
    private List<OnTouchListener> touchListeners = new ArrayList<>();

    private ZoomHelper zh = new ZoomHelper(1);

    public UserController(String pathOfLeftAndRight, String pathOfJump) {
        this(pathOfLeftAndRight, pathOfJump, null);
    }

    public UserController(String pathOfLeftAndRight, String pathOfJump, ZoomHelper zh) {
        if (zh != null) {
            this.zh = zh;
        }
        mLeftAndRight = new Texture(pathOfLeftAndRight);
        mJump = new Texture(pathOfJump);

        updateRectangle();
    }

    private void updateRectangle() {
        float x = zh.scalePixel(-fixCamera.viewportWidth / 2 + mLeftAndRight.getWidth() + 24);
        float y = zh.scalePixel(-fixCamera.viewportHeight / 2) + 12;
        rectangleRight = new Rectangle(x, y, zh.scalePixel(mLeftAndRight.getWidth()), zh.scalePixel(mLeftAndRight.getHeight()));
        rectangleLeft = new Rectangle(x - zh.scalePixel(mLeftAndRight.getWidth()) - 12, y, zh.scalePixel(mLeftAndRight.getWidth()), zh.scalePixel(mLeftAndRight.getHeight()));
        rectangleJump = new Rectangle(zh.scalePixel(fixCamera.viewportWidth / 2 - 60), zh.scalePixel(-fixCamera.viewportHeight / 2 + 12), zh.scalePixel(mJump.getWidth() * 0.07f), zh.scalePixel(mJump.getHeight() * 0.07f));
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 vec = new Vector3(screenX, screenY, 0);
        fixCamera.unproject(vec);
        if (rectangleRight.contains(vec.x, vec.y)) {
            isRightPress.isPress = true;
            isRightPress.pointer = pointer;
        } else if (rectangleLeft.contains(vec.x, vec.y)) {
            isLeftPress.isPress = true;
            isLeftPress.pointer = pointer;
        } else if (rectangleJump.contains(vec.x, vec.y)) {
            isJumpPress.isPress = true;
            isJumpPress.pointer = pointer;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (isRightPress.pointer == pointer) {
            isRightPress.isPress = false;
            isRightPress.pointer = -1;
        } else if (isLeftPress.pointer == pointer) {
            isLeftPress.isPress = false;
            isLeftPress.pointer = -1;
        } else if (isJumpPress.pointer == pointer) {
            isJumpPress.isPress = false;
            isJumpPress.pointer = -1;
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void render() {
        if (zh == null) {
            zh = new ZoomHelper(1);
        }
        batch.setProjectionMatrix(fixCamera.combined);

        batch.begin();
// right
        batch.draw(mLeftAndRight,
                rectangleRight.x, rectangleRight.y, // 圖片左下角的點
                0, 0, // 操作的中心點???
                mLeftAndRight.getWidth(), mLeftAndRight.getHeight(), // 渲染出來的寬高
                1, 1,
                0f,  // 逆時針旋轉 角度
                0, 0,  // 操作目標的起始點
                mLeftAndRight.getWidth(), mLeftAndRight.getHeight(), // 操作目標的寬高
                false, false);
// left
        batch.draw(mLeftAndRight,
                -fixCamera.viewportWidth / 2 + mLeftAndRight.getWidth() + 12, rectangleRight.y, // 圖片左下角的點
                0, mLeftAndRight.getHeight() / 2, // 操作的中心點???
                mLeftAndRight.getWidth(), mLeftAndRight.getHeight(), // 渲染出來的寬高
                zh.scalePixel(), zh.scalePixel(),
                180f,  // 逆時針旋轉 角度
                0, 0,  // 操作目標的起始點
                mLeftAndRight.getWidth(), mLeftAndRight.getHeight(), // 操作目標的寬高
                false, false);

// jump
        batch.draw(mJump,
                fixCamera.viewportWidth / 2 - 60, -fixCamera.viewportHeight / 2 + 24,
                0, 0,
                mJump.getWidth(), mJump.getHeight(),
                zh.scalePixel(0.07f), zh.scalePixel(0.07f),
                0f,
                0, 0,
                mJump.getWidth(), mJump.getHeight(),
                false, false);

        batch.end();

        for (OnTouchListener listener: touchListeners) {
            if (isRightPress.isPress) {
                listener.onTouchRight(1);
            }
            if (isLeftPress.isPress) {
                listener.onTouchLeft(1);
            }
            if (isJumpPress.isPress) {
                listener.onJump(1);
            }
        }
    }

    public void addOnTouchListener(OnTouchListener listener) {
        touchListeners.add(listener);
    }

    public void setCameraViewport(float width, float height) {
        fixCamera = new OrthographicCamera(width, height);
        updateRectangle();
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
