package com.mygdx.practice;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Nick, 2020-02-04
 */
public class JustTestFreeFall extends ApplicationAdapter {
    private static final int width = 40;
    private static final int height = 25;
    private Texture test;
    private SpriteBatch batch;
    private OrthographicCamera camera = new OrthographicCamera(width, height);

    private Box2DDebugRenderer box2dRender;
    private World world;
    private Body mainBody;
    private Body mainBody2;

    private int i = 0;

    @Override
    public void create() {
        test = new Texture("lady_beetle.png");
        batch = new SpriteBatch();
        world = new World(new Vector2(0,-0.05f), true);

        BodyDef test = new BodyDef();
        test.linearDamping = 0.01f;
        test.type = BodyDef.BodyType.DynamicBody;
        test.position.set(2, 0);

        mainBody = world.createBody(test);
        test.position.set(0, 0);
        mainBody2 = world.createBody(test);
        FixtureDef fdef = new FixtureDef();
        Shape shape = new CircleShape();
        shape.setRadius(0.16f);
        fdef.shape = shape;
        fdef.density = 1f;
        fdef.friction = 0.2f;
        mainBody.createFixture(fdef);
        mainBody2.createFixture(fdef);

//
        BodyDef groundDef = new BodyDef();
        groundDef.type = BodyDef.BodyType.StaticBody;
        groundDef.position.set(0, -height / 2);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(100, 1);

        FixtureDef groundFixDef = new FixtureDef();
        groundFixDef.shape = polygonShape;

        Body ground = world.createBody(groundDef);

        ground.createFixture(groundFixDef);

        box2dRender = new Box2DDebugRenderer();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        mainBody.applyLinearImpulse(new Vector2(0.001f, 0.0f), mainBody.getWorldCenter(), true);
        world.step(1 / 10f, 8, 3);


        box2dRender.render(world, camera.combined);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        Vector2 p = mainBody.getPosition();
        batch.draw(test,
                p.x - test.getWidth() / 200f, p.y - test.getHeight() / 200f,
                0, 0,
                test.getWidth(), test.getHeight(),
                1 / 100f, 1 / 100f,
                0,
                0, 0,
                test.getWidth(), test.getHeight(),
                false, false);
        p = mainBody2.getPosition();
        batch.draw(test,
                p.x - test.getWidth() / 200f, p.y - test.getHeight() / 200f,
                0, 0,
                test.getWidth(), test.getHeight(),
                1 / 100f, 1 / 100f,
                0,
                0, 0,
                test.getWidth(), test.getHeight(),
                false, false);


        batch.end();
        if (++i % 10 == 0) {
            Vector2 v = mainBody.getLinearVelocity();
            Gdx.app.log("render", String.format("Velocity: x->%s, y->%s", v.x, v.y));
            mainBody.applyLinearImpulse(new Vector2(0.001f, 0), mainBody.getWorldCenter(), true);
            if (i % 100 == 0) {
//                mainBody.applyLinearImpulse(new Vector2(0, 0.01f), mainBody.getWorldCenter(), true);
                Gdx.app.log("render", "================");
                i = 0;
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        test.dispose();
        world.dispose();
        box2dRender.dispose();
    }
}
