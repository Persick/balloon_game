package com.testgame.fatum.testgame.ballsGame;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by Дарья on 07.06.2017.
 */

interface DataBalloonGame {
    void downloadData();
    int getAmountData();
    Drawable getPicture(int i);
    double getRightAnswer(int i);
    ArrayList<Double> getWrongAnswers(int i);
}
