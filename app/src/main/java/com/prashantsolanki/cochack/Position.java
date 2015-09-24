package com.prashantsolanki.cochack;

/**
 * Created by Prashant on 5/17/2015.
 */
public class Position {
    float X;
    float Y;

    public Position(float x, float y) {
        X = x;
        Y = y;
    }

    public Position() {
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }
}
