package com.example.se_team5.ui.recipe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se_team5.HttpRequest;
import com.example.se_team5.MyGlobal;
import com.example.se_team5.R;
import com.example.se_team5.item.Item;
import com.example.se_team5.item.ItemHashMap;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailedActivity extends AppCompatActivity {
    private Integer like;
    private Integer recipe_id;
    private List<Item> items_list = new ArrayList<>();
    private List<Step> steps_list = new ArrayList<>();
    private StepAdapter stepAdapter;
    private ItemListAdapter itemListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detailed);

        final ImageView detail_image = findViewById(R.id.detail_image);
        final TextView recipe_title = findViewById(R.id.recipe_title);
        final TextView items = findViewById(R.id.items);
        final Button like_button = findViewById(R.id.like_button);


        // 넘겨준 정보 받기
        Intent intent = getIntent();

        String imagepath = intent.getExtras().getString("imagepath");
        String title = intent.getExtras().getString("title");
        recipe_id = intent.getExtras().getInt("recipe_id");

        Log.i("detail_activity",imagepath+", "+title+", "+recipe_id.toString());
        // 이미지와 제목 로드
        Picasso.get().load(MyGlobal.getData()+"/image/"+imagepath).into(detail_image);
        recipe_title.setText("~"+title+"~");


        // get detailed recipe info from server : GET method
        new getDetailedRecipe().execute("/recipe/detail", "?recipe_id=" + recipe_id);

        // get a reference to recyclerView
        RecyclerView steplistView = findViewById(R.id.step_list);
        RecyclerView itemlistView = findViewById(R.id.item_list);

        steplistView.setHasFixedSize(false);
        steplistView.setLayoutManager(new LinearLayoutManager(this));

        itemlistView.setHasFixedSize(false);
        itemlistView.setLayoutManager(new LinearLayoutManager(this));

        // step listView 어댑터 붙이기
        stepAdapter = new StepAdapter(steps_list);
        steplistView.setAdapter(stepAdapter);
        steplistView.setScrollContainer(false);
        stepAdapter.notifyDataSetChanged();

        // item listView 어댑터 붙이기
        itemListAdapter = new ItemListAdapter(items_list);
        itemlistView.setAdapter(itemListAdapter);
        itemlistView.setScrollContainer(false);
        itemListAdapter.notifyDataSetChanged();

        // 추천하기 버튼이 눌렸을 때
        like_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //HttpRequest request = new HttpRequest(MyGlobal.getData());
                //request.sendGet("/recipe/like","?recipe_id="+recipe_id);
                like_button.setEnabled(false);

            }
        });

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
                    JSONObject resObj = new JSONObject(response.substring(3));

                    like = resObj.getInt("like");
                    JSONArray items = resObj.getJSONArray("items");
                    JSONArray steps = resObj.getJSONArray("steps");

                    ItemHashMap itemHashMap = new ItemHashMap();
                    // 레시피 item 정보 받아오기
                    for (int i = 0; i < items.length(); i++) {
                        int id = items.getInt(i);
                        String name = itemHashMap.itemName(id);
                        items_list.add(new Item(name,0,id));
                        Log.i("item",name+", "+id);
                    }

                    // 레시피 step 정보 받아오기
                    for (int i = 0; i < steps.length(); i++) {
                        JSONObject step = steps.getJSONObject(i);
                        steps_list.add(new Step(step));
                    }


                    // 리스트에 변경사항 적용
                    stepAdapter.notifyDataSetChanged();
                    itemListAdapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                Log.e("Error", "JSONException");
            }
        }
    }

}
