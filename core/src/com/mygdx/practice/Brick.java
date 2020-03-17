package com.mygdx.practice;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.practice.model.BrickData;
import com.mygdx.practice.util.ZoomHelper;

/**
 * Nick, 2020/3/13
 */
public class Brick {
    private Body body;
    private BrickData brickData;
    private TiledMapTileLayer.Cell cell;

    public Brick(World world, RectangleMapObject mapObj, TiledMapTileLayer.Cell cell, ZoomHelper zh) {
        brickData = new BrickData(mapObj.getProperties());
        this.cell = cell;

        Rectangle rect = mapObj.getRectangle();

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(zh.scalePixel(rect.getX() + rect.getWidth() / 2), zh.scalePixel(rect.getY() + rect.getHeight() / 2));

        body = world.createBody(bdef);

        PolygonShape shape2 = new PolygonShape();
        shape2.setAsBox(zh.scalePixel(rect.getWidth() / 2), zh.scalePixel(rect.getHeight() / 2));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0f;
        fixtureDef.shape = shape2;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(brickData);
    }

    private void setTile(TiledMapTile tile) {
        cell.setTile(tile);
        if (tile == null) {
            body.getWorld().destroyBody(body);
        }
    }

    public void preRender(PreRenderCallback callback) {
        if (brickData != null && brickData.isMarioHitBrick()) {
            if (brickData.isBreakable()) {
                setTile(null);
                return;
            }

            Integer type = brickData.getProperties().get("type", Integer.class);
            setTile(callback.getMapTileWithType(type));
        }

    }

    public interface PreRenderCallback {
        TiledMapTile getMapTileWithType(int type);
    }

}
