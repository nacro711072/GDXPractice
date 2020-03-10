package com.mygdx.practice;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.practice.character.Character;
import com.mygdx.practice.character.Mario;
import com.mygdx.practice.component.UserController;
import com.mygdx.practice.map.MarioMapWrapper;
import com.mygdx.practice.model.BrickData;
import com.mygdx.practice.util.ZoomHelper;
import com.mygdx.practice.wrapper.MultiContactListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Nick, 2020-02-13
 */
public class MarioWorld implements Disposable, UserController.TouchListener {
    private World world;
    private ZoomHelper zh;

    private MarioMapWrapper map;
    private MapRenderer mapRender;

    private Box2DDebugRenderer box2dRender;

    private Mario mario;
    private List<Character> characters = new ArrayList<>();
    private MultiContactListener contactListeners = new MultiContactListener();


    MarioWorld(World world, ZoomHelper zoomHelper, String path) {
        this.world = world;
        this.zh = zoomHelper;
        box2dRender = new Box2DDebugRenderer();
        createMap(path);

        mario = new Mario(world, CharacterId.Mario);
        characters.add(mario);
        contactListeners.addContactListener(mario);

        world.setContactListener(mario);
    }

    private void createMap(String path) {
        TmxMapLoader mapLoader = new TmxMapLoader();
        map = new MarioMapWrapper(mapLoader.load(path));

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
            Fixture fixture = body.createFixture(fixtureDef);
            fixture.setUserData(new BrickData(mapObj.getProperties()));
        }

        mapRender = new OrthogonalTiledMapRenderer(map, zh.scalePixel());
    }

    public Body getBodyById(CharacterId id) {
        for (Character character: characters) {
            if (character.getId() == id) {
                return character.getBody();
            }
        }
        return null;
    }

    public void preRender() {
        mario.preRender();
        Array<Fixture> fixtureArray = new Array<>(world.getFixtureCount());
        world.getFixtures(fixtureArray);
        for (Fixture fixture: fixtureArray) {
            if (!(fixture.getUserData() instanceof BrickData)) continue;

            BrickData userData = (BrickData) fixture.getUserData();
            if (userData != null && userData.isMarioHitBrick()) {
                Vector2 p = fixture.getBody().getPosition();
                TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
                TiledMapTileLayer.Cell cell = layer.getCell((int) (p.x / zh.scalePixel() / 16), (int) (p.y / zh.scalePixel() / 16));

                if (userData.isBreakable()) {
                    cell.setTile(null);
                    fixture.getBody().destroyFixture(fixture);
                    continue;
                }

                Integer type = userData.getProperties().get("type", Integer.class);
                if (type == 2) {
                    cell.setTile(map.getTile(MarioMapWrapper.TileId.EMPTY_PROPS_BRICK));
                    fixture.setUserData(null);
                }
            }
        }
    }

    public void render(OrthographicCamera camera) {
        world.step(1 / 10f, 8, 3);

        mapRender.setView(camera);
        mapRender.render();

        box2dRender.render(world, camera.combined);

        for (Character character : characters) {
            character.render(camera, zh);
        }
    }

    @Override
    public void dispose() {
        world.dispose();
        map.dispose();
        box2dRender.dispose();
        for (Character character : characters) {
            character.dispose();
        }
    }

    @Override
    public void onTouchRight(int pointer) {
        mario.onTouchRight(pointer);
    }

    @Override
    public void onTouchLeft(int pointer) {
        mario.onTouchLeft(pointer);
    }

    @Override
    public void onJump(int pointer) {
        mario.onJump(pointer);
    }

    @Override
    public void onNoAction() {
        mario.onNoAction();
    }

    public enum CharacterId {
        Mario;
    }
}
