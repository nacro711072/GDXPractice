package com.mygdx.practice;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.practice.character.Character;
import com.mygdx.practice.character.Goomba;
import com.mygdx.practice.character.Mario;
import com.mygdx.practice.component.UserController;
import com.mygdx.practice.map.MarioMapWrapper;
import com.mygdx.practice.util.CameraHelper;
import com.mygdx.practice.util.ZoomHelper;
import com.mygdx.practice.wrapper.MultiContactListener;

import java.util.Iterator;
import java.util.LinkedList;
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
    private Rectangle cameraBound;
    private List<Character> characters = new LinkedList<>();
    private List<Brick> bricks = new LinkedList<>();
//    private MultiContactListener contactListeners = new MultiContactListener();
    private Texture enemiesTexture = new Texture("map/mario_enemies_bosses_sheet.png");


    MarioWorld(World world, ZoomHelper zoomHelper, String path, SpriteBatch spriteBatch) {
        this.world = world;
        this.zh = zoomHelper;
        box2dRender = new Box2DDebugRenderer();
        map = createMap(path);
        mapRender = new OrthogonalTiledMapRenderer(map, zh.scalePixel(), spriteBatch);

        mario = new Mario(world, zh);
//        Goomba testGoomba = new Goomba(world, enemiesTexture, new Vector2(13, 2));

        characters.add(mario);
//        characters.add(testGoomba);

//        contactListeners.addContactListener(mario);
//        contactListeners.addContactListener(testGoomba);

        world.setContactListener(new WorldContact());
    }

    public void setupCameraBound(Rectangle cameraBound) {
        this.cameraBound = cameraBound;
    }

    private MarioMapWrapper createMap(String path) {
        TmxMapLoader mapLoader = new TmxMapLoader();
        MarioMapWrapper map = new MarioMapWrapper(mapLoader.load(path));

        Rectangle rect;
        BodyDef bdef;
        Body body;
        FixtureDef fixtureDef;
        PolygonShape shape2;
        for (RectangleMapObject mapObj: map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            rect = mapObj.getRectangle();
            bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set(zh.scalePixel(rect.getX() + rect.getWidth() / 2), zh.scalePixel(rect.getY() + rect.getHeight() / 2));
            body = world.createBody(bdef);

            fixtureDef = new FixtureDef();
            shape2 = new PolygonShape();
            shape2.setAsBox(zh.scalePixel(rect.getWidth() / 2), zh.scalePixel(rect.getHeight() / 2));
            fixtureDef.shape = shape2;
            body.createFixture(fixtureDef);
        }
//        RectangleMapObject mapObject = map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class).get(0);

//        地板
// pipe
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
            TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
            TiledMapTileLayer.Cell cell = layer.getCell((int) ((rect.getX() + rect.getWidth() / 2) / 16), (int) ((rect.getY() + rect.getHeight() / 2) / 16));

            bricks.add(new Brick(world, mapObj, cell, zh));
        }

        for (TiledMapTileMapObject tileMapObject : ((MapGroupLayer) map.getLayers().get(5)).getLayers().get(0).getObjects().getByType(TiledMapTileMapObject.class)) {
            Gdx.app.log("goomba", "x: " + zh.scalePixel(tileMapObject.getX()));
            Goomba testGoomba = new Goomba(world, enemiesTexture, new Vector2(zh.scalePixel(tileMapObject.getX()), zh.scalePixel(tileMapObject.getY())));
            characters.add(testGoomba);

        }

        return map;
    }

    public void preRender(Camera camera, Vector2 panRange) {
        Iterator<Character> it = characters.iterator();
        while (it.hasNext()) {
            Character character = it.next();
            if (character.getLifeState().isDead()) {
                world.destroyBody(character.getBody());
                it.remove();
                character.dispose();
            }
        }
//        for (Character character: characters) {
//            if (character.getLifeState().isDead()) {
//                world.destroyBody(character.getBody());
//                characters.remove(character);
//                character.dispose();
//            }
//        }

        if (mario.getBody() != null) {
            CameraHelper.lookAt(camera, mario.getBody().getPosition(), panRange, cameraBound);
            mario.preRender();
        }

        for (Brick brick: bricks) {
            brick.preRender(new Brick.PreRenderCallback() {
                @Override
                public TiledMapTile getMapTileWithType(int type) {
                    return map.getTile(MarioMapWrapper.TileId.EMPTY_PROPS_BRICK);
                }
            });
        }

    }

    public void render(OrthographicCamera camera, SpriteBatch spriteBatch) {
        world.step(1 / 10f, 8, 3);

        mapRender.setView(camera);
        mapRender.render();

        box2dRender.render(world, camera.combined);

        for (Character character : characters) {
            character.render(camera, zh, spriteBatch);
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
}
