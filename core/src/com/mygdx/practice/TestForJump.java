package com.mygdx.practice;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

import static com.mygdx.practice.util.ZoomHelper.scalePixel;

/**
 * Nick, 2020-02-12
 */
public class TestForJump extends ApplicationAdapter {
    private static final int width = 40;
    private static final int height = 25;
    private Texture test;
    private SpriteBatch batch;
    private OrthographicCamera camera = new OrthographicCamera(width, height);

    private Box2DDebugRenderer box2dRender;
    private World world;
    private Body jumpBody;
    private Body commonBody;

    private TiledMap map;
    private TiledMapRenderer mapRender;

    private Stage stage;

    private int i = 0;

    @Override
    public void create() {
        test = new Texture("lady_beetle.png");
        batch = new SpriteBatch();
        world = new World(new Vector2(0,-0.09f), true);
        stage = new Stage(new ScalingViewport(Scaling.fill, width / 2, height / 2, camera));

        BodyDef test = new BodyDef();
        test.linearDamping = 0.01f;
        test.type = BodyDef.BodyType.DynamicBody;
        test.position.set(2, 10);

        jumpBody = world.createBody(test);
        test.position.set(4, 10);
        commonBody = world.createBody(test);
        FixtureDef fdef = new FixtureDef();
        Shape shape = new CircleShape();
        shape.setRadius(0.16f);
        fdef.shape = shape;
        fdef.density = 1f;
        fdef.friction = 0f;
        jumpBody.createFixture(fdef);
        commonBody.createFixture(fdef);

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
//        ===============
        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load("map/test.tmx");
        mapRender = new OrthogonalTiledMapRenderer(map, 0.01f);

        RectangleMapObject mapObject = map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class).get(0);
        Rectangle rect = mapObject.getRectangle();
//		camera.position.set()

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(scalePixel(rect.getX() + rect.getWidth() / 2), scalePixel(rect.getY() + rect.getHeight() / 2));

        Body body = world.createBody(bdef);
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape2 = new PolygonShape();
        shape2.setAsBox(scalePixel(rect.getWidth() / 2), scalePixel(rect.getHeight() / 2));
        fixtureDef.shape = shape2;
        body.createFixture(fixtureDef);

        for (RectangleMapObject mapObj: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            rect = mapObj.getRectangle();
            bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(scalePixel(rect.getX() + rect.getWidth() / 2), scalePixel(rect.getY() + rect.getHeight() / 2));

            body = world.createBody(bdef);
            fixtureDef = new FixtureDef();
            fixtureDef.friction = 0f;
            shape2 = new PolygonShape();
            shape2.setAsBox(scalePixel(rect.getWidth() / 2), scalePixel(rect.getHeight() / 2));
            fixtureDef.shape = shape2;
            body.createFixture(fixtureDef);
        }

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1 / 10f, 8, 3);

        mapRender.setView(camera);
        mapRender.render();
        box2dRender.render(world, camera.combined);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        Vector2 p = jumpBody.getPosition();
        batch.draw(test,
                p.x - test.getWidth() / 200f, p.y - test.getHeight() / 200f,
                0, 0,
                test.getWidth(), test.getHeight(),
                1 / 100f, 1 / 100f,
                0,
                0, 0,
                test.getWidth(), test.getHeight(),
                false, false);
        p = commonBody.getPosition();
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

        stage.act();
        stage.draw();

        Vector2 v1 = jumpBody.getLinearVelocity();
        Vector2 v2 = commonBody.getLinearVelocity();
        if (++i % 10 == 0 && v1.y == 0) {
            Gdx.app.log("render", String.format("Velocity1: x->%s, y->%s", v1.x, v1.y));
            Gdx.app.log("render", String.format("Velocity2: x->%s, y->%s", v2.x, v2.y));
            jumpBody.applyLinearImpulse(new Vector2(0.001f, 0), jumpBody.getWorldCenter(), true);
            commonBody.applyLinearImpulse(new Vector2(0.001f, 0), commonBody.getWorldCenter(), true);
            if (i % 200 == 0) {
                jumpBody.applyLinearImpulse(new Vector2(0, 0.03f), jumpBody.getWorldCenter(), true);
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
        map.dispose();
        stage.dispose();
    }
}
