package com.mygdx.practice;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.mygdx.practice.component.UserController;
import com.mygdx.practice.util.ZoomHelper;

/**
 * Nick, 2020-02-12
 */
public class MarioGame extends ApplicationAdapter {
    private static final int width = 40;
    private static final int height = 25;
    private UserController controller;
    private OrthographicCamera camera = new OrthographicCamera(width, height);

    private MarioWorld marioWorld;
    private SpriteBatch spriteBatch;

    private Stage stage;
    private ZoomHelper zh = new ZoomHelper(36);

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        World world = new World(new Vector2(0,-0.2f), true);
        marioWorld = new MarioWorld(world, zh, "map/test.tmx", spriteBatch);
        marioWorld.setupCameraBound(new Rectangle(width / 4f, height / 4f, 180, height / 2f));
        stage = new Stage(new ScalingViewport(Scaling.stretch, width / 2, height / 2, camera), spriteBatch);
        controller = new UserController("arrow.png", "up.png");
        controller.addTouchListener(marioWorld);
        controller.setCameraViewport(800, 500);
        Gdx.input.setInputProcessor(new InputMultiplexer(controller));

//        marioBody = marioWorld.getBodyById(MarioWorld.CharacterId.Mario);
    }

    private void preRender() {
//        Rectangle cameraBound = new Rectangle(width / 4f, height / 4f, 180, height / 2f);
//        CameraHelper.lookAt(camera, marioBody.getPosition(), new Vector2(1, 1), cameraBound);

        controller.preRender();
        marioWorld.preRender(camera, new Vector2(1, 1));
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        preRender();

        marioWorld.render(camera, spriteBatch);
        controller.render(spriteBatch);

        stage.act();
        stage.draw();

    }

    @Override
    public void dispose() {
        super.dispose();
        marioWorld.dispose();
        stage.dispose();
        controller.dispose();
        spriteBatch.dispose();
    }
}
