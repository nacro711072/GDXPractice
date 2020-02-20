package com.mygdx.practice;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private Texture marioSheet;
    private TextureRegion marioTexture;
    private SpriteBatch batch;
    private UserController controller;
    private OrthographicCamera camera = new OrthographicCamera(width, height);

    private MarioWorld marioWorld;
    private Body jumpBody;

    private Stage stage;
    private ZoomHelper zh = new ZoomHelper(36);

    @Override
    public void create() {
        marioWorld = new MarioWorld(new World(new Vector2(0,-0.2f), true), zh, "map/test.tmx");
        marioSheet = new Texture("mario_sheet.png");
        marioTexture = new TextureRegion(marioSheet, 1, 1, 16, 32);
        batch = new SpriteBatch();
        stage = new Stage(new ScalingViewport(Scaling.fillX, width / 2, height / 2, camera));
        controller = new UserController("arrow.png", "up.png");
        controller.addOnTouchListener(new UserController.OnTouchListener() {
            @Override
            public void onTouchRight(int pointer) {
                if (jumpBody.getLinearVelocity().x < 0.4) {
                    jumpBody.applyLinearImpulse(new Vector2(0.004f, 0), jumpBody.getWorldCenter(), true);
                    MarioBodyData userData = ((MarioBodyData) jumpBody.getUserData());
                    userData.face = true;
                    userData.changeState(MarioState.RUN);
                }
            }

            @Override
            public void onTouchLeft(int pointer) {
                if (jumpBody.getLinearVelocity().x > -0.4) {
                    jumpBody.applyLinearImpulse(new Vector2(-0.004f, 0), jumpBody.getWorldCenter(), true);
                    MarioBodyData userData = ((MarioBodyData) jumpBody.getUserData());
                    userData.face = false;
                    userData.changeState(MarioState.RUN);
                }
            }

            @Override
            public void onJump(int pointer) {
                MarioBodyData userData = ((MarioBodyData) jumpBody.getUserData());
                if (userData.getState() != MarioState.JUMP) {
                    jumpBody.applyLinearImpulse(new Vector2(0, 0.4f), jumpBody.getWorldCenter(), true);
                    userData.changeState(MarioState.JUMP);
                }
            }
        });
        controller.setCameraViewport(800, 500);
        Gdx.input.setInputProcessor(new InputMultiplexer(controller));

        jumpBody = marioWorld.getCharacter(MarioWorld.CharacterId.Mario).getBody();

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Rectangle cameraBound = new Rectangle(width / 4f, height / 4f, 180, height / 2f);
        CameraHelper.lookAt(camera, jumpBody.getPosition(), new Vector2(1, 1), cameraBound);

        marioWorld.render(camera);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        Vector2 p = jumpBody.getPosition();

        MarioBodyData userData = (MarioBodyData) jumpBody.getUserData();
        // LEFT
        if (userData.face) {
            batch.draw(marioTexture,
                    p.x - zh.scalePixel(marioTexture.getRegionWidth()) / 2f, p.y - zh.scalePixel(marioTexture.getRegionHeight()) / 2f,
                    marioTexture.getRegionWidth() * zh.scalePixel(), marioTexture.getRegionHeight() * zh.scalePixel()
            );
        } else {
            batch.draw(marioTexture,
                    p.x + zh.scalePixel(marioTexture.getRegionWidth() / 2f), p.y - zh.scalePixel(marioTexture.getRegionHeight() / 2f),
                    p.x, p.y,
                    -zh.scalePixel(marioTexture.getRegionWidth()), zh.scalePixel(marioTexture.getRegionHeight()),
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
        marioSheet.dispose();
        marioWorld.dispose();
        stage.dispose();
        controller.dispose();
    }
}
