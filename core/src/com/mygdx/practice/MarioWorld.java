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
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.practice.character.Character;
import com.mygdx.practice.character.Mario;
import com.mygdx.practice.model.FixtureUserData;
import com.mygdx.practice.model.MarioBodyData;
import com.mygdx.practice.model.MarioState;
import com.mygdx.practice.util.ZoomHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Nick, 2020-02-13
 */
public class MarioWorld implements Disposable {
    private World world;
    private ZoomHelper zh;

    private TiledMap map;
    private MapRenderer mapRender;

    private Box2DDebugRenderer box2dRender;

    private List<Character> characters = new ArrayList<>();

    MarioWorld(World world, ZoomHelper zoomHelper, String path) {
        this.world = world;
        this.zh = zoomHelper;
        box2dRender = new Box2DDebugRenderer();
        createMap(path);

        characters.add(new Mario(world, CharacterId.Mario));

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                FixtureUserData dataA = ((FixtureUserData) contact.getFixtureA().getUserData());
                FixtureUserData dataB = ((FixtureUserData) contact.getFixtureB().getUserData());
                if (dataA != null && dataA.type.equals("mario_foot")) {
                    ((MarioBodyData) contact.getFixtureA().getBody().getUserData()).changeState(MarioState.STAND);
                } else if (dataB != null && dataB.type.equals("mario_foot")) {
                    ((MarioBodyData) contact.getFixtureB().getBody().getUserData()).changeState(MarioState.STAND);
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    private void createMap(String path) {
        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load(path);

        RectangleMapObject mapObject = map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class).get(0);
        Rectangle rect = mapObject.getRectangle();

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(zh.scalePixel(rect.getX() + rect.getWidth() / 2), zh.scalePixel(rect.getY() + rect.getHeight() / 2));
//        地板
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

    public Character getCharacter(CharacterId id) {
        for (Character character: characters) {
            if (character.getId() == id) {
                return character;
            }
        }
        return null;
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

    public enum CharacterId {
        Mario;
    }
}
