package com.example.se_team5;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.se_team5.ui.recipe.RecipeInfo;
import com.example.se_team5.ui.recipe.RecipeInfoAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecommendListActivity extends AppCompatActivity {
    private List<RecipeInfo> recipe_list = new ArrayList<>(); // recipe list
    private RecipeInfoAdapter recipeInfoAdapter;
    private ShimmerRecyclerView shimmerRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_list);


        Intent intent = getIntent();
        String postData = intent.getExtras().getString("postData");


        // get a reference to recyclerView
        RecyclerView recyclerView = findViewById(R.id.recipe_listview2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // recycler view에 어댑터 붙이기
        recipeInfoAdapter = new RecipeInfoAdapter(this,recipe_list);
        recyclerView.setAdapter(recipeInfoAdapter);

        shimmerRecycler = findViewById(R.id.shimmer_recycler_view2);
        shimmerRecycler.showShimmerAdapter();

        new recommendRecipe().execute("/recipe/recommendation", postData);



    }

    private class recommendRecipe extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpRequest req = new HttpRequest(MyGlobal.getData());
            return req.sendPost(params[0], params[1]);

        }
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response == null) return ;
            if (response.substring(0,3).equals("200")) {
                try {
                    JSONObject jObject = new JSONObject(response.substring(3));
                    JSONArray recipe_main = jObject.getJSONArray("recipe_main");
                    int length = recipe_main.length();

                    // 레시피 각각의 정보 받아오기
                    for (int i = 0; i < length; i++) {
                        JSONObject recipe = recipe_main.getJSONObject(i);
                        // json parsing 해서 recipe list에 넣기
                        recipe_list.add(new RecipeInfo(recipe));

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                shimmerRecycler.hideShimmerAdapter();
                recipeInfoAdapter.notifyDataSetChanged();

            }
        }
    }
}
