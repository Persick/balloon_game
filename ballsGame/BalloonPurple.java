package com.testgame.fatum.testgame.ballsGame;

import android.support.constraint.ConstraintLayout;
import android.view.View;

/**
 * Created by Fatum on 07.05.2017.
 */

public class BalloonPurple extends Balloon {

    BalloonPurple(float speed, ConstraintLayout layoutForBalloon) {
        super(speed, layoutForBalloon);
    }

    @Override
    public void onClick(View v) {
        super.balloonsControl.onClickBonus(this);
    }

}
