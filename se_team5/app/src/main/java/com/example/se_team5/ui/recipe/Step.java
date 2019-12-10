package com.example.se_team5.ui.recipe;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/* 조리 과정을 나타내는 객체 */
public class Step {
    String imagepath; // 각 조리과정의 이미지
    String description; // 조리과정 설명
    /* JSON을 parsing하는 생성자 */
    Step(JSONObject step) {
        try {
            imagepath = step.getString("image");
            description = step.getString("description");

        } catch (JSONException e) {
            Log.e("Error", "JSONException");
        }
    }
}

