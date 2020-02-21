package com.mygdx.practice;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.mygdx.practice.component.UserController;
import com.mygdx.practice.model.MarioBodyData;
import com.mygdx.practice.util.CameraHelper;
import com.mygdx.practice.model.MarioState;
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
    private Body marioBody;

    private Stage stage;
    private ZoomHelper zh = new ZoomHelper(36);

    @Override
    public void create() {
        marioWorld = new MarioWorld(new World(new Vector2(0,-0.2f), true), zh, "map/test.tmx");
        stage = new Stage(new ScalingViewport(Scaling.fillX, width / 2, height / 2, camera));
        controller = new UserController("arrow.png", "up.png");
        controller.addTouchListener(marioWorld);
        controller.setCameraViewport(800, 500);
        Gdx.input.setInputProcessor(new InputMultiplexer(controller));

        marioBody = marioWorld.getBodyById(MarioWorld.CharacterId.Mario);
    }

    private void preRender() {
        Rectangle cameraBound = new Rectangle(width / 4f, height / 4f, 180, height / 2f);
        CameraHelper.lookAt(camera, marioBody.getPosition(), new Vector2(1, 1), cameraBound);

        controller.preRender();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        preRender();

        marioWorld.render(camera);
        controller.render();

        stage.act();
        stage.draw();

    }

    @Override
    public void dispose() {
        super.dispose();
        marioWorld.dispose();
        stage.dispose();
        controller.dispose();
    }
}
