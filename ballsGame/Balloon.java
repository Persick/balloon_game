package com.testgame.fatum.testgame.ballsGame;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Fatum on 06.05.2017.
 */

public abstract class Balloon implements View.OnClickListener {
    ConstraintLayout layoutForBalloon;
    private int height;
    private int width;
    static BalloonsControl balloonsControl;
    private float speed;


    private float centerY;
    private int centerX;


    Balloon(float speed, ConstraintLayout layoutForBalloon) {
        layoutForBalloon.setOnClickListener(this);
        this.layoutForBalloon = layoutForBalloon;
        this.speed = speed;
        width = layoutForBalloon.getLayoutParams().width;
        height = layoutForBalloon.getLayoutParams().height;
    }

    void updateY() {
        setCoordY(centerY - (speed / 40));
    }

    void setCoordinate(int x, int y) {
        centerX = x;
        centerY = y;
        final ViewGroup.MarginLayoutParams newLayoutParams = (ViewGroup.MarginLayoutParams) layoutForBalloon.getLayoutParams();
        newLayoutParams.setMargins(centerX - layoutForBalloon.getWidth() / 2, (int) centerY - layoutForBalloon.getHeight() / 2, 0, 0);
        balloonsControl.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layoutForBalloon.setLayoutParams(newLayoutParams);
            }
        });
    }

    public int getCenterX() {
        return centerX;
    }

    void setCoordY(float y) {
        centerY = y;
        final ViewGroup.MarginLayoutParams newLayoutParams = (ViewGroup.MarginLayoutParams) layoutForBalloon.getLayoutParams();
        newLayoutParams.setMargins(centerX - layoutForBalloon.getWidth() / 2, (int) centerY - layoutForBalloon.getHeight() / 2, 0, 0);
        balloonsControl.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layoutForBalloon.setLayoutParams(newLayoutParams);
            }
        });
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public float getCenterY() {
        return centerY;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }


    @Override
    public abstract void onClick(View v);
}
