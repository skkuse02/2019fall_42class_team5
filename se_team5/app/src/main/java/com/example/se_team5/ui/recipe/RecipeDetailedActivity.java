package com.example.se_team5.ui.recipe;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se_team5.HttpRequest;
import com.example.se_team5.MyGlobal;
import com.example.se_team5.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailedActivity extends AppCompatActivity {
    private Integer recipe_id;
    private String title;
    private BitmapDrawable main_image;
    private List<Step> steps_list = new ArrayList<>();
    private StepAdapter stepAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detailed);

        // 넘겨준 정보 받기
        Intent intent = getIntent();
        main_image = (BitmapDrawable) intent.getSerializableExtra("image");
        title = intent.getExtras().getString("title");
        recipe_id = intent.getExtras().getInt("recipe_id");

        // get detailed recipe info from server : GET method
        new getDetailedRecipe().execute("/recipe/detailed", "?recipe_id=" + recipe_id);

        // get a reference to recyclerView
        RecyclerView recyclerView = findViewById(R.id.step_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // recycler view에 어댑터 붙이기
        stepAdapter = new StepAdapter(this,steps_list);
        recyclerView.setAdapter(stepAdapter);
        stepAdapter.notifyDataSetChanged();

    }

    private class getDetailedRecipe extends AsyncTask<String, Void, String> {

        HttpRequest req = new HttpRequest(MyGlobal.getData());

        @Override
        protected String doInBackground(String... params) {
            return req.sendGet(params[0], params[1]);

        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {
                if (response.equals("")) return;
                if (response.substring(0, 3).equals("200")) {
                    // get json object
                    JSONObject jObject = new JSONObject(response.substring(3));

                    JSONArray step_array = jObject.getJSONArray("steps");
                    int length = step_array.length();

                    // 레시피 step들의 정보 받아오기
                    for (int i = 0; i < length; i++) {
                        JSONObject step = step_array.getJSONObject(i);
                        steps_list.add(new Step(step));

                    }
                    stepAdapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                Log.e("Error", "JSONException");
            }
        }
    }

}
