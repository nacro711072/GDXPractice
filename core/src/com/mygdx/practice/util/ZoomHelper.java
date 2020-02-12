package com.mygdx.practice.util;

/**
 * Nick, 2020-02-10
 */
public class ZoomHelper {
    public static final float PPM = 100f;

    public static float scalePixel(float in) {
        return in / PPM;
    }

    public static float scalePixel(int in) {
        return in / PPM;
    }

}
