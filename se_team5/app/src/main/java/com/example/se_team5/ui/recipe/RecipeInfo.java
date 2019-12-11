package com.example.se_team5.ui.recipe;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/* 리스트로 보이는 레시피 간단한 정보 객체 */
public class RecipeInfo {
    public Integer recipe_id; // 레시피 id
    public String title; // 레시피 제목
    public String imagepath; // 레시피 메인 이미지 주소
    public List<Integer> items; // 주요 아이템 리스트
    public Integer like; // 해당 레시피 추천 수
    public String description; // 레시피 소개

    /* JSON을 parsing하는 생성자 */
    public RecipeInfo(JSONObject recipe) {
        try {
            recipe_id = recipe.getInt("recipe_id");
            title = recipe.getString("recipe_name");
            like = recipe.getInt("like");

            items = new ArrayList<>();
            JSONArray _items = recipe.getJSONArray("items");
            for(int i=0;i<_items.length();i++) items.add(_items.getInt(i));

            description = recipe.getString("description");
            imagepath = recipe.getString("mainImage");

        }
        catch (JSONException e){
            Log.e("Error","JSONException");
        }
    }


}
