package com.mygdx.practice;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.practice.model.BrickData;
import com.mygdx.practice.model.FixtureUserData;
import com.mygdx.practice.model.MarioBodyState;
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
        float halfX = zh.scalePixel(rect.getWidth() / 2);
        float halfY = zh.scalePixel(rect.getHeight() / 2);

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(zh.scalePixel(rect.getX()) + halfX, zh.scalePixel(rect.getY()) + halfY);

        body = world.createBody(bdef);
        body.setUserData(brickData);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0f;

        int[] singX = new int[] {-1, 1, 1, -1, -1};
        int[] singY = new int[] {-1, -1, 1, 1, -1};
        float[] vertex = new float[] { halfX, halfY, halfX, halfY };
        EdgeShape edgeShape = new EdgeShape();
        for (int i = 0; i < 4; ++i) {

            edgeShape.set(vertex[0] * singX[i], vertex[1] * singY[i], vertex[2] * singX[i + 1], vertex[3] * singY[i + 1]);
            fixtureDef.shape = edgeShape;
            Fixture fixture = body.createFixture(fixtureDef);
            String name = "";
            switch (i) {
                case 0: {
                    name = "b";
                    break;
                }
                case 1: {
                    name = "r";
                    break;
                }
                case 2: {
                    name = "t";
                    break;
                }
                case 3: {
                    name = "l";
                    break;
                }
            }
            fixture.setUserData(new FixtureUserData("brick_" + name));
        }
//        shape2.set();
//        shape2.setAsBox(zh.scalePixel(rect.getWidth() / 2), zh.scalePixel(rect.getHeight() / 2));


//        fixture.setUserData(brickData);
    }

    private void setTile(TiledMapTile tile) {
        cell.setTile(tile);
        if (tile == null) {
            body.getWorld().destroyBody(body);
        }
    }

    public void preRender(MarioBodyState marioBodyState, PreRenderCallback callback) {
//        Gdx.app.log("test", "isMarioHitBrick: " + brickData.isMarioHitBrick());
        if (!marioBodyState.isSmallState()) {
            if (brickData != null && brickData.isMarioHitBrick()) {
                if (brickData.isBreakable()) {
                    setTile(null);
                    return;
                }

                Integer type = brickData.getProperties().get("type", Integer.class);
                setTile(callback.getMapTileWithType(type));
            }
        }

    }

    public interface PreRenderCallback {
        TiledMapTile getMapTileWithType(int type);
    }

}
