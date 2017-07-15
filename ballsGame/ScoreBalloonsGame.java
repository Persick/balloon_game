package com.testgame.fatum.testgame.ballsGame;

import com.testgame.fatum.testgame.Score;

/**
 * Created by Fatum on 06.05.2017.
 */

public class ScoreBalloonsGame {
    private int score = 0;
    private int winStreak = 0;
    private int bonusScore = 0;

    public void addScore(int count, int winStreakCount){
        score += count;
        bonusScore = winStreak * winStreakCount;
        score += bonusScore;
    }

    public int getScore() {
        return score;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public void setWinStreak(int winStreak) {
        this.winStreak = winStreak;
    }
}
