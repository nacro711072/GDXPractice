package com.mygdx.practice;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.mygdx.practice.component.UserController;
import com.mygdx.practice.util.CameraHelper;
import com.mygdx.practice.util.ZoomHelper;

/**
 * Nick, 2020-02-12
 */
public class MarioGame extends ApplicationAdapter {
    private static final int width = 40;
    private static final int height = 25;
    private Texture test;
    private SpriteBatch batch;
    private UserController controller;
    private OrthographicCamera camera = new OrthographicCamera(width, height);

    private MarioWorldCreator marioWorldCreator;
    private Body jumpBody;

    private Stage stage;
    private ZoomHelper zh = new ZoomHelper(40);

    @Override
    public void create() {
        marioWorldCreator = new MarioWorldCreator(new World(new Vector2(0,-0.2f), true), zh);
        test = new Texture("lady_beetle.png");
        batch = new SpriteBatch();
        stage = new Stage(new ScalingViewport(Scaling.fillX, width / 2, height / 2, camera));
        controller = new UserController("arrow.png", "up.png");
        controller.addOnTouchListener(new UserController.OnTouchListener() {
            @Override
            public void onTouchRight(int pointer) {
                if (jumpBody.getLinearVelocity().x < 0.4) {
                    jumpBody.applyLinearImpulse(new Vector2(0.004f, 0), jumpBody.getWorldCenter(), true);
                }
            }

            @Override
            public void onTouchLeft(int pointer) {
                if (jumpBody.getLinearVelocity().x > -0.4) {
                    jumpBody.applyLinearImpulse(new Vector2(-0.004f, 0), jumpBody.getWorldCenter(), true);
                }
            }

            @Override
            public void onJump(int pointer) {
                if (jumpBody.getLinearVelocity().y == 0) {
                    jumpBody.applyLinearImpulse(new Vector2(0, 0.2f), jumpBody.getWorldCenter(), true);
                }
            }
        });
        controller.setCameraViewport(800, 500);
        Gdx.input.setInputProcessor(new InputMultiplexer(controller));

        marioWorldCreator.createMap("map/test.tmx");

        jumpBody = marioWorldCreator.createCharacter(MarioWorldCreator.Character.Mario);

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        marioWorldCreator.render(camera);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        Vector2 p = jumpBody.getPosition();
//        Gdx.app.log("new", String.format("position: (%s, %s)", p.x, p.y));
        batch.draw(test,
                p.x - zh.scalePixel(test.getWidth() / 2f) / 2, p.y - zh.scalePixel(test.getHeight() / 2f) / 2,
                0, 0,
                test.getWidth(), test.getHeight(),
                zh.scalePixel() / 2, zh.scalePixel() / 2,
                0,
                0, 0,
                test.getWidth(), test.getHeight(),
                false, false);

        batch.end();

        controller.render();

        stage.act();
        stage.draw();

        Rectangle cameraBound = new Rectangle(width / 4f, height / 4f, 180, height / 2f);
        CameraHelper.lookAt(camera, jumpBody.getPosition(), new Vector2(1, 1), cameraBound);
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        test.dispose();
        marioWorldCreator.dispose();
        stage.dispose();
        controller.dispose();
    }
}
