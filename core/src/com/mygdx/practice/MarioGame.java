package com.mygdx.practice;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.mygdx.practice.component.UserController;
import com.mygdx.practice.model.Mario;
import com.mygdx.practice.model.MarioState;
import com.mygdx.practice.util.ZoomHelper;

/**
 * Nick, 2020-02-12
 */
public class MarioGame extends ApplicationAdapter {
    private static final int width = 40;
    private static final int height = 21;
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
        test = new Texture("mario_sheet.png");
        batch = new SpriteBatch();
        stage = new Stage(new ScalingViewport(Scaling.fill, width / 2, height / 2, camera));
        controller = new UserController("arrow.png", "up.png");
        controller.addOnTouchListener(new UserController.OnTouchListener() {
            @Override
            public void onTouchRight(int pointer) {
                if (jumpBody.getLinearVelocity().x < 0.4) {
                    jumpBody.applyLinearImpulse(new Vector2(0.004f, 0), jumpBody.getWorldCenter(), true);
                    Mario userData = ((Mario) jumpBody.getUserData());
                    userData.face = true;
                    userData.state = MarioState.RUN;
                }
            }

            @Override
            public void onTouchLeft(int pointer) {
                if (jumpBody.getLinearVelocity().x > -0.4) {
                    jumpBody.applyLinearImpulse(new Vector2(-0.004f, 0), jumpBody.getWorldCenter(), true);
                    Mario userData = ((Mario) jumpBody.getUserData());
                    userData.face = false;
                    userData.state = MarioState.RUN;
                }
            }

            @Override
            public void onJump(int pointer) {
                if (jumpBody.getLinearVelocity().y == 0) {
                    jumpBody.applyLinearImpulse(new Vector2(0, 0.2f), jumpBody.getWorldCenter(), true);
                    Mario userData = ((Mario) jumpBody.getUserData());
                    userData.state = MarioState.JUMP;
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
        TextureRegion a = new TextureRegion(test, 1, 1, 16, 32);

//        Gdx.input.getRotation()
        Mario userData = (Mario) jumpBody.getUserData();
        // LEFT
        if (userData.face) {
            batch.draw(a,
                    p.x - zh.scalePixel(a.getRegionWidth()) / 2f, p.y - zh.scalePixel(a.getRegionHeight()) / 2f,
                    a.getRegionWidth() * zh.scalePixel(), a.getRegionHeight() * zh.scalePixel()
            );
        } else {
            batch.draw(a,
                    p.x + zh.scalePixel(a.getRegionWidth() / 2f), p.y - zh.scalePixel(a.getRegionHeight() / 2f),
                    p.x, p.y,
                    -zh.scalePixel(a.getRegionWidth()), zh.scalePixel(a.getRegionHeight()),
                    1, 1,
                    0f);
        }
        // right

        batch.end();

        controller.render();

        stage.act();
        stage.draw();

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
