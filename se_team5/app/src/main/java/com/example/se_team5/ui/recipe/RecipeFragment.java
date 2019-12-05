package com.example.se_team5.ui.recipe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.se_team5.HttpRequest;
import com.example.se_team5.MyGlobal;
import com.example.se_team5.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends Fragment {
    private List<RecipeInfo> recipe_list = new ArrayList<>(); // recipe list
    private RecipeInfoAdapter recipeInfoAdapter;
    private ShimmerRecyclerView shimmerRecycler;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // recipe view 띄우기 - frame을 view로 변환
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);


        // button, text_view
        final Button search_button = view.findViewById(R.id.search_button);
        final TextView recipe_keyword = view.findViewById(R.id.recipe_keyword);


        // get a reference to recyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recipe_listview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // recycler view에 어댑터 붙이기
        recipeInfoAdapter = new RecipeInfoAdapter(getActivity(),recipe_list);
        recyclerView.setAdapter(recipeInfoAdapter);
        recipeInfoAdapter.notifyDataSetChanged();

        shimmerRecycler = view.findViewById(R.id.shimmer_recycler_view);
        shimmerRecycler.hideShimmerAdapter();


        // search button 누른 경우
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 검색 할 키워드가 있다면 검색 요청 보내기

                String search_keyword = recipe_keyword.getText().toString();
                if (search_keyword.length() == 0) return;
                // list 초기화
                recipe_list.clear();
                recipeInfoAdapter.notifyDataSetChanged();

                shimmerRecycler.showShimmerAdapter();
                // search_keyword를 보내서 'recipe_main' json list 받아온다.

                new searchRecipe().execute("/recipe/search", "?keyword="+search_keyword);


            }
        });

        return view;
    }

    private class searchRecipe extends AsyncTask<String, Void, String> {
        HttpRequest req = new HttpRequest(MyGlobal.getData());
        @Override
        protected String doInBackground(String... params) {
            return req.sendGet(params[0], params[1]);

        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {

                if(response.equals("")) return;
                if (response.substring(0, 3).equals("200")) {
                    JSONObject jObject = new JSONObject(response.substring(3));
                    JSONArray recipe_main = jObject.getJSONArray("recipe_main");
                    int length = recipe_main.length();

                    // 레시피 각각의 정보 받아오기
                    for (int i = 0; i < length; i++) {
                        JSONObject recipe = recipe_main.getJSONObject(i);
                        // json parsing 해서 recipe list에 넣기
                        recipe_list.add(new RecipeInfo(recipe));

                    }

                    recipeInfoAdapter.notifyDataSetChanged();
                    shimmerRecycler.hideShimmerAdapter();
                }

            } catch (Exception e) {
                Log.e("Error", "Exception");
                e.printStackTrace();
            }
        }
    }



}