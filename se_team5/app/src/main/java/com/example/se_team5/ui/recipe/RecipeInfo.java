package com.example.se_team5.ui.recipe;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeInfo {
    public Integer recipe_id;
    public String title;
    public String imagepath;
    public List<String> items;
    public Integer like;
    public String description;

    public RecipeInfo(JSONObject recipe) {
        try {
            recipe_id = recipe.getInt("recipe_id");
            title = recipe.getString("recipe_name");
            like = recipe.getInt("like");

            items = new ArrayList<>();
            JSONArray _items = recipe.getJSONArray("items");
            for(int i=0;i<_items.length();i++) items.add(_items.getString(i));

            description = recipe.getString("description");
            imagepath = recipe.getString("mainImage");

        }
        catch (JSONException e){
            Log.e("Error","JSONException");
        }
    }


}
