package com.mygdx.practice.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.List;

public class Arrow extends Texture implements InputProcessor {
    private OrthographicCamera fixCamera = new OrthographicCamera();
    private SpriteBatch batch = new SpriteBatch();
    private List<OnTouchListener> touchListeners = new ArrayList<>();

    public Arrow(String internalPath) {
        super(internalPath);
    }

    public Arrow(FileHandle file) {
        super(file);
    }

    public Arrow(FileHandle file, boolean useMipMaps) {
        super(file, useMipMaps);
    }

    public Arrow(FileHandle file, Pixmap.Format format, boolean useMipMaps) {
        super(file, format, useMipMaps);
    }

    public Arrow(Pixmap pixmap) {
        super(pixmap);
    }

    public Arrow(Pixmap pixmap, boolean useMipMaps) {
        super(pixmap, useMipMaps);
    }

    public Arrow(Pixmap pixmap, Pixmap.Format format, boolean useMipMaps) {
        super(pixmap, format, useMipMaps);
    }

    public Arrow(int width, int height, Pixmap.Format format) {
        super(width, height, format);
    }

    public Arrow(TextureData data) {
        super(data);
    }

    protected Arrow(int glTarget, int glHandle, TextureData data) {
        super(glTarget, glHandle, data);
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
        float x = -fixCamera.viewportWidth / 2 + getWidth() + 24;
        float y = -fixCamera.viewportHeight / 2;
        Rectangle rectangleRight = new Rectangle(x, y, getWidth(), getHeight());
        Rectangle rectangleLeft = new Rectangle(x - getWidth() - 12, y, getWidth(), getHeight());
        Vector3 vec = new Vector3(screenX, screenY, 0);
//		Vector3 size = camera.project(new Vector3(arror.getWidth(), arror.getHeight(), 0));
        fixCamera.unproject(vec);
//        System.out.println(String.format("vec: x1->%f, y1->%f", vec.x, vec.y));
        if (rectangleRight.contains(vec.x, vec.y)) {
            for (OnTouchListener listener: touchListeners) {
                listener.onTouchRight(pointer);
            }
        } else if (rectangleLeft.contains(vec.x, vec.y)) {
            for (OnTouchListener listener: touchListeners) {
                listener.onTouchLeft(pointer);
            }
        }

//        System.out.println(String.format("rectangle: x1->%f, y1->%f, x2->%f, y2->%f", rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height));
//        System.out.println(String.format("touchDown: x->%d, y->%d, p->%d, b->%d", screenX, screenY, pointer, button));
        return false;

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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

    public void setCameraViewport(float width, float height) {
        fixCamera = new OrthographicCamera(width, height);
//        fixCamera.viewportWidth = width;
//        fixCamera.viewportHeight = height;
    }

    public void render() {
        batch.setProjectionMatrix(fixCamera.combined);

        batch.begin();

        batch.draw(this,
                -fixCamera.viewportWidth / 2 + getWidth() + 24, -fixCamera.viewportHeight / 2, // 圖片左下角的點
                0, 0, // 操作的中心點???
                getWidth(), getHeight(), // 渲染出來的寬高
                1f, 1f,
                0f,  // 逆時針旋轉 角度
                0, 0,  // 操作目標的起始點
                getWidth(), getHeight(), // 操作目標的寬高
                false, false);

        batch.draw(this,
                -fixCamera.viewportWidth / 2 + getWidth() + 12, -fixCamera.viewportHeight / 2, // 圖片左下角的點
                0, getHeight() / 2, // 操作的中心點???
                getWidth(), getHeight(), // 渲染出來的寬高
                1f, 1f,
                180f,  // 逆時針旋轉 角度
                0, 0,  // 操作目標的起始點
                getWidth(), getHeight(), // 操作目標的寬高
                false, false);


        batch.end();
    }

    public void addOnTouchListener(OnTouchListener listener) {
        touchListeners.add(listener);
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }

    public interface OnTouchListener {
        void onTouchRight(int pointer);
        void onTouchLeft(int pointer);

    }
}
