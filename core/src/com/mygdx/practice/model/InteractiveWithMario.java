package com.mygdx.practice.model;

/**
 * Nick, 2020/4/21
 */
public interface InteractiveWithMario {

    Who getWho();
    void onContactMario();

    enum Who {
        Goomba, Mushroom;
    }
}
