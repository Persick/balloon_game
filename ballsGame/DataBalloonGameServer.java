package com.testgame.fatum.testgame.ballsGame;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by Дарья on 07.06.2017.
 */

class DataBalloonGameServer implements DataBalloonGame {

    @Override
    public void downloadData() {

    }

    @Override
    public int getAmountData() {
        return 0;
    }

    @Override
    public Drawable getPicture(int i) {
        return null;
    }

    @Override
    public double getRightAnswer(int i) {
        return 0;
    }

    @Override
    public ArrayList<Double> getWrongAnswers(int i) {
        return null;
    }
}
