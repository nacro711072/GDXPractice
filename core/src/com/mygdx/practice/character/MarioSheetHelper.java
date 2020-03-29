package com.mygdx.practice.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class MarioSheetHelper implements Disposable {
    private int margin = 1;
    private int width = 16;
    private int height = 16;
    private int beginX = 0;
    private int beginY = 0;

    private Texture marioSheet;
    private TextureRegion marioStandTexture;
    private Animation<TextureRegion> marioRunTextures;
    private TextureRegion marioJumpTexture;

    @Override
    public void dispose() {
        marioSheet.dispose();
        marioSheet = null;
        marioStandTexture = null;
        marioRunTextures = null;
        marioJumpTexture = null;
    }

    public static class Builder {
        private String path;
        private int margin = 0;
        private int height = 0;
        private int width = 0;
        private int beginX = 0;
        private int beginY = 0;

        public Builder(String path) {
            this.path = path;
        }

        public Builder setMargin(int margin) {
            this.margin = margin;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setBeginX(int x) {
            this.beginX = x;
            return this;
        }

        public Builder setBeginY(int y) {
            this.beginY = y;
            return this;
        }

        public MarioSheetHelper build() {
            return new MarioSheetHelper(path, margin, width, height, beginX, beginY);
        }
    }

    private MarioSheetHelper(String path, int margin, int width, int height, int beginX, int beginY) {
        this.margin = margin;
        this.width = width;
        this.height = height;
        this.beginX = beginX;
        this.beginY = beginY;
        createTexture(path);
    }

    private void createTexture(String path) {
        marioSheet = new Texture(path);
// stand
        marioStandTexture = new TextureRegion(marioSheet, beginX + margin, beginY + margin, width, height);
//        run
        Array<TextureRegion> tempFrame = new Array<>();
        for (int i = 1; i < 4; ++i) {
            tempFrame.add(new TextureRegion(marioSheet, beginX + margin + (width + margin) * i, beginY + margin, width, height));
        }
        marioRunTextures = new Animation<>(1f, tempFrame, Animation.PlayMode.LOOP_PINGPONG);
        tempFrame.clear();
// jump
        marioJumpTexture = new TextureRegion(marioSheet, beginX + margin + (width + margin) * 5, beginY + margin, width, height);
    }

    public TextureRegion getMarioStandTexture() {
        return marioStandTexture;
    }

    public TextureRegion getMarioJumpTexture() {
        return marioJumpTexture;
    }

    public TextureRegion getMarioRunningTexture(float time) {
        return marioRunTextures.getKeyFrame(time, true);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


}
