package com.testgame.fatum.testgame.ballsGame;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.testgame.fatum.testgame.ChoiceMiniGamesActivity;
import com.testgame.fatum.testgame.GamePause;
import com.testgame.fatum.testgame.HeaderFragment;
import com.testgame.fatum.testgame.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BalloonsGameActivity extends AppCompatActivity implements GamePause {


    BalloonsControl balloonsControl;
    ConstraintLayout mainLayout;
    FrameLayout containerForBalloons;
    int secondsGame = 0;
    ImageView light;
    TextView scoreText;
    TextView combo;
    TextView combo2;
    private boolean timerGoes = true;
    private boolean isPause = false;
    HeaderFragment headerFragment;
    ImageView bubble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balloons_game_new);
        headerFragment = (HeaderFragment) getFragmentManager().findFragmentById(R.id.header);
        headerFragment.setCountTasks(7); //TODO instead of 7 make final variable
        getContentLayout();

        timer();
        ViewTreeObserver observer = containerForBalloons.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                startBalloonsControlGame();
                containerForBalloons.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }


    private void getContentLayout() {
        mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);
        containerForBalloons = (FrameLayout) findViewById(R.id.containerForBalls);
        scoreText = (TextView) findViewById(R.id.score);
        combo = (TextView) findViewById(R.id.combo);
        combo2 = (TextView) findViewById(R.id.combo2);
        light = (ImageView) findViewById(R.id.light);
    }

    void startBalloonsControlGame() {
        balloonsControl = new BalloonsControl(mainLayout.getHeight(), mainLayout.getWidth(), this);
        ConstraintLayout view;

        for (int i = 0; i < 15; i++) {
            view = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.element_balloon_game, containerForBalloons, false);
            view.setLayoutParams(new ViewGroup.LayoutParams(mainLayout.getWidth() / 4, (mainLayout.getHeight() / 4)));
            containerForBalloons.addView(view);


            final ConstraintLayout finalView = view;
            balloonsControl.createBalloons(finalView);
        }

        view = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.element_balloon_game_bonus, containerForBalloons, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(mainLayout.getWidth() / 4, (mainLayout.getHeight() / 4)));
        containerForBalloons.addView(view);
        balloonsControl.createGreenBalloon(view);

        view = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.element_balloon_game_bonus, containerForBalloons, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(mainLayout.getWidth() / 4,  (mainLayout.getHeight() / 4)));
        containerForBalloons.addView(view);
        balloonsControl.createPurpleBalloon(view);

        view = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.element_balloon_game_bonus, containerForBalloons, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(mainLayout.getWidth() / 4,  (mainLayout.getHeight() / 4)));
        containerForBalloons.addView(view);
        balloonsControl.createBlueBalloon(view);
        balloonsControl.startUpdate();
    }

    void addPoints(final int score, int winStreak) {
        combo.setVisibility(View.VISIBLE);
        combo.setText("+" + balloonsControl.getPoints());
        if (winStreak >= 1) {
            combo2.setVisibility(View.VISIBLE);
            combo2.setText("Комбо x" + winStreak + "!");
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        combo.setVisibility(View.INVISIBLE);
                        combo2.setVisibility(View.INVISIBLE);
                        if (score < 1000) {
                            //scoreText.setText("0" + String.valueOf(score));
                            setTextScore("000" + String.valueOf(score));
                        } else {
                            //scoreText.setText(String.valueOf(score));
                            setTextScore("00" + String.valueOf(score));
                        }
                    }
                });
            }
        }, 1000);
    }

    void takeAwayLife(int countLife) {
        headerFragment.removeHeart(countLife);
    }

    void setColorBlock(final int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                headerFragment.taskResolved(i - 1);
            }
        });
    }

    void setTextScore(String text) {
        headerFragment.setScore(text);
    }

    void endGame(int score, int countStars, boolean isLosing) {
        timerGoes = false;
        EndGameBalloons youFragment = new EndGameBalloons();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()          // получаем экземпляр FragmentTransaction
                .add(R.id.mainLayout, youFragment)
                .commit();
        youFragment.setScore(score);
        youFragment.setNeedTime(true);
        youFragment.setCountStars(countStars);
        youFragment.setIsLosing(isLosing);
        youFragment.setTime(secondsGame);
    }

    void timer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (timerGoes) {
                    int minutes = secondsGame / 60;
                    int sec = secondsGame % 60;
                    String time = String.format("%02d:%02d", minutes, sec);
                    headerFragment.setTime(time);
                    secondsGame++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    void lightOn() {
        light.setVisibility(View.VISIBLE);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        light.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }, 1500);
    }

    void setClickableBalloon(boolean isClickable, Balloon balloon) {
        if (isClickable) {
            balloon.layoutForBalloon.setClickable(true);
        } else {
            balloon.layoutForBalloon.setClickable(false);
        }
    }

    void setClickableAll(boolean isClickable){
        ArrayList<Balloon> visibleBalloons = balloonsControl.getVisibleBalloons();
        if(isClickable){
            for (int i = 0; i < visibleBalloons.size(); i++) {
                setClickableBalloon(true, visibleBalloons.get(i));
            }
        } else {
            for (int i = 0; i < visibleBalloons.size(); i++) {
                setClickableBalloon(false, visibleBalloons.get(i));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ChoiceMiniGamesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
        timerGoes = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
        timerGoes = false;
    }

    public boolean isPause() {
        return isPause;
    }

    @Override
    public void pause() {
        //TODO stop movement, unclickable, button
        if (!isPause) {
            isPause = true;
            timerGoes = false;
            setClickableAll(false);
        } else {
            isPause = false;
            timerGoes = true;
            setClickableAll(true);
        }
    }

    private void moveBubbles( View view )
    {
        //RelativeLayout root = (RelativeLayout) findViewById( R.id.rootLayout );

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics( dm );
        int statusBarOffset = dm.heightPixels - containerForBalloons.getMeasuredHeight();

        int originalPos[] = new int[2];
        view.getLocationOnScreen( originalPos );

        int xDest = dm.widthPixels/2;
        xDest -= (view.getMeasuredWidth()/2);
        int yDest = dm.heightPixels/2 - (view.getMeasuredHeight()/2) - statusBarOffset;

        TranslateAnimation anim = new TranslateAnimation( 0, xDest - originalPos[0] , 0, yDest - originalPos[1] );
        anim.setDuration(1000);
        anim.setFillAfter( true );
        view.startAnimation(anim);
    }
}
