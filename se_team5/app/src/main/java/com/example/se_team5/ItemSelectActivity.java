package com.example.se_team5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se_team5.item.Item;
import com.example.se_team5.item.RecommendItemsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemSelectActivity extends AppCompatActivity {

    private RecyclerView recyclerView;          //리사이클러 뷰 선언
    private RecommendItemsAdapter myAdapter;    //뷰 어댑터 선언
    private ArrayList<Item> ITEM_LIST;          //사용자의 냉장고에 있는 아이템 리스트를 저장할 객체 선언

    private String user_id;     //사용자 아이디 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*추천 기능활용을 위해 선호하는 아이템, 비호하는 아이템을 선택하는 액티비티이다. */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        /* 로컬에서 사용자 아이디와 냉장고에 있는 아이템 리스트를 불러옴*/
        SharedPreferences sp = getSharedPreferences("userFile", Context.MODE_PRIVATE);
        ITEM_LIST = Item.jsonParsing(this, sp.getString("userRefrigerator",""));
        user_id = sp.getString("username","");

        /*추천 버튼 선언*/
        Button recommendButton = findViewById(R.id.recommendButton);

        /*리사이클러 뷰 및 매니저, 어댑터를 지정*/
        recyclerView = findViewById(R.id.recommendRecyclerView);
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(manager); // LayoutManager 등록
        myAdapter = new RecommendItemsAdapter(ITEM_LIST);
        recyclerView.setAdapter(myAdapter);  // Adapter 등록

        recommendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*추천버튼 클릭시 선호, 비호하는 아이템을 JSON으로 변환하여 송신한다. */

                JSONObject postData = new JSONObject();
                try {
                    postData.put("username",user_id);

                    SparseBooleanArray good = myAdapter.getmSelectedItems1();
                    JSONArray good_array = new JSONArray();
                    for(int i = 0; i < ITEM_LIST.size(); i++){
                        if(good.get(i, false))
                            good_array.put(ITEM_LIST.get(i).getId());
                    }
                    postData.put("good", good_array);

                    SparseBooleanArray bad = myAdapter.getmSelectedItems2();
                    JSONArray bad_array = new JSONArray();
                    for(int i = 0; i < ITEM_LIST.size(); i++){
                        if(bad.get(i, false))
                            bad_array.put(ITEM_LIST.get(i).getId());
                    }
                    postData.put("bad", bad_array);

                    if(good_array.length()==0){/* 선호하는 재료를 하나도 선택하지 않은 경우 알림*/
                        Toast.makeText(getParent(), "좋아하는 재료를 골라주세요", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = new Intent(ItemSelectActivity.this,RecommendActivity.class);
                        intent.putExtra("postData",postData.toString());
                        Log.d("Ddd", postData.toString());
                        ItemSelectActivity.this.startActivity(intent);
                        finish();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}