package com.mygdx.practice.map;

import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Nick, 2020/3/10
 */
public class MarioMapWrapper extends TiledMap {
    private TiledMap map;
    public MarioMapWrapper(TiledMap map) {
        this.map = map;
    }

    @Override
    public TiledMapTileSets getTileSets() {
        return map.getTileSets();
    }

    @Override
    public void setOwnedResources(Array<? extends Disposable> resources) {
        map.setOwnedResources(resources);
    }

    @Override
    public void dispose() {
        map.dispose();
    }

    @Override
    public MapLayers getLayers() {
        return map.getLayers();
    }

    @Override
    public MapProperties getProperties() {
        return map.getProperties();
    }

    public TiledMapTile getTile(TileId tileId) {
        return map.getTileSets().getTile(tileId.id);
    }

    public enum TileId {
        NULL(0), FLOOR_TILE(1), WHITE_LIGHT_BRICK(2), BRICK(3), EMPTY_PROPS_BRICK(4), PROPS_BRICK(25);

        private int id;
        private TileId(int id) {
            this.id = id;
        }
    }
}
