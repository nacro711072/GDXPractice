package com.mygdx.practice.character.mario;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.practice.model.MarioBodyState;

/**
 * Nick, 2020/4/6
 */
public class MarioTextureRepository {
    private MarioSheetHelper smallMario;
    private MarioSheetHelper bigMario;

    public MarioTextureRepository(String marioPath) {
        smallMario = new MarioSheetHelper.Builder("mario_sheet.png")
                .setMargin(1)
                .setWidth(16)
                .setHeight(16)
                .setBeginY(33)
                .build();

        bigMario = new MarioSheetHelper.Builder("mario_sheet.png")
                .setMargin(1)
                .setWidth(16)
                .setHeight(32)
                .build();

    }

    public TextureRegion getMarioStandTexture(MarioBodyState bodyState) {
        switch (bodyState) {
            case smallMario:
                return smallMario.getMarioStandTexture();
            case bigMario:
                return bigMario.getMarioStandTexture();
            case superMario:
                return null;
        }
        return null;
    }

    public TextureRegion getMarioJumpTexture(MarioBodyState bodyState) {
        switch (bodyState) {
            case smallMario:
                return smallMario.getMarioJumpTexture();
            case bigMario:
                return bigMario.getMarioJumpTexture();
            case superMario:
                return null;
        }
        return null;
    }

    public TextureRegion getMarioRunningTexture(MarioBodyState bodyState, float time) {
        switch (bodyState) {
            case smallMario:
                return smallMario.getMarioRunningTexture(time);
            case bigMario:
                return bigMario.getMarioRunningTexture(time);
            case superMario:
                return null;
        }
        return null;
    }

    public int getWidth(MarioBodyState bodyState) {
        switch (bodyState) {
            case smallMario:
                return smallMario.getWidth();
            case bigMario:
                return bigMario.getWidth();
            case superMario:
                return 0;
        }
        return 0;
    }

    public int getHeight(MarioBodyState bodyState) {
        switch (bodyState) {
            case smallMario:
                return smallMario.getHeight();
            case bigMario:
                return bigMario.getHeight();
            case superMario:
                return 0;
        }
        return 0;

    }

    public void dispose() {
        smallMario.dispose();
        bigMario.dispose();
    }
}
