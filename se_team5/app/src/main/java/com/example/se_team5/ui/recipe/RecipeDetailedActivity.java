package com.example.se_team5.ui.recipe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/* 레시피 상세보기를 제공하는 액티비티 */
public class RecipeDetailedActivity extends AppCompatActivity {
    private Boolean liked; // 사용자가 이전에 추천한 레시피인지
    private Integer recipe_id; // 레시피 id
    private String user_id; // 사용자 id
    private List<Item> items_list = new ArrayList<>(); // 레시피에 필요한 아이템들의 리스트
    private List<Step> steps_list = new ArrayList<>(); // step 객체 리스트
    private StepAdapter stepAdapter; // step 객체를 recycler view에 붙여주는 adapter
    private ItemListAdapter itemListAdapter; // item을 recycler view에 붙여주는 adapter
    private ArrayList<Item> AllItems_; // 전체 아이템 리스트

    private Button like_button; // 추천 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detailed);

        final ImageView detail_image = findViewById(R.id.detail_image);
        final TextView recipe_title = findViewById(R.id.recipe_title);
        like_button = findViewById(R.id.like_button);


        // 넘겨준 정보 받기
        Intent intent = getIntent();

        String imagepath = intent.getExtras().getString("imagepath"); // 메인 이미지 주소
        String title = intent.getExtras().getString("title"); // 레시피 타이틀
        recipe_id = intent.getExtras().getInt("recipe_id"); // 레시피 아이디


        // 이미지 받아오고, 제목 붙이기
        Picasso.get().load(MyGlobal.getData()+"/image/"+imagepath).into(detail_image);
        recipe_title.setText("~"+title+"~");


        // get detailed recipe info from server by GET method
        SharedPreferences sp = getSharedPreferences("userFile", Context.MODE_PRIVATE);
        user_id = sp.getString("username","");
        new getDetailedRecipe().execute("/recipe/detail", "?recipe_id="+recipe_id+"&username="+user_id);

        // get a reference to recyclerView
        RecyclerView steplistView = findViewById(R.id.step_list);
        RecyclerView itemlistView = findViewById(R.id.item_list);

        // 각각 layout manager 붙이고, fixed size 취소함
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
        itemListAdapter = new ItemListAdapter(items_list,user_id);
        itemlistView.setAdapter(itemListAdapter);
        itemlistView.setScrollContainer(false);
        itemListAdapter.notifyDataSetChanged();

        // 추천하기 버튼이 눌렸을 때
        like_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 추천 변경 method 실행
                new changeLike().execute("/recipe/detail/like","?username="+user_id+"&recipe_id="+recipe_id+"&like="+ (liked ? 0:1));
            }
        });

    }

    /* 서버로부터 detail한 recipe 정보를 받아오는 method */
    private class getDetailedRecipe extends AsyncTask<String, Void, String> {

        HttpRequest req = new HttpRequest(MyGlobal.getData());

        @Override
        protected String doInBackground(String... params) {
            // GET으로 받아오기
            return req.sendGet(params[0], params[1]);

        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {
                if (response.equals("")) return;
                // response code가 200이면
                if (response.substring(0, 3).equals("200")) {
                    // get json object
                    JSONObject resObj = new JSONObject(response.substring(3));
                    JSONArray items = resObj.getJSONArray("items");
                    JSONArray steps = resObj.getJSONArray("steps");

                    // recipe 추천 개수 정보 받아오기
                    int likes = resObj.getInt("like");
                    like_button.setText("❤️ "+ likes);

                    SharedPreferences sp = getSharedPreferences("userFile", MODE_PRIVATE);
                    AllItems_ = Item.gsonParsing(sp.getString("allItems",""));

                    // 레시피 item 정보 받아오기
                    for (int i = 0; i < items.length(); i++) {
                        int id = items.getInt(i);
                        String name = AllItems_.get(id-1).getName();
                        items_list.add(new Item(name,0,id));
                    }

                    // 레시피 step 정보 받아오기
                    for (int i = 0; i < steps.length(); i++) {
                        JSONObject step = steps.getJSONObject(i);
                        steps_list.add(new Step(step));
                    }

                    // 레시피 like 정보 받아오기
                    liked = resObj.getInt("liked") == 1;
                    if(liked){
                        // 이미 like 했으면 누른상태로 변경
                        like_button.setPressed(true);
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

    /* 레시피 추천 변경 method */
    private class changeLike extends AsyncTask<String,Void,String>{
        HttpRequest req = new HttpRequest(MyGlobal.getData());

        @Override
        protected String doInBackground(String... params) {
            // GET으로 받아오기
            return req.sendGet(params[0], params[1]);

        }
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {
                if (response.equals("")) return;
                // 응답이 200이면
                if (response.substring(0, 3).equals("200")) {
                    JSONObject resObj = new JSONObject(response.substring(3));
                    // 사용자가 추천을 한건지 취소한건지의 정보를 받아옴
                    boolean myaction = resObj.getInt("action")==1;
                    // 현재 레시피의 추천 개수 정보
                    int likes = resObj.getInt("like");

                    // 버튼 상태 변경
                    like_button.setPressed(myaction);
                    like_button.setText("❤️ "+ likes);
                    liked = myaction; // 사용지의 행동 업데이트
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
