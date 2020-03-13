package com.mygdx.practice;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.practice.map.MarioMapWrapper;
import com.mygdx.practice.model.BrickData;
import com.mygdx.practice.util.ZoomHelper;

/**
 * Nick, 2020/3/13
 */
public class Brick {
    private Body body;
    private BrickData brickData;
    private TiledMapTileLayer.Cell cell;
    private TiledMapTile tile;

    public Brick(World world, MarioMapWrapper map, RectangleMapObject mapObj, ZoomHelper zh) {
        Rectangle rect = mapObj.getRectangle();
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);

        this.cell = layer.getCell((int) ((rect.getX() + rect.getWidth() / 2) / 16), (int) ((rect.getY() + rect.getHeight() / 2) / 16));
        brickData = new BrickData(mapObj.getProperties());

        Integer type = brickData.getProperties().get("type", Integer.class);
        if (type == 2) {
            tile = map.getTile(MarioMapWrapper.TileId.EMPTY_PROPS_BRICK);
        }


        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(zh.scalePixel(rect.getX() + rect.getWidth() / 2), zh.scalePixel(rect.getY() + rect.getHeight() / 2));

        body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(zh.scalePixel(rect.getWidth() / 2), zh.scalePixel(rect.getHeight() / 2));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0f;
        fixtureDef.shape = shape;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(brickData);
    }

    public void preRender() {
        BrickData userData = brickData;
        if (userData != null && userData.isMarioHitBrick()) {

            if (userData.isBreakable()) {
                cell.setTile(null);
                body.getWorld().destroyBody(body);
                return;
            }
            cell.setTile(tile);
        }

    }
}
