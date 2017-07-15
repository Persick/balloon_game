package com.testgame.fatum.testgame.ballsGame;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.testgame.fatum.testgame.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Дарья on 07.06.2017.
 */

class DataBalloonGameLocal implements DataBalloonGame {
    class ElementGame {
        Drawable picture;
        double rightAnswer;
        ArrayList<Double> wrongAnswers;

        public ElementGame(Drawable picture, double rightAnswer, ArrayList<Double> wrongAnswers) {
            this.picture = picture;
            this.rightAnswer = rightAnswer;
            this.wrongAnswers = wrongAnswers;
        }
    }
    ArrayList<ElementGame> gameElements = new ArrayList<>();
    Context context;

    public DataBalloonGameLocal(Context context) {
        this.context = context;
    }

    @Override
    public void downloadData() {
        JSONArray obj = null;
        try {
            obj = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < obj.length(); i++) {
                JSONObject element = obj.getJSONObject(i);
                JSONArray wrongAnswers = element.getJSONArray("wrong answers");
                ArrayList<Double> wrongAnswersList = new ArrayList<>();
                for (int j = 0; j < wrongAnswers.length(); j++) {
                    wrongAnswersList.add(wrongAnswers.getDouble(j));
                }
                int id = element.getInt("id");
                double rightAnswer = element.getDouble("value");

                int id_ = context.getResources().getIdentifier(context.getPackageName() + ":drawable/id_" + id, null, null);
                Drawable picture = context.getResources().getDrawable(id_);

                gameElements.add(new ElementGame(picture, rightAnswer, wrongAnswersList));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getAmountData() {
        return gameElements.size();
    }

    @Override
    public Drawable getPicture(int i) {
        return gameElements.get(i).picture;
    }

    @Override
    public double getRightAnswer(int i) {
        return gameElements.get(i).rightAnswer;
    }

    @Override
    public ArrayList<Double> getWrongAnswers(int i) {
        return gameElements.get(i).wrongAnswers;
    }
    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.balloons_game_data);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


}
