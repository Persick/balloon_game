package com.testgame.fatum.testgame.ballsGame;

import android.support.constraint.ConstraintLayout;
import android.view.View;

/**
 * Created by Fatum on 07.05.2017.
 */

public class BalloonGreen extends Balloon {

    BalloonGreen(float speed, ConstraintLayout layoutForBalloon) {
        super(speed, layoutForBalloon);
    }

    @Override
    public void onClick(View v) {
        super.balloonsControl.onClickBonus(this);
    }

}