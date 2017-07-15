package com.testgame.fatum.testgame.ballsGame;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.testgame.fatum.testgame.Health;
import com.testgame.fatum.testgame.R;
import com.testgame.fatum.testgame.RandomFour;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Fatum on 06.05.2017.
 */

public class BalloonsControl {
    static final int COUNT_ROUNDS = 7;
    private int points = 100;
    private int bonusPoints = 100;

    private int height; //mainlayout
    private int width; //mainlayout

    private Random random = new Random(System.currentTimeMillis());


    private ScoreBalloonsGame score = new ScoreBalloonsGame();
    private ArrayList<Balloon> bonusBalloons = new ArrayList<>(); //все бонусные шарики
    private ArrayList<Balloon> visibleBalloons = new ArrayList<>(); //шарики на экране
    private ArrayList<BalloonNormal> waitingBalloons = new ArrayList<>(); //шарики вне экрана
    private Health health = new Health(4);
    BalloonsGameActivity gameActivity;
    private Context context;

    private int round = 0; // количество заданий
    private RandomFour randomFour;
    private DataBalloonGame dataBalloonGame;
    private ArrayList<Integer> idNumbers = new ArrayList<>();
    private ArrayList<Double> rightAnswers = new ArrayList<>();
    private ArrayList<Double> wrongAnswers = new ArrayList<>();

    private double rightAnswer;
    private double nextRightAnswer;

    private long timeCorrectAnswer;

    private boolean isEndOfGame = false;
    private boolean canGenerateNew = true;


    BalloonsControl(int height, int width, BalloonsGameActivity gameActivity) {
        this.height = height;
        this.width = width;
        this.gameActivity = gameActivity;
        this.context = gameActivity.getApplicationContext();
        dataBalloonGame = new DataBalloonGameLocal(context);
        dataBalloonGame.downloadData();
        initIdNumbers();
        initRightAnswers();
        initWrongAnswers();
        setTaskPicture();
        Balloon.balloonsControl = this;
    }

    private void initIdNumbers() {
        randomFour = new RandomFour(dataBalloonGame.getAmountData());
        for (int i = 0; i < COUNT_ROUNDS; i++) {
            idNumbers.add(randomFour.generate());
        }
    }

    private void initRightAnswers() {
        for (int i = 0; i < COUNT_ROUNDS; i++) {
            rightAnswers.add(dataBalloonGame.getRightAnswer(idNumbers.get(i)));
        }
    }

    private void initWrongAnswers() {
        if (round <= idNumbers.size() - 1) {
            wrongAnswers = dataBalloonGame.getWrongAnswers(idNumbers.get(round));
        }
    }

