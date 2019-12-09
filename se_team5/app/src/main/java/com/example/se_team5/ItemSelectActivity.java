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

    private RecyclerView recyclerView;
    private RecommendItemsAdapter myAdapter;
    private ArrayList<Item> AllItems_;
    private ArrayList<Item> ITEM_LIST;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        SharedPreferences sp = getSharedPreferences("userFile", Context.MODE_PRIVATE);
        AllItems_ = Item.gsonParsing(sp.getString("allItems",""));
        ITEM_LIST = jsonParsing(sp.getString("userRefrigerator",""));
        user_id = sp.getString("username","");

        Button recommendButton = findViewById(R.id.recommendButton);

        recyclerView = findViewById(R.id.recommendRecyclerView);
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(manager); // LayoutManager 등록

        myAdapter = new RecommendItemsAdapter(ITEM_LIST);
        recyclerView.setAdapter(myAdapter);  // Adapter 등록
        recommendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
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

                    if(good_array.length()==0){
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

    private ArrayList<Item> jsonParsing(String json) {
        SharedPreferences sp = getSharedPreferences("userFile", Context.MODE_PRIVATE);
        ArrayList<Item> AllItems_ = Item.gsonParsing(sp.getString("allItems",""));
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray ItemArray = jsonObject.getJSONArray("items");

            ArrayList<Item> li = new ArrayList<Item>();

            for(int i=0; i<ItemArray.length(); i++)
                li.add(AllItems_.get((int)ItemArray.get(i)-1));

            return li;
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}