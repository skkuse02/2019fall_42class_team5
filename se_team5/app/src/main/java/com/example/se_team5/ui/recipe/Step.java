package com.example.se_team5.ui.recipe;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Step {
    Integer order;
    String imagepath;
    String title;
    String description;

    Step(JSONObject step) {
        try {
            order = step.getInt("like");
            imagepath = step.getString("image");
            title = step.getString("recipe_name");
            description = step.getString("description");

        } catch (JSONException e) {
            Log.e("Error", "JSONException");
        }
    }
}