    private void setTaskPicture() {
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView imageView = (ImageView) gameActivity.findViewById(R.id.task);
                imageView.setImageDrawable(dataBalloonGame.getPicture(idNumbers.get(round)));
            }
        });
    }

    /*private void initRightAnswers() {
        Collections.addAll(rightAnswers, -0.1, 0.75, 4.3, -4.9, 4d, 10d, 31d, 702d);
    }*/

    private void update() {
        //каждые 50 милсек прибавляем к speed
        new Thread(new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                while (true) {
                    if (isEndOfGame) {
                        break;
                    }
                    if (!gameActivity.isPause()) {
                        long newTime = System.currentTimeMillis();
                        if (newTime - time > 25) {
                            time = newTime;
                            float maxHeight = 0;
                            if (visibleBalloons.size() == 0) {
                                maxHeight = 0;
                            }

                            for (int i = 0; i < visibleBalloons.size(); i++) {
                                if (maxHeight < visibleBalloons.get(i).getCenterY()) {
                                    maxHeight = visibleBalloons.get(i).getCenterY();
                                }

                                if (visibleBalloons.get(i).getCenterY() + visibleBalloons.get(i).getHeight() / 2 <= 0) {
                                    if (visibleBalloons.get(i) instanceof BalloonNormal) {
                                        BalloonNormal balloonNormal = (BalloonNormal) visibleBalloons.get(i);
                                        if (balloonNormal.getValue() == rightAnswers.get(round)) {
                                            changeTask();
                                        }
                                    }
                                    disappear(visibleBalloons.get(i));
                                }

                                if (i <= visibleBalloons.size() - 1) {
                                    visibleBalloons.get(i).updateY();
                                }
                            }

                            if (canGenerateNew && !isEndOfGame) {
                                if (!isRightAnswerOnScreen()) {
                                    if (visibleBalloons.size() >= 3) {
                                        generateBonusBalloon();
                                        generateBalloons();
                                        generateBalloons();
                                    } else if (visibleBalloons.size() < 3) {
                                        generateBonusBalloon();
                                        generateBalloons();
                                        generateBalloons();
                                        generateBalloons();
                                    }
                                } else if (visibleBalloons.size() <= 5 /*&& i == visibleBalloons.size() - 1*/ && maxHeight < height * 0.75) {
                                    generateBonusBalloon();
                                    generateBalloons();
                                }
                            }
                        }
                    }
                }
            }
        }).start();
    }

    void startUpdate() {
        update();
    }


    /**
     * @param visibility
     * @param balloon
     */
    private void setVisibility(final boolean visibility, final Balloon balloon) {
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visibility) {
                    balloon.layoutForBalloon.setVisibility(View.VISIBLE);
                } else {
                    balloon.layoutForBalloon.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    void createBalloons(ConstraintLayout layoutForBalloons) {
        BalloonNormal newBalloon = new BalloonNormal(getSpeed(), layoutForBalloons);
        newBalloon.layoutForBalloon.setVisibility(View.INVISIBLE);
        waitingBalloons.add(newBalloon);
    }


    private boolean setStartCoordinate(final Balloon balloon) {
        final int min = balloon.getWidth() / 2;
        final int max = width - balloon.getWidth() / 2;
        final int[] startX = {min + (random.nextInt(max - min))};
        int counter = 0;
        while (isTouching(startX[0])) {
            counter++;
            if (counter > 100) {
                return false;
            }
            startX[0] = min + (random.nextInt(max - min));
        }

        balloon.setCoordinate(startX[0], height - balloon.getHeight() / 2);
        setVisibility(true, balloon);
        if (balloon instanceof BalloonNormal) {
            balloon.setSpeed(getSpeed());
        } else {
            balloon.setSpeed(getBonusSpeed());
        }
        gameActivity.setClickableBalloon(true, balloon);
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                balloon.layoutForBalloon.setAlpha(1.0f);
                if (balloon instanceof BalloonNormal) {
                    ImageView imageView = (ImageView) balloon.layoutForBalloon.getChildAt(0);
                    imageView.setImageResource(R.drawable.balloons_game_unknown_sub);
                }
            }
        });
        return true;
    }

    //Метод, проверяющий, касается ли шарик другого
    private boolean isTouching(int startX) {
        for (int i = 0; i < visibleBalloons.size() - 1; i++) {
            Balloon b = visibleBalloons.get(i);
            if ((startX >= b.getCenterX() - b.getWidth()) && (startX <= b.getCenterX() + b.getWidth())
                    && (b.getCenterY() > height / 2)) {
                return true;
            }
        }
        return false;
    }

    //TODO to make this more beautiful and easy
    private void setValue(final BalloonNormal balloon) {
        int chance = random.nextInt(100) + 1;
        rightAnswer = rightAnswers.get(round);

        if (round < rightAnswers.size() - 1) {
            nextRightAnswer = rightAnswers.get(round + 1);
            if (!isRightAnswerOnScreen() && (chance > 50 || visibleBalloons.size() >= 2)) {
                balloon.setValue(rightAnswer);
            } else if (chance > 66 && visibleBalloons.size() >= 3 && !isNextRightAnswerOnScreen()) {
                for (int i = 0; i < visibleBalloons.size(); i++) {
                    if (visibleBalloons.get(i) instanceof BalloonNormal) {
                        BalloonNormal balloonNormal = (BalloonNormal) visibleBalloons.get(i);
                        if (balloonNormal.getValue() == rightAnswer && balloonNormal.getCenterY() < height * 0.25) {
                            balloon.setValue(nextRightAnswer);
                        } else {
                            /*balloon.setValue(values.get(0));
                            values.add(values.get(0));
                            values.remove(values.get(0));*/
                            manipulationsWithWrongBalloon(balloon, chance, rightAnswer);
                        }
                    }
                }
            } else {
                /*balloon.setValue(values.get(0));
                values.add(values.get(0));
                values.remove(values.get(0));*/
                manipulationsWithWrongBalloon(balloon, chance, rightAnswer);
            }
        } else {
            if (!isRightAnswerOnScreen()) {
                balloon.setValue(rightAnswer);
            } else {
                /*balloon.setValue(values.get(0));
                values.add(values.get(0));
                values.remove(values.get(0));*/
                manipulationsWithWrongBalloon(balloon, chance, rightAnswer);
            }
        }

        String text = Double.toString(balloon.getValue());
        if (text.endsWith(".0")) {
            text = text.substring(0, text.length() - 2);
        }

        text = text.replace(".", ",");

        final String finalText = text;
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) balloon.layoutForBalloon.getChildAt(1)).setText(finalText);
            }
        });
    }

    private void manipulationsWithWrongBalloon(BalloonNormal balloon, int chance, double rightAnswer) {
        if (wrongAnswers.size() > 0) {
            balloon.setValue(wrongAnswers.get(0));
            wrongAnswers.remove(0);
        } else {
            double anotherWrong;
            if (chance > 50) {
                anotherWrong = rightAnswer + (random.nextDouble() * 15);
            } else {
                anotherWrong = rightAnswer - (random.nextDouble() * 15);
            }

            anotherWrong = new BigDecimal(anotherWrong).setScale(2, RoundingMode.UP).doubleValue();
            balloon.setValue(anotherWrong);
        }
        //balloon.setValue(wrongAnswers.get(0));
        //Log.e(TAG, "manipulationsWithWrongBalloon: ", );
        //wrongAnswers.add(wrongAnswers.get(0));
        //wrongAnswers.remove(0);
    }

    private boolean isRightAnswerOnScreen() {
        for (int i = 0; i < visibleBalloons.size(); i++) {
            if (visibleBalloons.get(i) instanceof BalloonNormal) {
                BalloonNormal balloonNormal = (BalloonNormal) visibleBalloons.get(i);
                if (balloonNormal.getValue() == rightAnswers.get(round)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNextRightAnswerOnScreen() {
        for (int i = 0; i < visibleBalloons.size(); i++) {
            if (visibleBalloons.get(i) instanceof BalloonNormal) {
                BalloonNormal balloonNormal = (BalloonNormal) visibleBalloons.get(i);
                if (balloonNormal.getValue() == rightAnswers.get(round + 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void generateBalloons() {
        final BalloonNormal balloon = waitingBalloons.get(0);
        visibleBalloons.add(balloon);
        waitingBalloons.remove(balloon);

        if (setStartCoordinate(balloon)) {
            setValue(balloon);
        } else {
            visibleBalloons.remove(balloon);
            waitingBalloons.add(balloon);
        }
    }

    void checkAnswer(final BalloonNormal balloon) {
        if (isCorrect(balloon)) {
            if ((System.currentTimeMillis() - timeCorrectAnswer) < 8000 || round == 0) {
                score.addScore(points, bonusPoints);
                score.setWinStreak(score.getWinStreak() + 1);
            } else {
                score.setWinStreak(0);
                score.addScore(points, bonusPoints);
            }
            gameActivity.lightOn();
            //disappear(balloon);
            gameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView imageView = (ImageView) balloon.layoutForBalloon.getChildAt(0);
                    imageView.setImageResource(R.drawable.balloon_game_right);
                }
            });

            balloon.setSpeed(0);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    speedUp(balloon);
                }
            }, 300);

            gameActivity.addPoints(score.getScore(), score.getWinStreak() - 1);
            changeTask();
        } else {
            score.setWinStreak(0);
            speedUp(balloon);
            health.removeHealth(1);
            gameActivity.takeAwayLife(3 - health.getCountHealth());
            if (health.isDead()) {
                isEndOfGame = true;
                gameActivity.endGame(score.getScore(), 0, true);
            }
            changeTask();
        }
    }

    //правильный ли ответ
    private boolean isCorrect(BalloonNormal balloon) {
        return balloon.getValue() == rightAnswers.get(round);
    }

    //поменять условие
    private void changeTask() {
        round++;
        gameActivity.setColorBlock(round);
        initWrongAnswers();
        timeCorrectAnswer = System.currentTimeMillis();
        if (round > COUNT_ROUNDS - 1) {
            isEndOfGame = true;
            gameActivity.endGame(score.getScore(), countStars(score.getScore()), false);
        }
        //final int id = gameActivity.getResources().getIdentifier(context.getPackageName() + ":drawable/id_" + round, null, null);

        if (round <= idNumbers.size() - 1) {
            gameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView imageView = (ImageView) gameActivity.findViewById(R.id.task);
                    imageView.setImageDrawable(dataBalloonGame.getPicture(idNumbers.get(round)));
                }
            });
        }
    }

    private int countStars(int score) {
        if (score > 900) {
            return 3;
        } else if (score > 500 && score <= 900) {
            return 2;
        } else {
            return 1;
        }
    }

    private void disappear(Balloon balloon) {
        visibleBalloons.remove(balloon);
        if (balloon instanceof BalloonNormal) {
            waitingBalloons.add((BalloonNormal) balloon);
        }
        //setAlpha(balloon);
    }

    private void setAlpha(final Balloon balloon) {
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                balloon.layoutForBalloon.animate().alpha(0.0f).setDuration(1000);
            }
        });
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                setVisibility(false, balloon);
            }
        }, 900);
    }

    private void speedUp(Balloon balloon) {
        balloon.setSpeed(height / 2);
        gameActivity.setClickableBalloon(false, balloon);
    }

    /*private void blowUp(final BalloonNormal balloon) {
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView imageView;
                imageView = (ImageView) balloon.layoutForBalloon.getChildAt(0);
                imageView.setImageResource(R.drawable.explosion);
                ((TextView) balloon.layoutForBalloon.getChildAt(1)).setText("");

                waitingBalloons.add(balloon);
                visibleBalloons.remove(balloon);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        setVisibility(false, balloon);
                        gameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((ImageView) balloon.layoutForBalloon.getChildAt(0)).setImageResource(R.drawable.balloon);
                            }
                        });
                    }
                }, 500);
            }
        });
    }*/

    //TODO to make it easy
    void createGreenBalloon(ConstraintLayout layoutForBonusBalloon) {
        final Balloon greenBalloon = new BalloonGreen(getBonusSpeed(), layoutForBonusBalloon);
        setVisibility(false, greenBalloon);
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView imageView;
                imageView = (ImageView) greenBalloon.layoutForBalloon.getChildAt(0);
                imageView.setImageResource(R.drawable.time);
                //((TextView) greenBalloon.layoutForBalloon.getChildAt(1)).setText("");
            }
        });
        bonusBalloons.add(greenBalloon);
    }

    void createPurpleBalloon(ConstraintLayout layoutForBonusBalloon) {
        final Balloon purpleBalloon = new BalloonPurple(getBonusSpeed(), layoutForBonusBalloon);
        setVisibility(false, purpleBalloon);
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView imageView;
                imageView = (ImageView) purpleBalloon.layoutForBalloon.getChildAt(0);
                imageView.setImageResource(R.drawable.score);
                //((TextView) purpleBalloon.layoutForBalloon.getChildAt(1)).setText("");
            }
        });
        bonusBalloons.add(purpleBalloon);
    }

    void createBlueBalloon(ConstraintLayout layoutForBonusBalloon) {
        final Balloon blueBalloon = new BalloonBlue(getBonusSpeed(), layoutForBonusBalloon);
        setVisibility(false, blueBalloon);
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView imageView;
                imageView = (ImageView) blueBalloon.layoutForBalloon.getChildAt(0);
                imageView.setImageResource(R.drawable.idea);
                //((TextView) blueBalloon.layoutForBalloon.getChildAt(1)).setText("");
            }
        });
        bonusBalloons.add(blueBalloon);
    }

    private void generateBonusBalloon() {
        int chanceToGenerateBonusBalloon = random.nextInt(100);
        if (round > 0 && chanceToGenerateBonusBalloon >= 0 && chanceToGenerateBonusBalloon <= 25) {
            int i = 0;
            int chance = random.nextInt(100) + 1;

            if (chance <= 50) {
                i = 0;
            } else if (chance > 50 && chance <= 82) {
                i = 1;
            } else if (chance > 82) {
                i = 2;
            }

            boolean isAnyBonus = false;
            for (int j = 0; j < bonusBalloons.size(); j++) {
                if (visibleBalloons.contains(bonusBalloons.get(j))) {
                    isAnyBonus = true;
                    break;
                }
            }

            if (!isAnyBonus) {
                visibleBalloons.add(bonusBalloons.get(i));
                if (!setStartCoordinate(bonusBalloons.get(i))) {
                    visibleBalloons.remove(bonusBalloons.get(i));
                }
            }
        }
    }

    void onClickBonus(Balloon balloon) {
        if (balloon instanceof BalloonBlue) {
            //remove all instead right
            //disappear(balloon);
            speedUp(balloon);
            canGenerateNew = false;
            //int counter = 0;
            for (int i = 0; i < visibleBalloons.size(); ) {
                if (visibleBalloons.get(i) instanceof BalloonNormal) {
                    BalloonNormal balloonNormal = (BalloonNormal) visibleBalloons.get(i);
                    if (balloonNormal.getValue() != rightAnswers.get(round)) {
                        disappear(balloonNormal);
                        setAlpha(balloonNormal);
                    } else {
                        //counter++;
                        i++;
                    }
                } else {
                    i++;
                }
            }
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    canGenerateNew = true;
                }
            }, 3000);

        } else if (balloon instanceof BalloonGreen) {
            //speed = speed/2 for 5 sec
            //disappear(balloon);
            speedUp(balloon);
            canGenerateNew = false;
            for (int i = 0; i < visibleBalloons.size(); i++) {
                if (visibleBalloons.get(i) instanceof BalloonNormal) {
                    visibleBalloons.get(i).setSpeed(visibleBalloons.get(i).getSpeed() / 2);
                }
            }
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    canGenerateNew = true;
                    for (int i = 0; i < visibleBalloons.size(); i++) {
                        if (!(visibleBalloons.get(i).getSpeed() == height / 2)) {
                            visibleBalloons.get(i).setSpeed(visibleBalloons.get(i).getSpeed() * 2);
                        }
                    }

                }
            }, 5000);

        } else if (balloon instanceof BalloonPurple) {
            //score = score*2 for 10 sec
            speedUp(balloon);
            //disappear(balloon);
            points = points * 2;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    points = points / 2;
                }
            }, 10000);
        }
    }

    //время полета шарика в секундах (от 8 до 12)
    private float defineFlightTime() {
        return 10 + (random.nextFloat() * 4);
    }

    private float getSpeed() {
        return height / defineFlightTime();
    }

    private float getBonusSpeed() {
        return height / (3 + (random.nextFloat() * 2));
    }

    int getPoints() {
        return points;
    }

    ArrayList<Balloon> getVisibleBalloons() {
        return visibleBalloons;
    }
}
