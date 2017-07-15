package com.testgame.fatum.testgame.ballsGame;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.testgame.fatum.testgame.R;

/**
 * Created by Дарья on 28.05.2017.
 */

public class EndGameBalloons extends Fragment implements View.OnClickListener {
    View view;
    String time;
    int score;
    int countStars;
    ImageView[] stars = new ImageView[3];
    boolean needTime = true;
    boolean isLosing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_endgame, container, false);
        TextView textTime = (TextView) view.findViewById(R.id.textTime);
        TextView textScore = (TextView) view.findViewById(R.id.textScore);
        TextView estimate = (TextView) view.findViewById(R.id.textView8);
        stars[0] = (ImageView) view.findViewById(R.id.star1);
        stars[1] = (ImageView) view.findViewById(R.id.star2);
        stars[2] = (ImageView) view.findViewById(R.id.star3);
        for (int i = 0; i < stars.length; i++) {
            if (countStars - 1 < i) {
                stars[i].setColorFilter(getResources().getColor(R.color.block_gray), PorterDuff.Mode.SRC_ATOP);
            }
        }
        textScore.setText(String.valueOf(score));

        if (needTime) {
            textTime.setText(time);
        } else {
            textTime.setVisibility(View.INVISIBLE);
            (view.findViewById(R.id.textView10)).setVisibility(View.INVISIBLE);
        }

        if (isLosing) {
            estimate.setText("Вы проиграли!");
        }
        view.findViewById(R.id.nextGame).setOnClickListener(this);
        return view;
    }

    public void setTime(int timeSeconds) {
        int minutes = timeSeconds / 60;
        int sec = timeSeconds % 60;
        time = String.format("%02d:%02d", minutes, sec);

    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setCountStars(int count) {
        countStars = count;
    }

    public void setNeedTime(boolean need) {
        needTime = need;
    }

    public void setIsLosing(boolean isLosing) {
        this.isLosing = isLosing;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextGame:
                ClickNextLvl();
                break;
        }
    }

    void ClickNextLvl() {
        Intent intent = new Intent(getActivity(), BalloonsGameActivity.class);
        startActivity(intent);

    }
}
