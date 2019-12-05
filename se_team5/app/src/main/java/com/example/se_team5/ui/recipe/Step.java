package com.example.se_team5.ui.recipe;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Step {
    String imagepath;
    String description;
    Step(JSONObject step) {
        try {
            imagepath = step.getString("image");
            description = step.getString("description");

        } catch (JSONException e) {
            Log.e("Error", "JSONException");
        }
    }
}

