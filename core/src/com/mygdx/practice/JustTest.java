package com.mygdx.practice;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Nick, 2020-02-04
 */
public class JustTest extends ApplicationAdapter {
    private Texture test;
    private SpriteBatch batch;
    private OrthographicCamera camera = new OrthographicCamera(100, 50);

    @Override
    public void create() {
        test = new Texture("lady_beetle.png");
        batch = new SpriteBatch();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(test, 0, 0);

        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        test.dispose();
    }
}
