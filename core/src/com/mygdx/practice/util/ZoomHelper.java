package com.mygdx.practice.util;

/**
 * Nick, 2020-02-10
 */
public class ZoomHelper {
    private final float PPM;

    public ZoomHelper(float zoom) {
        PPM = zoom;
    }

    public float scalePixel(float in) {
        return in / PPM;
    }

    public float scalePixel(int in) {
        return in / PPM;
    }

    public float scalePixel() {
        return 1 / PPM;
    }

}
