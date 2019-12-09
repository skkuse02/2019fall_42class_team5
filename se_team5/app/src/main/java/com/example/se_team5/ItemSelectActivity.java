package com.example.se_team5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;

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

    private RecyclerView recyclerView;
    private RecommendItemsAdapter myAdapter;
    private ArrayList<Item> AllItems_;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        SharedPreferences sp = getSharedPreferences("userFile", Context.MODE_PRIVATE);
        AllItems_ = Item.gsonParsing(sp.getString("allItems",""));
        user_id = sp.getString("username","");

        Button recommendButton = findViewById(R.id.recommendButton);

        recyclerView = findViewById(R.id.recommendRecyclerView);
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(manager); // LayoutManager 등록

        myAdapter = new RecommendItemsAdapter(AllItems_);
        recyclerView.setAdapter(myAdapter);  // Adapter 등록
        recommendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                JSONObject postData = new JSONObject();
                try {
                    postData.put("username",user_id);

                    SparseBooleanArray good = myAdapter.getmSelectedItems1();
                    JSONArray good_array = new JSONArray();
                    for(int i = 0; i < AllItems_.size(); i++){
                        if(good.get(i, false))
                            good_array.put(i);
                    }
                    postData.put("good", good_array);

                    SparseBooleanArray bad = myAdapter.getmSelectedItems2();
                    JSONArray bad_array = new JSONArray();
                    for(int i = 0; i < AllItems_.size(); i++){
                        if(bad.get(i, false))
                            bad_array.put(i);
                    }
                    postData.put("bad", bad_array);

                    Intent intent = new Intent(ItemSelectActivity.this,RecommendActivity.class);
                    intent.putExtra("postData",postData.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}