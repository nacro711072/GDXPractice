package com.mygdx.practice;

/**
 * Nick, 2020/4/28
 */
public class SimpleCounter {
    private int initValue;
    private int currentCount;
    private Callback callback;

    public SimpleCounter(int initValue, Callback callback) {
        this.initValue = initValue;
        currentCount = initValue;
        this.callback = callback;
    }

    public void count() {
        if (--currentCount == 0) {
            callback.onFinish();
        }
    }

    public void reset() {
        currentCount = initValue;
    }

    public interface Callback {
        void onFinish();
    }
}
