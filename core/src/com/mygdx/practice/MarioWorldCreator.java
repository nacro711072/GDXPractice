package com.mygdx.practice;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.practice.model.Mario;
import com.mygdx.practice.util.ZoomHelper;

/**
 * Nick, 2020-02-13
 */
public class MarioWorldCreator implements Disposable {
    private World world;
    private ZoomHelper zh;

    private TiledMap map;
    private MapRenderer mapRender;

    private Box2DDebugRenderer box2dRender;

    MarioWorldCreator(World world, ZoomHelper zoomHelper) {
        this.world = world;
        this.zh = zoomHelper;
        box2dRender = new Box2DDebugRenderer();
    }

    public MarioWorldCreator(World world) {
        this(world, new ZoomHelper(1));
    }

//    public static MarioWorldCreator newInstance(World world, ZoomHelper zoomHelper) {
//        return new MarioWorldCreator(world, zoomHelper);
//    }

    public void createMap(String path) {
        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load(path);

        RectangleMapObject mapObject = map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class).get(0);
        Rectangle rect = mapObject.getRectangle();

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(zh.scalePixel(rect.getX() + rect.getWidth() / 2), zh.scalePixel(rect.getY() + rect.getHeight() / 2));

        Body body = world.createBody(bdef);
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape2 = new PolygonShape();
        shape2.setAsBox(zh.scalePixel(rect.getWidth() / 2), zh.scalePixel(rect.getHeight() / 2));
        fixtureDef.shape = shape2;
        body.createFixture(fixtureDef);
// tower
        for (RectangleMapObject mapObj: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            rect = mapObj.getRectangle();
            bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(zh.scalePixel(rect.getX() + rect.getWidth() / 2), zh.scalePixel(rect.getY() + rect.getHeight() / 2));

            body = world.createBody(bdef);
            fixtureDef = new FixtureDef();
            fixtureDef.friction = 0f;
            shape2 = new PolygonShape();
            shape2.setAsBox(zh.scalePixel(rect.getWidth() / 2), zh.scalePixel(rect.getHeight() / 2));
            fixtureDef.shape = shape2;
            body.createFixture(fixtureDef);
        }
// brick
        for (RectangleMapObject mapObj: map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            rect = mapObj.getRectangle();
            bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(zh.scalePixel(rect.getX() + rect.getWidth() / 2), zh.scalePixel(rect.getY() + rect.getHeight() / 2));

            body = world.createBody(bdef);
            fixtureDef = new FixtureDef();
            fixtureDef.friction = 0f;
            shape2 = new PolygonShape();
            shape2.setAsBox(zh.scalePixel(rect.getWidth() / 2), zh.scalePixel(rect.getHeight() / 2));
            fixtureDef.shape = shape2;
            body.createFixture(fixtureDef);
        }

        mapRender = new OrthogonalTiledMapRenderer(map, zh.scalePixel());
    }

    public Body createCharacter(Character character) {
        switch (character) {
            case Mario: {
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(0.18f, 0.34f);
//                shape.setRadius(0.24f);

                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                fdef.density = 0.9f;
                fdef.friction = 0f;

                BodyDef bodyDef = new BodyDef();
                bodyDef.linearDamping = 0.1f;
                bodyDef.type = BodyDef.BodyType.DynamicBody;
                bodyDef.position.set(2, 5);

                Body body = world.createBody(bodyDef);
                body.setUserData(new Mario());
                body.createFixture(fdef);
                return body;
            }
            default: return null;
        }
    }

    public void render(OrthographicCamera camera) {
        world.step(1 / 10f, 8, 3);

        mapRender.setView(camera);
        mapRender.render();

        box2dRender.render(world, camera.combined);
    }

    @Override
    public void dispose() {
        world.dispose();
        map.dispose();
        box2dRender.dispose();
    }

    public enum Character {
        Mario
    }
}
