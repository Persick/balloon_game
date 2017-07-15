package com.testgame.fatum.testgame.ballsGame;

import android.support.constraint.ConstraintLayout;
import android.view.View;


/**
 * Created by Fatum on 07.05.2017.
 */

public class BalloonNormal extends Balloon {
    private double value;

    BalloonNormal(float speed, ConstraintLayout layoutForBalloon) {
        super(speed, layoutForBalloon);
    }

    @Override
    public void onClick(View v) {
        super.balloonsControl.checkAnswer(this);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
